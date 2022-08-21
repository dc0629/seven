package top.flagshen.myqq.service.props.impl;

import org.springframework.beans.factory.annotation.Autowired;
import top.flagshen.myqq.dao.props.dto.PropsTotal;
import top.flagshen.myqq.dao.props.entity.PropsDO;
import top.flagshen.myqq.dao.props.mapper.PropsMapper;
import top.flagshen.myqq.service.props.IPropsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 道具 服务实现类
 * </p>
 *
 * @author 邓超
 * @since 2022-07-20
 */
@Service
public class PropsServiceImpl extends ServiceImpl<PropsMapper, PropsDO> implements IPropsService {

    @Autowired
    private PropsMapper propsMapper;

    @Override
    public List<PropsTotal> getPropsCount(String qq, Integer isUsed) {
        return propsMapper.getPropsCount(qq, isUsed);
    }
}
