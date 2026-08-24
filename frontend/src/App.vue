<template>
  <div id="app">
    <el-container class="app-container">
      <el-header class="app-header">
        <div class="header-left">
          <h2>数据查询与统计系统</h2>
        </div>
        <el-menu
          :default-active="activeMenu"
          mode="horizontal"
          router
          class="header-menu"
        >
          <el-menu-item index="/query/age">
            <el-icon><User /></el-icon>
            <span>年龄查询</span>
          </el-menu-item>
          <el-menu-item index="/query/mileage">
            <el-icon><DataLine /></el-icon>
            <span>里程查询</span>
          </el-menu-item>
          <el-menu-item index="/query/time">
            <el-icon><Timer /></el-icon>
            <span>时间查询</span>
          </el-menu-item>
          <el-menu-item index="/config">
            <el-icon><Setting /></el-icon>
            <span>配置管理</span>
          </el-menu-item>
        </el-menu>
      </el-header>
      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { User, DataLine, Timer, Setting } from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.app-container {
  min-height: 100vh;
}

.app-header {
  display: flex;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.header-left {
  margin-right: 30px;
  white-space: nowrap;
}

.header-left h2 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.header-menu {
  border-bottom: none !important;
  flex: 1;
}

.app-main {
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  padding: 20px;
}
</style>
