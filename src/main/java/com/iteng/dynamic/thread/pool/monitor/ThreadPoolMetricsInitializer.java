package com.iteng.dynamic.thread.pool.monitor;

import com.iteng.dynamic.thread.pool.service.ThreadPoolManager;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolMetricsInitializer {

    private MeterRegistry meterRegistry;

    private ThreadPoolManager threadPoolManager;

    public ThreadPoolMetricsInitializer(MeterRegistry meterRegistry, ThreadPoolManager threadPoolManager) {
        this.meterRegistry = meterRegistry;
        this.threadPoolManager = threadPoolManager;
        init();
    }

    private void init() {
        Map<String, ThreadPoolExecutor> threadPoolMap = this.threadPoolManager.getThreadPoolMap();
        Set<Map.Entry<String, ThreadPoolExecutor>> entries = threadPoolMap.entrySet();
        for (Map.Entry<String, ThreadPoolExecutor> entry: entries) {
            gauge(entry.getKey(), entry.getValue());
        }
    }

    private void gauge(String beanName, ThreadPoolExecutor threadPool) {
        Iterable<Tag> tags = getTags(beanName, threadPool);
        Metrics.gauge(metricName("core.size"), tags, threadPool, ThreadPoolExecutor::getCorePoolSize);
        Metrics.gauge(metricName("maximum.size"), tags, threadPool, ThreadPoolExecutor::getMaximumPoolSize);
        Metrics.gauge(metricName("active.count"), tags, threadPool, ThreadPoolExecutor::getActiveCount);
        Metrics.gauge(metricName("task.count"), tags, threadPool, ThreadPoolExecutor::getTaskCount);
        Metrics.gauge(metricName("completed.task.count"), tags, threadPool, ThreadPoolExecutor::getCompletedTaskCount);
        Metrics.gauge(metricName("queue.size"), tags, threadPool, value -> value.getQueue().size());
        Metrics.gauge(metricName("queue.capacity"), tags, threadPool, value -> value.getQueue().remainingCapacity() + value.getQueue().size());
    }

    private static String metricName(String name) {
        return String.join(".","thread.pool", name);
    }

    private Iterable<Tag> getTags(String beanName, ThreadPoolExecutor threadPool) {
        List<Tag> tags = new ArrayList<>(2);
        tags.add(Tag.of("thread.pool.name", beanName));
        tags.add(Tag.of("app.name", this.threadPoolManager.getApplicationName()));
        return tags;
    }
}