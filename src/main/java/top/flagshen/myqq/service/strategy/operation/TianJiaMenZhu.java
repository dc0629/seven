package top.flagshen.myqq.service.strategy.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.RobotTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service("添加盟主")
public class TianJiaMenZhu implements OperationStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplateSet;

    private final RobotTemplate robotTemplate;

    public TianJiaMenZhu(RobotTemplate robotTemplate) {
        this.robotTemplate = robotTemplate;
    }

    private final Set<String> MENGZHU_SET = new HashSet<>(Arrays.asList("xxx"));

    @Override
    public boolean operation(MyQQMessage message) {
        Set set = redisTemplateSet.opsForSet().members(RedisConstant.MENGZHU);
        if (CollectionUtils.isEmpty(set)) {
            redisTemplateSet.opsForSet().add(RedisConstant.MENGZHU, MENGZHU_SET.stream().toArray(String[]::new));
        } else {
            redisTemplateSet.opsForSet().add(RedisConstant.MENGZHU, message.getMqMsg());
        }
        //发送群消息
        robotTemplate.sendMsgEx(message.getMqRobot(),
                0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, "添加成功");
        return true;
    }
}
