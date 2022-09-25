package top.flagshen.myqq.entity.userinfo.resp;

import lombok.Data;

/**
 * <p>
 * 用户基本信息
 * </p>
 *
 * @author 17460
 * @since 2022-08-14
 */
@Data
public class UserInfoResp {

    /**
     * qq号
     */
    private String qqNum;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 类型，力量系,敏捷系,感知系,智力系,体质系
     */
    private String userType;

    /**
     * 力量
     */
    private Integer strength;

    /**
     * 敏捷
     */
    private Integer agile;

    /**
     * 感知
     */
    private Integer perception;

    /**
     * 智力
     */
    private Integer intelligence;

    /**
     * 体质
     */
    private Integer constitution;

    /**
     * 贡献度
     */
    private Integer contribution;

    /**
     * 银币
     */
    private Integer silverCoin;

    /**
     * 楚光好感度
     */
    private Integer goodOpinion;

    /**
     * 游戏理解
     */
    private Integer gameUnderstanding;

    /**
     * 经验值
     */
    private Integer empiricalValue;

    /**
     * 等级
     */
    private Integer level;

    /**
     * 今日占卜结果
     */
    private String zhanbuStr;

    /**
     * 今日赚钱结果
     */
    private String zhuanqianStr;

}
