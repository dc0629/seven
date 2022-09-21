package top.flagshen.myqq.common.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * The type Redis cache util.
 *
 * @author pan.xinyi @iwhalecloud.com
 * @Title:
 * @Description:
 * @date 2020 /7/8 - 13:23
 */
@Component
@Slf4j
public class RedisCacheUtil {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 分布式锁上锁
     *
     * @param cacheKey the cache key
     * @param value    the value
     * @param timeout  键值对缓存的时间，单位是毫秒
     * @return 设置成功返回true ，否则返回false
     */
    public boolean tryLock(String cacheKey, Object value, long timeout) {
        boolean isSuccess = redisTemplate.opsForValue().setIfAbsent(cacheKey, value, timeout, TimeUnit.SECONDS);
        return isSuccess;
    }

    /**
     * 删除分布式锁
     *
     * @param cacheKey the cache key
     */
    public void deleteLock(String cacheKey) {
        if (null != cacheKey) {
            redisTemplate.delete(cacheKey);
        }
    }
}
