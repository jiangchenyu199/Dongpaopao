<template>
  <div class="users-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" :icon="Refresh" @click="loadUsers">刷新</el-button>
        </div>
      </template>
      <div class="filter-row">
        <el-input v-model="keyword" placeholder="搜索用户昵称/手机号/ID" clearable style="width: 240px" :prefix-icon="Search"
          @keyup.enter="loadUsers" />
        <el-button type="primary" @click="loadUsers">查询</el-button>
      </div>
      <el-table v-loading="loading" :data="users" stripe style="width: 100%">
        <el-table-column prop="uid" label="用户ID" min-width="200" show-overflow-tooltip />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="balance" label="余额" width="100" align="right">
          <template #default="{ row }">¥{{ row.balance ?? '0.00' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ctime" label="注册时间" width="170">
          <template #default="{ row }">{{ formatTime(row.ctime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" type="danger" link size="small" @click="toggleStatus(row, 0)">
              禁用
            </el-button>
            <el-button v-else type="success" link size="small" @click="toggleStatus(row, 1)">
              启用
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next" @size-change="loadUsers" @current-change="loadUsers" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserList, updateUserStatus } from '@/api/user'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const users = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')

const formatTime = (t) => {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN')
}

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await getUserList({ page: page.value, size: size.value, keyword: keyword.value })
    const d = res?.data ?? res
    users.value = d?.records ?? d?.list ?? []
    total.value = d?.total ?? users.value.length
  } catch {
    users.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const toggleStatus = async (row, status) => {
  try {
    await updateUserStatus(row.uid, status)
    row.status = status
    ElMessage.success(status === 1 ? '已启用' : '已禁用')
  } catch {
    ElMessage.error('操作失败')
  }
}

onMounted(loadUsers)
</script>

<style scoped>
.users-page {
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
</style>
