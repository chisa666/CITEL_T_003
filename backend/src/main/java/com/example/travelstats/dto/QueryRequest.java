package com.example.travelstats.dto;

import java.util.List;

/**
 * 区间查询请求DTO
 * 三种查询模式共用的请求结构
 */
public class QueryRequest {

    /** 区间列表 */
    private List<RangeItem> ranges;

    /** 区间筛选标签：只返回命中该区间的记录，为空则返回全部 */
    private String filterRange;

    /** 当前页码 */
    private int page = 1;

    /** 每页显示数量 */
    private int pageSize = 20;

    public List<RangeItem> getRanges() {
        return ranges;
    }

    public void setRanges(List<RangeItem> ranges) {
        this.ranges = ranges;
    }

    public String getFilterRange() {
        return filterRange;
    }

    public void setFilterRange(String filterRange) {
        this.filterRange = filterRange;
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

    /**
     * 单个区间条目
     */
    public static class RangeItem {

        /** 区间标签(如"10-20岁") */
        private String label;

        /** 最小值 */
        private int min;

        /** 最大值 */
        private int max;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }
    }
}
