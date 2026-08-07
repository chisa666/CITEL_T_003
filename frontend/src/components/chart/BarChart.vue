<template>
  <div ref="chartRef" class="chart-container"></div>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount, getCurrentInstance } from 'vue'

const props = defineProps({
  chartData: { type: Object, required: true }
})

const { proxy } = getCurrentInstance()
const chartRef = ref(null)
let chartInstance = null

const initChart = () => {
  if (!chartRef.value) return

  if (!chartInstance) {
    chartInstance = proxy.$echarts.init(chartRef.value)
  }

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
      bottom: '3%',
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

  chartInstance.setOption(option, true)
}

const handleResize = () => {
  chartInstance?.resize()
}

watch(() => props.chartData, () => {
  initChart()
}, { deep: true })

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>
