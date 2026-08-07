package com.example.travelstats.controller;

import com.example.travelstats.common.Result;
import com.example.travelstats.service.DataImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 数据导入控制器
 */
@RestController
@RequestMapping("/api/data")
public class DataImportController {

    private static final Logger log = LoggerFactory.getLogger(DataImportController.class);

    private final DataImportService dataImportService;

    public DataImportController(DataImportService dataImportService) {
        this.dataImportService = dataImportService;
    }

    /**
     * 导入数据文件
     * 通过指定文件路径导入（可也支持上传方式）
     */
    @PostMapping("/import")
    public Result<?> importData(@RequestParam(value = "filePath", required = false) String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return Result.badRequest("请提供数据文件路径");
        }

        log.info("开始导入数据: {}", filePath);
        Map<String, Object> importResult = dataImportService.importDataFile(filePath);
        return Result.ok("导入完成", importResult);
    }

    /**
     * 获取数据状态
     */
    @GetMapping("/status")
    public Result<?> getStatus() {
        Map<String, Object> status = dataImportService.getDataStatus();
        return Result.ok(status);
    }
}
