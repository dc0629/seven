package top.flagshen.myqq.service.strategy.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.XiaoshenTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;

@Service("修改name")
public class XiuGaiName implements OperationStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private final XiaoshenTemplate xsTemplate;

    public XiuGaiName(XiaoshenTemplate xsTemplate) {
        this.xsTemplate = xsTemplate;
    }

    @Override
    public boolean operation(MyQQMessage message) {
        redisTemplate.opsForValue().set(RedisConstant.NAME, message.getMqMsg());
        //发送群消息
        xsTemplate.sendMsgEx(message.getMqRobot(),
                0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, "修改name成功");
        return true;
    }
}
