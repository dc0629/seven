package top.flagshen.myqq.service.strategy.play;

import catcode.CatCodeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.dao.updatereminder.entity.UpdateReminderDO;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.PlayStrategy;
import top.flagshen.myqq.service.updatereminder.IUpdateReminderService;

import java.util.concurrent.TimeUnit;

/**
 * /关闭更新提醒
 */
@Service("关闭更新提醒")
public class GuanBiGengXinTiXing implements PlayStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    private IUpdateReminderService updateReminderService;

    @Override
    public boolean play(MyQQMessage message) {
        // 先查询存不存在
        UpdateReminderDO updateReminderDO = updateReminderService.getOne(new LambdaQueryWrapper<UpdateReminderDO>()
                .eq(UpdateReminderDO::getGroupNum, message.getMqFromid())
                .eq(UpdateReminderDO::getQqNum, message.getMqFromqq()).last("limit 1"));
        // 不存在就结束
        if (updateReminderDO == null) {
            return true;
        }
        // 否则删除
        boolean remove = updateReminderService.removeById(updateReminderDO);
        if (remove) {
            String key = "gbgxtx:" + message.getMqFromid() + message.getMqFromqq();
            // 防止刷屏，10分钟内关闭过就不提示
            if (redisTemplate.hasKey(key)) {
                return true;
            }
            redisTemplate.opsForValue().set(key, "1", 10, TimeUnit.MINUTES);
            CatCodeUtil util = CatCodeUtil.INSTANCE;
            // 构建at
            String at = util.toCat("at", "code="+message.getMqFromqq());
            //发送群消息
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), at + " 关闭成功");
        }
        return true;
    }
}
