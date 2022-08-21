package top.flagshen.myqq.service.props;

import com.baomidou.mybatisplus.extension.service.IService;
import top.flagshen.myqq.dao.props.dto.PropsTotal;
import top.flagshen.myqq.dao.props.entity.PropsDO;

import java.util.List;

/**
 * <p>
 * 道具 服务类
 * </p>
 *
 * @author 邓超
 * @since 2022-07-20
 */
public interface IPropsService extends IService<PropsDO> {

    /**
     * 获取各个道具数量
     * @param qq
     * @param isUsed 是否使用，传null查全部
     * @return
     */
    List<PropsTotal>  getPropsCount(String qq, Integer isUsed);
}
