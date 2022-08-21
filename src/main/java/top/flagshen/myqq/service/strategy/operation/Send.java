package top.flagshen.myqq.service.strategy.operation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.XiaoshenTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;

import java.util.Arrays;
import java.util.List;

@Service("s")
public class Send implements OperationStrategy {

    private final XiaoshenTemplate xsTemplate;

    public Send(XiaoshenTemplate xsTemplate) {
        this.xsTemplate = xsTemplate;
    }

    private static final List<String> manageGroup = Arrays.asList("1","2","3","4","5","6");

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
        String operate = message.getMqMsg().substring(0, j);// 发给哪个群
        String content = message.getMqMsg().substring(j + 1);// 发的内容
        if (StringUtils.isEmpty(content)) {
            return true;
        }
        String qunNum = "";
        if ("all".equals(operate)) {
            for (String groupQQ: manageGroup) {
                //发送群消息
                xsTemplate.sendMsgEx("444",
                        0, TypeConstant.MSGTYPE_GROUP,
                        groupQQ, null, content);
            }
        }
        if ("弟子".equals(operate)) {
            qunNum = "1";
        } else if ("执事".equals(operate)) {
            qunNum = "2";
        } else if ("舵主".equals(operate)) {
            qunNum = "3";
        } else if ("管理".equals(operate)) {
            qunNum = "4";
        } else if ("1".equals(operate)) {
            qunNum = "5";
        } else if ("2".equals(operate)) {
            qunNum = "6";
        }
        //发送群消息
        xsTemplate.sendMsgEx(message.getMqRobot(),
                0, TypeConstant.MSGTYPE_GROUP,
                qunNum, null, content);
        return true;
    }
}
