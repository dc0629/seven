package top.flagshen.myqq.service.group;

import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;
import top.flagshen.myqq.entity.common.MyQQMessage;

public interface IGroupManageService {

    /**
     * 监控并且禁言，个人3连禁言发言人，群体5连禁言第5人
     * @param groupMsg
     * @return
     */
    void jkjinyan(GroupMsg groupMsg, MsgSender sender);

    /**
     * 有人被禁言就记录并且提示禁言次数
     * @param message
     * @return
     */
    void jinyanlog(MyQQMessage message);

    /**
     * 敏感词检测
     * @param groupMsg
     * @return
     */
    void mgc(GroupMsg groupMsg, MsgSender sender);
}
