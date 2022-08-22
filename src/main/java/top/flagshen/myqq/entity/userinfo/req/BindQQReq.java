package top.flagshen.myqq.entity.userinfo.req;

import lombok.Data;

/**
 * 绑定qq
 *
 * @author 17460
 */
@Data
public class BindQQReq {

    /**
     * qq号
     */
    private String qqNum;

    /**
     * 密码
     */
    private String password;

    /**
     * openId
     */
    private String openId;

}
