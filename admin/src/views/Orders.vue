<template>
  <div class="orders-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
          <el-button type="primary" :icon="Refresh" @click="loadOrders">刷新</el-button>
        </div>
      </template>
      <div class="filter-row">
        <el-input v-model="keyword" placeholder="搜索订单ID/下单人/接单人" clearable style="width: 240px" :prefix-icon="Search"
          @keyup.enter="loadOrders" />
        <el-select v-model="status" placeholder="订单状态" clearable style="width: 120px">
          <el-option label="待接单" value="D" />
          <el-option label="进行中" value="J" />
          <el-option label="已完成" value="S" />
          <el-option label="已取消" value="C" />
        </el-select>
        <el-button type="primary" @click="loadOrders">查询</el-button>
      </div>
      <el-table v-loading="loading" :data="orders" stripe style="width: 100%">
        <el-table-column prop="oid" label="订单ID" min-width="180" show-overflow-tooltip />
        <el-table-column prop="xdr" label="下单人" width="120" />
        <el-table-column prop="jdr" label="接单人" width="120" />
        <el-table-column prop="amount" label="金额" width="80" align="right">
          <template #default="{ row }">¥{{ row.amount ?? '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTypeMap[row.status]">{{ statusTextMap[row.status] ?? row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="showDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next" @size-change="loadOrders" @current-change="loadOrders" />
      </div>
    </el-card>

    <el-drawer v-model="detailVisible" title="订单详情" size="400" direction="rtl">
      <div v-if="currentOrder" class="order-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="订单ID">{{ currentOrder.oid }}</el-descriptions-item>
          <el-descriptions-item label="下单人">{{ currentOrder.xdr }}</el-descriptions-item>
          <el-descriptions-item label="接单人">{{ currentOrder.jdr ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="金额">¥{{ currentOrder.amount }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ statusTextMap[currentOrder.status] }}</el-descriptions-item>
          <el-descriptions-item label="评分">{{ currentOrder.rating ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ formatTime(currentOrder.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="接单时间">{{ formatTime(currentOrder.acceptTime) || '-' }}</el-descriptions-item>
          <el-descriptions-item label="完成时间">{{ formatTime(currentOrder.completeTime) || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrderList, getOrderDetail } from '@/api/order'
import { Search, Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const orders = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')
const status = ref('')
const detailVisible = ref(false)
const currentOrder = ref(null)

const statusTextMap = { D: '待接单', J: '进行中', S: '已完成', C: '已取消' }
const statusTypeMap = { D: 'info', J: 'warning', S: 'success', C: 'danger' }

const formatTime = (t) => {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN')
}

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await getOrderList({ page: page.value, size: size.value, keyword: keyword.value, status: status.value })
    const d = res?.data ?? res
    orders.value = d?.records ?? d?.list ?? []
    total.value = d?.total ?? orders.value.length
  } catch {
    orders.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const showDetail = async (row) => {
  try {
    const res = await getOrderDetail(row.oid)
    currentOrder.value = res?.data ?? res ?? row
    detailVisible.value = true
  } catch {
    currentOrder.value = row
    detailVisible.value = true
  }
}

onMounted(loadOrders)
</script>

<style scoped>
.orders-page {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-row {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.order-detail {
  padding: 0 8px;
}
</style>
