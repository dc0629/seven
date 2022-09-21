package top.flagshen.myqq.common.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存配置注解
 *
 * @author dengchao
 */

@Retention(RetentionPolicy.RUNTIME) // 该注解什么时候有效
@Target({ ElementType.METHOD }) // 对方法有效
public @interface CacheFind {

	/**
	 * 缓存键前缀
	 * 
	 * @return
	 */
	String prefixKeyName();

	/**
	 * 缓存过期时间，单位秒
	 * 
	 * @return
	 */
	long expireTime() default 20L;

	/**
	 * 自定义的key，用el表达式，例如 key = "':informationId:' + #req.informationId + '_language:' + #req.language"
	 */
	String key() default "";

}
