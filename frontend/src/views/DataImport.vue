<template>
  <div class="page-container">
    <h3 class="section-title">数据导入</h3>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>导入数据文件</span>
          </template>
          <el-form>
            <el-form-item label="文件路径">
              <el-input
                v-model="filePath"
                placeholder="请输入数据文件的完整路径"
                clearable
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                @click="handleImport"
                :loading="importing"
              >
                开始导入
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>数据库状态</span>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="数据表">
              person_travel_data
            </el-descriptions-item>
            <el-descriptions-item label="总记录数">
              {{ dataStatus.totalRecords ?? '--' }}
            </el-descriptions-item>
          </el-descriptions>
          <el-button
            type="info"
            @click="handleRefreshStatus"
            style="margin-top: 12px"
          >
            刷新状态
          </el-button>
        </el-card>
      </el-col>
    </el-row>

    <!-- 导入结果 -->
    <el-card v-if="importResult" style="margin-top: 20px">
      <template #header>
        <span>导入结果</span>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="总行数">
          {{ importResult.totalLines }}
        </el-descriptions-item>
        <el-descriptions-item label="成功数">
          <el-tag type="success">{{ importResult.successCount }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="失败数">
          <el-tag type="danger">{{ importResult.failedCount }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <el-table
        v-if="importResult.errors && importResult.errors.length > 0"
        :data="importResult.errors"
        style="margin-top: 12px"
        max-height="300"
      >
        <el-table-column prop="batch" label="批次" width="80" />
        <el-table-column prop="reason" label="错误原因" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { importData, getDataStatus } from '../api/dataApi'
import { ElMessage } from 'element-plus'

const filePath = ref('')
const importing = ref(false)
const importResult = ref(null)
const dataStatus = ref({})

const handleImport = async () => {
  if (!filePath.value) {
    ElMessage.warning('请输入文件路径')
    return
  }
  importing.value = true
  try {
    const res = await importData(filePath.value)
    importResult.value = res.data
    ElMessage.success('导入完成')
    handleRefreshStatus()
  } catch (e) {
    ElMessage.error('导入失败')
  } finally {
    importing.value = false
  }
}

const handleRefreshStatus = async () => {
  try {
    const res = await getDataStatus()
    dataStatus.value = res.data
  } catch (e) {
    // ignore
  }
}

onMounted(() => {
  handleRefreshStatus()
})
</script>
