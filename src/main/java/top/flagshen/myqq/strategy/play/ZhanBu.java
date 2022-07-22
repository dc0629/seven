package top.flagshen.myqq.strategy.play;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.XiaoshenTemplate;
import top.flagshen.myqq.entity.MyQQMessage;
import top.flagshen.myqq.strategy.PlayStrategy;
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
    public boolean play(MyQQMessage message) {
        String key = RedisConstant.DIVINATION + message.getMqFromqq();
        if (redisTemplate.hasKey(key)) {
            return true;
        }
        int yun = (int) (Math.random()*100 + 1);
        if (yun < 10) {
            yun = (int)(Math.random()*51 + 10);
        }
        //发送群消息
        xsTemplate.sendMsgEx(message.getMqRobot(), 0, TypeConstant.MSGTYPE_GROUP, message.getMqFromid(), null, ContentUtil.zhanbu(yun, message.getMqFromqq()));
        redisTemplateInt.opsForValue().set(key, yun, 24, TimeUnit.HOURS);
        return true;
    }
}
