package top.flagshen.myqq.strategy.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.flagshen.myqq.entity.MyQQMessage;
import top.flagshen.myqq.strategy.StudyStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StudyStrategyContext {

    private final Map<String, StudyStrategy> strategyMap = new ConcurrentHashMap<>();

    @Autowired
    public StudyStrategyContext(Map<String, StudyStrategy> strategyMap) {
        strategyMap.forEach((k, v)-> this.strategyMap.put(k, v));
    }

    public boolean study(String operation, MyQQMessage message) {

        if (!"xxx".equals(message.getMqFromid())) {
            return false;
        }
        StudyStrategy studyStrategy = strategyMap.get(operation);
        return studyStrategy != null ? studyStrategy.study(message) : false;
    }

}
