package top.flagshen.myqq.service.strategy.study;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.XiaoshenTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.StudyStrategy;

@Service("查看总分")
public class QueryScore implements StudyStrategy {

    @Autowired
    RedisTemplate<String, Integer> redisTemplate;

    private final XiaoshenTemplate xsTemplate;

    public QueryScore(XiaoshenTemplate xsTemplate) {
        this.xsTemplate = xsTemplate;
    }

    @Override
    public boolean study(MyQQMessage message) {
        //发送群消息
        xsTemplate.sendMsgEx(message.getMqRobot(), 0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, "当前总分：" + redisTemplate.opsForValue().get(RedisConstant.STUDY_SCORE));
        return true;
    }
}
