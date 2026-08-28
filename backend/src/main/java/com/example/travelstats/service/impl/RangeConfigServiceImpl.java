package com.example.travelstats.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.travelstats.dto.RangeConfigDTO;
import com.example.travelstats.entity.QueryRangeConfig;
import com.example.travelstats.mapper.RangeConfigMapper;
import com.example.travelstats.service.RangeConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 区间配置管理服务实现类
 */
@Service
public class RangeConfigServiceImpl implements RangeConfigService {

    private static final Logger log = LoggerFactory.getLogger(RangeConfigServiceImpl.class);

    private final RangeConfigMapper rangeConfigMapper;

    public RangeConfigServiceImpl(RangeConfigMapper rangeConfigMapper) {
        this.rangeConfigMapper = rangeConfigMapper;
    }

    @Override
    public List<QueryRangeConfig> listAll() {
        return rangeConfigMapper.selectList(
                new QueryWrapper<QueryRangeConfig>()
                        .orderByAsc("id"));
    }

    @Override
    public List<QueryRangeConfig> listByType(String queryType) {
        return rangeConfigMapper.findByQueryType(queryType);
    }

    @Override
    public QueryRangeConfig save(RangeConfigDTO dto) {
        //创建实体对象
        QueryRangeConfig config = new QueryRangeConfig();
        //设置属性
        config.setConfigName(dto.getConfigName());
        config.setQueryType(dto.getQueryType());
        config.setRangeData(dto.getRangeData());
        //设置创建时间和更新时间
        config.setCreatedAt(LocalDateTime.now());
        config.setUpdatedAt(LocalDateTime.now());

        rangeConfigMapper.insert(config);   // 插入数据库
        log.info("区间配置已保存: name={}, type={}", dto.getConfigName(), dto.getQueryType());
        return config;
    }

    @Override
    public void delete(Long id) {
        rangeConfigMapper.deleteById(id);
        log.info("区间配置已删除: id={}", id);
    }
}
