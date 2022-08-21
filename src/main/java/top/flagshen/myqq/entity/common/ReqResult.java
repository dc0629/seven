package top.flagshen.myqq.entity.common;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author 小棽
 * @date 2021/6/20 21:33
 */
public class ReqResult {

    @JsonProperty("status")
    private Integer status;
    public ReqResult(){}
    public ReqResult(Integer status){
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
