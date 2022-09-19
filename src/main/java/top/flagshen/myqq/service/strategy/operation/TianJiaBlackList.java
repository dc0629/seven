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

    @Autowired
    private RobotTemplate robotTemplate;

    @Autowired
    private IBlacklistService blacklistService;

    @Override
    public boolean operation(MyQQMessage message) {
        BlacklistDO blacklistDO = new BlacklistDO();
        blacklistDO.setQqNum(message.getMqMsg());
        // 添加黑名单
        blacklistService.saveOrUpdate(blacklistDO);
        //发送群消息
        robotTemplate.sendMsgEx(message.getMqRobot(), message.getMqFromid(), "添加成功");
        return true;
    }
}
