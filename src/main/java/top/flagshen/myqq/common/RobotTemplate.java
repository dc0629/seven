package top.flagshen.myqq.common;

import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.bot.BotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dengchao
 */
@Component
@Slf4j
public class RobotTemplate {

    @Autowired
    private BotManager manager;

    public void msgRecall(GroupMsg groupMsg) {
        Bot bot = manager.getBot(groupMsg.getBotInfo().getBotCode());
        bot.getSender().SETTER.setMsgRecall(groupMsg.getFlag());
    }

    public void setGroupBan(String botCode, String group, String member, Long time) {
        Bot bot = manager.getBot(botCode);
        bot.getSender().SETTER.setGroupBan(group, member, time);
    }

    public void sendMsgEx(String botCode, String group, String msg) {
        try {
            Bot bot = manager.getBot(botCode);
            bot.getSender().SENDER.sendGroupMsg(group, msg);
        } catch (Exception e) {
            log.info(e.getMessage());
            log.info(msg);
        }
    }

    public void sendGroupNotice(String botCode, String group, String title, String content) {
        try {
            Bot bot = manager.getBot(botCode);
            bot.getSender().SENDER.sendGroupNotice(group, title, content, false, false, false, false);
        } catch (Exception e) {
            log.info(e.getMessage());
            log.info(content);
        }
    }

    public GroupMemberInfo getMemberInfo(String groupId, String qq) {
        Bot bot = manager.getBot("1462152250");
        try {
            return bot.getSender().GETTER.getMemberInfo(groupId, qq);
        } catch (Exception e) {
        }
        return null;
    }

    public List<String> getGroupMemberList(String group) {
        Bot bot = manager.getBot("");
        List<GroupMemberInfo> groupMemberInfos = bot.getSender().GETTER.getGroupMemberList(group).getResults();
        return groupMemberInfos.stream().map(GroupMemberInfo::getAccountCode).collect(Collectors.toList());
    }
}
