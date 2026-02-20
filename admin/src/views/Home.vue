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
        <el-card shadow="hover" class="stat-card">
          <div class="stat-body">
            <el-icon :size="32" color="#f59e0b"><Box /></el-icon>
            <div class="stat-info">
              <el-statistic title="今日订单">
                <template #default>--</template>
              </el-statistic>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-body">
            <el-icon :size="32" color="#f59e0b"><User /></el-icon>
            <div class="stat-info">
              <el-statistic title="用户总数">
                <template #default>--</template>
              </el-statistic>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-body">
            <el-icon :size="32" color="#f59e0b"><CircleCheck /></el-icon>
            <div class="stat-info">
              <el-statistic title="已完成订单">
                <template #default>--</template>
              </el-statistic>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="hover" class="quick-card">
      <template #header>
        <span>快捷入口</span>
      </template>
      <el-text type="info" size="small" class="quick-hint">对接后端后，此处将展示各业务模块入口</el-text>
      <div class="quick-tags">
        <el-tag>订单管理</el-tag>
        <el-tag type="success">用户管理</el-tag>
        <el-tag type="warning">系统设置</el-tag>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { Box, User, CircleCheck } from '@element-plus/icons-vue'
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

.quick-card .quick-hint {
  display: block;
  margin-bottom: 16px;
}

.quick-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
</style>
