package com.example.travelstats.controller;

import com.example.travelstats.common.Result;
import com.example.travelstats.dto.QueryRequest;
import com.example.travelstats.dto.QueryResult;
import com.example.travelstats.service.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * 查询统计控制器
 * 提供三种查询模式的RESTful API
 */
@RestController
@RequestMapping("/api/query")
public class QueryController {

    private static final Logger log = LoggerFactory.getLogger(QueryController.class);

    private final QueryService queryService;

    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    /**
     * 按出生年份(年龄)区间查询
     */
    @PostMapping("/age")
    public Result<?> queryByAge(@RequestBody QueryRequest request) {
        log.info("年龄区间查询: ranges={}, page={}", request.getRanges().size(), request.getPage());
        QueryResult result = queryService.queryByAge(request);
        return Result.ok("查询成功", result);
    }

    /**
     * 按飞行里程区间查询
     */
    @PostMapping("/mileage")
    public Result<?> queryByMileage(@RequestBody QueryRequest request) {
        log.info("里程区间查询: ranges={}, page={}", request.getRanges().size(), request.getPage());
        QueryResult result = queryService.queryByMileage(request);
        return Result.ok("查询成功", result);
    }

    /**
     * 按飞行时间区间查询
     */
    @PostMapping("/time")
    public Result<?> queryByTime(@RequestBody QueryRequest request) {
        log.info("时间区间查询: ranges={}, page={}", request.getRanges().size(), request.getPage());
        QueryResult result = queryService.queryByTime(request);
        return Result.ok("查询成功", result);
    }
}
