package top.flagshen.myqq.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 检测对应的字段
 */
@Retention(RetentionPolicy.RUNTIME) // 该注解什么时候有效
@Target({ ElementType.FIELD }) //作用与参数上
public @interface FieldInterface {

    //属性字段名称 默认空字符串
    String name() default "";
}