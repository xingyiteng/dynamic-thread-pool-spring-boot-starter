package com.iteng.dynamic.thread.pool.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

// TODO 拒绝策略监控
public class RejectedInvocationHandler implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RejectedInvocationHandler.class);
 
    private final Object target;

    public RejectedInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) args[1];
        if (Objects.equals(method.getName(), "rejectedExecution")) {
            rejectBefore(executor);
        }
        return method.invoke(target, args);
    }

    private void rejectBefore(ThreadPoolExecutor threadPoolExecutor) {
        logger.info("动态线程池 - 触发拒绝策略");
        // 触发拒绝策略告警
    }
}