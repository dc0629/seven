package top.flagshen.myqq.service.strategy.operation;

import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.XiaoshenTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;

@Service("在吗")
public class ZaiMa implements OperationStrategy {

    private final XiaoshenTemplate xsTemplate;

    public ZaiMa(XiaoshenTemplate xsTemplate) {
        this.xsTemplate = xsTemplate;
    }

    @Override
    public boolean operation(MyQQMessage message) {
        //发送群消息
        xsTemplate.sendMsgEx(message.getMqRobot(),
                0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, "我在");
        return true;
    }
}
