package top.flagshen.myqq.service.strategy.play;

import catcode.CatCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.Permissions;
import top.flagshen.myqq.common.cache.RedisConstant;
import top.flagshen.myqq.common.constants.SystemConstants;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.PlayStrategy;
import top.flagshen.myqq.util.ContentUtil;
import top.flagshen.myqq.util.DateUtil;

import java.util.concurrent.TimeUnit;

@Service("占卜")
public class ZhanBu implements PlayStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisTemplate<String, Integer> redisTemplateInt;

    @Override
    @Permissions(groupNums = "423430656,954804208,481024974")
    public boolean play(MyQQMessage message) {
        String qq = message.getMqFromqq();
        if ("481024974".equals(message.getMqFromid())) {
            qq += SystemConstants.TEST;
        }
        String key = RedisConstant.DIVINATION + qq;
        String strKey = RedisConstant.DIVINATIONSTR + qq;
        int yun = 0;
        // 占卜过了结束
        if (redisTemplate.hasKey(strKey)) {
            return true;
        }
        // 已有运气就用已有的，没有就用新的
        if (redisTemplate.hasKey(key)) {
            yun = redisTemplateInt.opsForValue().get(key);
        } else {
            yun = ContentUtil.getYun();
        }
        // 构建at
        CatCodeUtil util = CatCodeUtil.INSTANCE;
        String at = util.toCat("at", "code="+message.getMqFromqq());
        String zhanbu = ContentUtil.zhanbu(yun);
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), at + "\r\n" + zhanbu);
        // 午夜过期,如果测试群就是5分钟过期
        redisTemplateInt.opsForValue().set(key, yun,
                "481024974".equals(message.getMqFromid()) ? 60000 : DateUtil.getMidnightMillis(),
                TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set(strKey, zhanbu,
                "481024974".equals(message.getMqFromid()) ? 60000 : DateUtil.getMidnightMillis(),
                TimeUnit.MILLISECONDS);
        return true;
    }
}
