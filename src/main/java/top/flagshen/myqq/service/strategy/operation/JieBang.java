package top.flagshen.myqq.service.strategy.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.dao.userinfo.entity.UserInfoDO;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;
import top.flagshen.myqq.service.userinfo.IUserInfoService;

@Service("解除绑定")
public class JieBang implements OperationStrategy {

    @Autowired
    private IUserInfoService userInfoService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean operation(MyQQMessage message) {
        // 先查询是否存在
        UserInfoDO user = userInfoService.getById(message.getMqMsg());
        if (user != null) {
            // 如果存在，就修改密码
            userInfoService.unbind(user.getQqNum());
            redisTemplate.delete(user.getOpenId());
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), "解除绑定");
        } else {
            // 如果不存在，就提示
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), "账号不存在");
        }
        return true;
    }
}
