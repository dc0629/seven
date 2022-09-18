package top.flagshen.myqq.service.strategy.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.dao.blacklist.entity.BlacklistDO;
import top.flagshen.myqq.service.blacklist.IBlacklistService;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.RobotTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;

@Service("添加黑名单")
public class TianJiaBlackList implements OperationStrategy {

    private final RobotTemplate robotTemplate;

    public TianJiaBlackList(RobotTemplate robotTemplate) {
        this.robotTemplate = robotTemplate;
    }

    @Autowired
    private IBlacklistService blacklistService;

    @Override
    public boolean operation(MyQQMessage message) {
        BlacklistDO blacklistDO = new BlacklistDO();
        blacklistDO.setQqNum(message.getMqMsg());
        // 添加黑名单
        blacklistService.saveOrUpdate(blacklistDO);
        //发送群消息
        robotTemplate.sendMsgEx(message.getMqRobot(),
                0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, "添加成功");
        return true;
    }
}
