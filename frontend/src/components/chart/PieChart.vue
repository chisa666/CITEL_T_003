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
  // 确保容器有可见的宽高
  if (container.offsetWidth === 0 || container.offsetHeight === 0) {
    // 容器不可见（如隐藏在非 active tab 内），延迟重试
    setTimeout(() => initChart(), 100)
    return
  }

  // 先销毁旧实例，再创建新实例确保尺寸正确
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }

  chartInstance = proxy.$echarts.init(container)

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
      orient: 'horizontal',
      bottom: 10
    },
    series: [{
      type: 'pie',
      radius: ['40%', '65%'],
      center: ['50%', '45%'],
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

  chartInstance.setOption(option)
}

const handleResize = () => {
  chartInstance?.resize()
}

// 监测数据变化，immediate 确保挂载时首次渲染
watch(() => props.chartData, () => {
  initChart()
}, { deep: true, immediate: true })

// 暴露 resize 方法供父组件调用
defineExpose({ resize: handleResize })

onMounted(() => {
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>
