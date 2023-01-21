package top.flagshen.myqq.dao.userinfo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import top.flagshen.myqq.dao.userinfo.entity.UserInfoDO;
import top.flagshen.myqq.entity.userinfo.resp.RankResp;

import java.util.List;

/**
 * <p>
 * 用户基本信息 Mapper 接口
 * </p>
 *
 * @author 17460
 * @since 2022-08-14
 */
public interface UserInfoMapper extends BaseMapper<UserInfoDO> {

    /**
     * 解除绑定
     * @param qq
     */
    void unbind(@Param("qq") String qq);

    /**
     * 获取银币排行
     * @return
     */
    List<RankResp> getRankCoin();

}
