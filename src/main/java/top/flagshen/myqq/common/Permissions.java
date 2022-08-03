package top.flagshen.myqq.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可访问的群权限注解
 * @author dc
 * @Date 2021/1/15 16:55
 */

@Retention(RetentionPolicy.RUNTIME) // 该注解什么时候有效
@Target({ ElementType.METHOD }) // 对方法有效
public @interface Permissions {

	/**
	 * 能够访问的群,用逗号分隔
	 * 
	 * @return
	 */
	String groupNums() default "";

}
