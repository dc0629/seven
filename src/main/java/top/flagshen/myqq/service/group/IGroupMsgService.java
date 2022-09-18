package top.flagshen.myqq.service.group;

import love.forte.simbot.api.message.events.GroupMemberIncrease;
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
     * 普通发消息
     * @param groupId
     * @param content
     */
    void sendMsg(String groupId, String content);

    /**
     * 入群发欢迎消息
     * @param groupMemberIncrease
     * @return
     */
    ReqResult ruqunMsg(GroupMemberIncrease groupMemberIncrease);

}
