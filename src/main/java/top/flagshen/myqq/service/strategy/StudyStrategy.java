package top.flagshen.myqq.service.strategy;

import top.flagshen.myqq.entity.common.MyQQMessage;

/**
 * 学习的策略模式接口
 */
public interface StudyStrategy {

    /**
     * 学习
     * @param message
     * @return
     */
    boolean study(MyQQMessage message);
}
