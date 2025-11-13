package cn.iocoder.yudao.module.lowcode.integrator;

import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.lowcode.enums.ErrorCodeConstants.INTEGRATOR_FILE_SYNC_EXECUTING_ERROR;

/**
 * 集成器同步锁 Redis DAO
 *
 * @author leo
 */
@Repository
public class IntegratorExecutorLock {

    public static final String INTEGRATOR_EXECUTOR_LOCK = "integrator_executor:lock:%d";


    @Resource
    private RedissonClient redissonClient;


    @SneakyThrows
    public void lock(Long id, Long ttl, Runnable runnable) {
        RLock lock = redissonClient.getLock(formatKey(id));
        if (lock.tryLock(1, ttl, TimeUnit.SECONDS)) {
            try {
                //执行逻辑
                runnable.run();
            } finally {
                lock.unlock();
            }
        } else {
            throw exception(INTEGRATOR_FILE_SYNC_EXECUTING_ERROR);
        }
    }

    private static String formatKey(Long id) {
        return String.format(INTEGRATOR_EXECUTOR_LOCK, id);
    }

}
