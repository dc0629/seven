package top.flagshen.myqq.common.context;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import top.flagshen.myqq.dao.userinfo.entity.UserInfoDO;
import top.flagshen.myqq.service.userinfo.IUserInfoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @author dengchao
 */
public class InvocationContextSetupInterceptor extends HandlerInterceptorAdapter implements Ordered {

	private static final String OPEN_ID_NAME = "open-id";

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	@Autowired
	private IUserInfoService userInfoService;

	private static final int DEFAULT_ORDER = Ordered.HIGHEST_PRECEDENCE + 10;

	@Override
	public int getOrder() {
		return DEFAULT_ORDER;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (LocalInvocationContext.getContext() == null) {
			String qqNum = null;
			String openId = request.getHeader(OPEN_ID_NAME);
			if (StringUtils.isNotBlank(openId)) {
				// 如果key不存在，就去数据库查一遍
				if (!redisTemplate.hasKey(openId)) {
					UserInfoDO user = userInfoService.getOne(new LambdaQueryWrapper<UserInfoDO>()
							.select(UserInfoDO::getQqNum)
							.eq(UserInfoDO::getOpenId, openId));
					if (user != null) {
						qqNum = user.getQqNum();
					}
				} else {
					// 从缓存中获取对应的qq号
					qqNum = redisTemplate.opsForValue().get(openId);
				}
				// 续一天的缓存，证明这个人活跃着
				redisTemplate.opsForValue().set(openId, qqNum, 1, TimeUnit.DAYS);
			}
			String traceId = MDC.get("traceId");
			InvocationContext context = new DefaultInvocationContext(traceId, qqNum);
			LocalInvocationContext.bindContext(context);
		}
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		super.afterCompletion(request, response, handler, ex);
		LocalInvocationContext.unbindContext();
	}

}
