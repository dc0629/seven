package top.flagshen.myqq.common;

import com.alibaba.fastjson.JSONObject;
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
import top.flagshen.myqq.entity.TianXingResult;
import top.flagshen.myqq.entity.NovelAttribute;
import top.flagshen.myqq.service.IGroupMsgService;
import top.flagshen.myqq.util.HttpApiUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@Slf4j
public class SaticScheduleTask {

    @Autowired
    private IGroupMsgService groupMsgService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final XiaoshenTemplate xsTemplate;

    private static final List<String> GROUP_NUM = Arrays.asList("xxx","xxx");

    public SaticScheduleTask(XiaoshenTemplate xsTemplate) {
        this.xsTemplate = xsTemplate;
    }

    //群发更新信息
    @Scheduled(cron = "0/5 * * * * ?")
    private void configureTasks() {
        Document document;
        try {
            // 已有的章节名
            String oldChapter = redisTemplate.opsForValue().get(RedisConstant.CHAPTER_NAME);
            document = Jsoup.connect("https://m.qidian.com/book/1029391348.html").get();
            Elements select = document.select("div[class='category-item-container']>a");
            if (CollectionUtils.isEmpty(select)) {
                return;
            }
            Element e = select.get(0);
            NovelAttribute novelAttribute = new NovelAttribute();
            String newChapter = "";// 新的章节数
            String fictionChapter = e.text();
            String fictionUrl = e.attr("abs:href");
            novelAttribute.setFictionUrl(fictionUrl);// 链接
            novelAttribute.setFictionChapter(fictionChapter);// 标题
            String fictionAlt = e.attr("abs:alt");
            fictionAlt = fictionAlt.substring(fictionAlt.lastIndexOf(": ") + 2);
            novelAttribute.setFictionNumber(fictionAlt);// 字数
            newChapter = fictionChapter;
            // 如果两次章节数不同，就说明更新了
            if (!newChapter.equals(oldChapter)) {
                if (newChapter.contains("不是请假")) {
                    groupMsgService.batchSendMsg(novelAttribute, RedisConstant.LATER_TEMPLATE);
                } else if (newChapter.contains("请假")) {
                    groupMsgService.batchSendMsg(novelAttribute, RedisConstant.VACATION_TEMPLATE);
                } else {
                    groupMsgService.batchSendMsg(novelAttribute, RedisConstant.TEMPLATE);
                }
                // 把新的数量存入缓存
                redisTemplate.opsForValue().set(RedisConstant.CHAPTER_NAME, newChapter);
            }
        } catch (Exception e) {
            log.info("抓取更新异常:{}",e.getMessage());
        }
    }

    //每天早上7点早安
    @Scheduled(cron = "0 0 7 * * ?")
    private void goodMorning() {
        String content = getContent("https://api.tianapi.com/zaoan/index?key=5a8f9b5cc21c2edfc17562d4ac5a1019");
        if (StringUtils.isBlank(content)) return;
        for (String num : GROUP_NUM) {
            //发送群消息
            xsTemplate.sendMsgEx("444",
                    0, TypeConstant.MSGTYPE_GROUP,
                    num, null, content);
        }
    }

    //每天晚上11点晚安
    @Scheduled(cron = "0 30 23 * * ?")
    private void goodEvening() {
        String content = getContent("https://api.tianapi.com/wanan/index?key=5a8f9b5cc21c2edfc17562d4ac5a1019");
        if (StringUtils.isBlank(content)) return;
        for (String num : GROUP_NUM) {
            //发送群消息
            xsTemplate.sendMsgEx("444",
                    0, TypeConstant.MSGTYPE_GROUP,
                    num, null, content);
        }
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
            //发送群消息
            xsTemplate.sendMsgEx("444",
                    0, TypeConstant.MSGTYPE_GROUP,
                    "55", null, "懒宝，9点啦还不起床，扣分扣分，扣5分\n" + "当前总分：" + scoreTotal);
        }

    }

    /**
     * 接入天行数据api
     * @param url
     * @return
     */
    private String getContent(String url) {
        JSONObject jsonObject = HttpApiUtil.httpClientCommon(HttpMethodConstants.HTTP_GET,
                url, null);
        if (jsonObject != null) {
            TianXingResult goodMorningResult = jsonObject.toJavaObject(TianXingResult.class);
            if (goodMorningResult.getCode().equals(200)) {
                return goodMorningResult.getNewslist().get(0).getContent();
            }
        }
        return null;
    }
}