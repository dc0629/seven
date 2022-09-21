package top.flagshen.myqq.entity.userinfo.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author dengchao
 */
public enum UserTypeEnum {

    /**
     * 力量系
     */
    STRENGTH("STRENGTH", "力量系", 0),

    /**
     * 敏捷系
     */
    AGILE("AGILE", "敏捷系", 1),

    /**
     * 感知系
     */
    PERCEPTION("PERCEPTION", "感知系", 2),

    /**
     * 智力系
     */
    INTELLIGENCE("INTELLIGENCE", "智力系", 3),

    /**
     * 体质系
     */
    CONSTITUTION("CONSTITUTION", "体质系", 4),

    /**
     * 其他不支持类型
     */
    UNKNOWN("UNKNOWN", "其他不支持类型", 999);

    /**
     * 枚举代码
     */
    private final String code;

    /**
     * 枚举描述
     */
    private final String desc;

    /**
     * 属性在的位置
     */
    private final Integer index;

    /**
     * 构造器
     *
     * @param code code
     * @param desc desc
     */
    UserTypeEnum(String code, String desc, Integer index) {
        this.code = code;
        this.desc = desc;
        this.index = index;
    }

    /**
     * 通过code获取枚举
     *
     * @param code
     * @return
     */
    public static UserTypeEnum getByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return UNKNOWN;
        }

        for (UserTypeEnum statusEnum : values()) {
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
    public static UserTypeEnum getByDesc(String desc) {
        if (StringUtils.isBlank(desc)) {
            return UNKNOWN;
        }

        for (UserTypeEnum statusEnum : values()) {
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

    public Integer getIndex() {
        return index;
    }
}
