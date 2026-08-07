package com.example.travelstats.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.travelstats.entity.PersonTravelData;
import com.example.travelstats.mapper.PersonTravelDataMapper;
import com.example.travelstats.service.DataImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据导入服务实现类
 * 使用JUC多线程并发批量入库
 */
@Service
public class DataImportServiceImpl implements DataImportService {

    private static final Logger log = LoggerFactory.getLogger(DataImportServiceImpl.class);

    private static final int BATCH_SIZE = 500;

    private final PersonTravelDataMapper personTravelDataMapper;
    private final ThreadPoolExecutor importExecutor;

    public DataImportServiceImpl(PersonTravelDataMapper personTravelDataMapper,
                                 ThreadPoolExecutor importExecutor) {
        this.personTravelDataMapper = personTravelDataMapper;
        this.importExecutor = importExecutor;
    }

    @Override
    public Map<String, Object> importDataFile(String filePath) {
        Map<String, Object> result = new LinkedHashMap<>();

        try {
            // 1. 读取并解析文件
            List<PersonTravelData> allData = parseFile(filePath);
            int totalLines = allData.size();
            result.put("totalLines", totalLines);

            if (totalLines == 0) {
                result.put("successCount", 0);
                result.put("failedCount", 0);
                result.put("errors", Collections.emptyList());
                return result;
            }

            // 2. 分批处理
            List<List<PersonTravelData>> batches = partition(allData, BATCH_SIZE);

            // 3. JUC并发批量入库
            CountDownLatch latch = new CountDownLatch(batches.size());
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);
            List<Map<String, Object>> errorList = Collections.synchronizedList(new ArrayList<>());

            for (int i = 0; i < batches.size(); i++) {
                final int batchIndex = i;
                final List<PersonTravelData> batch = batches.get(i);

                importExecutor.submit(() -> {
                    try {
                        int saved = batchInsert(batch);
                        successCount.addAndGet(saved);
                        int failed = batch.size() - saved;
                        if (failed > 0) {
                            failCount.addAndGet(failed);
                            Map<String, Object> err = new LinkedHashMap<>();
                            err.put("batch", batchIndex + 1);
                            err.put("reason", "部分数据插入失败");
                            errorList.add(err);
                        }
                    } catch (Exception e) {
                        failCount.addAndGet(batch.size());
                        Map<String, Object> err = new LinkedHashMap<>();
                        err.put("batch", batchIndex + 1);
                        err.put("reason", e.getMessage());
                        errorList.add(err);
                        log.error("批量导入失败: batch={}", batchIndex, e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // 4. 等待所有批次完成
            latch.await();

            result.put("successCount", successCount.get());
            result.put("failedCount", failCount.get());
            result.put("errors", errorList);

            log.info("数据导入完成: 总计{}条, 成功{}条, 失败{}条",
                    totalLines, successCount.get(), failCount.get());

        } catch (Exception e) {
            log.error("数据导入异常", e);
            result.put("totalLines", 0);
            result.put("successCount", 0);
            result.put("failedCount", 0);
            result.put("errors", Collections.singletonList(
                    Collections.singletonMap("reason", "文件解析失败: " + e.getMessage())));
        }

        return result;
    }

    @Override
    public Map<String, Object> getDataStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        long totalCount = personTravelDataMapper.selectCount(new QueryWrapper<>());
        status.put("totalRecords", totalCount);
        status.put("tableName", "person_travel_data");
        return status;
    }

    /**
     * 解析数据文件
     * 文件格式: 每行以分号分隔: person_id;gender;birth_year;total_mileage;total_travel_time
     */
    private List<PersonTravelData> parseFile(String filePath) throws Exception {
        List<PersonTravelData> dataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] fields = line.split(";");
                if (fields.length < 5) {
                    log.warn("跳过格式不正确的行: {}", line);
                    continue;
                }

                try {
                    Long personId = Long.parseLong(fields[0].trim());
                    Integer gender = Integer.parseInt(fields[1].trim());
                    Integer birthYear = Integer.parseInt(fields[2].trim());
                    Long totalMileage = Long.parseLong(fields[3].trim());
                    Long totalTravelTime = Long.parseLong(fields[4].trim());

                    dataList.add(new PersonTravelData(personId, gender, birthYear, totalMileage, totalTravelTime));
                } catch (NumberFormatException e) {
                    log.warn("跳过数字格式错误的行: {}", line);
                }
            }
        }

        return dataList;
    }

    /**
     * 将一个列表按指定大小分批
     */
    private List<List<PersonTravelData>> partition(List<PersonTravelData> list, int size) {
        List<List<PersonTravelData>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }

    /**
     * 批量插入数据（逐条insert，实际项目中可使用insertBatchSomeColumn）
     */
    private int batchInsert(List<PersonTravelData> batch) {
        int count = 0;
        for (PersonTravelData data : batch) {
            int rows = personTravelDataMapper.insert(data);
            if (rows > 0) {
                count++;
            }
        }
        return count;
    }
}
