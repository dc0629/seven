package top.flagshen.myqq.service.strategy.operation;

import org.springframework.stereotype.Service;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;

import java.util.HashMap;
import java.util.Map;

@Service("下线")
public class XiaXian implements OperationStrategy {

    @Override
    public boolean operation(MyQQMessage message) {
        Map<String, Object> map = new HashMap<>();
        map.put("c1", message.getMqRobot());
        return true;
    }
}
