package top.flagshen.myqq.service.strategy.operation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.RobotTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;

import java.util.Arrays;
import java.util.List;

@Service("s")
public class Send implements OperationStrategy {

    @Autowired
    private RobotTemplate robotTemplate;

    private static final List<String> manageGroup = Arrays.asList("xxx");

    /**
     * 向着对应群发消息，指令为  /s 舵主 发消息
     * @param message
     * @return
     */
    @Override
    public boolean operation(MyQQMessage message) {
        if (StringUtils.isBlank(message.getMqMsg())) {
            return true;
        }
        int j = message.getMqMsg().indexOf(" ");
        if (j < 0) {
            return true;
        }
        String operate = message.getMqMsg().substring(0, j);// 发给哪个群
        String content = message.getMqMsg().substring(j + 1);// 发的内容
        if (StringUtils.isEmpty(content)) {
            return true;
        }
        String qunNum = "";
        if ("all".equals(operate)) {
            for (String groupQQ: manageGroup) {
                //发送群消息
                robotTemplate.sendMsgEx("1462152250", groupQQ, content);
            }
        }
         if ("v1".equalsIgnoreCase(operate)) {
            qunNum = "111";
        } else if ("v2".equalsIgnoreCase(operate)) {
            qunNum = "222";
        } else if ("管理".equals(operate)) {
            qunNum = "333";
        } else if ("占卜".equals(operate)) {
            qunNum = "444";
        }
        //发送群消息
        robotTemplate.sendMsgEx(message.getMqRobot(), qunNum, content);
        return true;
    }
}
