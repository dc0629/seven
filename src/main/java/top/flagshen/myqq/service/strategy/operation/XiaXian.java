package top.flagshen.myqq.service.strategy.operation;

import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.XiaoshenTemplate;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;

import java.util.HashMap;
import java.util.Map;

@Service("下线")
public class XiaXian implements OperationStrategy {

    private final XiaoshenTemplate xsTemplate;

    public XiaXian(XiaoshenTemplate xsTemplate) {
        this.xsTemplate = xsTemplate;
    }

    @Override
    public boolean operation(MyQQMessage message) {
        Map<String, Object> map = new HashMap<>();
        map.put("c1", message.getMqRobot());
        //发送群消息
        xsTemplate.tongyongPost("Api_Logout", map);
        return true;
    }
}
