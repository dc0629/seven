package top.flagshen.myqq.service.strategy.operation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.dao.userinfo.entity.UserInfoDO;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;
import top.flagshen.myqq.service.userinfo.IUserInfoService;

@Service("添加账号")
public class TianJiaZhangHao implements OperationStrategy {

    @Autowired
    private IUserInfoService userInfoService;

    @Override
    public boolean operation(MyQQMessage message) {
        String mqMsg = message.getMqMsg();
        int j = mqMsg.indexOf(" ");
        if (j < 0) {
            return true;
        }
        String qqNum = mqMsg.substring(0, j);// 账号
        String password = mqMsg.substring(j + 1);// 密码
        if (StringUtils.isBlank(password)) {
            return true;
        }
        // 先查询是否存在
        UserInfoDO user = userInfoService.getById(qqNum);
        if (user != null) {
            // 如果存在，就修改密码
            user.setPassword(password);
            userInfoService.updateById(user);
        } else {
            // 如果不存在，就添加
            UserInfoDO addUser = new UserInfoDO();
            addUser.setQqNum(qqNum);
            addUser.setPassword(password);
            userInfoService.save(addUser);
        }
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), "添加成功");
        return true;
    }
}
