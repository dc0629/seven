package top.flagshen.myqq.service.strategy.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;

@Service("查看模板")
public class ChaKanMoBan implements OperationStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean operation(MyQQMessage message) {
        String s = redisTemplate.opsForValue().get(RedisConstant.TEMPLATE);
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), s);
        return true;
    }
}
