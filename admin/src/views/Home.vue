<template>
  <div class="home-page">
    <el-card class="welcome-card" shadow="never">
      <template #header>
        <span class="card-header-text">欢迎使用东跑跑管理后台</span>
      </template>
      <el-text type="info">校园跑腿系统 · 在这里管理订单、用户与系统配置</el-text>
    </el-card>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card" v-loading="statsLoading">
          <div class="stat-body">
            <el-icon :size="32" color="#f59e0b"><Box /></el-icon>
            <div class="stat-info">
              <el-statistic title="今日订单" :value="stats.todayOrders" />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card" v-loading="statsLoading">
          <div class="stat-body">
            <el-icon :size="32" color="#f59e0b"><User /></el-icon>
            <div class="stat-info">
              <el-statistic title="用户总数" :value="stats.totalUsers" />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card" v-loading="statsLoading">
          <div class="stat-body">
            <el-icon :size="32" color="#f59e0b"><CircleCheck /></el-icon>
            <div class="stat-info">
              <el-statistic title="已完成订单" :value="stats.completedOrders" />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span>近7日订单趋势</span>
          </template>
          <v-chart :option="orderTrendOption" autoresize class="chart" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span>订单状态分布</span>
          </template>
          <v-chart :option="orderStatusOption" autoresize class="chart" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Box, User, CircleCheck } from '@element-plus/icons-vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { getDashboardStats } from '@/api/dashboard'

use([CanvasRenderer, LineChart, PieChart, GridComponent, TooltipComponent, LegendComponent])

const statsLoading = ref(false)
const stats = ref({
  todayOrders: 0,
  totalUsers: 0,
  completedOrders: 0
})

const loadStats = async () => {
  statsLoading.value = true
  try {
    const res = await getDashboardStats()
    const d = (res && res.data != null && typeof res.data === 'object') ? res.data : (res && typeof res === 'object' ? res : {})
    stats.value = {
      todayOrders: Number(d.todayOrders ?? d.today_orders ?? 0),
      totalUsers: Number(d.totalUsers ?? d.total_users ?? 0),
      completedOrders: Number(d.completedOrders ?? d.completed_orders ?? 0)
    }
  } catch {
    stats.value = { todayOrders: 0, totalUsers: 0, completedOrders: 0 }
  } finally {
    statsLoading.value = false
  }
}

onMounted(() => {
  loadStats()
})

const orderTrendOption = {
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  },
  yAxis: { type: 'value', name: '订单数' },
  series: [{
    name: '订单数',
    type: 'line',
    smooth: true,
    data: [12, 18, 15, 22, 28, 35, 30],
    areaStyle: { opacity: 0.2 }
  }]
}

const orderStatusOption = {
  tooltip: { trigger: 'item' },
  legend: { bottom: 0 },
  series: [{
    type: 'pie',
    radius: ['40%', '70%'],
    center: ['50%', '45%'],
    data: [
      { value: 45, name: '待接单', itemStyle: { color: '#f59e0b' } },
      { value: 28, name: '进行中', itemStyle: { color: '#3b82f6' } },
      { value: 82, name: '已完成', itemStyle: { color: '#10b981' } },
      { value: 5, name: '已取消', itemStyle: { color: '#6b7280' } }
    ]
  }]
}
</script>

<style scoped>
.home-page {
  width: 100%;
}

.welcome-card {
  margin-bottom: 24px;
  border: 1px solid rgba(245, 158, 11, 0.2);
}

.welcome-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-bottom: 1px solid rgba(245, 158, 11, 0.15);
}

.card-header-text {
  font-size: 18px;
  font-weight: 600;
}

.stats-row {
  margin-bottom: 24px;
}

.stat-body {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.stat-info :deep(.el-statistic__head) {
  font-size: 13px;
  color: #6b7280;
}

.stat-info :deep(.el-statistic__content) {
  font-size: 22px;
  font-weight: 700;
}

.chart-card {
  margin-bottom: 24px;
}

.chart {
  height: 280px;
}
</style>
