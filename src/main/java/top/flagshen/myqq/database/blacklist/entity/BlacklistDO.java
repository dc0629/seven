package top.flagshen.myqq.database.blacklist.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    private Date createTime;


}
