package top.flagshen.myqq.controller;

import love.forte.simbot.annotation.Listen;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnGroupMemberIncrease;
import love.forte.simbot.api.message.events.GroupMemberIncrease;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MuteGet;
import love.forte.simbot.api.sender.MsgSender;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.group.IGroupManageService;
import top.flagshen.myqq.service.group.IGroupMsgService;

/**
 * @author dengchao
 */
@RestController
public class MsgController {

    @Autowired
    private IGroupMsgService groupMsgService;

    @Autowired
    private IGroupManageService groupManageService;

    /**
     * 群消息监听
     * @param groupMsg
     */
    @OnGroup
    public void onGroupMsg(GroupMsg groupMsg, MsgSender sender) {
        // 系统消息直接结束
        if (GroupMsg.Type.SYS.equals(groupMsg.getGroupMsgType())) {
            return;
        }
        // 群消息就开始判断是不是有人在连图，连图就禁言
        groupManageService.jkjinyan(groupMsg, sender);
        MyQQMessage message = new MyQQMessage();
        String text = groupMsg.getText();
        if (StringUtils.isBlank(text)) {
            return;
        }
        // 敏感词信息进行撤回
        groupManageService.mgc(groupMsg, sender);
        message.setMqMsg(text);
        // 后面的内容就判断是不是/开头，不是就结束
        if (!"/".equals(text.substring(0, 1))) {
            return;
        }
        text = text.substring(1);
        if (StringUtils.isEmpty(text)) {
            return;
        }
        message.setMqFromid(groupMsg.getGroupInfo().getGroupCode());
        message.setMqFromqq(groupMsg.getAccountInfo().getAccountCode());
        message.setMqRobot(groupMsg.getBotInfo().getBotCode());
        groupMsgService.manageGroupMsg(text, message);
    }

    /**
     * 群成员增加事件
     * @param groupMemberIncrease
     */
    @OnGroupMemberIncrease
    public void onGroupAddRequest(GroupMemberIncrease groupMemberIncrease, MsgSender sender) {
        groupMsgService.ruqunMsg(groupMemberIncrease, sender);
    }

    /**
     * 群成员被禁言事件
     * @param muteGet
     */
    @Listen(MuteGet.class)
    public void onGroupMute(MuteGet muteGet) {

    }

}
