package top.flagshen.myqq.service.strategy.play;

import catcode.CatCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.Permissions;
import top.flagshen.myqq.common.cache.RedisConstant;
import top.flagshen.myqq.dao.userinfo.entity.UserJuedouDO;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.PlayStrategy;
import top.flagshen.myqq.service.userinfo.IUserJuedouService;
import top.flagshen.myqq.util.ContentUtil;
import top.flagshen.myqq.util.DateUtil;

import java.util.concurrent.TimeUnit;

@Service("接受")
public class JieShou implements PlayStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    private IUserJuedouService juedouService;

    @Override
    @Permissions(groupNums = "423430656,954804208,903959441")
    public boolean play(MyQQMessage message) {
        String group = message.getMqFromid();
        // 接受人
        String jieshouQQ = message.getMqFromqq();
        String jieshouKey = RedisConstant.JIESHOU + group + ":" + jieshouQQ;
        // 如果没有被发起挑战，接受就没反应
        if(!redisTemplate.hasKey(jieshouKey)) {
            return true;
        }
        // 发起人
        String juedouQQ = redisTemplate.opsForValue().get(jieshouKey);
        // 失败的倒霉蛋
        String loserQQ = jieshouQQ;
        String winQQ = juedouQQ;

        // 获取 或 生成当天幸运值
        int jieshouYun;
        int juedouYun;
        String jieshouYunKey = RedisConstant.DIVINATION + jieshouQQ;
        String juedouYunKey = RedisConstant.DIVINATION + juedouQQ;
        if (redisTemplate.hasKey(jieshouYunKey)) {
            jieshouYun = Integer.valueOf(redisTemplate.opsForValue().get(jieshouYunKey));
        } else {
            jieshouYun = ContentUtil.getYun();
            redisTemplate.opsForValue().set(jieshouYunKey, String.valueOf(jieshouYun), DateUtil.getMidnightMillis(), TimeUnit.MILLISECONDS);
        }

        if (redisTemplate.hasKey(juedouYunKey)) {
            juedouYun = Integer.valueOf(redisTemplate.opsForValue().get(juedouYunKey));
        } else {
            juedouYun = ContentUtil.getYun();
            redisTemplate.opsForValue().set(juedouYunKey, String.valueOf(juedouYun), DateUtil.getMidnightMillis(), TimeUnit.MILLISECONDS);
        }

        // 20加今日幸运值 的比为获胜概率 例如接受人运气100,发起人运气20 接受人获胜概率：发起人获胜概率为120：40
        if ((int)(Math.random()*(jieshouYun+juedouYun+40)) < (jieshouYun+20)) {
            loserQQ = juedouQQ;
            winQQ = jieshouQQ;
        }

        // 删除key
        String juedouKey = RedisConstant.JUEDOU + group + ":" + juedouQQ;
        redisTemplate.delete(jieshouKey);
        redisTemplate.delete(juedouKey);

        // 禁言时长
        int jinyantime = (int)(Math.random()*6 + 5);
        // 设置决斗禁言key，防止有人逃课
        String juedouJinYanKey = RedisConstant.JUEDOU_JINYAN + group + ":" + winQQ;
        redisTemplate.opsForValue().set(juedouJinYanKey, String.valueOf(jinyantime * 60), jinyantime, TimeUnit.MINUTES);
        // 更新今日胜场
        UserJuedouDO win = juedouService.getById(winQQ+":"+group);
        int winCount = 0;
        if (win == null) {
            UserJuedouDO userJuedouDO = new UserJuedouDO();
            userJuedouDO.setId(winQQ+":"+group);
            userJuedouDO.setQq(winQQ);
            userJuedouDO.setGroupNum(group);
            juedouService.save(userJuedouDO);
            winCount = 1;
        } else {
            winCount = win.getWinCount() + 1;
            win.setWinCount(winCount);
            juedouService.updateById(win);
        }

        CatCodeUtil util = CatCodeUtil.INSTANCE;
        String at = util.toCat("at", "code="+loserQQ);
        String at1 = util.toCat("at", "code="+winQQ);
        message.getSender().SENDER.sendGroupMsg(group, "哦！" + at + " 在决斗中战败住院，胜场被清零\n"+
                at1+" 在决斗中获胜，当前胜场「"+winCount+"」场，同时被关进大牢，时长" +
                jinyantime + "分钟");

        // 战败的人今天不能再发起或接受挑战
        String juedouCdKey = RedisConstant.JUEDOU_CD + group + ":" + loserQQ;
        redisTemplate.opsForValue().set(juedouCdKey, "1",
                DateUtil.getMidnightMillis(), TimeUnit.MILLISECONDS);



        UserJuedouDO loser = juedouService.getById(loserQQ+":"+group);
        if (loser != null) {
            loser.setWinCount(0);
            juedouService.updateById(loser);
        }
        message.getSender().SETTER.setGroupBan(group, winQQ, Long.valueOf(jinyantime) * 60);

        return true;
    }

}
