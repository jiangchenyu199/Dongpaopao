<template>
    <div class="profile-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>个人设置</span>
                </div>
            </template>

            <el-tabs v-model="activeTab" type="border-card">
                <el-tab-pane label="账号安全" name="security">
                    <el-form ref="securityFormRef" :model="securityForm" :rules="securityRules" label-width="120px"
                        style="max-width: 600px">
                        <el-form-item label="当前密码" prop="currentPassword">
                            <el-input v-model="securityForm.currentPassword" type="password" placeholder="请输入当前密码"
                                show-password />
                        </el-form-item>
                        <el-form-item label="新密码" prop="newPassword">
                            <el-input v-model="securityForm.newPassword" type="password" placeholder="请输入新密码"
                                show-password />
                        </el-form-item>
                        <el-form-item label="确认密码" prop="confirmPassword">
                            <el-input v-model="securityForm.confirmPassword" type="password" placeholder="请再次输入新密码"
                                show-password />
                        </el-form-item>
                        <el-form-item label="手机号" prop="phone">
                            <el-input v-model="securityForm.phone" placeholder="请输入手机号" />
                        </el-form-item>
                        <el-form-item label="邮箱" prop="email">
                            <el-input v-model="securityForm.email" placeholder="请输入邮箱" />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="handleSaveSecurity">保存</el-button>
                            <el-button @click="handleResetSecurity">重置</el-button>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>

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
                        <el-form-item label="界面语言">
                            <el-select v-model="preferenceForm.language" placeholder="请选择语言">
                                <el-option label="简体中文" value="zh-CN" />
                                <el-option label="English" value="en-US" />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="每页显示条数">
                            <el-select v-model="preferenceForm.pageSize" placeholder="请选择">
                                <el-option label="10条/页" :value="10" />
                                <el-option label="20条/页" :value="20" />
                                <el-option label="50条/页" :value="50" />
                                <el-option label="100条/页" :value="100" />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="自动保存">
                            <el-switch v-model="preferenceForm.autoSave" />
                            <span style="margin-left: 10px; color: #909399">编辑内容时自动保存</span>
                        </el-form-item>
                        <el-form-item label="深色模式">
                            <el-switch v-model="preferenceForm.darkMode" />
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

const activeTab = ref('security')
const securityFormRef = ref()

const securityForm = reactive({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
    phone: '13800138000',
    email: 'admin@dongpaopao.com'
})

const validateConfirmPassword = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请再次输入新密码'))
    } else if (value !== securityForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
    } else {
        callback()
    }
}

const securityRules = {
    currentPassword: [
        { required: true, message: '请输入当前密码', trigger: 'blur' }
    ],
    newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, validator: validateConfirmPassword, trigger: 'blur' }
    ],
    phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
    ],
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ]
}

const handleSaveSecurity = () => {
    securityFormRef.value.validate((valid) => {
        if (valid) {
            ElMessage.success('保存成功')
            securityForm.currentPassword = ''
            securityForm.newPassword = ''
            securityForm.confirmPassword = ''
        } else {
            ElMessage.error('请检查表单填写是否正确')
            return false
        }
    })
}

const handleResetSecurity = () => {
    securityFormRef.value.resetFields()
    ElMessage.info('已重置为默认值')
}

const preferenceForm = reactive({
    themeColor: '#409EFF',
    language: 'zh-CN',
    pageSize: 10,
    autoSave: true,
    darkMode: false
})

const handleSavePreference = () => {
    ElMessage.success('系统偏好保存成功')
}

const handleResetPreference = () => {
    preferenceForm.themeColor = '#409EFF'
    preferenceForm.language = 'zh-CN'
    preferenceForm.pageSize = 10
    preferenceForm.autoSave = true
    preferenceForm.darkMode = false
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
