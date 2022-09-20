package top.flagshen.myqq.service.strategy.operation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.dao.forbidden.entity.ForbiddenLogDO;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.forbidden.IForbiddenLogService;
import top.flagshen.myqq.service.strategy.OperationStrategy;

/**
 * /查禁言次数 qq号 群号
 * 群号可为空
 */
@Service("查禁言次数")
public class ChaJinYanCount implements OperationStrategy {

    @Autowired
    private IForbiddenLogService forbiddenLogService;

    @Override
    public boolean operation(MyQQMessage message) {
        if (StringUtils.isBlank(message.getMqMsg())) {
            return true;
        }
        int j = message.getMqMsg().indexOf(" ");
        String qqNum = null;// qq号
        String groupNum = null;// 群号
        if (j > 0) {
            qqNum = message.getMqMsg().substring(0, j);// qq号
            groupNum = message.getMqMsg().substring(j + 1);// 群号
        } else {
            qqNum = message.getMqMsg();
        }
        if (StringUtils.isEmpty(qqNum)) {
            return true;
        }
        int count = forbiddenLogService.count(new LambdaQueryWrapper<ForbiddenLogDO>()
                .eq(ForbiddenLogDO::getQqNum, qqNum)
                .eq(StringUtils.isNotBlank(groupNum), ForbiddenLogDO::getGroupNum, groupNum));
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), "禁言次数："+count);
        return true;
    }
}