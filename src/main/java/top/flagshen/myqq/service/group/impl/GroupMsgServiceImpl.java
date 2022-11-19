package top.flagshen.myqq.service.group.impl;

import catcode.CatCodeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.api.message.events.GroupMemberIncrease;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import love.forte.simbot.api.sender.MsgSender;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.flagshen.myqq.common.RobotTemplate;
import top.flagshen.myqq.common.cache.RedisConstant;
import top.flagshen.myqq.dao.updatereminder.entity.UpdateReminderDO;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.entity.common.NovelAttribute;
import top.flagshen.myqq.service.blacklist.IBlacklistService;
import top.flagshen.myqq.service.group.IGroupMsgService;
import top.flagshen.myqq.service.strategy.context.OperationStrategyContext;
import top.flagshen.myqq.service.strategy.context.PlayStrategyContext;
import top.flagshen.myqq.service.strategy.context.StudyStrategyContext;
import top.flagshen.myqq.service.updatereminder.IUpdateReminderService;
import top.flagshen.myqq.util.ContentUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class GroupMsgServiceImpl implements IGroupMsgService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RobotTemplate robotTemplate;

    private static final List<String> manageGroup = Arrays.asList("xxx");

    @Autowired
    private OperationStrategyContext operationStrategyContext;

    @Autowired
    private PlayStrategyContext playStrategyContext;

    @Autowired
    private StudyStrategyContext StudyStrategyContext;

    @Autowired
    private IBlacklistService blacklistService;

    @Autowired
    private IUpdateReminderService updateReminderService;

    // 设置更新提醒时每一批@20个人
    private static final int batchSize = 20;

    @Override
    public void manageGroupMsg(String msg, MyQQMessage message) {
        // 获取第一个空格的位置
        int i = msg.indexOf(" ");
        String operate;
        if (i < 0) {
            // 如果是单项的指令，msg就是对应的操作，例如/查看模板  /查看效果
            operate = msg;
        } else {
            // 如果是用空格隔开的多项的指令，例如/修改模板 xxx  /修改name xxx,就从空格开始断开
            operate = msg.substring(0, i);
            // 提取消息中空格后的内容重新存入
            message.setMqMsg(msg.substring(i + 1));
        }
        // 一些特殊操作，只开放给管理群和测试群
        if (operationStrategyContext.operation(message, operate)) {
            return;
        }
        // 学习打卡群
        if (StudyStrategyContext.study(message, operate)) {
            return;
        }
        // 一些玩法，占卜和改命，只开放给书友2群和舵主群，和管理测试群
        if (playStrategyContext.play(message, operate)) {
            return;
        }
        return;
    }


    @Override
    public void batchSendMsg(NovelAttribute novelAttribute, String templateKey) {
        String template = redisTemplate.opsForValue().get(templateKey);
        String name = redisTemplate.opsForValue().get(RedisConstant.NAME);
        Map valuesMap = new HashMap();
        valuesMap.put("name", name);
        valuesMap.put("title", novelAttribute.getFictionChapter());
        String url = novelAttribute.getFictionUrl();
        url = url.substring(0, url.length()-5);
        valuesMap.put("url", url);
        valuesMap.put("menu", ContentUtil.getRandomMenu());
        valuesMap.put("number", ContentUtil.getNumber(novelAttribute.getFictionNumber()));
        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String content= sub.replace(template);
        for (String groupQQ: manageGroup) {
            //给开启更新提醒的人发送更新消息
            if (RedisConstant.TEMPLATE.equals(templateKey)) {
                updateReminder(groupQQ);
            }
            //发送更新公告
            robotTemplate.sendMsgEx("xxx", groupQQ, content);
        }
    }

    @Override
    public void sendMsg(String groupId, String content) {
        robotTemplate.sendMsgEx("xxx", groupId, content);
    }

    @Override
    public void sendGroupNotice(String groupId, String title, String content) {
        robotTemplate.sendGroupNotice("1462152250", groupId, title, content);
    }

    @Override
    public GroupMemberInfo getMemberInfo(String groupId, String qq) {
        return robotTemplate.getMemberInfo(groupId, qq);
    }

    @Override
    public List<String> getGroupMemberNums(String group) {
        return robotTemplate.getGroupMemberList(group);
    }

    private void updateReminder(String groupNum) {
        try {
            // 获取群成员
            List<String> groupMemberList = robotTemplate.getGroupMemberList(groupNum);
            // 获取该群开启提醒的成员
            List<UpdateReminderDO> reminderList = updateReminderService.list(new LambdaQueryWrapper<UpdateReminderDO>()
                    .eq(UpdateReminderDO::getGroupNum, groupNum));
            if (CollectionUtils.isEmpty(reminderList)) {
                return;
            }
            StringBuffer sb = new StringBuffer();
            CatCodeUtil util = CatCodeUtil.INSTANCE;
            for (int i = 1; i <= reminderList.size(); i++) {
                // 群成员中包含才拼接 否则会报错
                if (groupMemberList.contains(reminderList.get(i-1).getQqNum())) {
                    sb.append(util.toCat("at", "code="+reminderList.get(i-1).getQqNum())).append(" ");
                }
                // 设置每一条消息上限20人
                if ((i != 1 && i % batchSize == 0) || i == reminderList.size()) {
                    robotTemplate.sendMsgEx("xxx", groupNum, sb.toString());
                    // 清空内容
                    sb.setLength(0);
                }
            }
        } catch (Exception e) {
            log.info("更新提醒异常：{}", e.getMessage());
        }

    }

    @Override
    public void ruqunMsg(GroupMemberIncrease groupMemberIncrease, MsgSender sender) {
        String groupCode = groupMemberIncrease.getGroupInfo().getGroupCode();
        String accountCode = groupMemberIncrease.getAccountInfo().getAccountCode();
        if ("xxx".equals(groupCode)) {
            return;
        }
        CatCodeUtil util = CatCodeUtil.INSTANCE;
        // 构建at
        String at = util.toCat("at", "code="+accountCode);
        String s = at + " 尊敬的预约玩家，欢迎加入404避难所！(*≧▽≦)" +
                "\r\n我是助理小柒，有什么不懂的不要问我，自己看群规哦o(≧v≦)o";
        sender.SENDER.sendGroupMsg(groupCode, s);

        String jinyanCountKey = RedisConstant.JINYAN_COUNT +  groupCode + ":" + accountCode;
        // 判断在redis中是否有key值
        if (redisTemplate.hasKey(jinyanCountKey)) {
            redisTemplate.opsForValue().set(jinyanCountKey, "3", 1, TimeUnit.DAYS);
            sender.SETTER.setGroupBan(groupCode, accountCode, 86400L);
        }

        String juedouloserKey = RedisConstant.JUEDOU_JINYAN +  groupCode + ":" + accountCode;
        // 判断在redis中是否有key值
        if (redisTemplate.hasKey(juedouloserKey)) {
            sender.SETTER.setGroupBan(groupCode, accountCode, Long.parseLong(redisTemplate.opsForValue().get(juedouloserKey)));
        }
    }

}
