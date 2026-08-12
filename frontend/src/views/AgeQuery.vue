<template>
  <div class="page-container">
    <h3 class="section-title">按出生年份（年龄）查询</h3>

    <!-- 区间定义区域 -->
    <el-card style="margin-bottom: 16px">
      <template #header>
        <span>定义年龄区间 <el-tag type="info" size="small">允许多区间、允许重叠</el-tag></span>
      </template>

      <div v-for="(range, index) in ranges" :key="index" class="range-row">
        <el-input
          v-model="range.label"
          placeholder="区间名称"
          style="width: 150px; margin-right: 8px"
        />
        <span style="margin: 0 4px">年龄</span>
        <el-input-number
          v-model="range.min"
          :min="0"
          :max="150"
          placeholder="最小"
          style="width: 120px; margin-right: 4px"
        />
        <span style="margin: 0 4px">—</span>
        <el-input-number
          v-model="range.max"
          :min="0"
          :max="150"
          placeholder="最大"
          style="width: 120px; margin-right: 8px"
        />
        <span style="margin-right: 4px">岁</span>
        <el-button
          type="danger"
          :disabled="ranges.length <= 1"
          @click="removeRange(index)"
        >
          删除
        </el-button>
      </div>

      <el-button type="primary" @click="addRange" style="margin-top: 8px">
        + 添加区间
      </el-button>

      <div style="margin-top: 16px">
        <el-button type="primary" @click="handleQuery" :loading="querying">
          查询
        </el-button>
        <el-button type="success" @click="handleSaveConfig">
          保存区间
        </el-button>
        <el-button type="warning" @click="showLoadDialog = true">
          加载区间
        </el-button>
      </div>
    </el-card>

    <!-- 结果展示区域 -->
    <div v-if="queryResult" class="result-section">
      <el-card>
        <template #header>
          <span>查询结果</span>
        </template>

        <el-tabs v-model="displayMode" class="display-tabs" @tab-change="handleTabChange">
          <el-tab-pane label="数据列表" name="table">
            <ResultTable
              :records="queryResult.tableData.records"
              :total="queryResult.tableData.total"
              :page="queryResult.tableData.page"
              :pageSize="queryResult.tableData.pageSize"
              :columns="tableColumns"
              @page-change="handlePageChange"
            />
          </el-tab-pane>
          <el-tab-pane label="柱状图" name="bar">
            <BarChart ref="barChartRef" :key="chartKey" :chartData="queryResult.chartData" />
          </el-tab-pane>
          <el-tab-pane label="饼状图" name="pie">
            <PieChart ref="pieChartRef" :key="chartKey" :chartData="queryResult.chartData" />
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>

    <!-- 加载配置弹窗 -->
    <el-dialog v-model="showLoadDialog" title="加载区间配置" width="500px">
      <el-radio-group v-model="selectedConfigId">
        <el-radio
          v-for="config in ageConfigs"
          :key="config.id"
          :value="config.id"
          style="display: block; margin-bottom: 8px"
        >
          {{ config.configName }}
        </el-radio>
      </el-radio-group>
      <template #footer>
        <el-button @click="showLoadDialog = false">取消</el-button>
        <el-button type="primary" @click="handleLoadConfig">加载</el-button>
      </template>
    </el-dialog>

    <!-- 保存配置弹窗 -->
    <el-dialog v-model="showSaveDialog" title="保存区间配置" width="400px">
      <el-form>
        <el-form-item label="配置名称">
          <el-input v-model="configName" placeholder="如：标准年龄分段" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSaveDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmSaveConfig">确认保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { queryByAge } from '../api/queryApi'
import { listConfigsByType, saveConfig, deleteConfig } from '../api/configApi'
import ResultTable from '../components/table/ResultTable.vue'
import BarChart from '../components/chart/BarChart.vue'
import PieChart from '../components/chart/PieChart.vue'

const tableColumns = [
  { prop: 'personId', label: '人员ID', width: 120 },
  { prop: 'genderName', label: '性别', width: 70 },
  { prop: 'birthYear', label: '出生年份', width: 100 },
  { prop: 'age', label: '年龄', width: 70 },
  { prop: 'totalMileage', label: '总旅行里程', width: 130 },
  { prop: 'totalTravelTime', label: '总旅行时间(分钟)', width: 140 }
]

const ranges = ref([
  { label: '区间1', min: 10, max: 20 },
  { label: '区间2', min: 20, max: 30 }
])

const querying = ref(false)
const queryResult = ref(null)
const displayMode = ref('table')
const currentPage = ref(1)
const chartKey = ref(0)
const barChartRef = ref(null)
const pieChartRef = ref(null)

const showLoadDialog = ref(false)
const showSaveDialog = ref(false)
const configName = ref('')
const ageConfigs = ref([])
const selectedConfigId = ref(null)

const addRange = () => {
  const idx = ranges.value.length + 1
  ranges.value.push({ label: '区间' + idx, min: 0, max: 0 })
}

const removeRange = (index) => {
  ranges.value.splice(index, 1)
}

const handleTabChange = (tabName) => {
  // 切换到图表tab时延迟resize，确保DOM已渲染
  if (tabName === 'bar' || tabName === 'pie') {
    setTimeout(() => {
      barChartRef.value?.resize?.()
      pieChartRef.value?.resize?.()
    }, 50)
  }
}

const handleQuery = async () => {
  // 校验
  for (const r of ranges.value) {
    if (!r.label || r.min > r.max) {
      ElMessage.warning('请正确填写区间: 名称不能为空，最小值不能大于最大值')
      return
    }
  }

  querying.value = true
  try {
    const res = await queryByAge({
      ranges: ranges.value.map(r => ({ label: r.label, min: r.min, max: r.max })),
      page: currentPage.value,
      pageSize: 20
    })
    queryResult.value = res.data
    ElMessage.success('查询成功')
  } catch (e) {
    // error handled by interceptor
  } finally {
    querying.value = false
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  handleQuery()
}

const handleSaveConfig = () => {
  showSaveDialog.value = true
}

const confirmSaveConfig = async () => {
  if (!configName.value) {
    ElMessage.warning('请输入配置名称')
    return
  }
  try {
    await saveConfig({
      configName: configName.value,
      queryType: 'AGE',
      rangeData: JSON.stringify(ranges.value)
    })
    ElMessage.success('保存成功')
    showSaveDialog.value = false
    configName.value = ''
  } catch (e) {
    // ignore
  }
}

const handleLoadConfig = async () => {
  if (!selectedConfigId.value) {
    ElMessage.warning('请选择一个配置')
    return
  }
  const config = ageConfigs.value.find(c => c.id === selectedConfigId.value)
  if (config) {
    try {
      const data = JSON.parse(config.rangeData)
      ranges.value = data
      ElMessage.success('加载成功')
    } catch (e) {
      ElMessage.error('配置数据解析失败')
    }
  }
  showLoadDialog.value = false
}

const loadConfigs = async () => {
  try {
    const res = await listConfigsByType('AGE')
    ageConfigs.value = res.data || []
  } catch (e) {
    // ignore
  }
}

// 打开加载弹窗时刷新列表
const originalShowLoadDialog = showLoadDialog
// 简单的watch方法
import { watch } from 'vue'
watch(showLoadDialog, (val) => {
  if (val) loadConfigs()
})
</script>

<style scoped>
.range-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  flex-wrap: wrap;
}
</style>
