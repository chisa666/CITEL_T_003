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
  columns: { type: Array, default: () => [] }
})

defineEmits(['page-change'])
</script>
