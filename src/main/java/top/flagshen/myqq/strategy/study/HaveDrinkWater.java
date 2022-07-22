package top.flagshen.myqq.strategy.study;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.common.TypeConstant;
import top.flagshen.myqq.common.XiaoshenTemplate;
import top.flagshen.myqq.entity.MyQQMessage;
import top.flagshen.myqq.strategy.StudyStrategy;

@Service("今天我喝水啦")
public class HaveDrinkWater implements StudyStrategy {

    private final XiaoshenTemplate xsTemplate;

    public HaveDrinkWater(XiaoshenTemplate xsTemplate) {
        this.xsTemplate = xsTemplate;
    }

    @Autowired
    private ScoreUtil scoreUtil;

    @Override
    public boolean study(MyQQMessage message) {
        //发送群消息
        xsTemplate.sendMsgEx(message.getMqRobot(), 0, TypeConstant.MSGTYPE_GROUP,
                message.getMqFromid(), null, scoreUtil.scoreCalculation(5));
        return true;
    }
}
