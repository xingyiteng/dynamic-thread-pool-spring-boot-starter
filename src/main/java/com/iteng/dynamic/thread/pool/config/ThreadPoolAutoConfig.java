package com.iteng.dynamic.thread.pool.config;

import com.iteng.dynamic.thread.pool.common.ThreadPoolConstants;
import com.iteng.dynamic.thread.pool.job.ThreadPoolListenerJob;
import com.iteng.dynamic.thread.pool.model.ThreadPoolConfig;
import com.iteng.dynamic.thread.pool.model.VariableLinkedBlockingQueue;
import com.iteng.dynamic.thread.pool.monitor.ThreadPoolMetricsInitializer;
import com.iteng.dynamic.thread.pool.registry.IRegistry;
import com.iteng.dynamic.thread.pool.service.ThreadPoolManager;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自动配置类
 */
@Configuration
@EnableScheduling
public class ThreadPoolAutoConfig {

    private final Logger logger = LoggerFactory.getLogger(ThreadPoolAutoConfig.class);

    @Bean
    public ThreadPoolManager threadPoolManager(ApplicationContext applicationContext,
                                               Map<String, ThreadPoolExecutor> threadPoolExecutorMap,
                                               IRegistry registry) {
        // 应用名称
        String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");

        // 设置缺省值
        if (Objects.isNull(applicationName)) {
            applicationName = UUID.randomUUID().toString();
        }

        // 线程池配置Map
        Map<String, ThreadPoolConfig> threadPoolExecutorConfigMap = new HashMap<>();

        // 从配置中心读取缓存并填充
        fillThreadPoolFromRegistry(applicationName, registry, threadPoolExecutorMap, threadPoolExecutorConfigMap);
        ThreadPoolManager threadPoolManager = new ThreadPoolManager(applicationName, threadPoolExecutorMap, threadPoolExecutorConfigMap);
        logger.info("动态线程池 - ThreadPoolManager初始化完成");
        return threadPoolManager;
    }

    @Bean
    public ThreadPoolListenerJob threadPoolListenerJob(ThreadPoolManager threadPoolManager) {
        ThreadPoolListenerJob threadPoolListenerJob = new ThreadPoolListenerJob(threadPoolManager);
        logger.info("动态线程池 - ThreadPoolListenerJob初始化完成");
        return threadPoolListenerJob;
    }

    @Bean
    @ConditionalOnProperty(name = "dynamic.thread.pool.config.monitor", havingValue = "true")
    public ThreadPoolMetricsInitializer threadPoolMetricsInitializer(MeterRegistry meterRegistry, ThreadPoolManager threadPoolManager){
        ThreadPoolMetricsInitializer threadPoolMetricsInitializer = new ThreadPoolMetricsInitializer(meterRegistry, threadPoolManager);
        logger.info("动态线程池 - ThreadPoolMetricsInitializer监控初始化完成");
        return threadPoolMetricsInitializer;
    }

    private void fillThreadPoolFromRegistry(String applicationName, IRegistry registry,
                                                Map<String, ThreadPoolExecutor> threadPoolExecutorMap,
                                                Map<String, ThreadPoolConfig> threadPoolExecutorConfigMap){
        // 获取配置中心数据
        List<ThreadPoolConfig> threadPoolConfigs = registry.queryThreadPoolConfigListByKey(ThreadPoolConstants.THREAD_POOL_REGISTRY_KEY + applicationName);
        if (threadPoolConfigs == null || threadPoolConfigs.isEmpty()) {
            return;
        }

        // 填充线程池Map、配置Map
        for (ThreadPoolConfig threadPoolConfig : threadPoolConfigs) {
            if (threadPoolConfig == null) {
                continue;
            }

            String threadPoolName = threadPoolConfig.getThreadPoolName();
            Integer corePoolSize = threadPoolConfig.getCorePoolSize();
            Integer maximumPoolSize = threadPoolConfig.getMaximumPoolSize();
            Integer queueCapacity = threadPoolConfig.getQueueCapacity();
            Boolean queueWarn = threadPoolConfig.getQueueWarn();
            Integer queueWarnThreshold = threadPoolConfig.getQueueWarnThreshold();
            Boolean queueAdapt = threadPoolConfig.getQueueAdapt();
            Integer queueAdaptThreshold = threadPoolConfig.getQueueAdaptThreshold();
            Integer queueAdaptMaxCapacity = threadPoolConfig.getQueueAdaptMaxCapacity();
            Boolean threadAdapt = threadPoolConfig.getThreadAdapt();
            Integer threadAdaptThreshold = threadPoolConfig.getThreadAdaptThreshold();
            Integer threadAdaptMaximumPoolSize = threadPoolConfig.getThreadAdaptMaximumPoolSize();

            // 填充线程池Map
            if (threadPoolExecutorMap != null) {
                ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolName);
                if (threadPoolExecutor != null) {
                    if (corePoolSize != null) {
                        threadPoolExecutor.setCorePoolSize(corePoolSize);
                    }
                    if (maximumPoolSize != null) {
                        threadPoolExecutor.setMaximumPoolSize(maximumPoolSize);
                    }
                    BlockingQueue<Runnable> queue = threadPoolExecutor.getQueue();
                    if (queue instanceof VariableLinkedBlockingQueue && queueCapacity != null){
                        ((VariableLinkedBlockingQueue<Runnable>) queue).setCapacity(queueCapacity);
                    }
                }
            }

            // 填充配置Map
            ThreadPoolConfig config = new ThreadPoolConfig(threadPoolName, corePoolSize, maximumPoolSize,
                    queueCapacity, queueWarn, queueWarnThreshold, queueAdapt, queueAdaptThreshold,
                    queueAdaptMaxCapacity, threadAdapt, threadAdaptThreshold, threadAdaptMaximumPoolSize);
            threadPoolExecutorConfigMap.put(threadPoolName, config);
        }
    }
}
