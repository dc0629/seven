package top.flagshen.myqq.database.userinfo.service;

import top.flagshen.myqq.database.userinfo.entity.UserInfoDO;
import com.baomidou.mybatisplus.extension.service.IService;
import top.flagshen.myqq.entity.resp.UserInfoResp;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author 17460
 * @since 2022-08-14
 */
public interface IUserInfoService extends IService<UserInfoDO> {

    UserInfoResp getUserDetail(String qqNum);
}
