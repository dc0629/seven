package top.flagshen.myqq.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.XiaoshenTemplate;
import top.flagshen.myqq.entity.MyQQMessage;
import top.flagshen.myqq.entity.ReqResult;
import top.flagshen.myqq.service.IGroupManageService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroupManageServiceImpl implements IGroupManageService {

    private final XiaoshenTemplate xsTemplate;

    public GroupManageServiceImpl(XiaoshenTemplate xsTemplate) {
        this.xsTemplate = xsTemplate;
    }

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private static final List<String> manageGroup = Arrays.asList("xxx","xxx");

    private static final String ftQQKey = "ftQQ:";//上一个发图的qq + 群号组成key
    private static final String qtNumKey = "qtNum:";//群体连图数量 + 群号组成key
    private static final String grNumKey = "grNum:";//个人连图数量 + 群号组成key
    Map<String, Object> map = new HashMap<>();
    Map<String, Object> jymap = new HashMap<>();

    @Override
    public synchronized ReqResult jkjinyan(MyQQMessage message) {
        // 是否开启禁言功能
        if (!redisTemplate.hasKey(RedisConstant.JINYAN)) {
            return null;
        }
        if (manageGroup.contains(message.getMqFromid())) {
            String fk = ftQQKey + message.getMqFromid();
            String qk = qtNumKey + message.getMqFromid();
            String gk = grNumKey + message.getMqFromid();
            if (message.getMqMsg().contains("[pic=")) {
                String ftQQ = (String) jymap.get(fk);// 上一个发图的qq
                Integer qtNumI = (Integer) jymap.get(qk);//群体连图数量
                Integer grNumI = (Integer) jymap.get(gk);//个人连图数量
                int qtNum = qtNumI != null ? qtNumI : 0;
                int grNum = grNumI != null ? grNumI : 0;
                qtNum += 1;
                jymap.put(qk, qtNum);
                // 如果个人数量0或者上一个发图人和这个是同一个，个人发图数就加一，否则就是1
                if (grNum == 0 || message.getMqFromqq().equals(ftQQ)) {
                    grNum += 1;
                    jymap.put(fk, message.getMqFromqq());
                    jymap.put(gk, grNum);
                } else {
                    grNum = 1;
                    jymap.put(fk, message.getMqFromqq());
                    jymap.put(gk, grNum);
                }

                // 如果群发数量=5，就禁言现在的发图人
                if (qtNum >= 5) {
                    jinyan(message, "连续五张图最后一，二位会被禁言喔\r\n" +
                            "[emoji=D485]([emoji=C2AFE38582C2AFD485])", ftQQ);
                    // 清空个人和群体发的数量
                    jymap.put(gk, 0);
                    jymap.put(qk, 0);
                    jymap.put(fk, "");
                    return null;
                }
                // 如果个人连发=3，就禁言现在发图的人
                else if (grNum >= 3) {
                    jinyan(message, "个人三连图会被禁言喔\r\n（[emoji=EFBDA1]ò [emoji=E28880] ó[emoji=EFBDA1]）", null);
                    // 清空个人和群体发的数量
                    jymap.put(gk, 0);
                    jymap.put(qk, 0);
                    jymap.put(fk, "");
                    return null;
                }
            } else {
                // 没人发图就置为空
                jymap.put(gk, 0);
                jymap.put(qk, 0);
                jymap.put(fk, "");
            }
        }
        return null;
    }

    /**
     * 满足条件就禁言
     * @param message
     * @param context
     * @param gk
     * @param qk
     */
    private void jinyan(MyQQMessage message, String context, String ftQQ) {
        map.put("c1", "444");
        map.put("c2", message.getMqFromid());
        // 禁言
        map.put("c3", message.getMqFromqq());
        map.put("c4", 600);
        xsTemplate.tongyongPost("Api_Shutup", map);
        // 禁言倒数第二个人
        if (StringUtils.isNotBlank(ftQQ)) {
            map.put("c3", ftQQ);
            xsTemplate.tongyongPost("Api_Shutup", map);
        }
        // 发消息
        xsTemplate.sendMsgEx(message.getMqRobot(),
                0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, context);
    }
}
