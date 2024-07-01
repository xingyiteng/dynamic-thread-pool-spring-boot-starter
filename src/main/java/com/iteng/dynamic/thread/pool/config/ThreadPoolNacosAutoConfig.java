package com.iteng.dynamic.thread.pool.config;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.iteng.dynamic.thread.pool.listener.ThreadPoolNacosListener;
import com.iteng.dynamic.thread.pool.registry.IRegistry;
import com.iteng.dynamic.thread.pool.registry.nacos.NacosRegistry;
import com.iteng.dynamic.thread.pool.service.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * nacos配置中心自动装配
 */
@Configuration
@ConditionalOnProperty(name = "dynamic.thread.pool.config.registry", havingValue = "nacos")
public class ThreadPoolNacosAutoConfig {

    private final Logger logger = LoggerFactory.getLogger(ThreadPoolNacosAutoConfig.class);

    @Bean
    public IRegistry nacosRegistry(NacosConfigManager nacosConfigManager) {
        logger.info("动态线程池 - 配置中心（nacos）初始化完成");
        return new NacosRegistry(nacosConfigManager);
    }

    @Bean
    public ThreadPoolNacosListener threadPoolNacosListener(NacosConfigManager nacosConfigManager, ThreadPoolManager threadPoolManager){
        logger.info("动态线程池 - 消息监听器（nacos）初始化完成");
        return new ThreadPoolNacosListener(nacosConfigManager, threadPoolManager);
    }
}
