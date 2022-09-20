package top.flagshen.myqq.service.strategy.operation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.flagshen.myqq.common.RobotTemplate;
import top.flagshen.myqq.dao.forbidden.dto.JinYanCount;
import top.flagshen.myqq.service.forbidden.IForbiddenLogService;
import top.flagshen.myqq.entity.common.MyQQMessage;
import top.flagshen.myqq.service.strategy.OperationStrategy;

import java.util.List;

/**
 * /查禁言排行 群号
 * 群号可为空
 */
@Service("查禁言排行")
public class ChaJinYanRank implements OperationStrategy {

    @Autowired
    private IForbiddenLogService forbiddenLogService;

    @Override
    public boolean operation(MyQQMessage message) {
        String groupNum = null;// 群号
        if (StringUtils.isNotBlank(message.getMqMsg()) && !"/查禁言排行".equals(message.getMqMsg())) {
            groupNum = message.getMqMsg();
        }

        List<JinYanCount> jinyanCount = forbiddenLogService.getJinyanCount(groupNum);
        if (CollectionUtils.isEmpty(jinyanCount)) {
            return true;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("禁言排行:");
        jinyanCount.forEach(jinYanCount -> {
            buffer.append("\r\n").append("qq: ")
                    .append(jinYanCount.getQqNum())
                    .append(" 次数: ")
                    .append(jinYanCount.getCount());
        });
        //发送群消息
        message.getSender().SENDER.sendGroupMsg(message.getMqFromid(), buffer.toString());
        return true;
    }
}
