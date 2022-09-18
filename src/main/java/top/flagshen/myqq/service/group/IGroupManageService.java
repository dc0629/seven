package top.flagshen.myqq.service.group;

import love.forte.simbot.api.message.events.GroupMsg;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.entity.common.ReqResult;

public interface IGroupManageService {

    /**
     * 监控并且禁言，个人3连禁言发言人，群体5连禁言第5人
     * @param groupMsg
     * @return
     */
    ReqResult jkjinyan(GroupMsg groupMsg);

    /**
     * 有人被禁言就记录并且提示禁言次数
     * @param message
     * @return
     */
    ReqResult jinyanlog(MyQQMessage message);

    /**
     * 敏感词检测
     * @param groupMsg
     * @return
     */
    ReqResult mgc(GroupMsg groupMsg);
}
