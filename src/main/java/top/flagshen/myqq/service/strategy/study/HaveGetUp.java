package top.flagshen.myqq.service.strategy.study;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.common.RobotTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.StudyStrategy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service("我起床啦")
public class HaveGetUp implements StudyStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RobotTemplate robotTemplate;

    @Autowired
    private ScoreUtil scoreUtil;

    @Override
    public boolean study(MyQQMessage message) {
        SimpleDateFormat dateFormater = new SimpleDateFormat("HHmm");
        String date= dateFormater.format(new Date());
        System.out.println(date);
        int time = Integer.parseInt(date);
        if (time >= 500 && time <= 730 && !redisTemplate.hasKey(RedisConstant.GET_UP)) {
            redisTemplate.opsForValue().set(RedisConstant.GET_UP, "1", 4, TimeUnit.HOURS);
            //发送群消息
            robotTemplate.sendMsgEx(message.getMqRobot(), message.getMqFromid(), "今天起来的好早啊，很棒哦，继续坚持吧\n" + scoreUtil.scoreCalculation(5));
        } else if (time > 730 && time <= 900 && !redisTemplate.hasKey(RedisConstant.GET_UP)) {
            redisTemplate.opsForValue().set(RedisConstant.GET_UP, "1", 2, TimeUnit.HOURS);
            //发送群消息
            robotTemplate.sendMsgEx(message.getMqRobot(), message.getMqFromid(), "现在起床还不晚呢，快快开始学习吧~");
        } else {
            robotTemplate.sendMsgEx(message.getMqRobot(), message.getMqFromid(), "现在起床不加分哦");
        }
        return true;
    }
}
