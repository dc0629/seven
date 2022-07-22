package top.flagshen.myqq.database.props.dto;

import lombok.Data;

/**
 * 道具数量
 */
@Data
public class PropsTotal {

    /**
     * 道具名
     */
    String propsName;

    /**
     * 数量
     */
    Integer total = 0;
}
