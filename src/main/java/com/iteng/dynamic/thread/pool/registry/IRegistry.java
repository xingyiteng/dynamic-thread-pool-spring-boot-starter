package com.iteng.dynamic.thread.pool.registry;

import com.iteng.dynamic.thread.pool.model.ThreadPoolConfig;

import java.util.List;

/**
 * 配置中心
 */
public interface IRegistry {

    /**
     * 根据key查询
     * @param key 配置数据对应的key
     * @return List<ThreadPoolConfig>
     */
    List<ThreadPoolConfig> queryThreadPoolConfigListByKey(String key);
}
