package top.flagshen.myqq.dao.userinfo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户熟练度表
 * </p>
 *
 * @author 17460
 * @since 2022-09-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_proficiency")
public class UserProficiencyDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "proficiency_id", type = IdType.AUTO)
    private Integer proficiencyId;

    /**
     * qq号
     */
    private String qqNum;

    /**
     * 熟练度类型
     */
    private String proficiencyType;

    /**
     * 熟练度等级
     */
    private Integer proficiencyLevel;

    /**
     * 熟练度经验值
     */
    private Double proficiencyExperience;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date modifyTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;


}
