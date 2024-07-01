package com.iteng.dynamic.thread.pool.registry.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.exception.NacosException;
import com.iteng.dynamic.thread.pool.common.ThreadPoolConstants;
import com.iteng.dynamic.thread.pool.model.ThreadPoolConfig;
import com.iteng.dynamic.thread.pool.registry.IRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Nacos配置中心
 */
public class NacosRegistry implements IRegistry {

    private final Logger logger = LoggerFactory.getLogger(NacosRegistry.class);

    private NacosConfigManager nacosConfigManager;

    public NacosRegistry(NacosConfigManager nacosConfigManager) {
        this.nacosConfigManager = nacosConfigManager;
    }

    @Override
    public List<ThreadPoolConfig> queryThreadPoolConfigListByKey(String key) {
        String jsonStr = null;
        try {
            jsonStr = nacosConfigManager.getConfigService().getConfig(key, ThreadPoolConstants.NACOS_GROUP, 5000);
        } catch (NacosException e) {
            logger.error("动态线程池 - 查询nacos配置失败：{}", e.getErrMsg());
        }
        return JSON.parseArray(jsonStr, ThreadPoolConfig.class);
    }
}
