package top.flagshen.myqq.database.props.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 道具
 * </p>
 *
 * @author 邓超
 * @since 2022-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("props")
public class PropsDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "props_id", type = IdType.AUTO)
    private Integer propsId;

    /**
     * qq号
     */
    private String qqNum;

    /**
     * 道具名
     */
    private String propsName;

    /**
     * 是否已经使用，0没有，1有
     */
    private Integer isUsed;

    /**
     * 是否已删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 创建时间
     */
    private Date createTime;


}
