package top.flagshen.myqq.database.forbidden.service.impl;

import top.flagshen.myqq.database.forbidden.entity.ForbiddenLogDO;
import top.flagshen.myqq.database.forbidden.mapper.ForbiddenLogMapper;
import top.flagshen.myqq.database.forbidden.service.IForbiddenLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 禁言记录表 服务实现类
 * </p>
 *
 * @author 17460
 * @since 2022-07-26
 */
@Service
public class ForbiddenLogServiceImpl extends ServiceImpl<ForbiddenLogMapper, ForbiddenLogDO> implements IForbiddenLogService {

}
