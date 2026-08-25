<template>
  <div>
    <el-table :data="records" stripe border style="width: 100%" max-height="500">
      <el-table-column
        v-for="col in columns"
        :key="col.prop"
        :prop="col.prop"
        :label="col.label"
        :width="col.width"
        :min-width="col.minWidth"
      />
      <!-- 所属区间列：以标签形式展示命中的所有区间 -->
      <el-table-column v-if="showMatchedRanges" label="所属区间" min-width="180">
        <template #default="{ row }">
          <el-tag
            v-for="(tag, i) in row.matchedRanges"
            :key="i"
            size="small"
            type="info"
            style="margin: 2px"
          >
            {{ tag }}
          </el-tag>
          <span v-if="!row.matchedRanges || row.matchedRanges.length === 0" style="color:#999">—</span>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-if="total > 0"
      :current-page="page"
      :page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next, jumper"
      style="margin-top: 16px; justify-content: flex-end"
      @current-change="$emit('page-change', $event)"
    />
  </div>
</template>

<script setup>
defineProps({
  records: { type: Array, default: () => [] },
  total: { type: Number, default: 0 },
  page: { type: Number, default: 1 },
  pageSize: { type: Number, default: 20 },
  columns: { type: Array, default: () => [] },
  /** 是否显示"所属区间"列 */
  showMatchedRanges: { type: Boolean, default: false }
})

defineEmits(['page-change'])
</script>
