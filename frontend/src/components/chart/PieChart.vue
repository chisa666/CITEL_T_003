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
      text: '各区间人数分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}人 ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'middle'
    },
    series: [{
      type: 'pie',
      radius: ['40%', '65%'],
      center: ['55%', '50%'],
      data: categories.map((name, i) => ({
        name,
        value: values[i] || 0
      })),
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      },
      label: {
        formatter: '{b}: {c}人'
      }
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
