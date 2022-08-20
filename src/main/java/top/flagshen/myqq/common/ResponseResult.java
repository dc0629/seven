package top.flagshen.myqq.common;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 统一格式返回
 *
 * @author denchao
 */
@Accessors(chain = true)
@Data
public class ResponseResult<T>  {

    private boolean success;

    private Integer errCode;

    private String errMsg;

    private T datas;

    private ResponseResult(boolean success, Integer errCode, String errMsg, T datas) {
        this.success = success;
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.datas = datas;
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
