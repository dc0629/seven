package top.flagshen.myqq.service.blacklist.impl;

import top.flagshen.myqq.dao.blacklist.entity.BlacklistDO;
import top.flagshen.myqq.dao.blacklist.mapper.BlacklistMapper;
import top.flagshen.myqq.service.blacklist.IBlacklistService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 黑名单 服务实现类
 * </p>
 *
 * @author 邓超
 * @since 2022-07-12
 */
@Service
public class BlacklistServiceImpl extends ServiceImpl<BlacklistMapper, BlacklistDO> implements IBlacklistService {

}
