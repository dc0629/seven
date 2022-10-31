package top.flagshen.myqq.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;
import top.flagshen.myqq.entity.common.NovelAttribute;

import java.util.*;

/**
 * 生成文本类
 */
@Slf4j
public final class ContentUtil {

    private static final List<String> MENU_LIST1 = Arrays.asList("东坡肉","酱大骨","变异猎犬肉排","泥沼蟹煎蛋","蒜泥死爪","辐射蝎蛋煲","双头牛排汉堡","烤变异鹿腿","蕨菜炒变异猪肉","油炸泥沼蟹肉饼","神秘的肉卷核子蟹","伊蕾娜特制风味肉干","管理者大人特制肉饼","避难所特色双头牛肉拉面","企业A级营养合剂","企业S级营养合剂","彷徨沼泽变异森蚺烤肉","学院特色油炸豹蛛女王","清蒸变异鲶鱼","麻辣箭蛙","油闷裂头蚁","弹壳谷焖饭","死亡兵团01号单兵口粮","81号钢铁厂员工套餐","白熊骑士团寿司","燃烧军团03号单兵口粮","变异野猪炖洞穴蘑菇","变异野猪肉煎饺","变异猎犬狗肉火锅");
    private static final List<String> MENU_LIST2 = Arrays.asList("泥沼蟹蛋糕","鬣蜥烤肉串","死爪蛋炒饭","油炸魔鬼蛾卵","烤辐射蟑螂","泥沼蟹土豆泥","美味魔鬼蛋","神奇鼹鼠肉蘸酱","寄居蟹酿洞穴蘑菇","鬣蜥肉串","死亡爪蛋卷","避难所科技辣椒肉酱","避难所特色双头牛肉","白开水","避难所科技棒棒糖","烩致幻蘑菇","佛跳墙(废土特供)","油炸裂头蚁卵","贝特街烤铃薯","变异果蛋糕","管理者特供爬行者刺身","玩家特制韭菜炒蛋","丛林兵团高能压缩饼干","弹壳谷手抓饼","巨石城特色卤猪头","鸦鸦特制蘑菇沙拉(限量)","丧尸刺身(限体质系食用)","羊角薯泥","垃圾君的蜥尾烤串");
    private static final List<String> MENU_LIST3 = Arrays.asList("鸦鸦的特色蘑菇汤","核子可乐","鬣蜥汤","变异鹿炖汤","樱桃味核子可乐","双头牛炖汤","铃薯汤","巴奇的特制变异黏菌汤","奇异果汁","变异水蛭浓汤","小柒特制薄荷味电浆","被污染的工业酒精(稀释)","被污染的工业酒精(未稀释)","保存完好的战前红酒","避难所特色拉面汤","小鱼的爱心浓汤","变异鲶鱼汤","资本家的悔恨泪水","藤藤的爱心奶茶","老板娘的自制啤酒","战熊的爱心甜汤(芝麻糊特供)","小柒特制草莓味电浆","昏睡红茶","美好时光致幻剂","变种人部落神秘浓汤","松子炖鱼汤","地精科技浓缩乙醇(限体质系食用)","殉爆者肉汤(限体质系食用)","小柒特制超频电浆(限智力系食用)");
    private static final List<String> ABILITY_LIST = Arrays.asList("力量系","敏捷系","体质系","智力系","感知系");
    private static final List<String> ACTION_LIST = Arrays.asList("打窝","探索地图","跑商","逛酒吧","制作武器"
            ,"驾驶飞机","烹饪美食","出售银币","驯服宠物","练习射击","被管理者割韭菜","开坦克","骑死爪","采蘑菇","水论坛","写攻略",
            "伐木","建造","种地","找cp","刷管理者好感度","蹬三轮","开飞艇","当神棍","寻宝");
    private static final List<String> YIZHONG_LIST = Arrays.asList("异种序列-蜥蜴","异种序列-猫娘","异种序列-猪头人",
            "异种序列-老鼠","异种序列-大白熊","异种序列-蘑菇人","异种序列-狗头人","异种序列-变色龙");

