package com.example.travelstats.service;

import com.example.travelstats.dto.RangeConfigDTO;
import com.example.travelstats.entity.QueryRangeConfig;

import java.util.List;

/**
 * 区间配置管理服务接口
 */
public interface RangeConfigService {

    /**
     * 获取所有已保存的区间配置
     */
    List<QueryRangeConfig> listAll();

    /**
     * 按查询类型获取配置
     */
    List<QueryRangeConfig> listByType(String queryType);

    /**
     * 保存区间配置
     */
    QueryRangeConfig save(RangeConfigDTO dto);

    /**
     * 删除区间配置
     */
    void delete(Long id);
}
