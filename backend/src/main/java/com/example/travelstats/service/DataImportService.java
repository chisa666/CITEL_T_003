package com.example.travelstats.service;

import java.util.Map;

/**
 * 数据导入服务接口
 */
public interface DataImportService {

    /**
     * 导入数据文件
     *
     * @param filePath 文件路径
     * @return 导入结果统计
     */
    Map<String, Object> importDataFile(String filePath);

    /**
     * 获取数据导入状态
     *
     * @return 总记录数
     */
    Map<String, Object> getDataStatus();
}
