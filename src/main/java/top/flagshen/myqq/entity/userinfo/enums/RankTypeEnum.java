package top.flagshen.myqq.entity.userinfo.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author dengchao
 */
public enum RankTypeEnum {

    /**
     * 银币排行
     */
    COIN("COIN", "银币排行"),

    /**
     * 使用医疗包次数
     */
    USED_BLOOD("USED_BLOOD", "使用医疗包次数");

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
    RankTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 通过code获取枚举
     *
     * @param code
     * @return
     */
    public static RankTypeEnum getByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return COIN;
        }

        for (RankTypeEnum statusEnum : values()) {
            if (StringUtils.equalsIgnoreCase(code, statusEnum.getCode())) {
                return statusEnum;
            }
        }

        return COIN;
    }

    /**
     * 通过desc获取枚举
     *
     * @param desc
     * @return
     */
    public static RankTypeEnum getByDesc(String desc) {
        if (StringUtils.isBlank(desc)) {
            return COIN;
        }

        for (RankTypeEnum statusEnum : values()) {
            if (StringUtils.equalsIgnoreCase(desc, statusEnum.getDesc())) {
                return statusEnum;
            }
        }

        return COIN;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
