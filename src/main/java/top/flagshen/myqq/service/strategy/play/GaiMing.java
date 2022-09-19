package top.flagshen.myqq.service.strategy.play;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.flagshen.myqq.common.Permissions;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.common.RobotTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.PlayStrategy;
import top.flagshen.myqq.util.ContentUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service("改命")
public class GaiMing implements PlayStrategy {

    @Autowired
    private RobotTemplate robotTemplate;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisTemplate<String, Integer> redisTemplateInt;

    private final Set<String> MENGZHU_SET = new HashSet<>(Arrays.asList("xxx"));

    @Override
    @Permissions(groupNums = "423430656")
    public boolean play(MyQQMessage message) {
        Set set = redisTemplate.opsForSet().members(RedisConstant.MENGZHU);
        if (CollectionUtils.isEmpty(set)) {
            redisTemplate.opsForSet().add(RedisConstant.MENGZHU, MENGZHU_SET.stream().toArray(String[]::new));
            if (!MENGZHU_SET.contains(message.getMqFromqq())) {
                return true;
            }
        } else if (!set.contains(message.getMqFromqq())) {
            return true;
        }
        String oldKey = RedisConstant.DIVINATION + message.getMqFromqq();
        String key = RedisConstant.GAIMING + message.getMqFromqq();
        if (redisTemplate.hasKey(key) || !redisTemplate.hasKey(oldKey)) {
            return true;
        }
        int oldYun = redisTemplateInt.opsForValue().get(oldKey);
        int yun = 0;
        if (oldYun == 100) {
            yun = 100;
        } else {
            yun = (int) (Math.random()*(100-oldYun) + oldYun + 1);
        }
        //发送群消息
        robotTemplate.sendMsgEx(message.getMqRobot(), message.getMqFromid(), ContentUtil.zhanbu(yun, message.getMqFromqq()));

        redisTemplateInt.opsForValue().set(key, yun,
                24, TimeUnit.HOURS);
        return true;
    }
}
