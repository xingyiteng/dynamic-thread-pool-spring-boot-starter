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

    /**
     * 队列是否自适应
     */
    private Boolean queueAdapt;

    /**
     * 队列自适应阈值
     */
    private Integer queueAdaptThreshold;

    /**
     * 队列自适应最大值
     */
    private Integer queueAdaptMaxCapacity;

    /**
     * 线程是否自适应
     */
    private Boolean threadAdapt;

    /**
     * 线程自适应阈值
     */
    private Integer threadAdaptThreshold;

    /**
     * 线程自适应最大值
     */
    private Integer threadAdaptMaximumPoolSize;

    public ThreadPoolConfig(String threadPoolName, Integer corePoolSize, Integer maximumPoolSize, Integer queueCapacity,
                            Boolean queueWarn, Integer queueWarnThreshold, Boolean queueAdapt, Integer queueAdaptThreshold,
                            Integer queueAdaptMaxCapacity, Boolean threadAdapt, Integer threadAdaptThreshold, Integer threadAdaptMaximumPoolSize) {
        ThreadPoolName = threadPoolName;
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.queueCapacity = queueCapacity;
        this.queueWarn = queueWarn;
        this.queueWarnThreshold = queueWarnThreshold;
        this.queueAdapt = queueAdapt;
        this.queueAdaptThreshold = queueAdaptThreshold;
        this.queueAdaptMaxCapacity = queueAdaptMaxCapacity;
        this.threadAdapt = threadAdapt;
        this.threadAdaptThreshold = threadAdaptThreshold;
        this.threadAdaptMaximumPoolSize = threadAdaptMaximumPoolSize;
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

    public Boolean getQueueAdapt() {
        return queueAdapt;
    }

    public void setQueueAdapt(Boolean queueAdapt) {
        this.queueAdapt = queueAdapt;
    }

    public Integer getQueueAdaptThreshold() {
        return queueAdaptThreshold;
    }

    public void setQueueAdaptThreshold(Integer queueAdaptThreshold) {
        this.queueAdaptThreshold = queueAdaptThreshold;
    }

    public Integer getQueueAdaptMaxCapacity() {
        return queueAdaptMaxCapacity;
    }

    public void setQueueAdaptMaxCapacity(Integer queueAdaptMaxCapacity) {
        this.queueAdaptMaxCapacity = queueAdaptMaxCapacity;
    }

    public Boolean getThreadAdapt() {
        return threadAdapt;
    }

    public void setThreadAdapt(Boolean threadAdapt) {
        this.threadAdapt = threadAdapt;
    }

    public Integer getThreadAdaptThreshold() {
        return threadAdaptThreshold;
    }

    public void setThreadAdaptThreshold(Integer threadAdaptThreshold) {
        this.threadAdaptThreshold = threadAdaptThreshold;
    }

    public Integer getThreadAdaptMaximumPoolSize() {
        return threadAdaptMaximumPoolSize;
    }

    public void setThreadAdaptMaximumPoolSize(Integer threadAdaptMaximumPoolSize) {
        this.threadAdaptMaximumPoolSize = threadAdaptMaximumPoolSize;
    }
}
