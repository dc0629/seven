package top.flagshen.myqq.strategy.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.XiaoshenTemplate;
import top.flagshen.myqq.entity.MyQQMessage;
import top.flagshen.myqq.entity.ReqResult;
import top.flagshen.myqq.strategy.OperationStrategy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service("添加盟主")
public class TianJiaMenZhu implements OperationStrategy {

    @Autowired
    RedisTemplate<String, Set> redisTemplateSet;

    private final XiaoshenTemplate xsTemplate;

    public TianJiaMenZhu(XiaoshenTemplate xsTemplate) {
        this.xsTemplate = xsTemplate;
    }

    private final Set<String> MENGZHU_SET = new HashSet<>(Arrays.asList("xxx"));

    @Override
    public boolean operation(MyQQMessage message) {
        Set set = redisTemplateSet.opsForValue().get(RedisConstant.MENGZHU);
        if (CollectionUtils.isEmpty(set)) {
            HashSet<String> strings = new HashSet<>(MENGZHU_SET);
            strings.add(message.getMqMsg());
            redisTemplateSet.opsForValue().set(RedisConstant.MENGZHU, strings);
        } else {
            Set<String> strings = redisTemplateSet.opsForValue().get(RedisConstant.MENGZHU);
            strings.add(message.getMqMsg());
            redisTemplateSet.opsForValue().set(RedisConstant.MENGZHU, strings);
        }
        //发送群消息
        xsTemplate.sendMsgEx(message.getMqRobot(),
                0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, "添加成功");
        return true;
    }
}
