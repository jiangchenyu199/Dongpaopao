<template>
  <div class="order-types-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>订单类型</span>
          <el-button type="primary" :icon="Refresh" @click="loadTypes">刷新</el-button>
        </div>
      </template>
      <el-table v-loading="loading" :data="types" stripe style="width: 100%">
        <el-table-column prop="orderTypeId" label="类型ID" min-width="120" />
        <el-table-column prop="typeName" label="名称" min-width="120" />
        <el-table-column prop="emojiIcon" label="图标" width="80" />
        <el-table-column prop="enabled" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && types.length === 0" description="暂无订单类型" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrderTypeList } from '@/api/orderType'
import { Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const types = ref([])

const formatTime = (t) => {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN')
}

const loadTypes = async () => {
  loading.value = true
  try {
    const res = await getOrderTypeList()
    types.value = res?.data ?? []
  } catch {
    types.value = []
  } finally {
    loading.value = false
  }
}

onMounted(loadTypes)
</script>

<style scoped>
.order-types-page {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
