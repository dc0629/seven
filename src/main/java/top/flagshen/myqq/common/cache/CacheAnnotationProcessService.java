package top.flagshen.myqq.common.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import top.flagshen.myqq.common.exception.MyException;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * 信息描述
 *
 * @author dengchao
 */
@Component
@Aspect
@Slf4j
public class CacheAnnotationProcessService implements Ordered {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private RedisCacheUtil redisCacheUtil;

	private static final String EMPTY_STRING = "";


	private static final long ZERO_EXPIRE_TIME = 0L;

	private static Integer DEFAULT_TIME_OUT = 50;

	//最多重试10次
	private static final Integer LOAD_RETRY_TIMES = 20;

	//每次等待30秒
	private static final Integer WAIT_LOAD_SLEEP_MILLS = 30;

	//每次重试如果从redis获取失败，最多等待1
	private static final Duration MAX_RETRY_WAIT_TIME = Duration.ofSeconds(1);

	//加锁前缀
	private static final String REDIS_LOCK_PREFIX = "REDIS_LOCK_PREFIX_";

	//二级缓存前缀
	private static final String SECOND_CACHE_PREFIX = "SECOND_CACHE_PREFIX_";

	//二级缓存多缓存60秒
	private static final Integer SECOND_CACHE_EXPIRE = 60;

	/**
	 * 拦截@CacheFind注解标识的方法. 通知选择: 缓存的实现应该选用环绕通知 步骤: 1.动态生成key 用户填写的key+用户提交的参数
	 */
	@Around("@annotation(cacheFind)")
	public Object around(ProceedingJoinPoint joinPoint, CacheFind cacheFind) throws Throwable {
		String prefixKeyName = cacheFind.prefixKeyName();
		long expireTime = cacheFind.expireTime();
		Object[] args = joinPoint.getArgs();
		String cacheKey = prefixKeyName;
		String methodName = joinPoint.getSignature().getName();

		String key = cacheFind.key();

		if (StringUtils.isNotBlank(key)) {
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			Method method = signature.getMethod();
			cacheKey += parseExpression(key, method, args);
		}


		Object redisResult = getFirstCacheFromRedis(cacheKey);
		//从一级缓存获取到数据，直接返回
		if (redisResult != null) {
			// 如果是自定义异常，就抛出
			if (redisResult instanceof MyException) {
				throw (MyException) redisResult;
			}
			return redisResult;
		} else {
			String lockKey = REDIS_LOCK_PREFIX + cacheKey;
			//尝试获取分布式锁，拿到锁的时候执行业务逻辑并更新一级，二级缓存
			if (redisCacheUtil.tryLock(lockKey,1, DEFAULT_TIME_OUT)) {
				try {
					Object result = joinPoint.proceed(args);
					saveToRedis(expireTime, cacheKey, result);
					return result;
				} catch (MyException e) {
					saveToRedis(expireTime, cacheKey, e);
					throw e;
				} finally {
					redisCacheUtil.deleteLock(lockKey);
				}
			} else {
				//获取锁失败，优先从二级缓存获取数据
				Object result = getSecondCacheFromRedis(cacheKey);
				if (result != null) {
					return result;
				}
				//二级缓存也是空的，重试10次，每次30ms，最多等待300ms，如果在此期间依然没有从缓存获取到数据，请求失败
				try {
					Object retryResult = waitValueLoaded(cacheKey);
					if (retryResult == null) {
						log.error("requestFailed|retry get from cache failed, method: {}, key: {}", methodName, cacheKey);
						throw new RuntimeException("requestFailed|get data wait retry timeout");
					}
					return retryResult;
				} catch (Exception e) {
					log.error("requestFailed|retry get from cache failed, method: {}, key: {}", methodName, cacheKey, e);
					throw new RuntimeException("requestFailed|get data from cache failed");
				}
			}
		}
	}


	/**
	 * 重试机制
	 * @param key 超时时间
	 */
	private Object waitValueLoaded(String key) throws InterruptedException {
		Instant deadline = Instant.now().plus(MAX_RETRY_WAIT_TIME);
		//最多重试20次
		for (int i = 0; i < LOAD_RETRY_TIMES; i++) {
			Object redisResult = getFirstCacheFromRedis(key);
			if (redisResult != null) {
				return redisResult;
			} else {
				//没有获取导数据，等待30毫秒
				TimeUnit.MILLISECONDS.sleep(WAIT_LOAD_SLEEP_MILLS);
			}
			if (Instant.now().isAfter(deadline)) {
				return null;
			}
		}
		return null;
	}

	/**
	 * 缓存数据保存，同时更新一级缓存和二级缓存
	 * @param expireTime 超时时间
	 * @param cacheKey 缓存key
	 * @param result 缓存内容
	 */
	private void saveToRedis(long expireTime, String cacheKey, Object result) {

		//设置一级缓存
		saveRedis(expireTime, cacheKey, result);

		//设置二级缓存，过期时间在一级缓存的过期时间上加60秒
		saveRedis(SECOND_CACHE_EXPIRE+expireTime, SECOND_CACHE_PREFIX + cacheKey, result);
	}

	private void saveRedis(long expireTime, String cacheKey, Object result) {
		if (expireTime > ZERO_EXPIRE_TIME) {
			redisTemplate.opsForValue().set(cacheKey, result, expireTime, TimeUnit.SECONDS);
		} else {
			redisTemplate.opsForValue().set(cacheKey, result);
		}
	}

	/**
	 * 从一级缓存读取数据
	 * @param cacheKey 缓存key
	 */
	private Object getFirstCacheFromRedis(String cacheKey) {
		Object redisResult = redisTemplate.opsForValue().get(cacheKey);
		return redisResult;
	}
	/**
	 * 从二级缓存读取数据
	 * @param cacheKey 缓存key
	 */
	private Object getSecondCacheFromRedis(String cacheKey) {
		Object redisResult = redisTemplate.opsForValue().get(SECOND_CACHE_PREFIX +cacheKey);
		return redisResult;
	}

	/**
	 * 解析
	 * @param expressionString
	 * @param method
	 * @param args
	 * @return
	 */
	private String parseExpression(String expressionString, Method method, Object[] args) {
		//获取被拦截方法参数名列表
		LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
		String[] paramNameArr = discoverer.getParameterNames(method);
		if (paramNameArr == null) {
			return EMPTY_STRING;
		}
		//SPEL解析
		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();
		for (int i = 0; i < paramNameArr.length; i++) {
			context.setVariable(paramNameArr[i], args[i]);
		}
		expressionString += EMPTY_STRING;
		return parser.parseExpression(expressionString).getValue(context, String.class);
	}

	@Override
	public int getOrder() {
		return 1;
	}
}
