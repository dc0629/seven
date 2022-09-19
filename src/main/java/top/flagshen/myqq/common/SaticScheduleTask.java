package top.flagshen.myqq.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
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
import top.flagshen.myqq.dao.updatereminder.entity.UpdateReminderDO;
import top.flagshen.myqq.entity.common.NovelAttribute;
import top.flagshen.myqq.entity.common.TianXingResult;
import top.flagshen.myqq.service.group.IGroupMsgService;
import top.flagshen.myqq.service.updatereminder.IUpdateReminderService;
import top.flagshen.myqq.util.HttpApiUtil;

import java.text.SimpleDateFormat;
import java.util.*;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@Slf4j
public class SaticScheduleTask {

    @Autowired
    private IGroupMsgService groupMsgService;

    @Autowired
    private IUpdateReminderService updateReminderService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    private static final List<String> GROUP_NUM = Arrays.asList("xxx");

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
            novelAttribute.setFictionUrl(fictionUrl);// 链接
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
                if (chapterNum != null && Integer.valueOf(newChapterNum) <= Integer.valueOf(chapterNum)) {
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

    //每天早上7点早安
    //@Scheduled(cron = "0 0 7 * * ?")
    private void goodMorning() {
        String content = getContent("https://api.tianapi.com/zaoan/index?key=xxx");
        if (StringUtils.isBlank(content)) return;
        if (!content.contains("早安")) {
            content += "早安！";
        }
        //发送群消息
        /*robotTemplate.sendMsgEx("1462152250",
                0, TypeConstant.MSGTYPE_GROUP,
                "531753196", null, content);*/

    }

    //每天晚上11点晚安
    //@Scheduled(cron = "0 30 23 * * ?")
    private void goodEvening() {
        String content = getContent("https://api.tianapi.com/wanan/index?key=xxx");
        if (StringUtils.isBlank(content)) return;
        if (!content.contains("晚安")) {
            content += "晚安！";
        }

        //发送群消息
        /*robotTemplate.sendMsgEx("1462152250",
                0, TypeConstant.MSGTYPE_GROUP,
                "531753196", null, content);*/
    }

    //每天晚上12点清空占卜缓存
    @Scheduled(cron = "0 0 0 * * ? ")
    private void cleanDivination() {
        Set<String> keys = redisTemplate.keys(RedisConstant.DIVINATION+"*");
        if (CollectionUtils.isNotEmpty(keys)) {
            redisTemplate.delete(keys);
        }
        Set<String> keys2 = redisTemplate.keys(RedisConstant.GAIMING+"*");
        if (CollectionUtils.isNotEmpty(keys2)) {
            redisTemplate.delete(keys2);
        }
    }

    //每天中午12点校验预约了更新提醒的人里哪些已经退群了，退群了的就移除
    @Scheduled(cron = "0 0 12 * * ? ")
    private void checkUpdateReminder() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("c1", "xxx");
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