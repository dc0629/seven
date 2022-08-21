package top.flagshen.myqq.dao.forbidden.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Param;
import top.flagshen.myqq.dao.forbidden.dto.JinYanCount;
import top.flagshen.myqq.dao.forbidden.entity.ForbiddenLogDO;

import java.util.List;

/**
 * <p>
 * 禁言记录表 Mapper 接口
 * </p>
 *
 * @author 17460
 * @since 2022-07-26
 */
public interface ForbiddenLogMapper extends BaseMapper<ForbiddenLogDO> {

    List<JinYanCount> getJinyanCount(@Param("groupNum") String groupNum);

}
