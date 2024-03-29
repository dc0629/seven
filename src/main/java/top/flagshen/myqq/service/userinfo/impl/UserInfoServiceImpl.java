package top.flagshen.myqq.service.userinfo.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.flagshen.myqq.common.HttpMethodConstants;
import top.flagshen.myqq.common.cache.CacheFind;
import top.flagshen.myqq.common.cache.RedisConstant;
import top.flagshen.myqq.common.constants.SystemConstants;
import top.flagshen.myqq.common.constants.YesOrNoConstants;
import top.flagshen.myqq.common.exception.ErrorCodeEnum;
import top.flagshen.myqq.common.exception.ExceptionAssertUtil;
import top.flagshen.myqq.common.exception.MyException;
import top.flagshen.myqq.dao.props.mapper.PropsMapper;
import top.flagshen.myqq.dao.userinfo.entity.UserInfoDO;
import top.flagshen.myqq.dao.userinfo.entity.UserProficiencyDO;
import top.flagshen.myqq.dao.userinfo.mapper.UserInfoMapper;
import top.flagshen.myqq.dao.userinfo.mapper.UserProficiencyMapper;
import top.flagshen.myqq.entity.log.enums.OperationTypeEnum;
import top.flagshen.myqq.entity.userinfo.enums.ProficiencyTypeEnum;
import top.flagshen.myqq.entity.userinfo.enums.RankTypeEnum;
import top.flagshen.myqq.entity.userinfo.enums.UserTypeEnum;
import top.flagshen.myqq.entity.userinfo.req.BindQQReq;
import top.flagshen.myqq.entity.userinfo.req.CreateUserReq;
import top.flagshen.myqq.entity.userinfo.resp.RankResp;
import top.flagshen.myqq.entity.userinfo.resp.UserInfoResp;
import top.flagshen.myqq.entity.userinfo.resp.WeiXinResp;
import top.flagshen.myqq.service.log.IOperationLogService;
import top.flagshen.myqq.service.userinfo.IUserInfoService;
import top.flagshen.myqq.util.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Autowired
    private UserProficiencyMapper userProficiencyMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private PropsMapper propsMapper;

    @Autowired
    private IOperationLogService operationLogService;

    private static final List<String> workList = Arrays.asList(
            "搬砖收获银币：",
            "伐木收获银币：",
            "烧水泥收获银币：",
            "烧炭收获银币：",
            "管理仓库收获银币：");

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
        // 今日占卜记录
        String zhanbuKey = RedisConstant.DIVINATIONSTR + qqNum;
        if (redisTemplate.hasKey(zhanbuKey)) {
            userInfoResp.setZhanbuStr(redisTemplate.opsForValue().get(zhanbuKey));
        }
        // 今日赚钱记录
        String workKey = RedisConstant.DAGONG + qqNum;
        if (redisTemplate.hasKey(workKey)) {
            userInfoResp.setZhuanqianStr(redisTemplate.opsForValue().get(workKey));
        }
        userInfoResp.setUserType(UserTypeEnum.getByCode(userInfoDO.getUserType()).getDesc());
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
        userInfoResp.setUserType(UserTypeEnum.getByCode(userInfoDO.getUserType()).getDesc());
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
        ExceptionAssertUtil.notBlank(req.getQqNum(), ErrorCodeEnum.PARAM_ILLEGAL);

        UserInfoDO userInfoDO = this.getOne(new LambdaQueryWrapper<UserInfoDO>()
                .eq(UserInfoDO::getQqNum, req.getQqNum())
                .last("limit 1"));
        // 没有查到的话，提示用户不存在
        ExceptionAssertUtil.notNull(userInfoDO, ErrorCodeEnum.USER_IS_NULL);
        if (StringUtils.isNotBlank(userInfoDO.getOpenId())) {
            throw new MyException("该账号已被绑定");
        }

        // 否则更新openId
        userInfoDO.setOpenId(req.getOpenId());
        this.updateById(userInfoDO);
        redisTemplate.opsForValue().set(req.getOpenId(), req.getQqNum(), 1, TimeUnit.DAYS);
    }

    @Override
    public void unbind(String qq) {
        userInfoMapper.unbind(qq);
    }

    @Override
    public UserInfoResp createUser(CreateUserReq req) {
        String qqNum = req.getQqNum();
        if (StringUtils.isBlank(qqNum) || StringUtils.isBlank(req.getUserType())
                || UserTypeEnum.UNKNOWN.getCode().equals(UserTypeEnum.getByCode(req.getUserType()))) {
            throw new MyException("参数错误");
        }
        if (qqNum.length() > 16) {
            throw new MyException("账号太长，不能超过16位");
        }
        if (req.getNickName() != null && req.getNickName().length() > 64) {
            throw new MyException("账号太长，不能超过64个字符");
        }
        // 如果是测试，是在qq号后面直接拼接
        if (YesOrNoConstants.YES.equals(req.getIsTest())) {
            qqNum += SystemConstants.TEST;
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

    @Override
    public String zhanbu(String qq, Integer isTest) {
        if (StringUtils.isBlank(qq)) {
            throw new MyException("参数错误");
        }
        if (YesOrNoConstants.YES.equals(isTest)) {
            qq += SystemConstants.TEST;
        }
        String key = RedisConstant.DIVINATION + qq;
        String strKey = RedisConstant.DIVINATIONSTR + qq;
        if (redisTemplate.hasKey(strKey)) {
            return redisTemplate.opsForValue().get(strKey);
        }
        int yun = ContentUtil.getYun();
        String zhanbu = ContentUtil.zhanbu(yun);
        redisTemplate.opsForValue().set(key, String.valueOf(yun),
                YesOrNoConstants.YES.equals(isTest) ? 60000 : DateUtil.getMidnightMillis(),
                TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set(strKey, zhanbu,
                YesOrNoConstants.YES.equals(isTest) ? 60000 : DateUtil.getMidnightMillis(),
                TimeUnit.MILLISECONDS);
        return zhanbu;
    }

    @Override
    public String work(String qq, Integer isTest) {
        if (StringUtils.isBlank(qq)) {
            throw new MyException("参数错误");
        }
        if (YesOrNoConstants.YES.equals(isTest)) {
            qq += SystemConstants.TEST;
        }
        UserInfoDO userInfo = this.getById(qq);
        if (userInfo == null) {
            throw new MyException("账号异常");
        }
        String zhanbuKey = RedisConstant.DIVINATION + qq;
        int yun = 50;
        if (redisTemplate.hasKey(zhanbuKey)) {
            yun = Integer.parseInt(redisTemplate.opsForValue().get(zhanbuKey));
        }
        String workKey = RedisConstant.DAGONG + qq;
        if (redisTemplate.hasKey(workKey)) {
            throw new MyException("今日已打工");
        }
        // 随机从5项工作中选一项 0搬砖，1伐木，2烧水泥，3烧炭，4管理仓库
        int r = (int) (Math.random() * 5);
        int coin = 0;
        // 熟练度等级
        int proficiencyLevel = 0;
        Double doubleValue;
        // 幸运值影响因子
        double yunMagnification = 1+(yun-50)*0.005;
        // 熟练度类型
        String proficiencyType = ProficiencyTypeEnum.getByIndex(r).getCode();
        UserProficiencyDO userProficiency = userProficiencyMapper.selectOne(
                new LambdaQueryWrapper<UserProficiencyDO>()
                        .eq(UserProficiencyDO::getQqNum, qq)
                        .eq(UserProficiencyDO::getProficiencyType, proficiencyType));
        if (userProficiency != null) {
            proficiencyLevel = userProficiency.getProficiencyLevel();
        }
        switch (r) {
            case 0:
                doubleValue = (userInfo.getStrength() * 0.2 + 0.4*proficiencyLevel) * 10 * yunMagnification;
                coin = doubleValue.intValue();
                break;
            case 1:
                doubleValue = (userInfo.getAgile() * 0.2 + 0.4*proficiencyLevel) * 10 * yunMagnification;
                coin = doubleValue.intValue();
                break;
            case 2:
                doubleValue = (userInfo.getConstitution() * 0.2 + 0.4*proficiencyLevel) * 10 * yunMagnification;
                coin = doubleValue.intValue();
                break;
            case 3:
                doubleValue = (userInfo.getPerception() * 0.2 + 0.4*proficiencyLevel) * 10 * yunMagnification;
                coin = doubleValue.intValue();
                break;
            case 4:
                doubleValue = (userInfo.getIntelligence() * 0.2 + 0.4*proficiencyLevel) * 10 * yunMagnification;
                coin = doubleValue.intValue();
                break;
            default:
                break;
        }
        // 更新银币和贡献度
        userInfo.setSilverCoin(userInfo.getSilverCoin() + coin);
        userInfo.setContribution(userInfo.getContribution() + 100);
        this.updateById(userInfo);
        // 熟练度增加
        Double proficiencyExperience = coin * 0.2 * userInfo.getIntelligence();
        // 更新或新增熟练度
        if (userProficiency == null) {
            UserProficiencyDO addUserProficiency = new UserProficiencyDO();
            addUserProficiency.setQqNum(qq);
            addUserProficiency.setProficiencyType(proficiencyType);
            addUserProficiency.setProficiencyExperience(proficiencyExperience);
            if (proficiencyExperience > 10) {
                addUserProficiency.setProficiencyLevel(1);
            }
            userProficiencyMapper.insert(addUserProficiency);
        } else {
            proficiencyExperience = userProficiency.getProficiencyExperience() + proficiencyExperience;
            if (proficiencyExperience < 10) {
                userProficiency.setProficiencyLevel(0);
            } else if (proficiencyExperience < 70) {
                userProficiency.setProficiencyLevel(1);
            } else if (proficiencyExperience < 170) {
                userProficiency.setProficiencyLevel(2);
            } else if (proficiencyExperience < 320) {
                userProficiency.setProficiencyLevel(3);
            } else if (proficiencyExperience < 530) {
                userProficiency.setProficiencyLevel(4);
            } else {
                userProficiency.setProficiencyLevel(5);
                proficiencyExperience = 530.00;
            }
            userProficiency.setProficiencyExperience(proficiencyExperience);
            userProficiencyMapper.updateById(userProficiency);
        }
        String result = workList.get(r) + coin;
        // 如果是测试，就只有5分钟过期，否则凌晨过期
        redisTemplate.opsForValue().set(workKey, result,
                YesOrNoConstants.YES.equals(isTest) ? 60000 : DateUtil.getMidnightMillis(),
                TimeUnit.MILLISECONDS);
        operationLogService.saveOperationLog(qq, OperationTypeEnum.ZHUANQIAN.getCode(), proficiencyType, String.valueOf(coin));
        return result;
    }

    @Override
    @CacheFind(prefixKeyName="rank",key="':rankType:'+#rankType",expireTime=300L)
    public List<RankResp> getRank(String rankType) {
        List<RankResp> rankCoin = new ArrayList<>(30);
        RankTypeEnum rankTypeEnum = RankTypeEnum.getByCode(rankType);
        switch (rankTypeEnum) {
            case COIN :
                rankCoin = userInfoMapper.getRankCoin();
                break;
            case USED_BLOOD :
                rankCoin = this.getRankUsedBlood();
                break;
            default:
                break;
        }
        int i = 1;
        for (RankResp rankResp : rankCoin) {
            if (i < 10) {
                rankResp.setNo("0" + (i++));
            } else {
                rankResp.setNo(String.valueOf(i++));
            }
        }
        return rankCoin;
    }

    /**
     * 获取使用医疗包数量排行
     * @return
     */
    private List<RankResp> getRankUsedBlood() {
        List<RankResp> rankCoin = propsMapper.getRankUsedBlood();
        if (CollectionUtils.isEmpty(rankCoin)) {
            return new ArrayList<>();
        }
        // 根据qqNum获取用户昵称
        List<String> qqNums = rankCoin.stream().map(RankResp::getQqNum).collect(Collectors.toList());
        List<UserInfoDO> infoDOS = userInfoMapper.selectList(
                new LambdaQueryWrapper<UserInfoDO>().in(UserInfoDO::getQqNum, qqNums));
        Map<String, String> nameMap = new HashMap<>(infoDOS.size());
        infoDOS.forEach(info -> nameMap.put(info.getQqNum(), info.getNickName()));

        rankCoin.forEach(rankResp -> rankResp.setNickName(StringUtils.defaultString(nameMap.get(rankResp.getQqNum()), "未注册账号")));
        return rankCoin;
    }

    @Override
    public String getMyRank(String qqNum, String rankType) {
        if (StringUtils.isBlank(qqNum)) {
            return StringUtils.EMPTY;
        }
        RankTypeEnum rankTypeEnum = RankTypeEnum.getByCode(rankType);
        String rank = StringUtils.EMPTY;
        switch (rankTypeEnum) {
            case COIN :
                rank = userInfoMapper.getMyRankCoin(qqNum);
                break;
            case USED_BLOOD :
                rank = propsMapper.getMyRankBlood(qqNum);
                break;
            default:
                break;
        }
        return StringUtils.defaultString(rank);
    }

    @Override
    public void updateName(BindQQReq req) {
        String nickName = req.getNickName();
        if (StringUtils.isBlank(nickName)) {
            throw new MyException("昵称不能为空");
        }
        if (MgcUtil.haveMgc(nickName)) {
            throw new MyException("昵称中包含敏感词");
        }
        nickName = nickName.trim();
        if (nickName.length() > 32) {
            throw new MyException("昵称过长");
        }
        userInfoMapper.update(new UserInfoDO(), new LambdaUpdateWrapper<UserInfoDO>()
                .set(UserInfoDO::getNickName, nickName)
                .eq(UserInfoDO::getQqNum, req.getQqNum()));
    }
}
