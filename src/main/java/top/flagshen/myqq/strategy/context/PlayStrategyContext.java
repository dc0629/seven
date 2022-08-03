package top.flagshen.myqq.strategy.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.flagshen.myqq.entity.MyQQMessage;
import top.flagshen.myqq.strategy.PlayStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlayStrategyContext {

    private final Map<String, PlayStrategy> strategyMap = new ConcurrentHashMap<>();

    @Autowired
    public PlayStrategyContext(Map<String, PlayStrategy> strategyMap) {
        strategyMap.forEach((k, v)-> this.strategyMap.put(k, v));
    }

    public boolean play(MyQQMessage message, String operation) {
        PlayStrategy playStrategy = strategyMap.get(operation);
        return playStrategy != null ? playStrategy.play(message) : false;
    }

}
