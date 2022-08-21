package top.flagshen.myqq.service.forbidden.impl;

import org.springframework.beans.factory.annotation.Autowired;
import top.flagshen.myqq.dao.forbidden.dto.JinYanCount;
import top.flagshen.myqq.dao.forbidden.entity.ForbiddenLogDO;
import top.flagshen.myqq.dao.forbidden.mapper.ForbiddenLogMapper;
import top.flagshen.myqq.service.forbidden.IForbiddenLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Autowired
    private ForbiddenLogMapper forbiddenLogMapper;

    @Override
    public List<JinYanCount> getJinyanCount(String groupNum) {
        return forbiddenLogMapper.getJinyanCount(groupNum);
    }
}
