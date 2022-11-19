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

    /**
     * 获取qq在14天内未使用的血包数量
     * @param qq
     * @return
     */
    int getBloodCount(String qq);

    /**
     * 新增血包
     * @param num
     */
    void addBlood(String qq, int num);

    /**
     * 使用血包
     */
    void useBlood(String qq);
}
