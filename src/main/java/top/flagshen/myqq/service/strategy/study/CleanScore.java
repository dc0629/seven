package top.flagshen.myqq.service.strategy.study;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.RobotTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.StudyStrategy;

@Service("清空总分")
public class CleanScore implements StudyStrategy {

    @Autowired
    RedisTemplate<String, Integer> redisTemplate;

    private final RobotTemplate robotTemplate;

    public CleanScore(RobotTemplate robotTemplate) {
        this.robotTemplate = robotTemplate;
    }

    @Override
    public boolean study(MyQQMessage message) {
        redisTemplate.opsForValue().set(RedisConstant.STUDY_SCORE, 0);
        //发送群消息
        robotTemplate.sendMsgEx(message.getMqRobot(), 0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, "当前总分：" + redisTemplate.opsForValue().get(RedisConstant.STUDY_SCORE));
        return true;
    }
}
