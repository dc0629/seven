package top.flagshen.myqq.dao.userinfo.mapper;

import top.flagshen.myqq.dao.userinfo.entity.UserJuedouDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 决斗记录表 Mapper 接口
 * </p>
 *
 * @author 17460
 * @since 2022-10-29
 */
public interface UserJuedouMapper extends BaseMapper<UserJuedouDO> {

    public void updateWeekWin();

}
