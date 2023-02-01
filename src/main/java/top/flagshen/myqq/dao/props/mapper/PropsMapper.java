package top.flagshen.myqq.dao.props.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Param;
import top.flagshen.myqq.dao.props.dto.PropsTotal;
import top.flagshen.myqq.dao.props.entity.PropsDO;
import top.flagshen.myqq.entity.userinfo.resp.RankResp;

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

    int getBloodCount(@Param("qq") String qq);

    PropsDO getFirstBlood(@Param("qq") String qq);

    /**
     * 获取使用了的医疗包排行
     * @return
     */
    List<RankResp> getRankUsedBlood();

    /**
     * 获取我的使用医疗包排行
     * @return
     */
    String getMyRankBlood(@Param("qqNum") String qqNum);
}
