package com.iteng.dynamic.thread.pool.job;

import com.iteng.dynamic.thread.pool.model.ThreadPoolConfig;
import com.iteng.dynamic.thread.pool.model.VariableLinkedBlockingQueue;
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
        logger.info(">>>>>>>>>>      动态线程池 - 定时任务开始 【间隔10s】      <<<<<<<<<<");
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
            logger.warn("队列元素:{}, 队列总容量:{}, 队列容量报警阈值:{}%, 队列自适应阈值:{}%", threadPoolExecutor.getQueue().size(),
                    threadPoolExecutor.getQueue().remainingCapacity() + threadPoolExecutor.getQueue().size(), config.getQueueWarnThreshold(), config.getQueueAdaptThreshold());

            // 队列阈值告警
            Boolean queueWarn = config.getQueueWarn();
            if (queueWarn != null && queueWarn) {
                if(queueSizeWarn(threadPoolExecutor, config)){
                    count++;
                }
            }

            // 队列自适应告警
            Boolean queueAdapt = config.getQueueAdapt();
            if(queueAdapt != null && queueAdapt){
                queueAdaptWarn(threadPoolExecutor, config);
            }
        }
        logger.info(">>>>>>>>>>      动态线程池 - 定时任务结束，发现" + count + "个警告。      <<<<<<<<<<");

    }

    /**
     * 队列容量阈值报警
     * @param threadPoolExecutor
     * @param threadPoolConfig
     * @return
     */
    private boolean queueSizeWarn(ThreadPoolExecutor threadPoolExecutor, ThreadPoolConfig threadPoolConfig){
        Integer queueWarnThreshold = threadPoolConfig.getQueueWarnThreshold();
        if (queueWarnThreshold == null) {
            return false;
        }
        // 阈值
        float qwt = (float)(queueWarnThreshold * 0.01);
        BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();
        // 当前队列元素个数
        int queueSize = queue.size();
        // 剩余队列容量
        int remainingCapacity = queue.remainingCapacity();
        // 队列总容量
        int totalCapacity = queueSize + remainingCapacity;
        // 超出阈值
        if(queueSize >= totalCapacity * qwt){
            // TODO 发送超出阈值告警
            logger.error("队列容量阈值报警 - 队列元素:{}, 队列总容量:{}, 超出阈值:{}%", queueSize, totalCapacity, queueWarnThreshold);
            return true;
        }
        return false;
    }

    /**
     * 队列自适应报警
     * @param threadPoolExecutor
     * @param threadPoolConfig
     */
    private void queueAdaptWarn(ThreadPoolExecutor threadPoolExecutor, ThreadPoolConfig threadPoolConfig){
        Integer queueAdaptThreshold = threadPoolConfig.getQueueAdaptThreshold();
        Integer queueAdaptMaxCapacity = threadPoolConfig.getQueueAdaptMaxCapacity();
        if (queueAdaptThreshold == null || queueAdaptMaxCapacity == null) {
            return;
        }
        // 阈值
        float qat = (float)(queueAdaptThreshold * 0.01);
        BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();
        // 当前队列元素个数
        int queueSize = queue.size();
        // 剩余队列容量
        int remainingCapacity = queue.remainingCapacity();
        // 队列总容量
        int totalCapacity = queueSize + remainingCapacity;
        if(queueSize >= totalCapacity * qat){
            // 自适应调整
            if(queue instanceof VariableLinkedBlockingQueue){
                int newCapacity = Math.min(queueAdaptMaxCapacity, (int)(totalCapacity * 1.5));
                ((VariableLinkedBlockingQueue<Runnable>) queue).setCapacity(newCapacity);
                // TODO 发送队列自适应告警
                logger.error("队列自适应调整报警 - 队列元素:{}, 旧队列总容量:{}, 新队列总容量:{}", queueSize, totalCapacity, newCapacity);
            }
        }
    }
}
