package top.flagshen.myqq.dao.blacklist.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 黑名单
 * </p>
 *
 * @author 邓超
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("blacklist")
public class BlacklistDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "qq_num", type = IdType.INPUT)
    private String qqNum;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


}
