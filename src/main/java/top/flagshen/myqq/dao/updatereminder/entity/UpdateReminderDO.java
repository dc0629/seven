package top.flagshen.myqq.dao.updatereminder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 更新提醒
 * </p>
 *
 * @author 17460
 * @since 2022-08-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("update_reminder")
public class UpdateReminderDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "reminder_id", type = IdType.AUTO)
    private Integer reminderId;

    /**
     * qq号
     */
    private String qqNum;

    /**
     * 群号
     */
    private String groupNum;


}
