<template>
    <div class="login-container">
        <el-card class="login-card" shadow="always">
            <template #header>
                <div class="card-header">
                    <span class="login-title">管理员登录</span>
                </div>
            </template>

            <el-form :model="form" :rules="rules" ref="formRef" size="large">
                <el-form-item prop="username">
                    <el-input v-model="form.username" placeholder="请输入用户名" clearable>
                        <template #prefix>
                            <el-icon>
                                <User />
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>

                <el-form-item prop="password">
                    <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password clearable>
                        <template #prefix>
                            <el-icon>
                                <Lock />
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>

                <el-button type="primary" class="login-button" @click="handleLogin" :loading="loading" size="large">
                    登录
                </el-button>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage, ElNotification } from 'element-plus'
import { login } from '@/api/user'
import { useUserStore } from '@/stores/user'


const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({
    username: '',
    password: ''
})

const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
    ]
}

const handleLogin = async () => {
    formRef.value.validate(async (valid) => {
        if (valid) {
            loading.value = true
            try {
                const response = await login({
                    username: form.username,
                    password: form.password
                })
                
                if (response.errCode === 0 || response.success) {
                    const { token, userInfo } = response.data
                    
                    userStore.setToken(token)
                    userStore.setUserInfo(userInfo)
                    
                    ElMessage.success('登录成功')
                    
                    if (userInfo.lastLoginTime) {
                        ElNotification({
                            title: '欢迎回来',
                            message: `上次登录时间：${userInfo.lastLoginTime}`,
                            type: 'success',
                            duration: 5000,
                            position: 'top-right'
                        })
                    }
                    
                    router.push('/home/dashboard')
                }
            } catch (error) {
                console.error('登录失败:', error)
            } finally {
                loading.value = false
            }
        }
    })
}
</script>

<style scoped>
.login-container {
    min-height: 100vh;
    min-width: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background-image: url('http://localhost:9000/admin/login.png');
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    position: relative;
}

.login-container::before {
    content: '';
    position: absolute;
    inset: 0;
    background: rgba(0, 0, 0, 0.3);
}

.login-card {
    width: 90%;
    max-width: 500px;
    position: relative;
    z-index: 1;
    background: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.3);
    border-radius: 16px;
    overflow: hidden;
}

.login-card :deep(.el-card__header) {
    background: rgba(255, 255, 255, 0.1);
    border-bottom: 1px solid rgba(255, 255, 255, 0.2);
    padding: 20px;
}

.login-card :deep(.el-card__body) {
    padding: 30px;
}

.card-header {
    text-align: center;
}

.login-title {
    font-size: 24px;
    font-weight: bold;
    color: #fff;
}

.login-card :deep(.el-form-item__label) {
    color: #fff;
}

.login-button {
    width: 100%;
    margin-top: 20px;
    height: 45px;
    font-size: 16px;
    font-weight: 500;
    border-radius: 8px;
    border: none;
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
    transition: all 0.3s ease;
}

.login-button:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.login-button:active {
    transform: translateY(0);
}

.login-card :deep(.el-input__wrapper) {
    background: rgba(255, 255, 255, 0.3);
    box-shadow: none;
    border: 1px solid rgba(255, 255, 255, 0.3);
}

.login-card :deep(.el-input__wrapper:hover),
.login-card :deep(.el-input__wrapper.is-focus) {
    background: rgba(255, 255, 255, 0.4);
    border-color: rgba(255, 255, 255, 0.5);
}

.login-card :deep(.el-input__inner) {
    color: #fff;
}

.login-card :deep(.el-input__inner::placeholder) {
    color: rgba(255, 255, 255, 0.7);
}

.login-card :deep(.el-button--primary) {
    width: 100%;
}
</style>
