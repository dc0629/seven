package top.flagshen.myqq.service.userinfo;

import com.baomidou.mybatisplus.extension.service.IService;
import top.flagshen.myqq.dao.userinfo.entity.UserInfoDO;
import top.flagshen.myqq.entity.userinfo.req.BindQQReq;
import top.flagshen.myqq.entity.userinfo.resp.UserInfoResp;
import top.flagshen.myqq.entity.userinfo.resp.WeiXinResp;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author 17460
 * @since 2022-08-14
 */
public interface IUserInfoService extends IService<UserInfoDO> {

    /**
     * 获取用户详情
     * @param qqNum
     * @return
     */
    UserInfoResp getUserDetail(String qqNum);

    /**
     *
     * @param code
     * @return
     */
    WeiXinResp getOpenId(String code);

    /**
     * 绑定qq
     * @param req
     */
    void bingQQ(BindQQReq req);
}
