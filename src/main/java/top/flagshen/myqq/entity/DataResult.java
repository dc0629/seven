package top.flagshen.myqq.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 小棽
 * @date 2021/6/20 19:47
 */
public class DataResult {
    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("data")
    private DataDTO data;
    public DataResult() {
    }
    public DataResult(Boolean success, Integer code, String msg) {
        this.success = success;
        this.code = code;
        this.msg = msg;
    }

    public DataResult(Boolean success, Integer code, String msg, DataDTO data) {
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        @JsonProperty("ret")
        private HashMap<String,Object> ret;

        public HashMap<String,Object> getRet() {
            return ret;
        }

        public void setRet(HashMap<String,Object> ret) {
            this.ret = ret;
        }
    }
}
