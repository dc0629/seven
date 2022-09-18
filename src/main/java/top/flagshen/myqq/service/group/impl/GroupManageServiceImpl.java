package top.flagshen.myqq.service.group.impl;

import catcode.CatCodeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.XiaoshenTemplate;
import top.flagshen.myqq.dao.forbidden.entity.ForbiddenLogDO;
import top.flagshen.myqq.service.forbidden.IForbiddenLogService;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.entity.common.ReqResult;
import top.flagshen.myqq.service.group.IGroupManageService;
import top.flagshen.myqq.util.MgcUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class GroupManageServiceImpl implements IGroupManageService {

    @Autowired
    private IForbiddenLogService forbiddenLogService;

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
     */
    private void jinyan(MyQQMessage message, String context, String ftQQ) {
        // 发消息
        xsTemplate.sendMsgEx(message.getMqRobot(),
                0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, context);
        map.put("c1", "444");
        map.put("c2", message.getMqFromid());
        // 禁言
        map.put("c3", message.getMqFromqq());
        map.put("c4", jyTime(message.getMqFromid(), message.getMqFromqq()));
        xsTemplate.tongyongPost("Api_Shutup", map);
        // 当倒数第二个人和倒数第一个人不是同一个人时 禁言倒数第二个人
        if (StringUtils.isNotBlank(ftQQ) && !message.getMqFromqq().equals(ftQQ)) {
            map.put("c3", ftQQ);
            map.put("c4", jyTime(message.getMqFromid(), ftQQ));
            xsTemplate.tongyongPost("Api_Shutup", map);
        }
    }

    public int jyTime(String id, String qq) {
        String jinyanCountKey = RedisConstant.JINYAN_COUNT +  id + ":" + qq;
        // 判断在redis中是否有key值
        Boolean redisKey = redisTemplate.hasKey(jinyanCountKey);
        // 如果没有key值，对他进行添加到redis中
        if (redisKey) {
            // 禁言次数
            Integer count = Integer.valueOf(redisTemplate.opsForValue().get(jinyanCountKey));
            count++;
            redisTemplate.opsForValue().set(jinyanCountKey, String.valueOf(count), 1, TimeUnit.DAYS);
            if (count == 2) {
                // 第二次禁言30分钟
                return 1800;
            } else if (count == 3) {
                // 最后一次禁言1天
                return 86400;
            }
        }
        // 存入key
        redisTemplate.opsForValue().set(jinyanCountKey, "1", 1, TimeUnit.DAYS);
        // 第一次禁言600秒
        return 600;
    }

    /**
     * 记录一条禁言日志
     * @param message
     * @return
     */
    @Override
    public ReqResult jinyanlog(MyQQMessage message) {
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
        xsTemplate.sendMsgEx(message.getMqRobot(), 0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, text);
        return new ReqResult(1);
    }

    @Override
    public ReqResult mgc(MyQQMessage message) {
        // 是否开启撤回功能
        if (!redisTemplate.hasKey(RedisConstant.CHEHUI)) {
            return null;
        }
        try {
            Set<String> strings = MgcUtil.loadFile();
            strings.forEach(s -> {
                if (StringUtils.isNotBlank(s) && message.getMqMsg().contains(s)) {
                    log.info("撤回消息:{}",message.getMqMsg());
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("c1", message.getMqRobot());
                    hashMap.put("c2", message.getMqFromid());
                    // 撤回消息
                    hashMap.put("c3", message.getMqMsgseq());
                    hashMap.put("c4", message.getMqMsgid());
                    xsTemplate.tongyongPost("Api_WithdrawMsg", hashMap);
                }
            });
        } catch (Exception e) {
            log.info("撤回出错:{}", e.getMessage());
        }

        return null;
    }
}
