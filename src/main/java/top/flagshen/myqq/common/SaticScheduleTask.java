package top.flagshen.myqq.common;

import catcode.CatCodeUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import top.flagshen.myqq.common.cache.RedisConstant;
import top.flagshen.myqq.dao.updatereminder.entity.UpdateReminderDO;
import top.flagshen.myqq.dao.userinfo.entity.UserJuedouDO;
import top.flagshen.myqq.entity.common.NovelAttribute;
import top.flagshen.myqq.entity.common.TianXingResult;
import top.flagshen.myqq.service.group.IGroupMsgService;
import top.flagshen.myqq.service.updatereminder.IUpdateReminderService;
import top.flagshen.myqq.service.userinfo.IUserJuedouService;
import top.flagshen.myqq.util.HttpApiUtil;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@Slf4j
public class SaticScheduleTask {

    @Autowired
    private IGroupMsgService groupMsgService;

    @Autowired
    private IUpdateReminderService updateReminderService;

    @Autowired
    private IUserJuedouService userJuedouService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final List<String> GROUP_NUM = Arrays.asList("xxx");
    private static final List<String> GROUP_GL = Arrays.asList("xxx");

    //群发更新信息
    @Scheduled(cron = "0/5 * * * * ?")
    private void configureTasks() {
        Document document;
        try {
            // 已有的章节名
            String oldChapter = redisTemplate.opsForValue().get(RedisConstant.CHAPTER_NAME);
            // 最新章节数
            String chapterNum = redisTemplate.opsForValue().get(RedisConstant.CHAPTER_NUM);
            document = Jsoup.connect("https://m.qidian.com/book/1029391348.html").get();
            Elements select = document.select("a[class='charpter-link auto-report']");
            if (CollectionUtils.isEmpty(select)) {
                return;
            }
            Element e = select.get(0);
            NovelAttribute novelAttribute = new NovelAttribute();
            String newChapter = "";// 新的章节名
            String fictionChapter = e.text();
            if (fictionChapter.contains("最新章节 ")) {
                fictionChapter = fictionChapter.substring(5);
            }
            String fictionUrl = e.attr("abs:href");
            String replaceUrl = fictionUrl.replace("m.qidian.com/book","vipreader.qidian.com/chapter");
            novelAttribute.setFictionUrl(replaceUrl);// 链接
            novelAttribute.setFictionChapter(fictionChapter);// 标题
            String fictionAlt = e.attr("abs:alt");
            fictionAlt = fictionAlt.substring(fictionAlt.lastIndexOf(": ") + 2);
            novelAttribute.setFictionNumber(fictionAlt);// 字数
            newChapter = fictionChapter;
            int zhang = newChapter.indexOf("章");
            int di = newChapter.indexOf("第");
            // 判断是否有 第章两个字，如果是请假可以走后面的流程
            if (di == 0 && zhang > 0) {
                String newChapterNum = newChapter.substring(1, zhang);
                // 如果最新章节数量小于等于老的章节数量，结束
                if (chapterNum != null && Integer.parseInt(newChapterNum) <= Integer.parseInt(chapterNum)) {
                    return;
                }
                // 并且记录最新章节数
                redisTemplate.opsForValue().set(RedisConstant.CHAPTER_NUM, newChapterNum);
            }
            // 如果两次章节名不同，就说明更新了
            if (!newChapter.equals(oldChapter)) {
                if (newChapter.contains("不是请假")) {
                    groupMsgService.batchSendMsg(novelAttribute, RedisConstant.LATER_TEMPLATE);
                } else if (newChapter.contains("请假")) {
                    groupMsgService.batchSendMsg(novelAttribute, RedisConstant.VACATION_TEMPLATE);
                } else {
                    groupMsgService.batchSendMsg(novelAttribute, RedisConstant.TEMPLATE);
                }
                // 把新的章节名存入缓存
                redisTemplate.opsForValue().set(RedisConstant.CHAPTER_NAME, newChapter);
            }
        } catch (Exception e) {
            log.info("抓取更新异常:{}",e.getMessage());
        }
    }

    //每天中午12点校验预约了更新提醒的人里哪些已经退群了，退群了的就移除
    @Scheduled(cron = "0 0 12 * * ? ")
    private void checkUpdateReminder() {
        for (String groupNum : GROUP_NUM) {
            // 查询该群预约了更新提醒的人
            List<UpdateReminderDO> updateReminderDOList = updateReminderService.list(new LambdaQueryWrapper<UpdateReminderDO>()
                    .eq(UpdateReminderDO::getGroupNum, groupNum));
            if (CollectionUtils.isEmpty(updateReminderDOList)) {
                continue;
            }
            List<String> groupMemberNums = groupMsgService.getGroupMemberNums(groupNum);
            updateReminderDOList.forEach(updateReminderDO -> {
                // 如果该qq号不在群成员中，就删除
                if (!groupMemberNums.contains(updateReminderDO.getQqNum())) {
                    updateReminderService.removeById(updateReminderDO.getReminderId());
                }
            });
        }
    }

