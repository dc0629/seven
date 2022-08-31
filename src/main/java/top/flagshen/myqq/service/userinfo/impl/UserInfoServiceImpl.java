package top.flagshen.myqq.service.userinfo.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.HttpMethodConstants;
import top.flagshen.myqq.common.exception.ErrorCodeEnum;
import top.flagshen.myqq.common.exception.ExceptionAssertUtil;
import top.flagshen.myqq.common.exception.MyException;
import top.flagshen.myqq.dao.userinfo.entity.UserInfoDO;
import top.flagshen.myqq.dao.userinfo.mapper.UserInfoMapper;
import top.flagshen.myqq.entity.userinfo.req.BindQQReq;
import top.flagshen.myqq.entity.userinfo.resp.UserInfoResp;
import top.flagshen.myqq.entity.userinfo.resp.WeiXinResp;
import top.flagshen.myqq.service.userinfo.IUserInfoService;
import top.flagshen.myqq.util.AesUtils;
import top.flagshen.myqq.util.HttpApiUtil;

import java.util.concurrent.TimeUnit;

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

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    public UserInfoResp getUserDetail(String qqNum) {
        UserInfoResp userInfoResp = new UserInfoResp();
        if (StringUtils.isBlank(qqNum)) {
            throw new MyException("请重启小程序");
        }
        UserInfoDO userInfoDO = this.getById(qqNum);
        if (userInfoDO == null) {
            throw new MyException("账号异常");
        }
        BeanUtils.copyProperties(userInfoDO, userInfoResp);
        return userInfoResp;
    }

    @Override
    public UserInfoResp getSearchUserDetail(String qqNum) {
        // 解密
        qqNum = AesUtils.decodeHexString(qqNum);
        if (StringUtils.isBlank(qqNum)) {
            throw new MyException(ErrorCodeEnum.INVALID_QR_CODE);
        }
        UserInfoResp userInfoResp = new UserInfoResp();
        UserInfoDO userInfoDO = this.getById(qqNum);
        if (userInfoDO == null) {
            throw new MyException(ErrorCodeEnum.INVALID_QR_CODE);
        }
        BeanUtils.copyProperties(userInfoDO, userInfoResp);
        return userInfoResp;
    }

    @Override
    public WeiXinResp getOpenId(String code) {
        WeiXinResp resp = new WeiXinResp();
        String url = "https://api.weixin.qq.com/sns/jscode2session?;
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
            redisTemplate.opsForValue().set(openid.toString(), userInfo.getQqNum(), 1, TimeUnit.DAYS);
        }
        return resp;
    }

    @Override
    public void bingQQ(BindQQReq req) {
        ExceptionAssertUtil.notBlank(req.getOpenId(), ErrorCodeEnum.PARAM_ILLEGAL);
        ExceptionAssertUtil.notBlank(req.getPassword(), ErrorCodeEnum.PARAM_ILLEGAL);
        ExceptionAssertUtil.notBlank(req.getQqNum(), ErrorCodeEnum.PARAM_ILLEGAL);

        UserInfoDO userInfoDO = this.getOne(new LambdaQueryWrapper<UserInfoDO>()
                .eq(UserInfoDO::getQqNum, req.getQqNum())
                .eq(UserInfoDO::getPassword, req.getPassword())
                .last("limit 1"));
        // 没有查到的话，都提示密码错误
        ExceptionAssertUtil.notNull(userInfoDO, ErrorCodeEnum.PASSWORD_ERROR);
        // 已经被绑定的话也提示密码错误
        if (StringUtils.isNotBlank(userInfoDO.getOpenId())) {
            throw new MyException(ErrorCodeEnum.PASSWORD_ERROR);
        }

        // 否则更新openId
        userInfoDO.setOpenId(req.getOpenId());
        this.updateById(userInfoDO);
        redisTemplate.opsForValue().set(req.getOpenId(), req.getQqNum(), 1, TimeUnit.DAYS);
    }
}
