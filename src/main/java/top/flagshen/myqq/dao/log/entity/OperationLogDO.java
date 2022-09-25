package top.flagshen.myqq.dao.log.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 操作日志表
 * </p>
 *
 * @author 17460
 * @since 2022-09-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("operation_log")
public class OperationLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "log_id", type = IdType.AUTO)
    private Integer logId;

    /**
     * qq
     */
    private String qqNum;

    /**
     * 操作类型，赚钱，探索
     */
    private String operationType;

    /**
     * 操作子类，具体的工作 搬砖还是什么
     */
    private String operationCategoryType;

    /**
     * 操作收获，赚钱就是收获银币
     */
    private String operationHarvest;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 操作时间，如果补卡就记录补卡时间
     */
    private Date operationTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;


}
