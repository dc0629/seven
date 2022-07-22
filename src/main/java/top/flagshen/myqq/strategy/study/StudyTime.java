package top.flagshen.myqq.strategy.study;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.XiaoshenTemplate;
import top.flagshen.myqq.entity.MyQQMessage;
import top.flagshen.myqq.strategy.StudyStrategy;

@Service("学习时间")
public class StudyTime implements StudyStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private final XiaoshenTemplate xsTemplate;

    public StudyTime(XiaoshenTemplate xsTemplate) {
        this.xsTemplate = xsTemplate;
    }

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
        xsTemplate.sendMsgEx(message.getMqRobot(), 0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, scoreUtil.scoreCalculation(score));
        return true;
    }
}
