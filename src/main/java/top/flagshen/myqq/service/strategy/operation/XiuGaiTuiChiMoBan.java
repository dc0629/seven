package top.flagshen.myqq.service.strategy.operation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.cache.RedisConstant;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;

@Service("修改推迟模板")
public class XiuGaiTuiChiMoBan implements OperationStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean operation(MyQQMessage message) {
        if (StringUtils.isBlank(message.getMqMsg())) {
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), "推迟模板不能为空");
        }
        redisTemplate.opsForValue().set(RedisConstant.LATER_TEMPLATE, message.getMqMsg());
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), "修改推迟模板成功");
        return true;
    }
}
