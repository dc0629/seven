package top.flagshen.myqq.service.strategy.study;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.dao.props.entity.PropsDO;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.props.IPropsService;
import top.flagshen.myqq.service.strategy.StudyStrategy;

@Service("使用道具")
public class UseProps implements StudyStrategy {

    @Autowired
    private IPropsService propsService;

    @Override
    public boolean study(MyQQMessage message) {
        // 准备使用的道具名
        String propName = message.getMqMsg();
        // 查询有没有这个道具，随便查一个
        PropsDO one = propsService.getOne(new LambdaQueryWrapper<PropsDO>()
                .eq(PropsDO::getQqNum, "333")
                .eq(PropsDO::getIsUsed, 0)
                .eq(PropsDO::getPropsName, propName)
                .last("limit 1"));
        if (one == null) {
            //发送群消息
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), "我没有这个道具");
            return true;
        }
        // 将使用状态修改为1，已使用
        one.setIsUsed(1);
        propsService.updateById(one);
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), "使用成功");
        return true;
    }
}
