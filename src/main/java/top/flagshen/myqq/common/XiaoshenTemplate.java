package top.flagshen.myqq.common;

import love.forte.simbot.bot.Bot;
import love.forte.simbot.bot.BotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.flagshen.myqq.entity.common.DataResult;
import top.flagshen.myqq.entity.common.PushMessage;
import top.flagshen.myqq.util.HttpApiUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author dengchao
 */
@Component
public final class XiaoshenTemplate {
    @Value("${xiaoshen.host}")
    private String host;
    @Value("${xiaoshen.port}")
    private String port;
    @Value("${xiaoshen.token}")
    private String token;

    @Autowired
    private BotManager manager;

    /**
     * 通用发消息
     * @param function
     * @param params
     * @return
     */
    public DataResult tongyongGet(String function,Map<String, Object> params) {
        return HttpApiUtil.sendGet(host, port, token, function, params);
    }
    public Map<String,Object> tongyongPost(String function,Map<String, Object> params) {
        return HttpApiUtil.sendPost(host, port,new PushMessage(function,token,params));
    }
    /**
     * 记录日志到框架
     * @param info
     * @return
     */
    public DataResult logInfo(String info) {
        Map<String, Object> params = new HashMap<>();
        params.put("c1", info);
        return HttpApiUtil.sendGet(host, port, token, "Api_OutPut", params);
    }

    public Map<String, Object> sendMsg(String robotqq, int type, String group, String targetqq, String msg) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("c1", robotqq);
        params.put("c2",type);
        params.put("c3",group);
        params.put("c4",targetqq);
        params.put("c5",msg);
        PushMessage pushMessage = new PushMessage("Api_SendMsg", token, params);

//        return HttpApiUtil.sendPost(host, port, pushMessage);
        return HttpApiUtil.sendPost(host, port, pushMessage);
    }

    public void sendMsgEx(String robotqq,int niming,int type,String group,String targetqq,String msg) {
        Bot bot = manager.getBot(robotqq);
        bot.getSender().SENDER.sendGroupMsg(group, msg);
    }
}
