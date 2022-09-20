package top.flagshen.myqq.service.strategy.study;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.StudyStrategy;

@Service("我喝水啦")
public class HaveDrinkWater implements StudyStrategy {

    @Autowired
    private ScoreUtil scoreUtil;

    @Override
    public boolean study(MyQQMessage message) {
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), scoreUtil.scoreCalculation(5));
        return true;
    }
}
