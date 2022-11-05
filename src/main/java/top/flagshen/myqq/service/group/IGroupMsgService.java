package top.flagshen.myqq.service.group;

import love.forte.simbot.api.message.events.GroupMemberIncrease;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import love.forte.simbot.api.sender.MsgSender;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.entity.common.NovelAttribute;

import java.util.List;

public interface IGroupMsgService {

    /**
     * 对群的一些操作
     * @param msg
     * @param message
     * @return
     */
    void manageGroupMsg(String msg, MyQQMessage message);

    /**
     * 发送更新公告
     * @param novelAttribute
     * @param template
     * @return
     */
    void batchSendMsg(NovelAttribute novelAttribute, String template);

    /**
     * 普通发消息
     * @param groupId
     * @param content
     */
    void sendMsg(String groupId, String content);

    /**
     * 发公告
     * @param groupId
     * @param content
     */
    void sendGroupNotice(String groupId, String title, String content);

    /**
     * 获取群成员信息
     * @param groupId
     * @param qq
     * @return
     */
    GroupMemberInfo getMemberInfo(String groupId, String qq);

    /**
     * 获取群成员
     * @param group
     * @return
     */
    List<String> getGroupMemberNums(String group);

    /**
     * 入群发欢迎消息
     * @param groupMemberIncrease
     * @return
     */
    void ruqunMsg(GroupMemberIncrease groupMemberIncrease, MsgSender sender);

}
