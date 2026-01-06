<template>
    <div class="profile-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>个人设置</span>
                </div>
            </template>

            <el-tabs v-model="activeTab" type="border-card">
                <el-tab-pane label="系统偏好" name="preference">
                    <el-form :model="preferenceForm" label-width="120px" style="max-width: 600px">
                        <el-form-item label="主题颜色">
                            <el-radio-group v-model="preferenceForm.themeColor">
                                <el-radio label="#409EFF">蓝色</el-radio>
                                <el-radio label="#67C23A">绿色</el-radio>
                                <el-radio label="#E6A23C">橙色</el-radio>
                                <el-radio label="#F56C6C">红色</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item label="每页显示条数">
                            <el-select v-model="preferenceForm.pageSize" placeholder="请选择">
                                <el-option label="10条/页" :value="10" />
                                <el-option label="20条/页" :value="20" />
                                <el-option label="50条/页" :value="50" />
                                <el-option label="100条/页" :value="100" />
                            </el-select>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="handleSavePreference">保存</el-button>
                            <el-button @click="handleResetPreference">重置</el-button>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
            </el-tabs>
        </el-card>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('preference')

const preferenceForm = reactive({
    themeColor: '#409EFF',
    pageSize: 10
})

const handleSavePreference = () => {
    ElMessage.success('系统偏好保存成功')
}

const handleResetPreference = () => {
    preferenceForm.themeColor = '#409EFF'
    preferenceForm.pageSize = 10
    ElMessage.info('已重置为默认值')
}
</script>

<style scoped>
.profile-container {
    padding: 20px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 16px;
    font-weight: bold;
    color: #303133;
}
</style>
