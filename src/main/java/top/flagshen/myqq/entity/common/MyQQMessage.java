package top.flagshen.myqq.entity.common;

import lombok.Data;
import top.flagshen.myqq.common.FieldInterface;

/**
 * @author dengchao
 */
@Data
public class MyQQMessage {
    /**
     * 收到消息的机器人
     */
    private String mqRobot;
    /**
     * 消息来源群号
     */
    @FieldInterface
    private String mqFromid;
    /**
     * 消息来源qq
     */
    private String mqFromqq;
    /**
     * 消息内容
     */
    private String mqMsg;


}
