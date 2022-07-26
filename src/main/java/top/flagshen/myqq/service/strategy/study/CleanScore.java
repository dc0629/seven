package top.flagshen.myqq.service.strategy.study;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.cache.RedisConstant;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.StudyStrategy;

@Service("清空总分")
public class CleanScore implements StudyStrategy {

    @Autowired
    RedisTemplate<String, Integer> redisTemplate;

    @Override
    public boolean study(MyQQMessage message) {
        redisTemplate.opsForValue().set(RedisConstant.STUDY_SCORE, 0);
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), "当前总分：" + redisTemplate.opsForValue().get(RedisConstant.STUDY_SCORE));
        return true;
    }
}
