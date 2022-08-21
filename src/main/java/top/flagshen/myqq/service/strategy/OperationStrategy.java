package top.flagshen.myqq.service.strategy;

import top.flagshen.myqq.entity.common.MyQQMessage;

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
