package top.flagshen.myqq.service.strategy.operation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.common.RobotTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;
import top.flagshen.myqq.util.ContentUtil;

@Service("查看请假效果")
public class ChaKanQinJiaXiaoGuo implements OperationStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean operation(MyQQMessage message) {
        String template = redisTemplate.opsForValue().get(RedisConstant.VACATION_TEMPLATE);
        if (StringUtils.isEmpty(template)) {
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), "没设置请假模板");
            return true;
        }
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), ContentUtil.getContent(template,
                        redisTemplate.opsForValue().get(RedisConstant.NAME),
                        redisTemplate.opsForValue().get(RedisConstant.TITLE),
                        redisTemplate.opsForValue().get(RedisConstant.URL),
                        redisTemplate.opsForValue().get(RedisConstant.NUMBER)));
        return true;
    }
}
