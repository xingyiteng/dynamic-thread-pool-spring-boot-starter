package com.iteng.dynamic.thread.pool.service;

import com.iteng.dynamic.thread.pool.model.ThreadPoolConfig;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池管理器
 */
public class ThreadPoolManager {

    // 应用名称
    private String applicationName;

    // 线程池对象Map
    private Map<String, ThreadPoolExecutor> threadPoolMap;

    // 线程池配置Map
    private Map<String, ThreadPoolConfig> threadPoolConfigMap;

    public ThreadPoolManager(String applicationName,
                             Map<String, ThreadPoolExecutor> threadPoolExecutorMap,
                             Map<String, ThreadPoolConfig> threadPoolConfigMap) {
        this.applicationName = applicationName;
        threadPoolMap = threadPoolExecutorMap;
        this.threadPoolConfigMap = threadPoolConfigMap;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Map<String, ThreadPoolExecutor> getThreadPoolMap() {
        return threadPoolMap;
    }

    public Map<String, ThreadPoolConfig> getThreadPoolConfigMap() {
        return threadPoolConfigMap;
    }
}
