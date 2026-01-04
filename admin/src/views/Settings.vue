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
                    <el-form :model="basicForm" label-width="120px" style="max-width: 600px">
                        <el-form-item label="系统名称">
                            <el-input v-model="basicForm.systemName" placeholder="请输入系统名称" />
                        </el-form-item>
                        <el-form-item label="系统Logo">
                            <el-upload
                                class="logo-uploader"
                                action="#"
                                :show-file-list="false"
                                :auto-upload="false"
                                :on-change="handleLogoChange"
                            >
                                <img v-if="basicForm.logoUrl" :src="basicForm.logoUrl" class="logo" />
                                <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
                            </el-upload>
                        </el-form-item>
                        <el-form-item label="系统描述">
                            <el-input
                                v-model="basicForm.description"
                                type="textarea"
                                :rows="4"
                                placeholder="请输入系统描述"
                            />
                        </el-form-item>
                        <el-form-item label="联系电话">
                            <el-input v-model="basicForm.contactPhone" placeholder="请输入联系电话" />
                        </el-form-item>
                        <el-form-item label="联系邮箱">
                            <el-input v-model="basicForm.contactEmail" placeholder="请输入联系邮箱" />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="handleSaveBasic">保存</el-button>
                            <el-button @click="handleResetBasic">重置</el-button>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>

                <el-tab-pane label="账号安全" name="security">
                    <el-form :model="securityForm" label-width="120px" style="max-width: 600px">
                        <el-form-item label="当前密码">
                            <el-input
                                v-model="securityForm.currentPassword"
                                type="password"
                                placeholder="请输入当前密码"
                                show-password
                            />
                        </el-form-item>
                        <el-form-item label="新密码">
                            <el-input
                                v-model="securityForm.newPassword"
                                type="password"
                                placeholder="请输入新密码"
                                show-password
                            />
                        </el-form-item>
                        <el-form-item label="确认密码">
                            <el-input
                                v-model="securityForm.confirmPassword"
                                type="password"
                                placeholder="请再次输入新密码"
                                show-password
                            />
                        </el-form-item>
                        <el-form-item label="手机号">
                            <el-input v-model="securityForm.phone" placeholder="请输入手机号" disabled />
                            <el-button type="primary" link style="margin-left: 10px">修改</el-button>
                        </el-form-item>
                        <el-form-item label="邮箱">
                            <el-input v-model="securityForm.email" placeholder="请输入邮箱" disabled />
                            <el-button type="primary" link style="margin-left: 10px">修改</el-button>
                        </el-form-item>
                        <el-form-item label="两步验证">
                            <el-switch v-model="securityForm.twoFactorAuth" />
                            <span style="margin-left: 10px; color: #909399">开启后登录时需要输入验证码</span>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="handleSaveSecurity">保存</el-button>
                            <el-button @click="handleResetSecurity">重置</el-button>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>

                <el-tab-pane label="通知设置" name="notification">
                    <el-form :model="notificationForm" label-width="120px" style="max-width: 600px">
                        <el-form-item label="邮件通知">
                            <el-switch v-model="notificationForm.emailEnabled" />
                        </el-form-item>
                        <el-form-item label="接收邮箱">
                            <el-input v-model="notificationForm.emailAddress" placeholder="请输入接收邮箱" />
                        </el-form-item>
                        <el-divider />
                        <el-form-item label="短信通知">
                            <el-switch v-model="notificationForm.smsEnabled" />
                        </el-form-item>
                        <el-form-item label="接收手机">
                            <el-input v-model="notificationForm.phoneNumber" placeholder="请输入接收手机号" />
                        </el-form-item>
                        <el-divider />
                        <el-form-item label="新订单提醒">
                            <el-switch v-model="notificationForm.newOrder" />
                        </el-form-item>
                        <el-form-item label="订单状态变更">
                            <el-switch v-model="notificationForm.orderStatusChange" />
                        </el-form-item>
                        <el-form-item label="用户注册提醒">
                            <el-switch v-model="notificationForm.userRegister" />
                        </el-form-item>
                        <el-form-item label="系统异常提醒">
                            <el-switch v-model="notificationForm.systemError" />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="handleSaveNotification">保存</el-button>
                            <el-button @click="handleResetNotification">重置</el-button>
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
import { Plus } from '@element-plus/icons-vue'

