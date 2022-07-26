package top.flagshen.myqq.common.exception;

import org.apache.commons.lang3.StringUtils;

/**
 * 错误码枚举
 */
public enum ErrorCodeEnum {

    PARAM_ILLEGAL("PARAM_ILLEGAL", 1, "请求参数异常!"),
    PASSWORD_ERROR("PASSWORD_ERROR", 2, "密码错误"),
    USER_IS_NULL("USER_IS_NULL", 2, "账号不存在"),
    INVALID_QR_CODE("INVALID_QR_CODE", 3, "无效二维码"),
    UNKNOWN_EXCEPTION("UNKNOWN_EXCEPTION", 9999, "未知异常"),

    ;

    /**
     * 错误码字符串
     */
    private final String errorCode;

    /**
     * 标准错误码
     */
    private final Integer errorSpecCode;

    /**
     * 错误描述
     */
    private final String description;

    /**
     * 构造函数
     *
     * @param errorCode     error code
     * @param errorSpecCode error spec code
     * @param description   description
     */
    ErrorCodeEnum(String errorCode, Integer errorSpecCode, String description) {
        this.errorCode = errorCode;
        this.errorSpecCode = errorSpecCode;
        this.description = description;
    }

    /**
     * 根据错误码获取错误码枚举
     *
     * @param errorCode error code
     * @return by code
     */
    public static ErrorCodeEnum getByCode(String errorCode) {
        for (ErrorCodeEnum tmpErrorCode : ErrorCodeEnum.values()) {
            if (StringUtils.equals(tmpErrorCode.getErrorCode(), errorCode)) {
                return tmpErrorCode;
            }
        }
        return UNKNOWN_EXCEPTION;
    }

    /**
     * Getter method for property <tt>errorCode</tt>.
     *
     * @return property value of errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Getter method for property <tt>errorSpecCode</tt>.
     *
     * @return property value of errorSpecCode
     */
    public Integer getErrorSpecCode() {
        return errorSpecCode;
    }

    /**
     * Getter method for property <tt>description</tt>.
     *
     * @return property value of description
     */
    public String getDescription() {
        return description;
    }
}
