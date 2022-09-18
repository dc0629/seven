package top.flagshen.myqq.service.strategy.play;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.Permissions;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.XiaoshenTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.PlayStrategy;
import top.flagshen.myqq.util.ContentUtil;

import java.util.concurrent.TimeUnit;

@Service("占卜")
public class ZhanBu implements PlayStrategy {

    private final XiaoshenTemplate xsTemplate;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisTemplate<String, Integer> redisTemplateInt;

    public ZhanBu(XiaoshenTemplate xsTemplate) {
        this.xsTemplate = xsTemplate;
    }

    @Override
    @Permissions(groupNums = "423430656,954804208")
    public boolean play(MyQQMessage message) {
        String key = RedisConstant.DIVINATION + message.getMqFromqq();
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
        //发送群消息
        xsTemplate.sendMsgEx(message.getMqRobot(), 0, TypeConstant.MSGTYPE_GROUP, message.getMqFromid(), null, ContentUtil.zhanbu(yun, message.getMqFromqq()));
        redisTemplateInt.opsForValue().set(key, yun, 24, TimeUnit.HOURS);
        return true;
    }
}
