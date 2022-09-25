package top.flagshen.myqq.service.strategy.play;

import catcode.CatCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.flagshen.myqq.common.Permissions;
import top.flagshen.myqq.common.cache.RedisConstant;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.PlayStrategy;
import top.flagshen.myqq.util.ContentUtil;
import top.flagshen.myqq.util.DateUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service("改命")
public class GaiMing implements PlayStrategy {

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
        CatCodeUtil util = CatCodeUtil.INSTANCE;
        // 构建at
        String at = util.toCat("at", "code="+message.getMqFromqq());
        String zhanbu = ContentUtil.zhanbu(yun);
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), at + "\r\n" + zhanbu);
        // 午夜12点过期
        redisTemplateInt.opsForValue().set(key, yun, DateUtil.getMidnightMillis(), TimeUnit.MILLISECONDS);
        redisTemplateInt.opsForValue().set(oldKey, yun, DateUtil.getMidnightMillis(), TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set(RedisConstant.DIVINATIONSTR + message.getMqFromqq(), zhanbu, DateUtil.getMidnightMillis(), TimeUnit.MILLISECONDS);
        return true;
    }
}
