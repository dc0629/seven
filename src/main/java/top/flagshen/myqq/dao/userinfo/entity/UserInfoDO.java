package top.flagshen.myqq.dao.userinfo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
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
@EqualsAndHashCode(callSuper = false)
@TableName("user_info")
public class UserInfoDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * qq号
     */
    @TableId(value = "qq_num", type = IdType.INPUT)
    private String qqNum;

    /**
     * 绑定了的微信小程序openId
     */
    private String openId;

    /**
     * 密码
     */
    private String password;

    /**
     * 类型，力量系STRENGTH，敏捷系AGILE，感知系PERCEPTION，智力系INTELLIGENCE，体质系CONSTITUTION
     */
    private String userType;

    /**
     * 昵称
     */
    private String nickName;

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
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date modifyTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 是否测试
     */
    private Integer isTest;

}
