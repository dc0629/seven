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
        if (redisTemplate.hasKey(key)) {
            return true;
        }
        int yun = (int) (Math.random()*100);
        if (yun < 30) {
            // 30%的概率幸运值是10-39
            yun = (int)(Math.random()*30 + 10);
        } else if  (yun < 90) {
            // 60%的概率幸运值是40-79
            yun = (int)(Math.random()*40 + 40);
        } else {
            // 10%的概率幸运值是80-100
            yun = (int)(Math.random()*21 + 80);
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
        redisTemplate.opsForValue().set(RedisConstant.DIVINATIONSTR + qq, zhanbu,
                "481024974".equals(message.getMqFromid()) ? 60000 : DateUtil.getMidnightMillis(),
                TimeUnit.MILLISECONDS);
        return true;
    }
}
