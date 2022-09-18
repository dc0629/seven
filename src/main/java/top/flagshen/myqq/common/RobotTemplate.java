package top.flagshen.myqq.common;

import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.bot.BotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dengchao
 */
@Component
public final class RobotTemplate {

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

    public void sendMsgEx(String botCode,int niming,int type,String group,String targetqq,String msg) {
        Bot bot = manager.getBot(botCode);
        bot.getSender().SENDER.sendGroupMsg(group, msg);
    }
}
