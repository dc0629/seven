package top.flagshen.myqq.strategy.study;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.XiaoshenTemplate;
import top.flagshen.myqq.entity.MyQQMessage;
import top.flagshen.myqq.strategy.StudyStrategy;

@Service("清空总分")
public class CleanScore implements StudyStrategy {

    @Autowired
    RedisTemplate<String, Integer> redisTemplate;

    private final XiaoshenTemplate xsTemplate;

    public CleanScore(XiaoshenTemplate xsTemplate) {
        this.xsTemplate = xsTemplate;
    }

    @Override
    public boolean study(MyQQMessage message) {
        redisTemplate.opsForValue().set(RedisConstant.STUDY_SCORE, 0);
        //发送群消息
        xsTemplate.sendMsgEx(message.getMqRobot(), 0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, "当前总分：" + redisTemplate.opsForValue().get(RedisConstant.STUDY_SCORE));
        return true;
    }
}
