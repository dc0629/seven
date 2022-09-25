package top.flagshen.myqq.service.log.impl;

import cn.hutool.core.thread.NamedThreadFactory;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.flagshen.myqq.dao.log.entity.OperationLogDO;
import top.flagshen.myqq.dao.log.mapper.OperationLogMapper;
import top.flagshen.myqq.service.log.IOperationLogService;

import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 操作日志表 服务实现类
 * </p>
 *
 * @author 17460
 * @since 2022-09-25
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLogDO> implements IOperationLogService {

    /**
     * 用户活跃度标签线程池
     */
    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(8, 8, 1L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory("OPERATION_LOG_POOL",false));

    /**
     * Close.
     */
    @PreDestroy
    public void close() {
        THREAD_POOL.shutdown();
    }



    public void saveOperationLog(String qq, String operationType, String operationCategoryType, String operationHarvest) {
        try {
            THREAD_POOL.execute(() -> {
                saveOperationLog2(qq, operationType, operationCategoryType, operationHarvest);
            });
        } catch (Exception e) {
            // 阻塞队列已满且超出最大线程数后直接记录
            saveOperationLog2(qq, operationType, operationCategoryType, operationHarvest);
        }
    }

    private void saveOperationLog2(String qq, String operationType, String operationCategoryType, String operationHarvest) {
        OperationLogDO operationLogDO = new OperationLogDO();
        operationLogDO.setQqNum(qq);
        operationLogDO.setOperationType(operationType);
        operationLogDO.setOperationCategoryType(operationCategoryType);
        operationLogDO.setOperationHarvest(operationHarvest);
        operationLogDO.setOperationTime(new Date());
        this.save(operationLogDO);
    }
}
