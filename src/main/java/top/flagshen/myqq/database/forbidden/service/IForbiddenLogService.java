package top.flagshen.myqq.database.forbidden.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.flagshen.myqq.database.forbidden.dto.JinYanCount;
import top.flagshen.myqq.database.forbidden.entity.ForbiddenLogDO;

import java.util.List;

/**
 * <p>
 * 禁言记录表 服务类
 * </p>
 *
 * @author 17460
 * @since 2022-07-26
 */
public interface IForbiddenLogService extends IService<ForbiddenLogDO> {

    /**
     * 查禁言次数前十的人，传群号就是对应群下
     * @param groupNum
     * @return
     */
    List<JinYanCount> getJinyanCount(String groupNum);

}
