package top.flagshen.myqq.database.forbidden.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 禁言记录表
 * </p>
 *
 * @author 17460
 * @since 2022-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("forbidden_log")
public class ForbiddenLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "log_id", type = IdType.AUTO)
    private Integer logId;

    /**
     * 被禁言的qq号
     */
    private String qqNum;

    /**
     * 被禁言的群
     */
    private String groupNum;

    private Date createTime;


}
