package top.flagshen.myqq.entity.userinfo.req;

import lombok.Data;

/**
 * 创建账号请求
 * @author dengchao
 */
@Data
public class CreateUserReq {

    /**
     * qq号
     */
    private String qqNum;

    /**
     * openId
     */
    private String openId;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 所属序列 力量系STRENGTH，敏捷系AGILE，感知系PERCEPTION，智力系INTELLIGENCE，体质系CONSTITUTION
     */
    private String userType;

    /**
     * 是否为测试账号
     */
    private Integer isTest;

}
