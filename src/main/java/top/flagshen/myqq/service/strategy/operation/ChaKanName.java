package top.flagshen.myqq.service.strategy.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.cache.RedisConstant;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;

@Service("查看name")
public class ChaKanName implements OperationStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean operation(MyQQMessage message) {
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), redisTemplate.opsForValue().get(RedisConstant.NAME));
        return true;
    }
}
