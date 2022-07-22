package top.flagshen.myqq.strategy.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.database.blacklist.service.IBlacklistService;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.XiaoshenTemplate;
import top.flagshen.myqq.entity.MyQQMessage;
import top.flagshen.myqq.strategy.OperationStrategy;

@Service("移除黑名单")
public class YiChuBlackList implements OperationStrategy {

    private final XiaoshenTemplate xsTemplate;

    public YiChuBlackList(XiaoshenTemplate xsTemplate) {
        this.xsTemplate = xsTemplate;
    }

    @Autowired
    private IBlacklistService blacklistService;

    @Override
    public boolean operation(MyQQMessage message) {
        // 移除黑名单
        blacklistService.removeById(message.getMqMsg());
        //发送群消息
        xsTemplate.sendMsgEx(message.getMqRobot(),
                0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, "移除成功");
        return true;
    }
}
