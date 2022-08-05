package top.flagshen.myqq.strategy.play;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.XiaoshenTemplate;
import top.flagshen.myqq.database.updatereminder.entity.UpdateReminderDO;
import top.flagshen.myqq.database.updatereminder.service.IUpdateReminderService;
import top.flagshen.myqq.entity.MyQQMessage;
import top.flagshen.myqq.strategy.PlayStrategy;

import java.util.concurrent.TimeUnit;

/**
 * /开启更新提醒
 */
@Service("开启更新提醒")
public class KaiQiGengXinTiXing implements PlayStrategy {

    private final XiaoshenTemplate xsTemplate;

    public KaiQiGengXinTiXing(XiaoshenTemplate xsTemplate) {
        this.xsTemplate = xsTemplate;
    }

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
            //发送群消息
            xsTemplate.sendMsgEx(message.getMqRobot(),
                    0, TypeConstant.MSGTYPE_GROUP,
                    message.getMqFromid(), null, "[@"+message.getMqFromqq()+"]" + " 开启成功");
        }
        return true;
    }
}
