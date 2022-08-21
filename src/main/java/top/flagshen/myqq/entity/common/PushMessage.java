package top.flagshen.myqq.entity.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * @author 小棽
 * @date 2021/6/20 19:53
 */
public class PushMessage {

    @JsonProperty("function")
    private String function;
    @JsonProperty("token")
    private String token;
    @JsonProperty("params")
    private Map<String, Object> params;

    public PushMessage() {
    }

    public PushMessage(String function, String token, Map<String, Object> params) {
        this.function = function;
        this.token = token;
        this.params = params;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

}
