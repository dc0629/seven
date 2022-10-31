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

@Service("关闭决斗")
public class GuanBiJueDou implements PlayStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    @Permissions(groupNums = "423430656,954804208,903959441")
    public boolean play(MyQQMessage message) {
        String qq = message.getMqFromqq();
        CatCodeUtil util = CatCodeUtil.INSTANCE;
        // 构建at
        String at = util.toCat("at", "code="+qq);
        Set set = redisTemplate.opsForSet().members(RedisConstant.JUEDOU_OPEN_SET);
        if (!CollectionUtils.isEmpty(set) && !set.contains(qq)) {
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(),
                    at +" 还没开启呢，不要再关闭啦");
            return true;
        }
        // 从开启名单中移除
        redisTemplate.opsForSet().remove(RedisConstant.JUEDOU_OPEN_SET, qq);

        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(),
                at +" 关闭决斗成功，决斗邀请会被拒绝啦，可以发送「/开启决斗」重新接受挑战");
        return true;
    }

}