    //每天早上9点，判断有没有起床
    @Scheduled(cron = "0 0 9 * * ?")
    private void getUp() {
        if (redisTemplate.hasKey(RedisConstant.GET_UP)) {
            // 如果起床了，清除key
            redisTemplate.delete(RedisConstant.GET_UP);
        } else {
            // 如果还没起床，就要扣分
            int scoreTotal = Integer.parseInt(redisTemplate.opsForValue().get(RedisConstant.STUDY_SCORE));
            scoreTotal = scoreTotal - 5;
            redisTemplate.opsForValue().set(RedisConstant.STUDY_SCORE, String.valueOf(scoreTotal));
            groupMsgService.sendMsg("531753196", "懒宝，9点啦还不起床，扣分扣分，扣5分\n" + "当前总分：" + scoreTotal);
        }
    }

    //每天早上7点早安
    @Scheduled(cron = "0 0 7 * * ?")
    private void goodMorning() {
        String content = getContent("https://api.tianapi.com/zaoan/index?key=xxx");
        if (StringUtils.isBlank(content)) return;
        if (!content.contains("早安")) {
            content += "早安！";
        }
        groupMsgService.sendMsg("531753196", content);
    }

    //每天早上10点，查7日金价
    @Scheduled(cron = "0 30 11 * * ?")
    private void jinJia() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK, -7);
        String url = "https://api.jijinhao.com/quoteCenter/history.htm?code=JO_42660&style=3&startDate=" +
                dateFormat.format(calendar.getTime()) +
                "&endDate=" + new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String result = HttpApiUtil.httpClientCommon(HttpMethodConstants.HTTP_GET,
                url, null);
        result = result.substring(16);
        JSONObject jsonObject = JSON.parseObject(result);
        if (jsonObject == null) {
            return;
        }
        JSONArray dataArr = JSON.parseArray(jsonObject.get("data").toString());
        if (dataArr == null) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("7日金价：");
        for(int j = 0;j<dataArr.size();j++){
            JSONObject obj = dataArr.getJSONObject(j);
            sb.append("\r\n日期：")
                    .append(dateFormat.format(new Date(Long.valueOf(obj.get("time").toString()))))
                    .append(" 价格：").append(obj.get("q1"));
        }
        groupMsgService.sendMsg("531753196", sb.toString());
    }

    //每天凌晨12点评选决斗冠军
    @Scheduled(cron = "0 0 0 * * ? ")
    private void juedouWang() {
        UserJuedouDO winner = userJuedouService.getOne(new LambdaQueryWrapper<UserJuedouDO>()
                .ne(UserJuedouDO::getWinCount, 0)
                .eq(UserJuedouDO::getGroupNum, "423430656")
                .notIn(UserJuedouDO::getQq, GROUP_GL)
                .orderByDesc(UserJuedouDO::getWinCount)
                .orderByAsc(UserJuedouDO::getModifyTime)
                .last("limit 1"));
        if (winner == null) {
            return;
        }

        CatCodeUtil util = CatCodeUtil.INSTANCE;
        String at = util.toCat("at", "code="+winner.getQq());
        try {
            groupMsgService.sendMsg("423430656", "〓〓今日决斗冠军已经诞生!〓〓\n〓〓今日决斗冠军已经诞生!〓〓\n〓〓今日决斗冠军已经诞生!〓〓\n"
                    + at + " 以" + winner.getWinCount() + "场胜利的记录获得「决斗冠军」称号，恭喜恭喜！");
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        // 今日胜场不为0的，添加进周胜利场
        userJuedouService.updateWeekWin();

        // 清空胜利场次
        userJuedouService.update(new LambdaUpdateWrapper<UserJuedouDO>().set(UserJuedouDO::getWinCount, 0));

        // 发当日公告
        List<UserJuedouDO> weekList = userJuedouService.list(new LambdaQueryWrapper<UserJuedouDO>()
                .ne(UserJuedouDO::getWeekCount, 0)
                .eq(UserJuedouDO::getGroupNum, "423430656")
                .notIn(UserJuedouDO::getQq, GROUP_GL)
                .orderByDesc(UserJuedouDO::getWeekCount)
                .orderByAsc(UserJuedouDO::getModifyTime).last("limit 10"));
        GroupMemberInfo memberInfo = groupMsgService.getMemberInfo("423430656", winner.getQq());
        String title = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")) + "决斗结果公布";
        StringBuilder text = new StringBuilder("〓〓今日决斗冠军已经诞生!〓〓\n〓〓今日决斗冠军已经诞生!〓〓\n〓〓今日决斗冠军已经诞生!〓〓\n"
                + memberInfo!=null ? memberInfo.getAccountRemarkOrNickname() : winner.getQq() + " 以" + winner.getWinCount() + "场胜利的记录获得「决斗冠军」称号，恭喜恭喜！\n");
        text.append("当前周胜场排行\n");
        for (int i = 0; i < weekList.size(); i++) {
            text.append("第").append(i+1).append("位胜场数:").append(weekList.get(i).getWeekCount()).append("\n");
        }
        groupMsgService.sendGroupNotice("423430656", title, text.toString());
    }

    //每周结算
    @Scheduled(cron = "30 0 0 * * 1")
    private void weekJuedouWang() {
        // 获取周胜最多的人
        List<UserJuedouDO> list = userJuedouService.list(new LambdaQueryWrapper<UserJuedouDO>()
                .ne(UserJuedouDO::getWeekCount, 0)
                .eq(UserJuedouDO::getGroupNum, "423430656")
                .notIn(UserJuedouDO::getQq, GROUP_GL)
                .orderByDesc(UserJuedouDO::getWeekCount)
                .orderByAsc(UserJuedouDO::getModifyTime).last("limit 10"));

        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        List<String> chenghao = Arrays.asList("「决斗王」\n", "「决斗宗师」\n", "「决斗大师」\n");
        StringBuffer content = new StringBuffer();
        CatCodeUtil util = CatCodeUtil.INSTANCE;

        int size = list.size() < 3 ? list.size() : 3;
        for (int i=0; i < size; i++) {
            UserJuedouDO userJuedouDO = list.get(i);
            String at = util.toCat("at", "code="+userJuedouDO.getQq());
            content.append(at).append(" 本周总胜场为:").append(userJuedouDO.getWeekCount())
                    .append(" 获得称号").append(chenghao.get(i));
        }

        try {
            groupMsgService.sendMsg("423430656", "〓〓本周决斗大会结果公布!〓〓\n〓〓本周决斗大会结果公布!〓〓\n〓〓本周决斗大会结果公布!〓〓\n"
                    + content.toString() + "恭喜以上获奖玩家！祝福他们前程似锦，战无不胜，武运昌隆！");
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        StringBuilder text = new StringBuilder();
        String title = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")) + "决斗大会总结果公布";
        for (int i=0; i < list.size(); i++) {
            UserJuedouDO userJuedouDO = list.get(i);
            GroupMemberInfo memberInfo = groupMsgService.getMemberInfo("423430656", userJuedouDO.getQq());
            if (i < 3) {
                if (memberInfo != null) {
                    text.append(memberInfo.getAccountRemarkOrNickname());
                }
                text.append("(").append(userJuedouDO.getQq()).append(")").append(" 本周总胜场为:").append(userJuedouDO.getWeekCount())
                        .append(" 获得称号").append(chenghao.get(i));
                if (i == 2) {
                    text.append("恭喜以上获奖玩家！祝福他们前程似锦，战无不胜，武运昌隆！\n");
                }
            } else {
                if (memberInfo != null) {
                    text.append(memberInfo.getAccountRemarkOrNickname());
                }
                text.append("(").append(userJuedouDO.getQq()).append(")").append(" 本周总胜场为:").append(userJuedouDO.getWeekCount()).append("\n");
            }
        }
        groupMsgService.sendGroupNotice("423430656", title, "〓〓本周决斗大会结果公布!〓〓\n〓〓本周决斗大会结果公布!〓〓\n〓〓本周决斗大会结果公布!〓〓\n"
                + text.toString());
        // 清空周胜利场次
        userJuedouService.update(new LambdaUpdateWrapper<UserJuedouDO>().set(UserJuedouDO::getWeekCount, 0));

    }

    /**
     * 接入天行数据api
     * @param url
     * @return
     */
    private String getContent(String url) {
        String result = HttpApiUtil.httpClientCommon(HttpMethodConstants.HTTP_GET,
                url, null);
        JSONObject jsonObject = JSON.parseObject(result);
        if (jsonObject != null) {
            TianXingResult goodMorningResult = jsonObject.toJavaObject(TianXingResult.class);
            if (goodMorningResult.getCode().equals(200)) {
                return goodMorningResult.getNewslist().get(0).getContent();
            }
        }
        return null;
    }
}