package com.iteng.dynamic.thread.pool.job;

import com.iteng.dynamic.thread.pool.model.ThreadPoolConfig;
import com.iteng.dynamic.thread.pool.service.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 监听线程池配置
 */
public class ThreadPoolListenerJob {
    private final Logger logger = LoggerFactory.getLogger(ThreadPoolListenerJob.class);

    private ThreadPoolManager threadPoolManager;

    public ThreadPoolListenerJob(ThreadPoolManager threadPoolManager) {
        this.threadPoolManager = threadPoolManager;
    }

    @Scheduled(cron = "0/10 * * * * ?")
    public void startListenerJob() {
        logger.info("动态线程池 - 检查告警开始【间隔10s】");
        Map<String, ThreadPoolExecutor> threadPoolMap = threadPoolManager.getThreadPoolMap();
        Map<String, ThreadPoolConfig> configMap = threadPoolManager.getThreadPoolConfigMap();
        Set<String> threadPoolNames = threadPoolMap.keySet();
        int count = 0;
        if (Objects.isNull(threadPoolNames) || threadPoolNames.isEmpty()) {
            logger.info("动态线程池 - 检查告警结束，发现" + count + "个警告。");
            return;
        }
        for (String threadPoolName : threadPoolNames) {
            ThreadPoolExecutor threadPoolExecutor = threadPoolMap.get(threadPoolName);
            ThreadPoolConfig config = configMap.get(threadPoolName);
            if (config == null){
                continue;
            }
            logger.warn("活跃线程数:{}, 总任务数量:{}, 已完成任务数量:{}", threadPoolExecutor.getActiveCount(), threadPoolExecutor.getTaskCount(), threadPoolExecutor.getCompletedTaskCount());
            logger.warn("检查 - 队列元素:{}, 队列总容量:{}, 阈值:{}%", threadPoolExecutor.getQueue().size(),
                    threadPoolExecutor.getQueue().remainingCapacity() + threadPoolExecutor.getQueue().size(), config.getQueueWarnThreshold());
            Boolean queueWarn = config.getQueueWarn();
            if (queueWarn != null && queueWarn) {
                Integer queueWarnThreshold = config.getQueueWarnThreshold();
                if (queueWarnThreshold == null) {
                    continue;
                }
                // 阈值
                float qwt = (float)(queueWarnThreshold * 0.01);
                BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();
                // 当前队列元素个数
                int queueSize = queue.size();
                // 队列剩余容量
                int remainingCapacity = queue.remainingCapacity();
                // 队列总容量
                int totalCapacity = queueSize + remainingCapacity;
                // 超出阈值
                if(queueSize >= totalCapacity * qwt){
                    // TODO 发送超出阈值告警
                    logger.error("报警 - 队列元素:{}, 队列总容量:{}, 超出阈值:{}%", queueSize, totalCapacity, queueWarnThreshold);
                    count++;
                }
            }
        }
        logger.info("动态线程池 - 检查告警结束，发现" + count + "个警告。");
    }
}
