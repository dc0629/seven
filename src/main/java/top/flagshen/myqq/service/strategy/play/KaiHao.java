package top.flagshen.myqq.service.strategy.play;

import catcode.CatCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.Permissions;
import top.flagshen.myqq.common.constants.YesOrNoConstants;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.entity.userinfo.enums.UserTypeEnum;
import top.flagshen.myqq.entity.userinfo.req.CreateUserReq;
import top.flagshen.myqq.entity.userinfo.resp.UserInfoResp;
import top.flagshen.myqq.service.strategy.PlayStrategy;
import top.flagshen.myqq.service.userinfo.IUserInfoService;

@Service("开号")
public class KaiHao implements PlayStrategy {

    @Autowired
    private IUserInfoService userInfoService;

    @Override
    @Permissions(groupNums = "423430656,481024974")
    public boolean play(MyQQMessage message) {
        CreateUserReq userReq = new CreateUserReq();
        // 如果是测试群
        if ("481024974".equals(message.getMqFromid())) {
            userReq.setIsTest(YesOrNoConstants.YES);
        }
        userReq.setNickName(message.getMqFromqqName());
        userReq.setQqNum(message.getMqFromqq());
        userReq.setUserType(UserTypeEnum.getByDesc(message.getMqMsg()).getCode());
        UserInfoResp user = new UserInfoResp();
        try {
            user = userInfoService.createUser(userReq);
        } catch (Exception e) {
            return true;
        }
        CatCodeUtil util = CatCodeUtil.INSTANCE;
        // 构建at
        String at = util.toCat("at", "code="+message.getMqFromqq());
        String msg = at + " 开号成功" +
                "\r\n力量：" + user.getStrength() +
                "\r\n敏捷：" + user.getAgile() +
                "\r\n感知：" + user.getPerception() +
                "\r\n智力：" + user.getIntelligence() +
                "\r\n体质：" + user.getConstitution() +
                "\n\n请好好为联盟添砖加瓦吧(ง •̀_•́)ง";
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), msg);
        return true;
    }
}
