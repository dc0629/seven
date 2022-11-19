package top.flagshen.myqq.service.strategy.play;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.Permissions;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.PlayStrategy;

@Service("禁言")
public class JinYan implements PlayStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    @Permissions(groupNums = "423430656")
    public boolean play(MyQQMessage message) {
        /*String mqMsg = message.getMqMsg();
        if (StringUtils.isBlank(mqMsg)) {
            return true;
        }
        try {
            long time = Long.valueOf(Double.valueOf(mqMsg).intValue());
            if (time > 4320) {
                time = 4320;
            }
            if (time <= 0) {
                return true;
            }
            message.getSender().SETTER.setGroupBan(message.getMqFromid(), message.getMqFromqq(), time * 60);
        } catch (Exception e) {
        }*/

        return true;
    }

}
