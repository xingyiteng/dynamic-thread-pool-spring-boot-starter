package com.iteng.dynamic.thread.pool.model;

/**
 * 线程池配置
 */
public class ThreadPoolConfig {

    /**
     * 线程池名称
     */
    private String ThreadPoolName;

    /**
     * 核心线程数
     */
    private Integer corePoolSize;

    /**
     * 最大线程数
     */
    private Integer maximumPoolSize;

    /**
     * 队列容量
     */
    private Integer queueCapacity;

    /**
     * 队列是否告警
     */
    private Boolean queueWarn;

    /**
     * 队列告警阈值
     */
    private Integer queueWarnThreshold;

    public ThreadPoolConfig(String threadPoolName, Integer corePoolSize, Integer maximumPoolSize, Integer queueCapacity, Boolean queueWarn, Integer queueWarnThreshold) {
        ThreadPoolName = threadPoolName;
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.queueCapacity = queueCapacity;
        this.queueWarn = queueWarn;
        this.queueWarnThreshold = queueWarnThreshold;
    }

    public String getThreadPoolName() {
        return ThreadPoolName;
    }

    public void setThreadPoolName(String threadPoolName) {
        ThreadPoolName = threadPoolName;
    }

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Integer getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(Integer maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public Integer getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(Integer queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public Boolean getQueueWarn() {
        return queueWarn;
    }

    public void setQueueWarn(Boolean queueWarn) {
        this.queueWarn = queueWarn;
    }

    public Integer getQueueWarnThreshold() {
        return queueWarnThreshold;
    }

    public void setQueueWarnThreshold(Integer queueWarnThreshold) {
        this.queueWarnThreshold = queueWarnThreshold;
    }
}
