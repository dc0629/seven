package top.flagshen.myqq.dao.forbidden.dto;

import lombok.Data;

/**
 * qq号和对应的禁言次数
 */
@Data
public class JinYanCount {

    /**
     * qq号
     */
    String qqNum;

    /**
     * 数量
     */
    Integer count = 0;
}
