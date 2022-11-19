package top.flagshen.myqq.service.strategy.play;

import catcode.CatCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.Permissions;
import top.flagshen.myqq.dao.userinfo.entity.UserInfoDO;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.props.IPropsService;
import top.flagshen.myqq.service.strategy.PlayStrategy;
import top.flagshen.myqq.service.userinfo.IUserInfoService;
import top.flagshen.myqq.util.DateUtil;

import java.util.concurrent.TimeUnit;

@Service("购买修复液")
@Slf4j
public class BuyBlood implements PlayStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    private IPropsService propsService;

    @Autowired
    private IUserInfoService userInfoService;

    @Override
    @Permissions(groupNums = "423430656,954804208,903959441")
    public boolean play(MyQQMessage message) {
        String qq = message.getMqFromqq();
        UserInfoDO userInfo = userInfoService.getById(qq);

        String buyBloodKey = "BUY_BLOOD_CD:" + qq;
        if (redisTemplate.hasKey(buyBloodKey)) {
            return true;
        }
        redisTemplate.opsForValue().set(buyBloodKey, "1", DateUtil.getMidnightMillis(), TimeUnit.MILLISECONDS);
        CatCodeUtil util = CatCodeUtil.INSTANCE;
        String at = util.toCat("at", "code="+qq);
        if (userInfo == null || userInfo.getSilverCoin() < 10) {
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), at + " 余额不足");
            return true;
        }
        userInfo.setSilverCoin(userInfo.getSilverCoin() - 10);
        userInfoService.updateById(userInfo);
        propsService.addBlood(qq, 1);
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), at + " 购买成功,消耗10银币");
        return true;
    }

}
