package top.flagshen.myqq.service.strategy.play;

import catcode.CatCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.flagshen.myqq.common.Permissions;
import top.flagshen.myqq.common.cache.RedisConstant;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.PlayStrategy;

import java.util.Set;

@Service("开启决斗")
public class KaiQiJueDou implements PlayStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    @Permissions(groupNums = "423430656,954804208,903959441")
    public boolean play(MyQQMessage message) {
        String qq = message.getMqFromqq();

        CatCodeUtil util = CatCodeUtil.INSTANCE;
        String at = util.toCat("at", "code="+qq);

        Set set = redisTemplate.opsForSet().members(RedisConstant.JUEDOU_OPEN_SET);
        if (!CollectionUtils.isEmpty(set) && set.contains(qq)) {
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(),
                    at +" 已经开过啦，不要重复开啦");
            return true;
        }

        // 添加进开启名单
        redisTemplate.opsForSet().add(RedisConstant.JUEDOU_OPEN_SET, qq);
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(),
                at +" 开启决斗成功，能够收到决斗邀请啦，可以发送「/关闭决斗」不再接受挑战");
        return true;
    }

}
