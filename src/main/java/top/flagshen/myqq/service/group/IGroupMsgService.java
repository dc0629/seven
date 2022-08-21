package top.flagshen.myqq.service.group;

import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.entity.common.NovelAttribute;
import top.flagshen.myqq.entity.common.ReqResult;

public interface IGroupMsgService {

    /**
     * 对群的一些操作
     * @param msg
     * @param message
     * @return
     */
    ReqResult manageGroupMsg(String msg, MyQQMessage message);

    /**
     * 发送更新公告
     * @param novelAttribute
     * @param template
     * @return
     */
    ReqResult batchSendMsg(NovelAttribute novelAttribute, String template);

    /**
     * 入群发欢迎消息
     * @param message
     * @return
     */
    ReqResult ruqunMsg(MyQQMessage message);

}