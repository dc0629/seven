package top.flagshen.myqq.service.strategy.play;

import catcode.CatCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.Permissions;
import top.flagshen.myqq.common.cache.RedisConstant;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.PlayStrategy;

@Service("拒绝")
public class JuJue implements PlayStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    @Permissions(groupNums = "423430656,954804208,903959441")
    public boolean play(MyQQMessage message) {
        String group = message.getMqFromid();
        // 接受人
        String jieshouQQ = message.getMqFromqq();
        String jieshouKey = RedisConstant.JIESHOU + group + ":" + jieshouQQ;
        // 如果没有被发起挑战，拒绝就没反应
        if(!redisTemplate.hasKey(jieshouKey)) {
            return true;
        }
        // 发起人
        String juedouQQ = redisTemplate.opsForValue().get(jieshouKey);
        // 删除key
        String juedouKey = RedisConstant.JUEDOU + group + ":" + juedouQQ;
        redisTemplate.delete(jieshouKey);
        redisTemplate.delete(juedouKey);


        CatCodeUtil util = CatCodeUtil.INSTANCE;
        String at = util.toCat("at", "code="+juedouQQ);
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(),
                at + " 你发起的决斗已被对方拒绝，请重新选人挑战吧");
        return true;
    }

}
