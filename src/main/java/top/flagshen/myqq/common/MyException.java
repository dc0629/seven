package top.flagshen.myqq.common;


import lombok.ToString;

/**
 * 统一业务异常基类
 *
 * @author dengchao
 */
@ToString
public class MyException extends RuntimeException {

    private final Integer errCode;

    private final String errMsg;

    public MyException(ErrorCodeEnum expoErrors) {
        this(expoErrors.getErrorSpecCode(), expoErrors.getDescription());
    }

    public MyException(Integer errCode) {
        this(errCode, errCode.toString());
    }

    public MyException(Integer errCode, String errMsg) {
        this(errCode, errMsg, null);
    }

    public MyException(Integer errCode, Throwable cause) {
        this(errCode, errCode.toString(), cause);
    }

    public MyException(Integer errCode, String errMsg, Throwable cause) {
        super(errMsg, cause);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public Integer getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

}
