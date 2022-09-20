package top.flagshen.myqq.service.strategy.operation;

import org.springframework.stereotype.Service;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;

@Service("在吗")
public class ZaiMa implements OperationStrategy {

    @Override
    public boolean operation(MyQQMessage message) {
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), "我在");
        return true;
    }
}
