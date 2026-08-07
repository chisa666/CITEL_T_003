package com.example.travelstats.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.travelstats.entity.PersonTravelData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 人员出行数据 Mapper
 * 继承MyBatis-Plus BaseMapper，自动获得CRUD功能
 */
@Mapper
public interface PersonTravelDataMapper extends BaseMapper<PersonTravelData> {

    /**
     * 按年龄区间查询
     * 年龄 = 当前年份 - 出生年份
     * 允许多区间重叠，使用OR条件
     */
    List<PersonTravelData> queryByAgeRanges(@Param("ranges") List<QueryRangeItem> ranges);

    /**
     * 按飞行里程区间查询
     * 允许多区间重叠，使用OR条件
     */
    List<PersonTravelData> queryByMileageRanges(@Param("ranges") List<QueryRangeItem> ranges);

    /**
     * 按飞行时间区间查询
     * 不允许区间重叠
     */
    List<PersonTravelData> queryByTimeRanges(@Param("ranges") List<QueryRangeItem> ranges);

    /**
     * 统计每个年龄区间的人数
     * @return 每个区间的标签和人数
     */
    List<RangeCountResult> countByAgeRanges(@Param("ranges") List<QueryRangeItem> ranges);

    /**
     * 统计每个里程区间的人数
     */
    List<RangeCountResult> countByMileageRanges(@Param("ranges") List<QueryRangeItem> ranges);

    /**
     * 统计每个时间区间的人数
     */
    List<RangeCountResult> countByTimeRanges(@Param("ranges") List<QueryRangeItem> ranges);

    /**
     * 区间查询参数
     */
    class QueryRangeItem {
        private String label;
        private int min;
        private int max;

        public QueryRangeItem() {}

        public QueryRangeItem(String label, int min, int max) {
            this.label = label;
            this.min = min;
            this.max = max;
        }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public int getMin() { return min; }
        public void setMin(int min) { this.min = min; }

        public int getMax() { return max; }
        public void setMax(int max) { this.max = max; }
    }

    /**
     * 区间统计结果
     */
    class RangeCountResult {
        private String rangeLabel;
        private long personCount;

        public String getRangeLabel() { return rangeLabel; }
        public void setRangeLabel(String rangeLabel) { this.rangeLabel = rangeLabel; }

        public long getPersonCount() { return personCount; }
        public void setPersonCount(long personCount) { this.personCount = personCount; }
    }
}
