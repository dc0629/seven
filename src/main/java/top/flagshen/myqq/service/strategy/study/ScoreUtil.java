package top.flagshen.myqq.service.strategy.study;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.dao.props.dto.PropsTotal;
import top.flagshen.myqq.dao.props.entity.PropsDO;
import top.flagshen.myqq.service.props.IPropsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分数计算工具
 *
 */
@Component
public class ScoreUtil {

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    @Autowired
    private IPropsService propsService;

    /**
     * 得分计算
     * @param score 本次得分
     * @return
     */
    String scoreCalculation(int score) {
        StringBuffer sb = new StringBuffer();
        if (score > 0) {
            sb.append("恭喜获得积分").append(score).append("分\n");
        } else {
            sb.append("很遗憾积分").append(score).append("\n");
        }
        //总积分
        Integer scoreTotal = redisTemplate.opsForValue().get(RedisConstant.STUDY_SCORE);
        if (scoreTotal == null) {
            scoreTotal = score;
        } else {
            scoreTotal = scoreTotal + (score);
        }
        redisTemplate.opsForValue().set(RedisConstant.STUDY_SCORE, scoreTotal);
        List<PropsTotal> propsTotal = propsService.getPropsCount("333", null);
        Map<String, Integer> totalMap = new HashMap<>(8);
        if (propsTotal != null) {
            totalMap = propsTotal.stream().collect(Collectors.toMap(PropsTotal::getPropsName, PropsTotal::getTotal));
        }
        // 每150分得一张瑞幸咖啡兑换卡
        if (scoreTotal/150 > (totalMap.get("瑞幸咖啡兑换卡") != null ? totalMap.get("瑞幸咖啡兑换卡") : 0)) {
            addProps("瑞幸咖啡兑换卡");
            sb.append("获得道具瑞幸咖啡兑换卡\n");
        }
        // 每200分得一张零食兑换卡
        if (scoreTotal/200 > (totalMap.get("零食兑换卡") != null ? totalMap.get("零食兑换卡") : 0)) {
            addProps("零食兑换卡");
            sb.append("获得道具零食兑换卡\n");
        }
        // 每300分得一张蛋糕兑换卡
        if (scoreTotal/300 > (totalMap.get("蛋糕兑换卡") != null ? totalMap.get("蛋糕兑换卡") : 0)) {
            addProps("蛋糕兑换卡");
            sb.append("获得道具蛋糕兑换卡\n");
        }
        // 每600分得一张大餐兑换卡
        if (scoreTotal/600 > (totalMap.get("大餐兑换卡") != null ? totalMap.get("大餐兑换卡") : 0)) {
            addProps("大餐兑换卡");
            sb.append("获得道具大餐兑换卡\n");
        }
        // 每800分得一张请假卡
        if (scoreTotal/800 > (totalMap.get("请假卡") != null ? totalMap.get("请假卡") : 0)) {
            addProps("请假卡");
            sb.append("获得道具请假卡\n");
        }
        // 每1000分得一张礼物卡
        if (scoreTotal/1000 > (totalMap.get("礼物卡") != null ? totalMap.get("礼物卡") : 0)) {
            addProps("礼物卡");
            sb.append("获得道具礼物卡\n");
        }
        sb.append("当前总分：").append(scoreTotal);
        return sb.toString();
    }

    private void addProps(String propsName) {
        PropsDO propsDO = new PropsDO();
        propsDO.setQqNum("333");
        propsDO.setPropsName(propsName);
        propsService.save(propsDO);
    }
}
