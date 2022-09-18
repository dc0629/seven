package top.flagshen.myqq.service.strategy.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.service.blacklist.IBlacklistService;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.RobotTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;

@Service("移除黑名单")
public class YiChuBlackList implements OperationStrategy {

    private final RobotTemplate robotTemplate;

    public YiChuBlackList(RobotTemplate robotTemplate) {
        this.robotTemplate = robotTemplate;
    }

    @Autowired
    private IBlacklistService blacklistService;

    @Override
    public boolean operation(MyQQMessage message) {
        // 移除黑名单
        blacklistService.removeById(message.getMqMsg());
        //发送群消息
        robotTemplate.sendMsgEx(message.getMqRobot(),
                0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, "移除成功");
        return true;
    }
}
