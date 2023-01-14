package top.flagshen.myqq.service.group.impl;

import catcode.CatCodeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.cache.RedisConstant;
import top.flagshen.myqq.dao.forbidden.entity.ForbiddenLogDO;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.forbidden.IForbiddenLogService;
import top.flagshen.myqq.service.group.IGroupManageService;
import top.flagshen.myqq.util.MgcUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class GroupManageServiceImpl implements IGroupManageService {

    @Autowired
    private IForbiddenLogService forbiddenLogService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private static final List<String> manageGroup = Arrays.asList("xxx");

    private static final String ftQQKey = "ftQQ:";//上一个发图的qq + 群号组成key
    private static final String qtNumKey = "qtNum:";//群体连图数量 + 群号组成key
    private static final String grNumKey = "grNum:";//个人连图数量 + 群号组成key
    private static final String cyKey = "cyQQ:";//参与连图成员 + 群号组成key
    private static Map<String, Object> jymap = new HashMap<>();
    // 参与发图的人
    private static Map<String, Set<String>> cyMap = new HashMap<>();

    @Override
    public synchronized void jkjinyan(GroupMsg groupMsg, MsgSender sender) {
        // 是否开启禁言功能
        if (!redisTemplate.hasKey(RedisConstant.JINYAN)) {
            return;
        }
        String groupCode = groupMsg.getGroupInfo().getGroupCode();
        String accountCode = groupMsg.getAccountInfo().getAccountCode();
        if (manageGroup.contains(groupCode)) {
            String fk = ftQQKey + groupCode;
            String qk = qtNumKey + groupCode;
            String gk = grNumKey + groupCode;
            String ck = cyKey + groupCode;
            MessageContent msgContent = groupMsg.getMsgContent();
            Set<String> cySet = cyMap.get(ck) == null ? new HashSet<>() : cyMap.get(ck);
            if (msgContent != null && (msgContent.getMsg().contains("image") || msgContent.getMsg().contains("marketFace"))) {
                cySet.add(accountCode);
                String ftQQ = (String) jymap.get(fk);// 上一个发图的qq
                Integer qtNumI = (Integer) jymap.get(qk);//群体连图数量
                Integer grNumI = (Integer) jymap.get(gk);//个人连图数量
                int qtNum = qtNumI != null ? qtNumI : 0;
                int grNum = grNumI != null ? grNumI : 0;
                qtNum += 1;
                jymap.put(qk, qtNum);
                // 如果个人数量0或者上一个发图人和这个是同一个，个人发图数就加一，否则就是1
                if (grNum == 0 || accountCode.equals(ftQQ)) {
                    grNum += 1;
                    jymap.put(fk, accountCode);
                    jymap.put(gk, grNum);
                    cyMap.put(ck, cySet);
                } else {
                    grNum = 1;
                    jymap.put(fk, accountCode);
                    jymap.put(gk, grNum);
                    cyMap.put(ck, cySet);
                }

                // 如果个人连发=3，就禁言现在发图的人
                if (grNum >= 3) {
                    // 发消息
                    sender.SENDER.sendGroupMsg(groupCode, "个人三连图会被禁言喔\n" +
                            "（｡ò ∀ ó｡）");
                    // 禁言
                    sender.SETTER.setGroupBan(groupCode, accountCode, jyTime(groupCode, accountCode));
                    // 清空个人和群体发的数量
                    jymap.put(gk, 0);
                    jymap.put(qk, 0);
                    jymap.put(fk, "");
                    cySet.clear();
                    cyMap.put(ck, cySet);
                    return;
                }
                // 如果群发数量=5，就禁言现在的发图人
                if (qtNum >= 5) {
                    // 发消息
                    sender.SENDER.sendGroupMsg(groupCode, "连续五张图参与者都会被禁言喔\r\n" +
                            "（｡ò ∀ ó｡）");
                    cySet.forEach(qq -> {
                        try {
                            // 禁言
                            sender.SETTER.setGroupBan(groupCode, qq, jyTime(groupCode, qq));
                        } catch (Exception e) {}
                    });
                    // 清空个人和群体发的数量
                    jymap.put(gk, 0);
                    jymap.put(qk, 0);
                    cySet.clear();
                    cyMap.put(ck, cySet);
                    return;
                }
            } else {
                // 没人发图就置为空
                jymap.put(gk, 0);
                jymap.put(qk, 0);
                jymap.put(fk, "");
                cySet.clear();
                cyMap.put(ck, cySet);
            }
        }
        return;
    }

    public Long jyTime(String id, String qq) {
        redisTemplate.opsForValue().set(RedisConstant.JINYAN_FLAG +  id + ":" + qq, "1", 40, TimeUnit.SECONDS);
        String jinyanCountKey = RedisConstant.JINYAN_COUNT +  id + ":" + qq;
        // 判断在redis中是否有key值
        Boolean redisKey = redisTemplate.hasKey(jinyanCountKey);
        // 如果没有key值，对他进行添加到redis中
        if (redisKey) {
            // 禁言次数
            int count = Integer.parseInt(redisTemplate.opsForValue().get(jinyanCountKey));
            count++;
            redisTemplate.opsForValue().set(jinyanCountKey, String.valueOf(count), 1, TimeUnit.DAYS);
            if (count == 2) {
                // 第二次禁言30分钟
                return 1800L;
            } else if (count >= 3) {
                // 最后一次禁言1天
                return 86400L;
            }
        }
        // 存入key
        redisTemplate.opsForValue().set(jinyanCountKey, "1", 1, TimeUnit.DAYS);
        // 第一次禁言600秒
        return 600L;
    }

    /**
     * 记录一条禁言日志
     * @param message
     * @return
     */
    @Override
    public void jinyanlog(MyQQMessage message) {
        ForbiddenLogDO forbiddenLogDO = new ForbiddenLogDO();
        forbiddenLogDO.setQqNum(message.getMqFromqq());
        forbiddenLogDO.setGroupNum(message.getMqFromid());
        forbiddenLogService.save(forbiddenLogDO);

        int count = forbiddenLogService.count(new LambdaQueryWrapper<ForbiddenLogDO>()
                .eq(ForbiddenLogDO::getQqNum, message.getMqFromqq()));
        CatCodeUtil util = CatCodeUtil.INSTANCE;
        // 构建at
        String at = util.toCat("at", "code="+message.getMqFromqq());
        String text = at +
                "\r\n这是你的第" + count + "次被禁言，禁言次数过多可能会被移出群聊哦";
        // 发消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), text);
    }

    @Override
    public void mgc(GroupMsg groupMsg, MsgSender sender) {
        // 是否开启撤回功能
        if (!redisTemplate.hasKey(RedisConstant.CHEHUI) || "641684580".equals(groupMsg.getGroupInfo().getGroupCode())) {
            return;
        }
        try {
            if (MgcUtil.haveMgc(groupMsg.getText())) {
                log.info("撤回消息:{}",groupMsg.getText());
                sender.SETTER.setMsgRecall(groupMsg.getFlag());
            }
        } catch (Exception e) {
            log.info("撤回出错:{}", e.getMessage());
        }
    }
}
