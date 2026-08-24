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
        return executeQuery(request,
                personTravelDataMapper::queryByAgeRanges,
                personTravelDataMapper::countByAgeRanges);
    }

    @Override
    public QueryResult queryByMileage(QueryRequest request) {
        return executeQuery(request,
                personTravelDataMapper::queryByMileageRanges,
                personTravelDataMapper::countByMileageRanges);
    }

    @Override
    public QueryResult queryByTime(QueryRequest request) {
        return executeQuery(request,
                personTravelDataMapper::queryByTimeRanges,
                personTravelDataMapper::countByTimeRanges);
    }

    /**
     * 执行查询的通用方法
     *
     * @param request    查询请求
     * @param dataQuery  数据查询函数
     * @param countQuery 统计查询函数
     */
    private QueryResult executeQuery(QueryRequest request,
                                     java.util.function.Function<List<QueryRangeItem>, List<PersonTravelData>> dataQuery,
                                     java.util.function.Function<List<QueryRangeItem>, List<RangeCountResult>> countQuery) {

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

        // 3. 执行区间统计查询
        List<RangeCountResult> countResults = countQuery.apply(rangeItems);

        // 4. 组装分页表格数据
        int page = request.getPage();
        int pageSize = request.getPageSize();
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, allData.size());

        List<Map<String, Object>> pageRecords;
        if (fromIndex < allData.size()) {
            pageRecords = allData.subList(fromIndex, toIndex).stream()
                    .map(this::convertToMap)
                    .collect(Collectors.toList());
        } else {
            pageRecords = Collections.emptyList();
        }

        // 5. 组装图表数据
        List<String> categories = new ArrayList<>();
        List<Long> dataValues = new ArrayList<>();
        for (RangeCountResult cr : countResults) {
            categories.add(cr.getRangeLabel());
            dataValues.add(cr.getPersonCount());
        }

        // 6. 构建返回结果
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
        return result; // 返回查询结果
    }

    /**
     * 将实体转换为Map(便于JSON序列化)
     */
    private Map<String, Object> convertToMap(PersonTravelData data) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("personId", data.getPersonId());
        map.put("gender", data.getGender());
        map.put("genderName", data.getGender() == 0 ? "女" : "男");
        map.put("birthYear", data.getBirthYear());
        map.put("age", java.time.Year.now().getValue() - data.getBirthYear());
        map.put("totalMileage", data.getTotalMileage());
        map.put("totalTravelTime", data.getTotalTravelTime());
        return map;
    }
}
