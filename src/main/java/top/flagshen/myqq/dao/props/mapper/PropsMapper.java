package top.flagshen.myqq.dao.props.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Param;
import top.flagshen.myqq.dao.props.dto.PropsTotal;
import top.flagshen.myqq.dao.props.entity.PropsDO;

import java.util.List;

/**
 * <p>
 * 道具 Mapper 接口
 * </p>
 *
 * @author 邓超
 * @since 2022-07-20
 */
public interface PropsMapper extends BaseMapper<PropsDO> {

    List<PropsTotal> getPropsCount(@Param("qq") String qq, @Param("isUsed") Integer isUsed);
}
