package top.flagshen.myqq.service.strategy.study;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.RobotTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.StudyStrategy;

@Service("我运动啦")
public class HaveSports implements StudyStrategy {

    @Autowired
    private ScoreUtil scoreUtil;

    @Override
    public boolean study(MyQQMessage message) {
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), scoreUtil.scoreCalculation(10));
        return true;
    }
}
