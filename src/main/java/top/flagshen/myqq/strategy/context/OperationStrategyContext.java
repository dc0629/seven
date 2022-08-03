package top.flagshen.myqq.strategy.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.flagshen.myqq.common.Permissions;
import top.flagshen.myqq.entity.MyQQMessage;
import top.flagshen.myqq.strategy.OperationStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OperationStrategyContext {

    private final Map<String, OperationStrategy> strategyMap = new ConcurrentHashMap<>();

    @Autowired
    public OperationStrategyContext(Map<String, OperationStrategy> strategyMap) {
        strategyMap.forEach((k, v)-> this.strategyMap.put(k, v));
    }

    @Permissions(groupNums = "111,222")
    public boolean operation(MyQQMessage message, String operation) {
        OperationStrategy operationStrategy = strategyMap.get(operation);
        return operationStrategy != null ? operationStrategy.operation(message) : false;
    }

}
