package top.flagshen.myqq.service.props.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import top.flagshen.myqq.dao.props.dto.PropsTotal;
import top.flagshen.myqq.dao.props.entity.PropsDO;
import top.flagshen.myqq.dao.props.mapper.PropsMapper;
import top.flagshen.myqq.service.props.IPropsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Override
    public int getBloodCount(String qq) {
        return propsMapper.getBloodCount(qq);
    }

    @Override
    public void addBlood(String qq, int num) {
        List<PropsDO> list = new ArrayList<>();
        for (int i = 0;i < num;i ++) {
            PropsDO propsDO = new PropsDO().setQqNum(qq).setPropsName("医疗包");
            list.add(propsDO);
        }
        this.saveBatch(list);
    }

    @Override
    public void useBlood(String qq) {
        PropsDO blood = propsMapper.selectOne(new LambdaQueryWrapper<PropsDO>()
                .eq(PropsDO::getQqNum, qq).eq(PropsDO::getPropsName, "医疗包")
                .orderByAsc(PropsDO::getCreateTime).last("limit 1"));
        blood.setIsUsed(1);
        propsMapper.updateById(blood);
    }
}
