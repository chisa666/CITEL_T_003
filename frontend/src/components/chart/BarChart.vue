<template>
  <div ref="chartRef" class="chart-container" style="width:100%;min-height:400px;"></div>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount, nextTick, getCurrentInstance } from 'vue'
//接收父组件传来的图表数据props
const props = defineProps({
  chartData: { type: Object, required: true }
})

const { proxy } = getCurrentInstance()
const chartRef = ref(null)
// 创建图表实例
let chartInstance = null
// 初始化图表
const initChart = async () => {
  await nextTick()
  if (!chartRef.value) return

  const container = chartRef.value
  if (container.offsetWidth === 0 || container.offsetHeight === 0) {
    setTimeout(() => initChart(), 100)
    return
  }

  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }

  chartInstance = proxy.$echarts.init(container)

  const categories = props.chartData.categories || []
  const values = props.chartData.series?.[0]?.data || []

  const option = {
    title: {
      text: '各区间人数统计',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: categories,
      axisLabel: { rotate: 15 }
    },
    yAxis: {
      type: 'value',
      name: '人数',
      minInterval: 1
    },
    series: [{
      type: 'bar',
      data: values,
      itemStyle: {
        color: new proxy.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#5470C6' },
          { offset: 1, color: '#91CC75' }
        ])
      },
      label: {
        show: true,
        position: 'top',
        formatter: '{c}人'
      },
      barMaxWidth: 60
    }]
  }

  chartInstance.setOption(option)
}

const handleResize = () => {
  chartInstance?.resize()
}
// 监听图表数据变化，重新初始化图表
watch(() => props.chartData, () => {
  initChart()
}, { deep: true, immediate: true })

defineExpose({ resize: handleResize })

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>
