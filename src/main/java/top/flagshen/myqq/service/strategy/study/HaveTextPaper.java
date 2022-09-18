package top.flagshen.myqq.service.strategy.study;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.RobotTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.StudyStrategy;

@Service("我周末做试卷啦")
public class HaveTextPaper implements StudyStrategy {

    private final RobotTemplate robotTemplate;

    public HaveTextPaper(RobotTemplate robotTemplate) {
        this.robotTemplate = robotTemplate;
    }

    @Autowired
    private ScoreUtil scoreUtil;

    @Override
    public boolean study(MyQQMessage message) {
        //发送群消息
        robotTemplate.sendMsgEx(message.getMqRobot(), 0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, scoreUtil.scoreCalculation(10));
        return true;
    }
}