const activeTab = ref('basic')

const basicForm = reactive({
    systemName: '东跑跑管理系统',
    logoUrl: '',
    description: '东跑跑订单管理系统，提供便捷的订单管理服务',
    contactPhone: '400-888-8888',
    contactEmail: 'support@dongpaopao.com'
})

const securityForm = reactive({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
    phone: '13800138000',
    email: 'admin@dongpaopao.com',
    twoFactorAuth: false
})

const notificationForm = reactive({
    emailEnabled: true,
    emailAddress: 'admin@dongpaopao.com',
    smsEnabled: false,
    phoneNumber: '13800138000',
    newOrder: true,
    orderStatusChange: true,
    userRegister: true,
    systemError: true
})

const preferenceForm = reactive({
    themeColor: '#409EFF',
    language: 'zh-CN',
    pageSize: 10,
    autoSave: true,
    darkMode: false
})

const handleLogoChange = (file) => {
    const reader = new FileReader()
    reader.readAsDataURL(file.raw)
    reader.onload = () => {
        basicForm.logoUrl = reader.result
    }
}

const handleSaveBasic = () => {
    ElMessage.success('基本信息保存成功')
}

const handleResetBasic = () => {
    basicForm.systemName = '东跑跑管理系统'
    basicForm.logoUrl = ''
    basicForm.description = '东跑跑订单管理系统，提供便捷的订单管理服务'
    basicForm.contactPhone = '400-888-8888'
    basicForm.contactEmail = 'support@dongpaopao.com'
    ElMessage.info('已重置为默认值')
}

const handleSaveSecurity = () => {
    if (!securityForm.currentPassword) {
        ElMessage.warning('请输入当前密码')
        return
    }
    if (!securityForm.newPassword) {
        ElMessage.warning('请输入新密码')
        return
    }
    if (securityForm.newPassword !== securityForm.confirmPassword) {
        ElMessage.error('两次输入的密码不一致')
        return
    }
    ElMessage.success('密码修改成功')
    securityForm.currentPassword = ''
    securityForm.newPassword = ''
    securityForm.confirmPassword = ''
}

const handleResetSecurity = () => {
    securityForm.currentPassword = ''
    securityForm.newPassword = ''
    securityForm.confirmPassword = ''
    securityForm.twoFactorAuth = false
    ElMessage.info('已重置为默认值')
}

const handleSaveNotification = () => {
    if (notificationForm.emailEnabled && !notificationForm.emailAddress) {
        ElMessage.warning('请输入接收邮箱')
        return
    }
    if (notificationForm.smsEnabled && !notificationForm.phoneNumber) {
        ElMessage.warning('请输入接收手机号')
        return
    }
    ElMessage.success('通知设置保存成功')
}

const handleResetNotification = () => {
    notificationForm.emailEnabled = true
    notificationForm.emailAddress = 'admin@dongpaopao.com'
    notificationForm.smsEnabled = false
    notificationForm.phoneNumber = '13800138000'
    notificationForm.newOrder = true
    notificationForm.orderStatusChange = true
    notificationForm.userRegister = true
    notificationForm.systemError = true
    ElMessage.info('已重置为默认值')
}

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
.settings-container {
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

.logo-uploader {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    width: 178px;
    height: 178px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.logo-uploader:hover {
    border-color: #409EFF;
}

.logo-uploader-icon {
    font-size: 28px;
    color: #8c939d;
}

.logo {
    width: 178px;
    height: 178px;
    object-fit: cover;
}
</style>
