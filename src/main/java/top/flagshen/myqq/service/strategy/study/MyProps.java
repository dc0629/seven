package top.flagshen.myqq.service.strategy.study;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.flagshen.myqq.dao.props.dto.PropsTotal;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.props.IPropsService;
import top.flagshen.myqq.service.strategy.StudyStrategy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("看看我的背包")
public class MyProps implements StudyStrategy {

    @Autowired
    private IPropsService propsService;

    @Override
    public boolean study(MyQQMessage message) {
        StringBuffer sb = new StringBuffer();
        List<PropsTotal> propsTotal = propsService.getPropsCount("333", 0);
        if (CollectionUtils.isEmpty(propsTotal)) {
            //发送群消息
            message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), "我的背包空空的");
            return true;
        }

        sb.append("我的背包里有:");
        Map<String, Integer> propsMap = propsTotal.stream().collect(Collectors.toMap(PropsTotal::getPropsName, PropsTotal::getTotal));
        for (Map.Entry<String, Integer> entry : propsMap.entrySet()) {
            sb.append("\n").append(entry.getKey()).append("*").append(entry.getValue());
        }
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), sb.toString());
        return true;
    }
}
