 <template>
  <div class="page-container">
    <h3 class="section-title">区间配置管理</h3>

    <el-tabs v-model="filterType" @tab-change="loadConfigs">
      <el-tab-pane label="年龄区间" name="AGE" />
      <el-tab-pane label="里程区间" name="MILEAGE" />
      <el-tab-pane label="时间区间" name="TIME" />
    </el-tabs>

    <el-table :data="configs" stripe style="margin-top: 12px">
      <!-- 序号列：显示行号，避免展示数据库内部自增主键 -->
      <el-table-column type="index" label="序号" width="70" />
      <el-table-column prop="configName" label="配置名称" />
      <el-table-column prop="queryType" label="查询类型" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.queryType === 'AGE'" type="primary">年龄</el-tag>
          <el-tag v-else-if="row.queryType === 'MILEAGE'" type="success">里程</el-tag>
          <el-tag v-else type="warning">时间</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="rangeData" label="区间数据" min-width="250">
        <template #default="{ row }">
          <el-tag
            v-for="(r, i) in parseRangeData(row.rangeData)"
            :key="i"
            style="margin: 2px"
            size="small"
          >
            {{ r.label }}: {{ r.min }} - {{ r.max }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" width="80" fixed="right">
        <template #default="{ row }">
          <el-button type="danger" size="small" @click="handleDelete(row.id)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listConfigsByType, deleteConfig } from '../api/configApi'

const filterType = ref('AGE')
const configs = ref([])

const parseRangeData = (rangeData) => {
  try {
    return JSON.parse(rangeData)
  } catch {
    return []
  }
}

const loadConfigs = async () => {
  try {
    const res = await listConfigsByType(filterType.value)
    configs.value = res.data || []
  } catch (e) {
    // ignore
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除此配置吗？', '确认删除', {
      type: 'warning'
    })
    await deleteConfig(id)
    ElMessage.success('删除成功')
    loadConfigs()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadConfigs()
})
</script>
