package top.flagshen.myqq.entity.userinfo.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 熟练度类型枚举
 * @author dengchao
 */
public enum ProficiencyTypeEnum {

    /**
     * 搬砖
     */
    BANZHUAN("BANZHUAN", "搬砖", 0),

    /**
     * 伐木
     */
    FAMU("FAMU", "伐木", 1),

    /**
     * 烧水泥
     */
    SHAOSHUINI("SHAOSHUINI", "烧水泥", 2),

    /**
     * 烧炭
     */
    SHAOTAN("SHAOTAN", "烧炭", 3),

    /**
     * 管理仓库
     */
    GUANLICANGKU("GUANLICANGKU", "管理仓库", 4),

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
    ProficiencyTypeEnum(String code, String desc, Integer index) {
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
    public static ProficiencyTypeEnum getByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return UNKNOWN;
        }

        for (ProficiencyTypeEnum statusEnum : values()) {
            if (StringUtils.equalsIgnoreCase(code, statusEnum.getCode())) {
                return statusEnum;
            }
        }

        return UNKNOWN;
    }

    /**
     * 通过index获取枚举
     *
     * @param index
     * @return
     */
    public static ProficiencyTypeEnum getByIndex(Integer index) {
        if (index == null) {
            return UNKNOWN;
        }

        for (ProficiencyTypeEnum statusEnum : values()) {
            if (index.equals(statusEnum.getIndex())) {
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
