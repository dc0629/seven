package top.flagshen.myqq.service.strategy.play;

import catcode.CatCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.Permissions;
import top.flagshen.myqq.common.cache.RedisConstant;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.PlayStrategy;

@Service("取消决斗")
public class QuXiao implements PlayStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    @Permissions(groupNums = "423430656,954804208,903959441")
    public boolean play(MyQQMessage message) {
        String group = message.getMqFromid();
        // 发起人
        String juedouQQ = message.getMqFromqq();
        String juedouKey = RedisConstant.JUEDOU + group + ":" + juedouQQ;
        // 如果没有发起挑战，取消就没反应
        if(!redisTemplate.hasKey(juedouKey)) {
            return true;
        }
        // 被挑战人
        String jieshouQQ = redisTemplate.opsForValue().get(juedouKey);
        // 删除key
        String jieshouKey = RedisConstant.JIESHOU + group + ":" + jieshouQQ;
        redisTemplate.delete(jieshouKey);
        redisTemplate.delete(juedouKey);


        CatCodeUtil util = CatCodeUtil.INSTANCE;
        String at = util.toCat("at", "code="+juedouQQ);
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(),
                at + " 取消成功");
        return true;
    }

}