    public static String zhanbu(int yun) {
        Random random = new Random();
        String yi = ACTION_LIST.get(random.nextInt(ACTION_LIST.size()));

        String ji = ACTION_LIST.get(random.nextInt(ACTION_LIST.size()));

        while (ji.equals(yi)) {
            ji = ACTION_LIST.get(random.nextInt(ACTION_LIST.size()));
        }

        String remark = "";
        if (yun > 94) {
            remark = "是欧皇(*′▽｀)ノノ";
        } else if ("开飞机".equals(ji)) {
            remark = "亡牌飞行员准备出动";
        } else if ("打窝".equals(ji)) {
            remark = "上钩的鱼溜走了哦~";
        } else if ("采蘑菇".equals(ji)) {
            remark = "要被变异水蛭吃掉了";
        } else if ("写攻略".equals(yi)) {
            remark = "新版本更新公告就靠你啦";
        }

        String ability = "";
        int xulie = (int) (Math.random()*100 + 1);
        if (xulie > 95) {
            ability = YIZHONG_LIST.get(random.nextInt(YIZHONG_LIST.size()));
        } else {
            ability = ABILITY_LIST.get(random.nextInt(ABILITY_LIST.size()));
        }

        String text = "幸运值：" + yun +
                "\r\n幸运序列：" + ability +
                "\r\n宜："+ yi + " 忌：" + ji;

        if (!StringUtils.isEmpty(remark)) {
            text = text + "\r\n" + remark;
        }
        return text;
    }

    /**
     * 获取运气值
     * @return
     */
    public static int getYun() {
        int yun = (int) (Math.random()*100);
        if (yun < 30) {
            // 30%的概率幸运值是10-39
            yun = (int)(Math.random()*30 + 10);
        } else if  (yun < 90) {
            // 60%的概率幸运值是40-79
            yun = (int)(Math.random()*40 + 40);
        } else {
            // 10%的概率幸运值是80-100
            yun = (int)(Math.random()*21 + 80);
        }
        return yun;
    }

    public static String getContent(String template, String name, String title, String url, String number) {
        Map valuesMap = new HashMap();
        valuesMap.put("name", name);
        Document document;
        try {
            document = Jsoup.connect("https://m.qidian.com/book/1029391348.html").get();
            Elements select = document.select("a[class='charpter-link auto-report']");
            NovelAttribute novelAttribute = new NovelAttribute();
            if (!CollectionUtils.isEmpty(select)) {
                Element e = select.get(0);
                String fictionUrl = e.attr("abs:href");
                String replaceUrl = fictionUrl.replace("m.qidian.com/book","vipreader.qidian.com/chapter");
                novelAttribute.setFictionUrl(replaceUrl);
                String fictionChapter = e.text();
                if (fictionChapter.contains("最新章节 ")) {
                    fictionChapter = fictionChapter.substring(5);
                }
                novelAttribute.setFictionChapter(fictionChapter);
                // 字数
                String fictionAlt = e.attr("abs:alt");
                fictionAlt = fictionAlt.substring(fictionAlt.lastIndexOf(": ") + 2);
                novelAttribute.setFictionNumber(fictionAlt);
                novelAttribute.setFictionUrl(novelAttribute.getFictionUrl().substring(0, novelAttribute.getFictionUrl().length()-5));
            }
            valuesMap.put("title", novelAttribute.getFictionChapter() != null ? novelAttribute.getFictionChapter() : title);
            valuesMap.put("url", novelAttribute.getFictionUrl() != null ? novelAttribute.getFictionUrl() : url);
            valuesMap.put("menu", getRandomMenu());
            valuesMap.put("number", getNumber(novelAttribute.getFictionNumber() != null ? novelAttribute.getFictionNumber() : number));
            StringSubstitutor sub = new StringSubstitutor(valuesMap);
            return sub.replace(template);
        } catch (Exception e) {
            log.info("生成公告错误:{}" + e.getMessage());
        }
        return null;
    }

    // 获取随机菜单
    public static String getRandomMenu() {
        //随机对象
        Random random = new Random();
        return MENU_LIST1.get(random.nextInt(MENU_LIST1.size())) +
                "，" + MENU_LIST2.get(random.nextInt(MENU_LIST2.size())) +
                "，" + MENU_LIST3.get(random.nextInt(MENU_LIST3.size()));
    }

    public static String getNumber(String number) {
        int num = Integer.parseInt(number);
        int k = num/1000;
        int d = (num - k*1000)/100;
        if (d > 0) {
            return k + "." + d + "k";
        }
        return k + "k";
    }

}
