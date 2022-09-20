package top.flagshen.myqq.service.strategy.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;

@Service("修改title")
public class XiuGaiTitle implements OperationStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean operation(MyQQMessage message) {
        redisTemplate.opsForValue().set(RedisConstant.TITLE, message.getMqMsg());
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), "修改title成功");
        return true;
    }
}
