package top.flagshen.myqq.common.exception;

import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public final class ExceptionAssertUtil {

	private ExceptionAssertUtil() {
	}

	/**
	 * 断言不为true时抛出对应异常
	 *
	 */
	public static void isTrue(boolean expValue, ErrorCodeEnum resultCode) {
		if (!expValue) {
			throw new MyException(resultCode);
		}
	}

	/**
	 * 断言不为false时抛异常
	 *
	 * @param expValue   the exp value
	 * @param resultCode the result code
	 */
	public static void isFalse(boolean expValue, ErrorCodeEnum resultCode) {
		isTrue(!expValue, resultCode);
	}

	/**
	 * 不为空，否则报异常
	 *
	 */
	public static void notBlank(String str, ErrorCodeEnum resultCode) {
		isTrue(StringUtils.isNotBlank(str), resultCode);
	}

	/**
	 * 不为null，否则报异常
	 */
	public static void notNull(Object object, ErrorCodeEnum resultCode) {
		isTrue(object != null, resultCode);
	}

}
