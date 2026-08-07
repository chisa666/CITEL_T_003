package com.example.travelstats.dto;

import java.util.List;
import java.util.Map;

/**
 * 查询结果DTO
 * 包含分页列表数据和图表统计数据
 */
public class QueryResult {

    /** 表格分页数据 */
    private TableData tableData;

    /** 图表统计数据 */
    private ChartData chartData;

    public TableData getTableData() {
        return tableData;
    }

    public void setTableData(TableData tableData) {
        this.tableData = tableData;
    }

    public ChartData getChartData() {
        return chartData;
    }

    public void setChartData(ChartData chartData) {
        this.chartData = chartData;
    }

    /**
     * 表格分页数据
     */
    public static class TableData {

        /** 当前页记录列表 */
        private List<Map<String, Object>> records;

        /** 总记录数 */
        private long total;

        /** 当前页码 */
        private int page;

        /** 每页显示数量 */
        private int pageSize;

        public List<Map<String, Object>> getRecords() {
            return records;
        }

        public void setRecords(List<Map<String, Object>> records) {
            this.records = records;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
    }

    /**
     * 图表统计数据
     */
    public static class ChartData {

        /** X轴/分类标签 */
        private List<String> categories;

        /** Y轴/系列数据 */
        private List<SeriesItem> series;

        public List<String> getCategories() {
            return categories;
        }

        public void setCategories(List<String> categories) {
            this.categories = categories;
        }

        public List<SeriesItem> getSeries() {
            return series;
        }

        public void setSeries(List<SeriesItem> series) {
            this.series = series;
        }
    }

    /**
     * 图表系列数据项
     */
    public static class SeriesItem {

        /** 系列名称 */
        private String name;

        /** 系列数据值 */
        private List<Long> data;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Long> getData() {
            return data;
        }

        public void setData(List<Long> data) {
            this.data = data;
        }
    }
}
