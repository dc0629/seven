package top.flagshen.myqq.service.strategy.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;

import java.util.Arrays;
import java.util.List;

@Service("发公告")
public class FaGongGao implements OperationStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private static final List<String> manageGroup = Arrays.asList("777329976","746814450","423430656");

    @Override
    public boolean operation(MyQQMessage message) {
        for (String groupQQ: manageGroup) {
            //发公告
            message.getSender().SENDER.sendGroupNotice(groupQQ, null, message.getMqMsg(), false, false, false, false);
        }
        return true;
    }
}
