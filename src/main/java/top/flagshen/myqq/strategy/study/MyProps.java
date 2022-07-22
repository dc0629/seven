package top.flagshen.myqq.strategy.study;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.XiaoshenTemplate;
import top.flagshen.myqq.database.props.dto.PropsTotal;
import top.flagshen.myqq.database.props.service.IPropsService;
import top.flagshen.myqq.entity.MyQQMessage;
import top.flagshen.myqq.strategy.StudyStrategy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("看看我的背包")
public class MyProps implements StudyStrategy {

    private final XiaoshenTemplate xsTemplate;

    public MyProps(XiaoshenTemplate xsTemplate) {
        this.xsTemplate = xsTemplate;
    }

    @Autowired
    private IPropsService propsService;

    @Override
    public boolean study(MyQQMessage message) {
        StringBuffer sb = new StringBuffer();
        List<PropsTotal> propsTotal = propsService.getPropsCount("333", 0);
        if (CollectionUtils.isEmpty(propsTotal)) {
            //发送群消息
            xsTemplate.sendMsgEx(message.getMqRobot(), 0, TypeConstant.MSGTYPE_GROUP,
                    message.getMqFromid(), null, "我的背包空空的");
            return true;
        }

        sb.append("我的背包里有:\n");
        Map<String, Integer> propsMap = propsTotal.stream().collect(Collectors.toMap(PropsTotal::getPropsName, PropsTotal::getTotal));
        for (Map.Entry<String, Integer> entry : propsMap.entrySet()) {
            sb.append(entry.getKey()).append("*").append(entry.getValue()).append("\n");
        }
        //发送群消息
        xsTemplate.sendMsgEx(message.getMqRobot(), 0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, sb.toString());
        return true;
    }
}
