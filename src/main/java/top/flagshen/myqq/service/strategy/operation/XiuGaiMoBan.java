package top.flagshen.myqq.service.strategy.operation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.common.RobotTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;

@Service("修改模板")
public class XiuGaiMoBan implements OperationStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RobotTemplate robotTemplate;

    @Override
    public boolean operation(MyQQMessage message) {
        if (StringUtils.isBlank(message.getMqMsg())) {
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), "模板不能为空");
        }
        redisTemplate.opsForValue().set(RedisConstant.TEMPLATE, message.getMqMsg());
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), "修改模板成功");
        return true;
    }
}
