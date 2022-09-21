package top.flagshen.myqq.service.strategy.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.cache.RedisConstant;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("发公告")
public class FaGongGao implements OperationStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private static final List<String> manageGroup = Arrays.asList("777329976","746814450","423430656");

    @Override
    public boolean operation(MyQQMessage message) {
        redisTemplate.opsForValue().set(RedisConstant.URL, message.getMqMsg());
        //发送群消息
        Map<String, Object> map = new HashMap<>();
        map.put("c1", message.getMqRobot());
        map.put("c4", message.getMqMsg());
        for (String groupQQ: manageGroup) {
            //发公告
            map.put("c2", groupQQ);
            //robotTemplate.tongyongPost("Api_PBGroupNotic", map);
        }
        return true;
    }
}
