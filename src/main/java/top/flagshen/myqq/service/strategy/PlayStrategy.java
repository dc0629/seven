package top.flagshen.myqq.service.strategy;

import top.flagshen.myqq.entity.common.MyQQMessage;

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
