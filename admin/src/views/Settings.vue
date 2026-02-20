<template>
  <div class="settings-page">
    <el-row :gutter="16">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>参数配置</span>
              <el-button type="primary" :icon="Refresh" @click="loadConfig">刷新</el-button>
            </div>
          </template>
          <el-table v-loading="loading" :data="configList" stripe>
            <el-table-column prop="configKey" label="配置键" width="180" />
            <el-table-column prop="configValue" label="配置值" min-width="200" show-overflow-tooltip />
            <el-table-column prop="description" label="描述" width="150" />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="openEdit(row)">编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!loading && configList.length === 0" description="暂无配置" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span>系统信息</span>
          </template>
          <el-descriptions v-if="systemInfo" :column="1" border size="small">
            <el-descriptions-item v-if="systemInfo.cpu" label="CPU">
              {{ systemInfo.cpu?.model }} ({{ systemInfo.cpu?.cores }}核)
            </el-descriptions-item>
            <el-descriptions-item v-if="systemInfo.memory" label="内存">
              {{ systemInfo.memory?.used }} / {{ systemInfo.memory?.total }}
            </el-descriptions-item>
            <el-descriptions-item v-if="systemInfo.jvm" label="JVM">
              {{ systemInfo.jvm?.usage }}
            </el-descriptions-item>
          </el-descriptions>
          <el-text v-else type="info" size="small">点击加载获取系统信息</el-text>
          <el-button type="primary" size="small" class="mt-2" @click="loadSystemInfo">加载</el-button>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="editVisible" title="编辑配置" width="400px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="配置键">
          <el-input v-model="editForm.configKey" disabled />
        </el-form-item>
        <el-form-item label="配置值">
          <el-input v-model="editForm.configValue" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="saveLoading" @click="saveConfig">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getConfigList, updateConfig } from '@/api/settings'
import request from '@/utils/request'
import { Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const configList = ref([])
const systemInfo = ref(null)
const editVisible = ref(false)
const saveLoading = ref(false)
const editForm = ref({ configKey: '', configValue: '' })

const loadConfig = async () => {
  loading.value = true
  try {
    const res = await getConfigList()
    configList.value = Array.isArray(res?.data) ? res.data : (res?.data ? [res.data] : [])
  } catch {
    configList.value = []
  } finally {
    loading.value = false
  }
}

const loadSystemInfo = async () => {
  try {
    const res = await request({ url: '/admin/system/config/info', method: 'get' })
    systemInfo.value = res?.data ?? res ?? null
  } catch {
    ElMessage.error('获取系统信息失败')
  }
}

const openEdit = (row) => {
  editForm.value = { configKey: row.configKey, configValue: row.configValue || '' }
  editVisible.value = true
}

const saveConfig = async () => {
  saveLoading.value = true
  try {
    await updateConfig(editForm.value.configKey, editForm.value.configValue)
    ElMessage.success('保存成功')
    editVisible.value = false
    loadConfig()
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saveLoading.value = false
  }
}

onMounted(loadConfig)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mt-2 {
  margin-top: 8px;
}
</style>
