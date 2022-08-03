package top.flagshen.myqq.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 可访问的群权限注解
 *
 * @author dc
 * @Date 2021/1/15 16:57
 */
@Component
@Aspect
@Slf4j
public class PermissionsAnnotationService {

	@Around("@annotation(permissions)")
	public Object around(ProceedingJoinPoint joinPoint, Permissions permissions) {
		try {
			String groupNumsStr = permissions.groupNums();
			if (StringUtils.isBlank(groupNumsStr)) {
				return joinPoint.proceed();
			}
			// 能够访问的群列表
			List<String> groupNums = Arrays.asList(groupNumsStr.split(","));
			Class<?> requestClass = joinPoint.getArgs()[0].getClass();
			Field[] declaredFields = requestClass.getDeclaredFields();
			for (Field declaredField : declaredFields) {
				// 判断是否方法上存在注解  FieldInterface
				boolean annotationPresent = declaredField.isAnnotationPresent(FieldInterface.class);
				if (annotationPresent) {
					// 获取自定义注解对象
					FieldInterface methodAnno = declaredField.getAnnotation(FieldInterface.class);
					// 根据对象获取注解值
					String isNotNullStr = methodAnno.name();
					// 设置可以访问私有变量
					declaredField.setAccessible(true);
					if ("class java.lang.String".equals(declaredField.getGenericType().toString())) {
						// 将属性的首字母大写
						String name = declaredField.getName();
						name = name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase());
						Method m = requestClass.getMethod("get" + name);
						// 获取参数值
						String groupNum = (String) m.invoke(joinPoint.getArgs()[0]);
						// 如果能访问的群里，不包括这个群，就直接结束
						if (!groupNums.contains(groupNum)) {
							return false;
						}
					}
				}
			}
			return joinPoint.proceed();
		} catch (Throwable e) {
			log.info("权限控制注解出问题了，快看一下" + e.getMessage());
		}
		return false;
	}

}
