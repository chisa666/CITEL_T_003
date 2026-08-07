package com.example.travelstats.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 查询区间配置实体类
 * 对应数据库表 query_range_config
 */
@TableName("query_range_config")
public class QueryRangeConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 配置名称 */
    private String configName;

    /** 查询类型: AGE-年龄, MILEAGE-里程, TIME-时间 */
    private String queryType;

    /** 区间数据(JSON格式): [{"label":"区间1","min":10,"max":20},...] */
    private String rangeData;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getRangeData() {
        return rangeData;
    }

    public void setRangeData(String rangeData) {
        this.rangeData = rangeData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
