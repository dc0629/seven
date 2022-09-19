package top.flagshen.myqq.service.strategy.study;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.RobotTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.StudyStrategy;

@Service("学习时间")
public class StudyTime implements StudyStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RobotTemplate robotTemplate;

    @Autowired
    private ScoreUtil scoreUtil;

    @Override
    public boolean study(MyQQMessage message) {
        String mqMsg = message.getMqMsg();
        if (StringUtils.isBlank(mqMsg)) {
            return true;
        }
        int score = 0;
        int time = Integer.parseInt(mqMsg);
        if (time < 2) {
            score = -10;
        } else if (time < 4) {
            score = -5;
        } else if (time < 6) {
            score = 5;
        } else {
            score = 10;
        }
        //发送群消息
        robotTemplate.sendMsgEx(message.getMqRobot(), message.getMqFromid(), scoreUtil.scoreCalculation(score));
        return true;
    }
}
