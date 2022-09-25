package top.flagshen.myqq.service.log;

import top.flagshen.myqq.dao.log.entity.OperationLogDO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 操作日志表 服务类
 * </p>
 *
 * @author 17460
 * @since 2022-09-25
 */
public interface IOperationLogService extends IService<OperationLogDO> {

    /**
     * 记录一条日志
     */
    void saveOperationLog(String qq, String operationType, String operationCategoryType, String operationHarvest);

}
