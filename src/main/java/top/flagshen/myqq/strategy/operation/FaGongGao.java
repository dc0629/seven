package top.flagshen.myqq.strategy.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.common.XiaoshenTemplate;
import top.flagshen.myqq.entity.MyQQMessage;
import top.flagshen.myqq.strategy.OperationStrategy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("发公告")
public class FaGongGao implements OperationStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private final XiaoshenTemplate xsTemplate;

    public FaGongGao(XiaoshenTemplate xsTemplate) {
        this.xsTemplate = xsTemplate;
    }

    private static final List<String> manageGroup = Arrays.asList("427654886","777329976","165832396","885454847","423430656");

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
            xsTemplate.tongyongPost("Api_PBGroupNotic", map);
        }
        return true;
    }
}
