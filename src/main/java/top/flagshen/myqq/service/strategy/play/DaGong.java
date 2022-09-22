package top.flagshen.myqq.service.strategy.play;

import catcode.CatCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.Permissions;
import top.flagshen.myqq.common.constants.YesOrNoConstants;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.PlayStrategy;
import top.flagshen.myqq.service.userinfo.IUserInfoService;

@Service("打工")
public class DaGong implements PlayStrategy {

    @Autowired
    private IUserInfoService userInfoService;

    @Override
    @Permissions(groupNums = "481024974")
    public boolean play(MyQQMessage message) {
        Integer isTest = 0;
        // 如果是测试群
        if ("481024974".equals(message.getMqFromid())) {
            isTest = YesOrNoConstants.YES;
        }
        String result;
        try {
            result = userInfoService.work(message.getMqFromqq(), isTest);
        } catch (Exception e) {
            return true;
        }

        CatCodeUtil util = CatCodeUtil.INSTANCE;
        // 构建at
        String at = util.toCat("at", "code="+message.getMqFromqq());
        String msg = at + " " +
                "\r\n" + result;
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), msg);
        return true;
    }
}
