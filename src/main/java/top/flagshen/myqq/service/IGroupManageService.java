package top.flagshen.myqq.service;

import top.flagshen.myqq.entity.MyQQMessage;
import top.flagshen.myqq.entity.NovelAttribute;
import top.flagshen.myqq.entity.ReqResult;

public interface IGroupManageService {

    /**
     * 监控并且禁言，个人3连禁言发言人，群体5连禁言第5人
     * @param message
     * @return
     */
    ReqResult jkjinyan(MyQQMessage message);
}
