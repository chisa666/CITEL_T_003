package com.example.travelstats.dto;

/**
 * 区间配置DTO
 */
public class RangeConfigDTO {

    /** 配置名称 */
    private String configName;

    /** 查询类型: AGE / MILEAGE / TIME */
    private String queryType;

    /** 区间数据JSON字符串 */
    private String rangeData;

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
}
