package top.flagshen.myqq.service.strategy.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.RobotTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;

@Service("查看模板")
public class ChaKanMoBan implements OperationStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private final RobotTemplate robotTemplate;

    public ChaKanMoBan(RobotTemplate robotTemplate) {
        this.robotTemplate = robotTemplate;
    }

    @Override
    public boolean operation(MyQQMessage message) {
        String s = redisTemplate.opsForValue().get(RedisConstant.TEMPLATE);
        //发送群消息
        robotTemplate.sendMsgEx(message.getMqRobot(), 0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, s);
        return true;
    }
}
