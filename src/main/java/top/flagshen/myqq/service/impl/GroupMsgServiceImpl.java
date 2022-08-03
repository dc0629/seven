package top.flagshen.myqq.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.XiaoshenTemplate;
import top.flagshen.myqq.database.blacklist.entity.BlacklistDO;
import top.flagshen.myqq.database.blacklist.service.IBlacklistService;
import top.flagshen.myqq.entity.MyQQMessage;
import top.flagshen.myqq.entity.NovelAttribute;
import top.flagshen.myqq.entity.ReqResult;
import top.flagshen.myqq.service.IGroupMsgService;
import top.flagshen.myqq.strategy.context.OperationStrategyContext;
import top.flagshen.myqq.strategy.context.PlayStrategyContext;
import top.flagshen.myqq.strategy.context.StudyStrategyContext;
import top.flagshen.myqq.util.ContentUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GroupMsgServiceImpl implements IGroupMsgService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private final XiaoshenTemplate xsTemplate;

    private static final List<String> manageGroup = Arrays.asList("xxx","xxx");

    public GroupMsgServiceImpl(XiaoshenTemplate xsTemplate) {
        this.xsTemplate = xsTemplate;
    }

    @Autowired
    private OperationStrategyContext operationStrategyContext;

    @Autowired
    private PlayStrategyContext playStrategyContext;

    @Autowired
    private StudyStrategyContext StudyStrategyContext;

    @Autowired
    private IBlacklistService blacklistService;

    @Override
    public ReqResult manageGroupMsg(String msg, MyQQMessage message) {
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
            return new ReqResult();
        }
        // 学习打卡群
        if (StudyStrategyContext.study(message, operate)) {
            return new ReqResult();
        }
        // 查询是否在黑名单中
        BlacklistDO blacklist = blacklistService.getById(message.getMqFromqq());
        if (blacklist != null) {
            return new ReqResult(1);
        }
        // 一些玩法，占卜和改命，只开放给书友2群和舵主群，和管理测试群
        if (playStrategyContext.play(message, operate)) {
            return new ReqResult(1);
        }

        return new ReqResult(1);
    }


    @Override
    public ReqResult batchSendMsg(NovelAttribute novelAttribute, String templateKey) {
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
            //发送群消息
            xsTemplate.sendMsgEx("444",
                    0, TypeConstant.MSGTYPE_GROUP,
                    groupQQ, null, content);
        }

        return null;
    }

    @Override
    public ReqResult ruqunMsg(MyQQMessage message) {
        String s = "[@"+message.getMqPassiveqq()+"]" + " 尊敬的预约玩家，欢迎加入404避难所！(*≧▽≦)" +
                "\r\n我是助理小柒，有什么不懂的不要问我，自己看群规哦o(≧v≦)o";
        xsTemplate.sendMsgEx(message.getMqRobot(),
                0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, s);
        return null;
    }

}
