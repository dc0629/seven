package top.flagshen.myqq.entity.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

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
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifyTime;

}
