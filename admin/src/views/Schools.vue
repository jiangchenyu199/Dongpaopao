<template>
  <div class="schools-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>学校管理</span>
          <el-button type="primary" :icon="Refresh" @click="loadSchools">刷新</el-button>
        </div>
      </template>
      <el-table v-loading="loading" :data="schools" stripe>
        <el-table-column prop="sid" label="ID" width="80" />
        <el-table-column prop="sname" label="学校名称" />
      </el-table>
      <el-empty v-if="!loading && schools.length === 0" description="暂无学校数据" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSchoolList } from '@/api/school'
import { Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const schools = ref([])

const loadSchools = async () => {
  loading.value = true
  try {
    const res = await getSchoolList()
    schools.value = res?.data ?? []
  } catch {
    schools.value = []
  } finally {
    loading.value = false
  }
}

onMounted(loadSchools)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
