package top.flagshen.myqq.service.strategy.play;

import catcode.CatCodeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.RobotTemplate;
import top.flagshen.myqq.dao.updatereminder.entity.UpdateReminderDO;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.PlayStrategy;
import top.flagshen.myqq.service.updatereminder.IUpdateReminderService;

import java.util.concurrent.TimeUnit;

/**
 * /开启更新提醒
 */
@Service("开启更新提醒")
public class KaiQiGengXinTiXing implements PlayStrategy {

    @Autowired
    private RobotTemplate robotTemplate;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    private IUpdateReminderService updateReminderService;

    @Override
    public boolean play(MyQQMessage message) {
        // 先查询存不存在，一个qq号只能在一个群预约，所以查询时候只根据qq号查，但是保存的时候保存群号
        UpdateReminderDO updateReminderDO = updateReminderService.getOne(new LambdaQueryWrapper<UpdateReminderDO>()
                .eq(UpdateReminderDO::getQqNum, message.getMqFromqq()).last("limit 1"));
        // 存在就结束
        if (updateReminderDO != null) {
            return true;
        }
        // 否则新增
        UpdateReminderDO addUpdateReminder = new UpdateReminderDO();
        addUpdateReminder.setGroupNum(message.getMqFromid());
        addUpdateReminder.setQqNum(message.getMqFromqq());
        boolean save = updateReminderService.save(addUpdateReminder);
        if (save) {
            String key = "kqgxtx:" + message.getMqFromid() + message.getMqFromqq();
            // 防止刷屏，10分钟内开启过就不提示
            if (redisTemplate.hasKey(key)) {
                return true;
            }
            redisTemplate.opsForValue().set(key, "1", 10, TimeUnit.MINUTES);
            CatCodeUtil util = CatCodeUtil.INSTANCE;
            // 构建at
            String at = util.toCat("at", "code="+message.getMqFromqq());
            //发送群消息
            robotTemplate.sendMsgEx(message.getMqRobot(), message.getMqFromid(), at + " 开启成功");
        }
        return true;
    }
}
