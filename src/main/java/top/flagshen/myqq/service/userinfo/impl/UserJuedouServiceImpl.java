package top.flagshen.myqq.service.userinfo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import top.flagshen.myqq.dao.userinfo.entity.UserJuedouDO;
import top.flagshen.myqq.dao.userinfo.mapper.UserJuedouMapper;
import top.flagshen.myqq.service.userinfo.IUserJuedouService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 决斗记录表 服务实现类
 * </p>
 *
 * @author 17460
 * @since 2022-10-29
 */
@Service
public class UserJuedouServiceImpl extends ServiceImpl<UserJuedouMapper, UserJuedouDO> implements IUserJuedouService {

    @Autowired
    private UserJuedouMapper userJuedouMapper;

    @Override
    public void updateWeekWin() {
        userJuedouMapper.updateWeekWin();
    }
}
