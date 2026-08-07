package com.example.travelstats.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.travelstats.entity.QueryRangeConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 查询区间配置 Mapper
 */
@Mapper
public interface RangeConfigMapper extends BaseMapper<QueryRangeConfig> {

    /**
     * 按查询类型查找所有配置
     */
    List<QueryRangeConfig> findByQueryType(@Param("queryType") String queryType);
}
