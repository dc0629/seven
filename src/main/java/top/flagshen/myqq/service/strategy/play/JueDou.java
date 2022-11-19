package top.flagshen.myqq.service.strategy.play;

import catcode.CatCodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.flagshen.myqq.common.Permissions;
import top.flagshen.myqq.common.cache.RedisConstant;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.PlayStrategy;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service("决斗")
public class JueDou implements PlayStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    @Permissions(groupNums = "423430656,954804208,903959441")
    public boolean play(MyQQMessage message) {
        String group = message.getMqFromid();
        String qq = message.getMqFromqq();
        String msg = message.getMessageContent().getMsg();
        String targetQQ = msg.substring(msg.lastIndexOf("CAT:at,code=") + 12, msg.lastIndexOf("]"));
        String jieshouKey = RedisConstant.JIESHOU + group + ":" + targetQQ;
        String juedouJinYanKey = RedisConstant.JUEDOU_JINYAN +  group + ":" + targetQQ;
        String juedouKey = RedisConstant.JUEDOU + group + ":" + qq;
        CatCodeUtil util = CatCodeUtil.INSTANCE;
        String at = util.toCat("at", "code="+targetQQ);
        String at1 = util.toCat("at", "code="+qq);
        // 发起决斗的人默认开启功能
        redisTemplate.opsForSet().add(RedisConstant.JUEDOU_OPEN_SET, qq);
        // 如果此人发起决斗中，或者被挑战人正在被挑战，禁言中，就没反应
        if(StringUtils.isBlank(targetQQ)) {
            return true;
        }
        if ("1462152250".equals(targetQQ)) {
            int jinyantime = getJinyanTime(group, qq);
            redisTemplate.opsForValue().set(RedisConstant.JUEDOU_JINYAN +  group + ":" + qq, String.valueOf(jinyantime*60), jinyantime, TimeUnit.MINUTES);
            message.getSender().SETTER.setGroupBan(group, qq, Long.valueOf(jinyantime) * 60);
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(),
                    at1 +" 怎么回事呢, 怎么可以挑战小柒呢，小柒要生气啦");
            return true;
        }
        if (qq.equals(targetQQ)) {
            int jinyantime = getJinyanTime(group, qq);
            redisTemplate.opsForValue().set(RedisConstant.JUEDOU_JINYAN +  group + ":" + qq, String.valueOf(jinyantime*60), jinyantime, TimeUnit.MINUTES);
            message.getSender().SETTER.setGroupBan(group, qq, Long.valueOf(jinyantime) * 60);
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(),
                    at1 +" 居然和自己决斗，一定是脑子坏掉了，先休息一会吧");
            return true;
        }
        if (redisTemplate.hasKey(RedisConstant.JUEDOU_CD + group + ":" + qq)) {
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(),
                    at1 +" 你已经在决斗中受伤住院，请不要逞强啦，明天再战吧");
            return true;
        }
        if (redisTemplate.hasKey(RedisConstant.JUEDOU_CD + group + ":" + targetQQ)) {
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(),
                    at1 +" 对方在决斗中受伤住院了，需要明天才能出院哦");
            return true;
        }
        if (redisTemplate.hasKey(RedisConstant.YILIAO_CD + group + ":" + qq)) {
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(),
                    at1 +" 你正在使用修复液进行恢复中");
            return true;
        }
        if (redisTemplate.hasKey(RedisConstant.YILIAO_CD + group + ":" + targetQQ)) {
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(),
                    at1 +" 对方正在使用修复液进行恢复中");
            return true;
        }
        if (redisTemplate.hasKey(juedouJinYanKey)) {
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(),
                    at1 +" 对方正在坐牢(禁言中)，请稍后再来");
            return true;
        }
        if (redisTemplate.hasKey(juedouKey)) {
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(),
                    at1 +" 你有一场决斗等待敌人接受，请稍后再来");
            return true;
        }
        if (redisTemplate.hasKey(RedisConstant.JIESHOU + group + ":" + qq)) {
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(),
                    at1 +" 有人还在等你接受决斗呢，请发送「/接受」完成决斗");
            return true;
        }
        if (redisTemplate.hasKey(jieshouKey) || redisTemplate.hasKey(RedisConstant.JUEDOU + group + ":" + targetQQ) ) {
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(),
                    at1 +" 对方正在决斗中，请勿打扰");
            return true;
        }
        Set set = redisTemplate.opsForSet().members(RedisConstant.JUEDOU_OPEN_SET);
        if (!CollectionUtils.isEmpty(set) && !set.contains(targetQQ)) {
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(),
                    at1 +" 你挑战的人还没有开启决斗，请另外找挑战对象吧");
            return true;
        }
        // 设置决斗人，接受人key
        redisTemplate.opsForValue().set(juedouKey, targetQQ, 10,  TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(jieshouKey, qq, 10,  TimeUnit.MINUTES);
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), " 锵锵，打输住院，打赢坐牢(禁言)\n"+
                at +"\n" + at1 + " 向你发起决斗邀请，快在10分钟内发送「/接受」迎接挑战吧，也可以发送「/拒绝」拒绝本次挑战");
        return true;
    }

    private int getJinyanTime(String group, String qq) {
        String juedou7Key = RedisConstant.JUEDOU_7_COUNT + group + ":" + qq;
        int baseTime = 56;
        if (redisTemplate.hasKey(juedou7Key)) {
            int integer = Integer.parseInt(redisTemplate.opsForValue().get(juedou7Key));
            baseTime = 60 * integer - 4;
            if (integer == 7) {
                redisTemplate.opsForValue().set(juedou7Key, "1", 7, TimeUnit.DAYS);
            } else {
                redisTemplate.opsForValue().set(juedou7Key, String.valueOf(integer + 1), 7, TimeUnit.DAYS);
            }
        } else {
            redisTemplate.opsForValue().set(juedou7Key, "2", 7, TimeUnit.DAYS);
        }
        return (int)(Math.random()*baseTime + 5);
    }

}
