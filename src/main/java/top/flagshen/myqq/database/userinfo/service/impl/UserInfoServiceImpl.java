package top.flagshen.myqq.database.userinfo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.database.userinfo.entity.UserInfoDO;
import top.flagshen.myqq.database.userinfo.mapper.UserInfoMapper;
import top.flagshen.myqq.database.userinfo.service.IUserInfoService;
import top.flagshen.myqq.entity.resp.UserInfoResp;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author 17460
 * @since 2022-08-14
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfoDO> implements IUserInfoService {

    @Override
    public UserInfoResp getUserDetail(String qqNum) {
        UserInfoResp userInfoResp = new UserInfoResp();
        UserInfoDO userInfoDO = this.getById(qqNum);
        if (userInfoDO == null) {
            return userInfoResp;
        }
        BeanUtils.copyProperties(userInfoDO, userInfoResp);
        return userInfoResp;
    }
}
