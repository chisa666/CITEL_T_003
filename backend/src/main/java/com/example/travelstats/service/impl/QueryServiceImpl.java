package com.example.travelstats.service.impl;

import com.example.travelstats.dto.QueryRequest;
import com.example.travelstats.dto.QueryResult;
import com.example.travelstats.entity.PersonTravelData;
import com.example.travelstats.mapper.PersonTravelDataMapper;
import com.example.travelstats.mapper.PersonTravelDataMapper.QueryRangeItem;
import com.example.travelstats.mapper.PersonTravelDataMapper.RangeCountResult;
import com.example.travelstats.service.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

/**
 * 查询统计服务实现类
 */
@Service
public class QueryServiceImpl implements QueryService {

    private static final Logger log = LoggerFactory.getLogger(QueryServiceImpl.class);

    private final PersonTravelDataMapper personTravelDataMapper;

    public QueryServiceImpl(PersonTravelDataMapper personTravelDataMapper) {
        this.personTravelDataMapper = personTravelDataMapper;
    }

    @Override
    public QueryResult queryByAge(QueryRequest request) {
        // 年龄 = 当前年份 - 出生年份
        int currentYear = java.time.Year.now().getValue();
        return executeQuery(request,
                personTravelDataMapper::queryByAgeRanges,
                personTravelDataMapper::countByAgeRanges,
                d -> currentYear - d.getBirthYear());
    }

    @Override
    public QueryResult queryByMileage(QueryRequest request) {
        return executeQuery(request,
                personTravelDataMapper::queryByMileageRanges,
                personTravelDataMapper::countByMileageRanges,
                PersonTravelData::getTotalMileage);
    }

    @Override
    public QueryResult queryByTime(QueryRequest request) {
        return executeQuery(request,
                personTravelDataMapper::queryByTimeRanges,
                personTravelDataMapper::countByTimeRanges,
                PersonTravelData::getTotalTravelTime);
    }

    /**
     * 执行查询的通用方法
     *
     * @param request        查询请求
     * @param dataQuery      数据查询函数
     * @param countQuery     统计查询函数
     * @param valueExtractor 取值函数：决定用哪个字段与区间比较
     */
    private QueryResult executeQuery(QueryRequest request,
                                     Function<List<QueryRangeItem>, List<PersonTravelData>> dataQuery,
                                     Function<List<QueryRangeItem>, List<RangeCountResult>> countQuery,
                                     ToLongFunction<PersonTravelData> valueExtractor) {

        // 1. 将请求区间转换为Mapper参数
        List<QueryRangeItem> rangeItems = request.getRanges().stream()
                .map(r -> {
                    QueryRangeItem item = new QueryRangeItem();
                    item.setLabel(r.getLabel());
                    item.setMin(r.getMin());
                    item.setMax(r.getMax());
                    return item;
                })
                .collect(Collectors.toList());

        // 2. 执行数据查询
        List<PersonTravelData> allData = dataQuery.apply(rangeItems);

        // 3. 计算每条记录命中的区间标签（重叠区间可能命中多个）
        Map<PersonTravelData, List<String>> matchedMap = new HashMap<>();
        for (PersonTravelData data : allData) {
            matchedMap.put(data, findMatchedRanges(data, rangeItems, valueExtractor));
        }

        // 4. 按区间标签过滤
        String filterRange = request.getFilterRange();
        if (filterRange != null && !filterRange.isEmpty()) {
            allData = allData.stream()
                    .filter(d -> matchedMap.get(d).contains(filterRange))
                    .collect(Collectors.toList());
        }

        // 5. 执行区间统计查询
        List<RangeCountResult> countResults = countQuery.apply(rangeItems);

        // 6. 组装分页表格数据
        int page = request.getPage();
        int pageSize = request.getPageSize();
        int fromIndex = (page - 1) * pageSize;                           // 计算当前页起始索引
        int toIndex = Math.min(fromIndex + pageSize, allData.size());    // 计算当前页结束索引

        List<Map<String, Object>> pageRecords;
        if (fromIndex < allData.size()) {
            pageRecords = allData.subList(fromIndex, toIndex).stream()
                    .map(d -> convertToMap(d, matchedMap.get(d)))
                    .collect(Collectors.toList());
        } else {
            pageRecords = Collections.emptyList();
        }

        // 7. 组装图表数据
        List<String> categories = new ArrayList<>();
        List<Long> dataValues = new ArrayList<>();
        for (RangeCountResult cr : countResults) {
            categories.add(cr.getRangeLabel());
            dataValues.add(cr.getPersonCount());
        }

        // 8. 构建返回结果
        QueryResult result = new QueryResult();
        // 表格数据
        QueryResult.TableData tableData = new QueryResult.TableData();
        tableData.setRecords(pageRecords);
        tableData.setTotal(allData.size());
        tableData.setPage(page);
        tableData.setPageSize(pageSize);
        result.setTableData(tableData);
        // 图表数据
        QueryResult.ChartData chartData = new QueryResult.ChartData();
        chartData.setCategories(categories);

        QueryResult.SeriesItem seriesItem = new QueryResult.SeriesItem();
        seriesItem.setName("人数");
        seriesItem.setData(dataValues);
        chartData.setSeries(Collections.singletonList(seriesItem));
        result.setChartData(chartData);

        log.debug("查询完成: 命中{}条, 图表分类{}个", allData.size(), categories.size());
        return result;
    }

    /**
     * 计算一条数据命中的区间标签列表（允许重叠时可能命中多个区间）
     *
     * @param data          人员出行数据
     * @param rangeItems    区间列表
     * @param valueExtractor 取值函数
     * @return 命中的区间标签列表
     */
    private List<String> findMatchedRanges(PersonTravelData data,
                                           List<QueryRangeItem> rangeItems,
                                           ToLongFunction<PersonTravelData> valueExtractor) {
        long value = valueExtractor.applyAsLong(data);
        List<String> matched = new ArrayList<>();
        for (QueryRangeItem r : rangeItems) {
            if (value >= r.getMin() && value <= r.getMax()) {
                matched.add(r.getLabel());
            }
        }
        return matched;             // 返回匹配的区间标签列表
    }

    /**
     * 将实体转换为Map(便于JSON序列化)
     */
    private Map<String, Object> convertToMap(PersonTravelData data, List<String> matchedRanges) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("personId", data.getPersonId());
        map.put("gender", data.getGender());
        map.put("genderName", data.getGender() == 0 ? "女" : "男");
        map.put("birthYear", data.getBirthYear());
        map.put("age", java.time.Year.now().getValue() - data.getBirthYear());
        map.put("totalMileage", data.getTotalMileage());
        map.put("totalTravelTime", data.getTotalTravelTime());
        map.put("matchedRanges", matchedRanges);
        return map;
    }
}
