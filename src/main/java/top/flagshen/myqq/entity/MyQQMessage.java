package top.flagshen.myqq.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author 小棽
 * @date 2021/6/20 19:43
 */
@Data
public class MyQQMessage {
    /**
     * 收到消息的机器人
     */
    @JsonProperty("MQ_robot")
    private String mqRobot;
    /**
     * 收到消息的类型
     */
    @JsonProperty("MQ_type")
    private Integer mqType;
    @JsonProperty("MQ_type_sub")
    private Integer mqTypeSub;
    /**
     * 消息来源群号
     */
    @JsonProperty("MQ_fromID")
    private String mqFromid;
    /**
     * 消息来源qq
     */
    @JsonProperty("MQ_fromQQ")
    private String mqFromqq;
    @JsonProperty("MQ_passiveQQ")
    private String mqPassiveqq;
    /**
     * 消息内容
     */
    @JsonProperty("MQ_msg")
    private String mqMsg;
    @JsonProperty("MQ_msgSeq")
    private String mqMsgseq;
    @JsonProperty("MQ_msgID")
    private String mqMsgid;
    @JsonProperty("MQ_msgData")
    private String mqMsgdata;
}
