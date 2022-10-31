package top.flagshen.myqq.dao.userinfo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 决斗记录表
 * </p>
 *
 * @author 17460
 * @since 2022-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_juedou")
public class UserJuedouDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    private String qq;
    private String groupNum;

    /**
     * 决斗胜场
     */
    private Integer winCount;

    /**
     * 一周决斗胜场
     */
    private Integer weekCount;

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


}
