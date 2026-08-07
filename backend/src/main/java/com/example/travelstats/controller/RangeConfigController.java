package com.example.travelstats.controller;

import com.example.travelstats.common.Result;
import com.example.travelstats.dto.RangeConfigDTO;
import com.example.travelstats.entity.QueryRangeConfig;
import com.example.travelstats.service.RangeConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 查询区间配置管理控制器
 */
@RestController
@RequestMapping("/api/config")
public class RangeConfigController {

    private static final Logger log = LoggerFactory.getLogger(RangeConfigController.class);

    private final RangeConfigService rangeConfigService;

    public RangeConfigController(RangeConfigService rangeConfigService) {
        this.rangeConfigService = rangeConfigService;
    }

    /**
     * 获取所有区间配置
     */
    @GetMapping("/list")
    public Result<?> listAll() {
        List<QueryRangeConfig> configs = rangeConfigService.listAll();
        return Result.ok(configs);
    }

    /**
     * 按查询类型获取配置(AGE/MILEAGE/TIME)
     */
    @GetMapping("/list/{queryType}")
    public Result<?> listByType(@PathVariable String queryType) {
        List<QueryRangeConfig> configs = rangeConfigService.listByType(queryType.toUpperCase());
        return Result.ok(configs);
    }

    /**
     * 保存区间配置
     */
    @PostMapping("/save")
    public Result<?> save(@RequestBody RangeConfigDTO dto) {
        if (dto.getConfigName() == null || dto.getConfigName().isEmpty()) {
            return Result.badRequest("配置名称不能为空");
        }
        if (dto.getQueryType() == null || dto.getQueryType().isEmpty()) {
            return Result.badRequest("查询类型不能为空");
        }
        if (dto.getRangeData() == null || dto.getRangeData().isEmpty()) {
            return Result.badRequest("区间数据不能为空");
        }

        QueryRangeConfig config = rangeConfigService.save(dto);
        log.info("区间配置已保存: name={}, type={}", dto.getConfigName(), dto.getQueryType());
        return Result.ok("保存成功", config);
    }

    /**
     * 删除区间配置
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        rangeConfigService.delete(id);
        return Result.ok("删除成功");
    }
}
