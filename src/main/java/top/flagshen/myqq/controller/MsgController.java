package top.flagshen.myqq.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.entity.common.ReqResult;
import top.flagshen.myqq.service.group.IGroupManageService;
import top.flagshen.myqq.service.group.IGroupMsgService;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;

/**
 * @author 小棽
 * @date 2021/6/20 19:45
 */
@RequestMapping("/msg")
@RestController
public class MsgController {

    @Autowired
    private IGroupMsgService groupMsgService;

    @Autowired
    private IGroupManageService groupManageService;

    @PostMapping
    public ReqResult newMsg(@RequestBody MyQQMessage message, HttpServletRequest request) throws Exception {
        message.setMqMsg(URLDecoder.decode(message.getMqMsg(), "utf-8"));
        // 先判断是否收到空消息
        if (StringUtils.isEmpty(message.getMqMsg())) {
            // 如果是进群消息，就触发入群通知
            if (TypeConstant.MSGTYPE_ONE_BE_AGREE_GROUP == message.getMqType()) {
                return groupMsgService.ruqunMsg(message);
            } else {
                return null;
            }
        }
        String mqMsg = message.getMqMsg();
        if (TypeConstant.MSGTYPE_GROUP == message.getMqType()) {
            // 群消息就开始判断是不是有人在连图，连图就禁言
            groupManageService.jkjinyan(message);
            // 撤回敏感消息
            groupManageService.mgc(message);
        } else if (TypeConstant.MSGTYPE_ONE_BE_BAN == message.getMqType()) {
            // 如果是有人被禁言，就存一下禁言记录
            groupManageService.jinyanlog(message);
        }
        // 后面的内容就判断是不是/开头，不是就结束
        if (!"/".equals(mqMsg.substring(0, 1))) {
            return new ReqResult(1);
        }
        mqMsg = mqMsg.substring(1);
        if (StringUtils.isEmpty(mqMsg)) {
            return new ReqResult(1);
        }

        switch (message.getMqType()) {
            case TypeConstant.MSGTYPE_NODEFINE://消息类型_未定义
                break;
            case TypeConstant.MSGTYPE_FRIEDN://消息类型_好友
                break;
            case TypeConstant.MSGTYPE_GROUP://消息类型_群
                return groupMsgService.manageGroupMsg(mqMsg, message);
        }
        return new ReqResult(1);
    }

}
