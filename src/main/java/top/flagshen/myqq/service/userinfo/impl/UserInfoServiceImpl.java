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
import top.flagshen.myqq.common.constants.YesOrNoConstants;
import top.flagshen.myqq.common.exception.ErrorCodeEnum;
import top.flagshen.myqq.common.exception.ExceptionAssertUtil;
import top.flagshen.myqq.common.exception.MyException;
import top.flagshen.myqq.dao.userinfo.entity.UserInfoDO;
import top.flagshen.myqq.dao.userinfo.mapper.UserInfoMapper;
import top.flagshen.myqq.entity.userinfo.enums.UserTypeEnum;
import top.flagshen.myqq.entity.userinfo.req.BindQQReq;
import top.flagshen.myqq.entity.userinfo.req.CreateUserReq;
import top.flagshen.myqq.entity.userinfo.resp.UserInfoResp;
import top.flagshen.myqq.entity.userinfo.resp.WeiXinResp;
import top.flagshen.myqq.service.userinfo.IUserInfoService;
import top.flagshen.myqq.util.AesUtils;
import top.flagshen.myqq.util.HttpApiUtil;

import java.util.ArrayList;
import java.util.List;
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

    @Override
    public UserInfoResp createUser(CreateUserReq req) {
        String qqNum = req.getQqNum();
        if (StringUtils.isBlank(qqNum) || StringUtils.isBlank(req.getUserType()) ) {
            throw new MyException("参数错误");
        }
        // 如果是测试，是在qq号后面直接拼接
        if (YesOrNoConstants.YES.equals(req.getIsTest())) {
            qqNum += "test";
        }
        // 判断用户是否已存在
        UserInfoDO userInfo = this.getById(qqNum);
        if (userInfo != null) {
            throw new MyException("账号已存在");
        }
        // 初始化用户信息
        UserInfoDO addUserInfo = initUser(qqNum, req.getNickName(), req.getUserType(), req.getIsTest(), req.getOpenId());
        this.save(addUserInfo);
        if (StringUtils.isNotBlank(req.getOpenId())) {
            redisTemplate.opsForValue().set(req.getOpenId(), req.getQqNum(), 1, TimeUnit.DAYS);
        }
        UserInfoResp userInfoResp = new UserInfoResp();
        BeanUtils.copyProperties(addUserInfo, userInfoResp);
        return userInfoResp;
    }

    /**
     * 初始化用户信息
     * x系的五维  就是 x 7点 其他的在3-6之间随机
     * 总和等于25
     * 比如  7 3 6 6 3
     *       7 4 3 5 6
     * @return
     */
    private static UserInfoDO initUser(String qqNum, String nickName, String userType, Integer isTest, String openId) {
        UserInfoDO userInfo = new UserInfoDO();
        userInfo.setQqNum(qqNum);
        userInfo.setOpenId(openId);
        userInfo.setIsTest(isTest);
        userInfo.setUserType(userType);
        userInfo.setNickName(nickName);

        // 总数25点属性，主属性7点，其他在3-6之间随机
        List<Integer> attributeList = new ArrayList<>(4);
        // 扣除主属性7点后剩余点数总量18
        int attributeCount = 18;
        int attribute = 0;
        // 随机数的上限
        int upLimit = 0;
        // 随机数的下限
        int lowerLimit = 0;
        // 随机生成4个属性
        for (int i = 0; i < 4; i++) {
            // 剩下位置保底都要大等于3点 所以剩余点-剩余位*3 大等于6，随机上限就是6，否则就是剩余点-剩余位*3
            if (attributeCount - (3-i)*3 >= 6) {
                upLimit = 6;
            } else {
                upLimit = attributeCount - (3-i)*3;
            }
            // 剩下位置保底都要小等于6点 所以剩余点-剩余位*6 小等于3，随机下限就是3，否则就是剩余点-剩余位*6
            if (attributeCount - (3-i)*6 <= 3) {
                lowerLimit = 3;
            } else {
                lowerLimit = attributeCount - (3-i)*6;
            }
            attribute = (int) (Math.random() * (upLimit - lowerLimit)) + lowerLimit;
            attributeList.add(attribute);
            attributeCount-=attribute;
        }

        // 将随机的4个属性2次打乱填入新数组中，因为实测发现最后一位属性总是可能是大的数字
        List<Integer> attributeListNew = new ArrayList<>(5);
        int r = 0;
        for (int i = 0; i < 5; i++) {
            // 如果是主属性的位置，设置为7
            if (i == UserTypeEnum.getByCode(userType).getIndex()) {
                attributeListNew.add(7);
                continue;
            }
            r = (int) (Math.random() * attributeList.size());
            attributeListNew.add(attributeList.get(r));
            attributeList.remove(r);
        }

        userInfo.setStrength(attributeListNew.get(0));
        userInfo.setAgile(attributeListNew.get(1));
        userInfo.setPerception(attributeListNew.get(2));
        userInfo.setIntelligence(attributeListNew.get(3));
        userInfo.setConstitution(attributeListNew.get(4));
        return userInfo;
    }
}
