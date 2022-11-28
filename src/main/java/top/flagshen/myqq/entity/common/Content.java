package top.flagshen.myqq.entity.common;

import lombok.Data;

@Data
public class Content {

    /**
     * 天气
     */
    private String weather;
    /**
     * 最低气温
     */
    private String lowest;
    /**
     * 最高气温
     */
    private String highest;
    /**
     * 风向
     */
    private String wind;
    /**
     * 风力
     */
    private String windsc;
    /**
     * 生活指数
     */
    private String tips;

}
