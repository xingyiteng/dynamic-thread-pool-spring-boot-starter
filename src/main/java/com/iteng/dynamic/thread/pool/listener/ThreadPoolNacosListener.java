package com.iteng.dynamic.thread.pool.listener;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.iteng.dynamic.thread.pool.common.ThreadPoolConstants;
import com.iteng.dynamic.thread.pool.model.ThreadPoolConfig;
import com.iteng.dynamic.thread.pool.model.VariableLinkedBlockingQueue;
import com.iteng.dynamic.thread.pool.service.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * nacos监听器
 */
public class ThreadPoolNacosListener {

    private final Logger logger = LoggerFactory.getLogger(ThreadPoolNacosListener.class);

    private NacosConfigManager nacosConfigManager;

    private ThreadPoolManager threadPoolManager;

    public ThreadPoolNacosListener(NacosConfigManager nacosConfigManager, ThreadPoolManager threadPoolManager){
        this.nacosConfigManager = nacosConfigManager;
        this.threadPoolManager = threadPoolManager;
        String applicationName = this.threadPoolManager.getApplicationName();
        try {
            this.nacosConfigManager.getConfigService().addListener(ThreadPoolConstants.THREAD_POOL_REGISTRY_KEY + applicationName,
                    ThreadPoolConstants.NACOS_GROUP,
                    new Listener() {
                        @Override
                        public Executor getExecutor() {
                            return Executors.newSingleThreadExecutor();
                        }
                        @Override
                        public void receiveConfigInfo(String configInfo) {
                            receiveThreadPoolConfig(configInfo);
                        }
                    });
        } catch (NacosException e) {
            logger.error("动态线程池 - 消息监听器（nacos）配置失败：{}", e.getErrMsg());
            throw new RuntimeException(e);
        }
    }

    private void receiveThreadPoolConfig(String configInfo) {
        List<ThreadPoolConfig> threadPoolConfigs = JSON.parseArray(configInfo, ThreadPoolConfig.class);
        if(threadPoolConfigs != null && !threadPoolConfigs.isEmpty()){
            for (ThreadPoolConfig threadPoolConfig : threadPoolConfigs) {
                updateThreadPoolAndConfig(threadPoolConfig);
            }
        }
    }

    private void updateThreadPoolAndConfig(ThreadPoolConfig threadPoolConfig){
        if (threadPoolConfig == null) {
            return;
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

        // 更新线程池Map
        ThreadPoolExecutor threadPoolExecutor = threadPoolManager.getThreadPoolMap().get(threadPoolName);
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

        // 更新配置Map
        Map<String, ThreadPoolConfig> threadPoolConfigMap = threadPoolManager.getThreadPoolConfigMap();
        ThreadPoolConfig config = threadPoolConfigMap.get(threadPoolName);
        if (config != null) {
            config.setCorePoolSize(corePoolSize);
            config.setMaximumPoolSize(maximumPoolSize);
            config.setQueueCapacity(queueCapacity);
            config.setQueueWarn(queueWarn);
            config.setQueueWarnThreshold(queueWarnThreshold);
            config.setQueueAdapt(queueAdapt);
            config.setQueueAdaptThreshold(queueAdaptThreshold);
            config.setQueueAdaptMaxCapacity(queueAdaptMaxCapacity);
            config.setThreadAdapt(threadAdapt);
            config.setThreadAdaptThreshold(threadAdaptThreshold);
            config.setThreadAdaptMaximumPoolSize(threadAdaptMaximumPoolSize);
        }else {
            ThreadPoolConfig tpc = new ThreadPoolConfig(threadPoolName, corePoolSize, maximumPoolSize,
                    queueCapacity, queueWarn, queueWarnThreshold, queueAdapt, queueAdaptThreshold,
                    queueAdaptMaxCapacity, threadAdapt, threadAdaptThreshold, threadAdaptMaximumPoolSize);
            threadPoolConfigMap.put(threadPoolName, tpc);
        }
        logger.info("动态线程池 - {}线程池配置更改成功。配置信息：{}", threadPoolName, JSON.toJSON(threadPoolConfig));
        // TODO 发送参数更改告警
    }
}
