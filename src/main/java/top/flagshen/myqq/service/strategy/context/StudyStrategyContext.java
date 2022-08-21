package top.flagshen.myqq.service.strategy.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.flagshen.myqq.common.Permissions;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.StudyStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StudyStrategyContext {

    private final Map<String, StudyStrategy> strategyMap = new ConcurrentHashMap<>();

    @Autowired
    public StudyStrategyContext(Map<String, StudyStrategy> strategyMap) {
        strategyMap.forEach((k, v)-> this.strategyMap.put(k, v));
    }

    @Permissions(groupNums = "333")
    public boolean study(MyQQMessage message, String operation) {
        StudyStrategy studyStrategy = strategyMap.get(operation);
        return studyStrategy != null ? studyStrategy.study(message) : false;
    }

}
