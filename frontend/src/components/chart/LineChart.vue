<template>
  <div ref="chartRef" class="chart-container" style="width:100%;min-height:400px;"></div>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount, nextTick, getCurrentInstance } from 'vue'

const props = defineProps({
  chartData: { type: Object, required: true }
})

const { proxy } = getCurrentInstance()
const chartRef = ref(null)
let chartInstance = null

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
      text: '各时间区间人数趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>人数: {c}人'
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
      boundaryGap: false
    },
    yAxis: {
      type: 'value',
      name: '人数',
      minInterval: 1
    },
    series: [{
      type: 'line',
      data: values,
      smooth: true,
      symbol: 'circle',
      symbolSize: 10,
      lineStyle: { width: 3, color: '#5470C6' },
      itemStyle: { color: '#5470C6' },
      areaStyle: {
        color: new proxy.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(84,112,198,0.3)' },
          { offset: 1, color: 'rgba(84,112,198,0.05)' }
        ])
      },
      label: {
        show: true,
        position: 'top',
        formatter: '{c}人'
      }
    }]
  }

  chartInstance.setOption(option)
}

const handleResize = () => {
  chartInstance?.resize()
}

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
