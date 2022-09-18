package top.flagshen.myqq.service.strategy.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.RobotTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;
import top.flagshen.myqq.util.ContentUtil;

@Service("查看推迟效果")
public class ChaKanTuiChiXiaoGuo implements OperationStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private final RobotTemplate robotTemplate;

    public ChaKanTuiChiXiaoGuo(RobotTemplate robotTemplate) {
        this.robotTemplate = robotTemplate;
    }

    @Override
    public boolean operation(MyQQMessage message) {
        String template = redisTemplate.opsForValue().get(RedisConstant.LATER_TEMPLATE);
        if (StringUtils.isEmpty(template)) {
            robotTemplate.sendMsgEx(message.getMqRobot(), 0, TypeConstant.MSGTYPE_GROUP,
                    message.getMqFromid(), null, "没设置推迟模板");
            return true;
        }
        //发送群消息
        robotTemplate.sendMsgEx(message.getMqRobot(), 0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, ContentUtil.getContent(template,
                        redisTemplate.opsForValue().get(RedisConstant.NAME),
                        redisTemplate.opsForValue().get(RedisConstant.TITLE),
                        redisTemplate.opsForValue().get(RedisConstant.URL),
                        redisTemplate.opsForValue().get(RedisConstant.NUMBER)));
        return true;
    }
}
