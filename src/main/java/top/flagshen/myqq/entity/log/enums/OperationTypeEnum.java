package top.flagshen.myqq.entity.log.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author dengchao
 */
public enum OperationTypeEnum {

    /**
     * 赚钱
     */
    ZHUANQIAN("ZHUANQIAN", "赚钱"),

    /**
     * 其他不支持类型
     */
    UNKNOWN("UNKNOWN", "其他不支持类型");

    /**
     * 枚举代码
     */
    private final String code;

    /**
     * 枚举描述
     */
    private final String desc;

    /**
     * 构造器
     *
     * @param code code
     * @param desc desc
     */
    OperationTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 通过code获取枚举
     *
     * @param code
     * @return
     */
    public static OperationTypeEnum getByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return UNKNOWN;
        }

        for (OperationTypeEnum statusEnum : values()) {
            if (StringUtils.equalsIgnoreCase(code, statusEnum.getCode())) {
                return statusEnum;
            }
        }

        return UNKNOWN;
    }

    /**
     * 通过desc获取枚举
     *
     * @param desc
     * @return
     */
    public static OperationTypeEnum getByDesc(String desc) {
        if (StringUtils.isBlank(desc)) {
            return UNKNOWN;
        }

        for (OperationTypeEnum statusEnum : values()) {
            if (StringUtils.equalsIgnoreCase(desc, statusEnum.getDesc())) {
                return statusEnum;
            }
        }

        return UNKNOWN;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
