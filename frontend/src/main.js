import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as echarts from 'echarts'
import App from './App.vue'
import router from './router/index.js'
import './styles/main.css'

const app = createApp(App)
app.use(ElementPlus)
app.use(router)

// 将ECharts挂载到全局实例，方便组件使用
app.config.globalProperties.$echarts = echarts

app.mount('#app')
