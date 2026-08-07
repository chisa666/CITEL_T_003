package com.example.travelstats.service;

import com.example.travelstats.dto.QueryRequest;
import com.example.travelstats.dto.QueryResult;

/**
 * 查询统计服务接口
 */
public interface QueryService {

    /**
     * 按年龄区间查询
     */
    QueryResult queryByAge(QueryRequest request);

    /**
     * 按飞行里程区间查询
     */
    QueryResult queryByMileage(QueryRequest request);

    /**
     * 按飞行时间区间查询
     */
    QueryResult queryByTime(QueryRequest request);
}
