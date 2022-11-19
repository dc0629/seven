package top.flagshen.myqq.service.strategy.play;

import catcode.CatCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.Permissions;
import top.flagshen.myqq.common.cache.RedisConstant;
import top.flagshen.myqq.dao.userinfo.entity.UserJuedouDO;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.props.IPropsService;
import top.flagshen.myqq.service.strategy.PlayStrategy;
import top.flagshen.myqq.service.userinfo.IUserJuedouService;
import top.flagshen.myqq.util.ContentUtil;
import top.flagshen.myqq.util.DateUtil;

import java.util.concurrent.TimeUnit;

@Service("接受")
@Slf4j
public class JieShou implements PlayStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    private IUserJuedouService juedouService;

    @Autowired
    private IPropsService propsService;

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
            jieshouYun = Integer.parseInt(redisTemplate.opsForValue().get(jieshouYunKey));
        } else {
            jieshouYun = ContentUtil.getYun();
            redisTemplate.opsForValue().set(jieshouYunKey, String.valueOf(jieshouYun), DateUtil.getMidnightMillis(), TimeUnit.MILLISECONDS);
        }

        if (redisTemplate.hasKey(juedouYunKey)) {
            juedouYun = Integer.parseInt(redisTemplate.opsForValue().get(juedouYunKey));
        } else {
            juedouYun = ContentUtil.getYun();
            redisTemplate.opsForValue().set(juedouYunKey, String.valueOf(juedouYun), DateUtil.getMidnightMillis(), TimeUnit.MILLISECONDS);
        }

        // 胜利场次超过3，6，9时，幸运值都降低20
        UserJuedouDO jieshou = juedouService.getById(jieshouQQ + ":" + group);
        if (jieshou != null) {
            jieshouYun = handleYun(jieshouYun, jieshou.getWinCount());
        }
        UserJuedouDO juedou = juedouService.getById(juedouQQ + ":" + group);
        if (juedou != null) {
            juedouYun = handleYun(juedouYun, juedou.getWinCount());
        }

        UserJuedouDO win;
        UserJuedouDO loser;
        // 20加今日幸运值 的比为获胜概率 例如接受人运气100,发起人运气20 接受人获胜概率：发起人获胜概率为120：40
        if ((int)(Math.random()*(jieshouYun+juedouYun+40)) < (jieshouYun+20)) {
            loserQQ = juedouQQ;
            loser = juedou;
            winQQ = jieshouQQ;
            win = jieshou;
        } else {
            win = juedou;
            loser = jieshou;
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

        CatCodeUtil util = CatCodeUtil.INSTANCE;
        String atLoser = util.toCat("at", "code="+loserQQ);
        String atWin = util.toCat("at", "code="+winQQ);
        StringBuilder winMsg = new StringBuilder();
        winMsg.append(atWin).append(" 在决斗中获胜，当前胜场「");
        // 更新今日胜场
        int winCount = 0;
        if (win == null) {
            UserJuedouDO userJuedouDO = new UserJuedouDO();
            userJuedouDO.setId(winQQ+":"+group);
            userJuedouDO.setQq(winQQ);
            userJuedouDO.setGroupNum(group);
            juedouService.save(userJuedouDO);
            winCount = 1;
            winMsg.append(winCount).append("」场，同时被关进大牢，时长").append(jinyantime).append("分钟");
        } else {
            winCount = win.getWinCount() + 1;
            win.setWinCount(winCount);
            winMsg.append(winCount).append("」场，同时被关进大牢，时长").append(jinyantime).append("分钟");
            // 胜场为2多一个血包，5多两个，8多3个，10开始，每赢一场多一个
            if (winCount == 2) {
                propsService.addBlood(winQQ, 1);
                winMsg.append("，获得修复液1份");
            } else if (winCount == 5) {
                propsService.addBlood(winQQ, 2);
                winMsg.append("，获得修复液2份");
            } else if (winCount == 8) {
                propsService.addBlood(winQQ, 3);
                winMsg.append("，获得修复液3份");
            } else if (winCount >= 10) {
                propsService.addBlood(winQQ, 1);
                winMsg.append("，获得修复液1份");
            }
            juedouService.updateById(win);
        }

        StringBuilder loserMsg = new StringBuilder();
        loserMsg.append("哦！").append(atLoser).append(" 在决斗中战败");
        // 如果输的人有血包，就可以扣除一个血包不清0胜场
        if (loser != null) {
            int blood = propsService.getBloodCount(loserQQ);
            if (blood > 0) {
                blood = blood - 1;
                propsService.useBlood(loserQQ);
                loserMsg.append("，使用修复液疗伤，十分钟后恢复，当前剩余修复液").append(blood).append("份\n");;
                String cdKey = RedisConstant.YILIAO_CD + group + ":" + loserQQ;
                redisTemplate.opsForValue().set(cdKey, "1",
                        10, TimeUnit.MINUTES);
            } else {
                loserMsg.append("住院，胜场被清零\n");
                loser.setWinCount(0);
                juedouService.updateById(loser);
                // 战败的人今天不能再发起或接受挑战
                String juedouCdKey = RedisConstant.JUEDOU_CD + group + ":" + loserQQ;
                redisTemplate.opsForValue().set(juedouCdKey, "1", DateUtil.getMidnightMillis(), TimeUnit.MILLISECONDS);
            }
        } else {
            loserMsg.append("住院，胜场被清零\n");
            // 战败的人今天不能再发起或接受挑战
            String juedouCdKey = RedisConstant.JUEDOU_CD + group + ":" + loserQQ;
            redisTemplate.opsForValue().set(juedouCdKey, "1", DateUtil.getMidnightMillis(), TimeUnit.MILLISECONDS);
        }
        String msg = loserMsg.toString() + winMsg.toString();
        message.getSender().SENDER.sendGroupMsg(group, msg);
        message.getSender().SETTER.setGroupBan(group, winQQ, Long.valueOf(jinyantime) * 60);

        return true;
    }

    /**
     * 处理运气，今日胜场大于等于3，6，9 都扣一次幸运值
     * @param yun 运气
     * @param winCount 今日胜场
     * @return
     */
    private static int handleYun(int yun, int winCount) {
        if (winCount >= 3) {
            yun = (yun - 5) > 0 ? (yun - 5) : 0;
        }
        if (winCount >= 6) {
            yun = (yun - 15) > 0 ? (yun - 15) : 0;
        }
        if (winCount >= 9) {
            yun = (yun - 40) > 0 ? (yun - 40) : 0;
        }
        return yun;
    }

}
