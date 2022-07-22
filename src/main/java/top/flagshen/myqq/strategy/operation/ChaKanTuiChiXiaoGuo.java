package top.flagshen.myqq.strategy.operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;
import top.flagshen.myqq.common.RedisConstant;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.XiaoshenTemplate;
import top.flagshen.myqq.entity.MyQQMessage;
import top.flagshen.myqq.entity.ReqResult;
import top.flagshen.myqq.strategy.OperationStrategy;
import top.flagshen.myqq.util.ContentUtil;

@Service("查看推迟效果")
public class ChaKanTuiChiXiaoGuo implements OperationStrategy {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private final XiaoshenTemplate xsTemplate;

    public ChaKanTuiChiXiaoGuo(XiaoshenTemplate xsTemplate) {
        this.xsTemplate = xsTemplate;
    }

    @Override
    public boolean operation(MyQQMessage message) {
        String template = redisTemplate.opsForValue().get(RedisConstant.LATER_TEMPLATE);
        if (StringUtils.isEmpty(template)) {
            xsTemplate.sendMsgEx(message.getMqRobot(), 0, TypeConstant.MSGTYPE_GROUP,
                    message.getMqFromid(), null, "没设置推迟模板");
            return true;
        }
        //发送群消息
        xsTemplate.sendMsgEx(message.getMqRobot(), 0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, ContentUtil.getContent(template,
                        redisTemplate.opsForValue().get(RedisConstant.NAME),
                        redisTemplate.opsForValue().get(RedisConstant.TITLE),
                        redisTemplate.opsForValue().get(RedisConstant.URL),
                        redisTemplate.opsForValue().get(RedisConstant.NUMBER)));
        return true;
    }
}
