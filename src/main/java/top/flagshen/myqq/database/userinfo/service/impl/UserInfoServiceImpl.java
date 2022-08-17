package top.flagshen.myqq.database.userinfo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.HttpMethodConstants;
import top.flagshen.myqq.database.userinfo.entity.UserInfoDO;
import top.flagshen.myqq.database.userinfo.mapper.UserInfoMapper;
import top.flagshen.myqq.database.userinfo.service.IUserInfoService;
import top.flagshen.myqq.entity.resp.UserInfoResp;
import top.flagshen.myqq.entity.resp.WeiXinResp;
import top.flagshen.myqq.util.AesUtils;
import top.flagshen.myqq.util.HttpApiUtil;

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
        // 先解密
        qqNum = AesUtils.decodeHexString(qqNum);
        UserInfoResp userInfoResp = new UserInfoResp();
        UserInfoDO userInfoDO = this.getById(qqNum);
        if (userInfoDO == null) {
            return userInfoResp;
        }
        BeanUtils.copyProperties(userInfoDO, userInfoResp);
        return userInfoResp;
    }

    @Override
    public WeiXinResp getOpenId(String code) {
        WeiXinResp resp = new WeiXinResp();
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=wxb387723fb3d81e28&secret=c7a4bb062c2a9cbf152f91f06a705271&grant_type=authorization_code&js_code="
                + code;
        String result = HttpApiUtil.httpClientCommon(HttpMethodConstants.HTTP_GET, url, null);
        JSONObject jsonObject = JSON.parseObject(result);
        if (jsonObject == null) {
            return resp;
        }
        Object openid = jsonObject.get("openid");
        if (openid == null) {
            return resp;
        }
        // 根据code查询到openId
        resp.setOpenId(openid.toString());
        UserInfoDO userInfo = this.getOne(new LambdaQueryWrapper<UserInfoDO>()
                .eq(UserInfoDO::getOpenId, openid.toString())
                .last("limit 1"));
        if (userInfo != null) {
            resp.setQqNum(userInfo.getQqNum());
        }
        return resp;
    }
}
