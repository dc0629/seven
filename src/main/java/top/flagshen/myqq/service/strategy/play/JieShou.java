package top.flagshen.myqq.service.strategy.play;

import catcode.CatCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
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

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
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

    /**
     * 连胜次数key
     */
    private static final String LIANSHEN = "LIANSHEN:";

    /**
     * 用药次数key
     */
    private static final String BLOOD_COUNT = "BLOOD_COUNT:";

    private static final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(true).build());

    /**
     * Close.
     */
    @PreDestroy
    public void close() {
        executorService.shutdown();
    }

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
        int jieshouLianShenCount = 0;
        int juedouLianShenCount = 0;
        int winLianShenCount;
        int jieshouBloodCount = 0;
        int juedouBloodCount = 0;
        int loserBloodCount;

        String jieShouLianShenKey = LIANSHEN + group + ":" + jieshouQQ;
        if (redisTemplate.hasKey(jieShouLianShenKey)) {
            jieshouLianShenCount = Integer.parseInt(redisTemplate.opsForValue().get(jieShouLianShenKey));
        }
        String jueDouLianShenKey = LIANSHEN + group + ":" + juedouQQ;
        if (redisTemplate.hasKey(jueDouLianShenKey)) {
            juedouLianShenCount = Integer.parseInt(redisTemplate.opsForValue().get(jueDouLianShenKey));
        }
        String jieshouBloodKey = BLOOD_COUNT + group + ":" + jieshouQQ;
        if (redisTemplate.hasKey(jieshouBloodKey)) {
            jieshouBloodCount = Integer.parseInt(redisTemplate.opsForValue().get(jieshouBloodKey));
        }
        String juedouBloodKey = BLOOD_COUNT + group + ":" + juedouQQ;
        if (redisTemplate.hasKey(juedouBloodKey)) {
            juedouBloodCount = Integer.parseInt(redisTemplate.opsForValue().get(juedouBloodKey));
        }


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

        jieshouYun = handleYun(jieshouYun, jieshouLianShenCount, jieshouBloodCount) + 20;
        juedouYun = handleYun(juedouYun, juedouLianShenCount, juedouBloodCount) + 20;
        log.info("jieshouYun:{}:{},juedouYun:{}:{}",jieshouQQ,jieshouYun,juedouQQ,juedouYun);
        int cont = jieshouYun + juedouYun;
        List<Boolean> list = new ArrayList<>(cont);
        for (int i = 0; i < jieshouYun; i++) {
            list.add(true);
        }
        for (int i = 0; i < juedouYun; i++) {
            list.add(false);
        }
        // 打乱数组
        Collections.shuffle(list);
        // 20加今日幸运值 的比为获胜概率 例如接受人运气100,发起人运气20 接受人获胜概率：发起人获胜概率为120：40
        if (list.get((int)(Math.random()*cont))) {
            loserQQ = juedouQQ;
            winQQ = jieshouQQ;
            winLianShenCount = jieshouLianShenCount;
            loserBloodCount = juedouBloodCount;
        } else {
            winLianShenCount = juedouLianShenCount;
            loserBloodCount = jieshouBloodCount;
        }
        log.info("winQQ:{},loserQQ:{}",winQQ,loserQQ);
        UserJuedouDO win = juedouService.getById(winQQ + ":" + group);
        UserJuedouDO loser = juedouService.getById(loserQQ + ":" + group);

        // 删除key
        String juedouKey = RedisConstant.JUEDOU + group + ":" + juedouQQ;
        redisTemplate.delete(jieshouKey);
        redisTemplate.delete(juedouKey);

        // 禁言时长
        int jinyantime = (int)(Math.random()*6 + 3);
        // 设置决斗禁言key，防止有人逃课
        String juedouJinYanKey = RedisConstant.JUEDOU_JINYAN + group + ":" + winQQ;
        redisTemplate.opsForValue().set(juedouJinYanKey, String.valueOf(jinyantime * 60), jinyantime*60+30, TimeUnit.SECONDS);

        CatCodeUtil util = CatCodeUtil.INSTANCE;
        String atLoser = util.toCat("at", "code="+loserQQ);
        String atWin = util.toCat("at", "code="+winQQ);
        StringBuilder winMsg = new StringBuilder();
        winMsg.append(atWin).append(" 在决斗中获胜，当前胜场「");

        // 更新胜者今日连胜场
        winLianShenCount = winLianShenCount + 1;
        redisTemplate.opsForValue().set(LIANSHEN + group + ":" + winQQ, String.valueOf(winLianShenCount), DateUtil.getMidnightMillis(), TimeUnit.MILLISECONDS);

        // 更新今日总胜场
        int winCount;
        if (win == null) {
            UserJuedouDO userJuedouDO = new UserJuedouDO();
            userJuedouDO.setId(winQQ+":"+group);
            userJuedouDO.setQq(winQQ);
            userJuedouDO.setGroupNum(group);
            juedouService.save(userJuedouDO);
            winCount = 1;
            winMsg.append(winCount).append("」场，巡捕30秒后到达，关进大牢").append(jinyantime).append("分钟");
        } else {
            winCount = win.getWinCount() + 1;
            win.setWinCount(winCount);
            winMsg.append(winCount).append("」场，巡捕30秒后到达，关进大牢").append(jinyantime).append("分钟");
            // 连胜3  6  9胜利给  1  2  3份修复液,总胜场10，20.30分别获得1，2，3份
            int getBloodCount = 0;
            if (winLianShenCount == 3) {
                getBloodCount+=1;
            } else if (winLianShenCount == 6) {
                getBloodCount+=2;
            } else if (winLianShenCount == 9) {
                getBloodCount+=3;
            }
            if (winCount == 10) {
                getBloodCount+=1;
            } else if (winCount == 20) {
                getBloodCount+=2;
            } else if (winCount == 30) {
                getBloodCount+=3;
            }
            if (getBloodCount > 0) {
                propsService.addBlood(winQQ, getBloodCount);
                winMsg.append("，获得修复液").append(getBloodCount).append("份");
            }
            juedouService.updateById(win);
        }

        StringBuilder loserMsg = new StringBuilder();
        loserMsg.append("哦！").append(atLoser).append(" 在决斗中战败");
        //更新败方今日连胜场
        redisTemplate.opsForValue().set(LIANSHEN + group + ":" + loserQQ, "0", DateUtil.getMidnightMillis(), TimeUnit.MILLISECONDS);

        // 如果输的人有血包，并且今日使用血包数量不超过3，就可以扣除一个血包不清0胜场
        if (loser != null) {
            int blood = propsService.getBloodCount(loserQQ);
            if (blood > 0 && loserBloodCount < 3) {
                blood = blood - 1;
                propsService.useBlood(loserQQ);
                loserMsg.append("，使用修复液疗伤，十分钟后恢复，当前剩余修复液").append(blood).append("份\n");;
                String cdKey = RedisConstant.YILIAO_CD + group + ":" + loserQQ;
                redisTemplate.opsForValue().set(cdKey, "1",
                        10, TimeUnit.MINUTES);
                redisTemplate.opsForValue().set(BLOOD_COUNT + group + ":" + loserQQ, String.valueOf(++loserBloodCount), DateUtil.getMidnightMillis(), TimeUnit.MILLISECONDS);
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

        // 延迟1分钟后禁言，用来放狠话
        String winQQ1 = winQQ;
        executorService.schedule(() -> {
            // 校验是否因为3连图被禁言了，防止后一次禁言覆盖前一次禁言，逃课
            if (!redisTemplate.hasKey(RedisConstant.JINYAN_FLAG +  group + ":" + winQQ1)) {
                message.getSender().SETTER.setGroupBan(group, winQQ1, Long.valueOf(jinyantime) * 60);
            }
        }, 30, TimeUnit.SECONDS);

        return true;
    }

    /**
     * 处理运气，连胜大于等于3，6，9 都扣一次幸运值
     * 扣到用药*10的运气
     * @param yun 运气
     * @param winCount 今日胜场
     * @return
     */
    private static int handleYun(int yun, int winCount, int bloodCount) {
        yun = (yun - bloodCount*10) > 0 ? (yun - bloodCount*10) : 0;
        if (winCount >= 3) {
            yun = (yun - 10) > 0 ? (yun - 10) : 0;
        }
        if (winCount >= 6) {
            yun = (yun - 15) > 0 ? (yun - 15) : 0;
        }
        if (winCount >= 9) {
            yun = (yun - 25) > 0 ? (yun - 25) : 0;
        }
        return yun;
    }
}
