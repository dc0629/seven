package top.flagshen.myqq.strategy;

import top.flagshen.myqq.entity.MyQQMessage;

/**
 * 重要操作的策略模式接口
 */
public interface OperationStrategy {

    /**
     * 操作
     * @param message
     * @return
     */
    boolean operation(MyQQMessage message);
}
