package top.flagshen.myqq.strategy;

import top.flagshen.myqq.entity.MyQQMessage;
import top.flagshen.myqq.entity.ReqResult;

/**
 * 玩法的策略模式接口
 */
public interface PlayStrategy {

    /**
     * 玩
     * @param message
     * @return
     */
    boolean play(MyQQMessage message);
}
