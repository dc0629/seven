package top.flagshen.myqq.entity.userinfo.resp;

import lombok.Data;

/**
 * <p>
 * 用户基本信息
 * </p>
 *
 * @author 17460
 * @since 2022-08-14
 */
@Data
public class WeiXinResp {

    /**
     * qq号 根据openId获取绑定的qq号，没绑定就为空
     *
     */
    private String qqNum;

    /**
     * openId
     */
    private String openId;

}
