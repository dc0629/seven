package top.flagshen.myqq.service;

import top.flagshen.myqq.entity.MyQQMessage;
import top.flagshen.myqq.entity.ReqResult;

public interface IGroupManageService {

    /**
     * 监控并且禁言，个人3连禁言发言人，群体5连禁言第5人
     * @param message
     * @return
     */
    ReqResult jkjinyan(MyQQMessage message);

    /**
     * 有人被禁言就记录并且提示禁言次数
     * @param message
     * @return
     */
    ReqResult jinyanlog(MyQQMessage message);
}
