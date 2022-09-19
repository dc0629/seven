package top.flagshen.myqq.service.strategy.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.RobotTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;

@Service("修改number")
public class XiuGaiNumber implements OperationStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RobotTemplate robotTemplate;

    @Override
    public boolean operation(MyQQMessage message) {
        redisTemplate.opsForValue().set(RedisConstant.NUMBER, message.getMqMsg());
        //发送群消息
        robotTemplate.sendMsgEx(message.getMqRobot(), message.getMqFromid(), "修改number成功");
        return true;
    }
}
