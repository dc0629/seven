package top.flagshen.myqq.common.handler;

import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.MDC;
import top.flagshen.myqq.common.constants.SystemConstants;

/**
 * 统一格式返回
 *
 * @author denchao
 */
@Accessors(chain = true)
@Data
public class ResponseResult<T>  {

    private String traceId;

    private boolean success;

    private Integer errCode;

    private String errMsg;

    private T datas;

    private ResponseResult(boolean success, Integer errCode, String errMsg, T datas) {
        this.success = success;
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.datas = datas;
        this.traceId = MDC.get(SystemConstants.MDC_TRACE_KEY);
    }

    public static <T> ResponseResult<T> success() {
        return success(null);
    }

    public static <T> ResponseResult<T> success(T datas) {
        return new ResponseResult<>(true, 0, "success", datas);
    }

    public static <T> ResponseResult<T> fail(int errCode) {
        return fail(errCode, null);
    }

    public static <T> ResponseResult<T> fail(int errCode, String errMsg) {
        return fail(errCode, errMsg, null);
    }

    public static <T> ResponseResult<T> fail(int errCode, String errMsg, T datas) {
        return new ResponseResult<>(false, errCode, errMsg, datas);
    }


}
