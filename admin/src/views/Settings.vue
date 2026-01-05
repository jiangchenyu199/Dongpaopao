<template>
    <div class="settings-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>系统设置</span>
                </div>
            </template>

            <el-tabs v-model="activeTab" type="border-card">
                <el-tab-pane label="基本信息" name="basic">
                    <el-table :data="configList" v-loading="loading" border>
                        <el-table-column prop="configKey" label="配置键" width="200" />
                        <el-table-column prop="configValue" label="配置值">
                            <template #default="{ row }">
                                <el-input v-if="row.editing" v-model="row.tempValue" />
                                <span v-else>{{ row.configValue }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="description" label="描述" width="200" />
                        <el-table-column label="操作" width="150">
                            <template #default="{ row }">
                                <el-button v-if="row.editing" type="primary" size="small"
                                    @click="handleConfirm(row)">确认</el-button>
                                <el-button v-if="row.editing" size="small" @click="handleCancel(row)">取消</el-button>
                                <el-button v-else type="primary" size="small" @click="handleEdit(row)">修改</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-tab-pane>
                <el-tab-pane label="系统信息" name="system">
                    <div v-loading="systemInfoLoading">
                        <div v-if="systemInfo">
                            <el-row :gutter="20">
                                <el-col :span="12">
                                    <el-card shadow="hover">
                                        <template #header>
                                            <div class="card-header">
                                                <el-icon><Cpu /></el-icon>
                                                <span>CPU信息</span>
                                            </div>
                                        </template>
                                        <el-descriptions :column="1" border>
                                            <el-descriptions-item label="型号">{{ systemInfo.cpu?.model }}</el-descriptions-item>
                                            <el-descriptions-item label="逻辑核心数">{{ systemInfo.cpu?.cores }}</el-descriptions-item>
                                            <el-descriptions-item label="物理核心数">{{ systemInfo.cpu?.physicalCores }}</el-descriptions-item>
                                            <el-descriptions-item label="系统负载">{{ systemInfo.cpu?.systemCpuLoad }}</el-descriptions-item>
                                        </el-descriptions>
                                    </el-card>
                                </el-col>
                                <el-col :span="12">
                                    <el-card shadow="hover">
                                        <template #header>
                                            <div class="card-header">
                                                <el-icon><Odometer /></el-icon>
                                                <span>内存信息</span>
                                            </div>
                                        </template>
                                        <el-descriptions :column="1" border>
                                            <el-descriptions-item label="总内存">{{ systemInfo.memory?.total }}</el-descriptions-item>
                                            <el-descriptions-item label="已用内存">{{ systemInfo.memory?.used }}</el-descriptions-item>
                                            <el-descriptions-item label="可用内存">{{ systemInfo.memory?.available }}</el-descriptions-item>
                                            <el-descriptions-item label="使用率">{{ systemInfo.memory?.usage }}</el-descriptions-item>
                                        </el-descriptions>
                                    </el-card>
                                </el-col>
                            </el-row>

                            <el-row :gutter="20" style="margin-top: 20px;">
                                <el-col :span="12">
                                    <el-card shadow="hover">
                                        <template #header>
                                            <div class="card-header">
                                                <el-icon><Monitor /></el-icon>
                                                <span>操作系统</span>
                                            </div>
                                        </template>
                                        <el-descriptions :column="1" border>
                                            <el-descriptions-item label="系统名称">{{ systemInfo.os?.name }}</el-descriptions-item>
                                            <el-descriptions-item label="系统版本">{{ systemInfo.os?.version }}</el-descriptions-item>
                                            <el-descriptions-item label="系统架构">{{ systemInfo.os?.arch }}</el-descriptions-item>
                                        </el-descriptions>
                                    </el-card>
                                </el-col>
                                <el-col :span="12">
                                    <el-card shadow="hover">
                                        <template #header>
                                            <div class="card-header">
                                                <el-icon><DataAnalysis /></el-icon>
                                                <span>JVM信息</span>
                                            </div>
                                        </template>
                                        <el-descriptions :column="1" border>
                                            <el-descriptions-item label="最大内存">{{ systemInfo.jvm?.max }}</el-descriptions-item>
                                            <el-descriptions-item label="已分配内存">{{ systemInfo.jvm?.total }}</el-descriptions-item>
                                            <el-descriptions-item label="已用内存">{{ systemInfo.jvm?.used }}</el-descriptions-item>
                                            <el-descriptions-item label="空闲内存">{{ systemInfo.jvm?.free }}</el-descriptions-item>
                                            <el-descriptions-item label="使用率">{{ systemInfo.jvm?.usage }}</el-descriptions-item>
                                        </el-descriptions>
                                    </el-card>
                                </el-col>
                            </el-row>
                        </div>
                        <div v-if="systemInfo?.error" class="error-message">
                            {{ systemInfo.error }}
                        </div>
                    </div>
                </el-tab-pane>
            </el-tabs>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Cpu, Odometer, Monitor, DataAnalysis } from '@element-plus/icons-vue'
import { getSystemConfigList, updateSystemConfig, getSystemInfo } from '@/api/settings'

const activeTab = ref('basic')
const configList = ref([])
const loading = ref(false)
const systemInfo = ref(null)
const systemInfoLoading = ref(false)

const fetchSystemConfigList = async () => {
    try {
        loading.value = true
        const res = await getSystemConfigList()
        if (res.data) {
            configList.value = res.data.map(item => ({
                ...item,
                editing: false,
                tempValue: item.configValue
            }))
        }
    } catch (error) {
        console.error('获取系统配置失败:', error)
        ElMessage.error('获取系统配置失败')
    } finally {
        loading.value = false
    }
}

const handleEdit = (row) => {
    row.editing = true
    row.tempValue = row.configValue
}

const handleCancel = (row) => {
    row.editing = false
    row.tempValue = row.configValue
}

const handleConfirm = async (row) => {
    try {
        loading.value = true
        await updateSystemConfig({
            configKey: row.configKey,
            configValue: row.tempValue
        })
        row.configValue = row.tempValue
        row.editing = false
        ElMessage.success('修改成功')
    } catch (error) {
        console.error('修改系统配置失败:', error)
        ElMessage.error('修改系统配置失败')
    } finally {
        loading.value = false
    }
}

const fetchSystemInfo = async () => {
    try {
        systemInfoLoading.value = true
        const res = await getSystemInfo()
        if (res.data) {
            systemInfo.value = res.data
        }
    } catch (error) {
        console.error('获取系统信息失败:', error)
        ElMessage.error('获取系统信息失败')
    } finally {
        systemInfoLoading.value = false
    }
}

watch(activeTab, (newTab) => {
    if (newTab === 'system' && !systemInfo.value) {
        fetchSystemInfo()
    }
})

onMounted(() => {
    fetchSystemConfigList()
})
</script>

<style scoped>
.settings-container {
    padding: 20px;
}

.card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: bold;
    color: #303133;
}

.card-header .el-icon {
    font-size: 18px;
}

.error-message {
    padding: 20px;
    text-align: center;
    color: #f56c6c;
    font-size: 14px;
}
</style>
