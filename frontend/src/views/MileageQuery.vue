<template>
  <div class="page-container">
    <h3 class="section-title">按飞行里程查询</h3>

    <el-card style="margin-bottom: 16px">
      <template #header>
        <span>定义里程区间 <el-tag type="info" size="small">允许多区间、允许重叠</el-tag></span>
      </template>

      <div v-for="(range, index) in ranges" :key="index" class="range-row">
        <el-input
          v-model="range.label"
          placeholder="区间名称"
          style="width: 150px; margin-right: 8px"
        />
        <span style="margin: 0 4px">里程</span>
        <el-input-number
          v-model="range.min"
          :min="0"
          placeholder="最小"
          style="width: 140px; margin-right: 4px"
        />
        <span style="margin: 0 4px">—</span>
        <el-input-number
          v-model="range.max"
          :min="0"
          placeholder="最大"
          style="width: 140px; margin-right: 8px"
        />
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

    <div v-if="queryResult" class="result-section">
      <el-card>
        <template #header>
          <span>查询结果</span>
        </template>

        <el-tabs v-model="displayMode" class="display-tabs" @tab-change="handleTabChange">
          <el-tab-pane label="数据列表" name="table">
            <!-- 区间筛选：下拉选择要查看的区间，全部=不过滤 -->
            <div style="margin-bottom: 12px; display: flex; align-items: center; gap: 8px">
              <span style="color: #606266">按区间筛选：</span>
              <el-select
                v-model="filterRange"
                placeholder="全部区间"
                clearable
                style="width: 200px"
                @change="handleFilterChange"
              >
                <el-option label="全部区间" value="" />
                <el-option
                  v-for="(r, i) in ranges"
                  :key="i"
                  :label="r.label"
                  :value="r.label"
                />
              </el-select>
            </div>
            <ResultTable
              :records="queryResult.tableData.records"
              :total="queryResult.tableData.total"
              :page="queryResult.tableData.page"
              :pageSize="queryResult.tableData.pageSize"
              :columns="tableColumns"
              show-matched-ranges
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

    <!-- 加载弹窗 -->
    <el-dialog v-model="showLoadDialog" title="加载区间配置" width="500px">
      <el-radio-group v-model="selectedConfigId">
        <el-radio
          v-for="config in configs"
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

    <!-- 保存弹窗 -->
    <el-dialog v-model="showSaveDialog" title="保存区间配置" width="400px">
      <el-form>
        <el-form-item label="配置名称">
          <el-input v-model="configName" placeholder="如：里程等级分段" />
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
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { queryByMileage } from '../api/queryApi'
import { listConfigsByType, saveConfig } from '../api/configApi'
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
  { label: '区间1', min: 0, max: 10000 },
  { label: '区间2', min: 5000, max: 20000 }
])

const querying = ref(false)
const queryResult = ref(null)
const displayMode = ref('table')
const currentPage = ref(1)
const chartKey = ref(0)
const barChartRef = ref(null)
const pieChartRef = ref(null)
const filterRange = ref('')

const showLoadDialog = ref(false)
const showSaveDialog = ref(false)
const configName = ref('')
const configs = ref([])
const selectedConfigId = ref(null)

const addRange = () => {
  const idx = ranges.value.length + 1
  ranges.value.push({ label: '区间' + idx, min: 0, max: 0 })
}

const removeRange = (index) => {
  ranges.value.splice(index, 1)
}

const handleTabChange = (tabName) => {
  if (tabName === 'bar' || tabName === 'pie') {
    setTimeout(() => {
      barChartRef.value?.resize?.()
      pieChartRef.value?.resize?.()
    }, 50)
  }
}

const handleQuery = async () => {
  for (const r of ranges.value) {
    if (!r.label || r.min > r.max) {
      ElMessage.warning('请正确填写区间')
      return
    }
  }
  // 若筛选值对应的区间已不存在，自动重置为全部
  if (filterRange.value && !ranges.value.some(r => r.label === filterRange.value)) {
    filterRange.value = ''
  }

  querying.value = true
  try {
    const res = await queryByMileage({
      ranges: ranges.value.map(r => ({ label: r.label, min: r.min, max: r.max })),
      filterRange: filterRange.value || null,
      page: currentPage.value,
      pageSize: 20
    })
    queryResult.value = res.data
    ElMessage.success('查询成功')
  } catch (e) {
    // ignore
  } finally {
    querying.value = false
  }
}

// 切换筛选区间时重新查询（回到第一页）
const handleFilterChange = () => {
  currentPage.value = 1
  handleQuery()
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
      queryType: 'MILEAGE',
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
  const config = configs.value.find(c => c.id === selectedConfigId.value)
  if (config) {
    try {
      ranges.value = JSON.parse(config.rangeData)
      ElMessage.success('加载成功')
    } catch (e) {
      ElMessage.error('配置数据解析失败')
    }
  }
  showLoadDialog.value = false
}

const loadConfigs = async () => {
  try {
    const res = await listConfigsByType('MILEAGE')
    configs.value = res.data || []
  } catch (e) {
    // ignore
  }
}

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
