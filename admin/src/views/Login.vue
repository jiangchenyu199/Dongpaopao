<template>
  <div class="login-page">
    <div class="login-bg">
      <div class="login-pattern" />
    </div>
    <el-card class="login-card" shadow="always">
      <template #header>
        <div class="login-header">
          <el-text tag="h1" class="login-title">东跑跑</el-text>
          <el-text type="info" size="small" class="login-subtitle">管理后台</el-text>
        </div>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" size="large" class="login-form" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" autocomplete="username" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" show-password
            autocomplete="current-password" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="login-btn" native-type="submit">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <el-divider />
      <el-text type="info" size="small" class="login-footer">校园跑腿系统 · 管理端</el-text>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid) => {
    if (!valid) return
    loading.value = true
    // 暂不对接后端，模拟登录成功
    setTimeout(() => {
      loading.value = false
      router.push('/home')
    }, 600)
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 50%, #fcd34d 100%);
}

.login-pattern {
  position: absolute;
  inset: 0;
  background-image: radial-gradient(circle at 20% 80%, rgba(245, 158, 11, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(251, 191, 36, 0.2) 0%, transparent 40%);
  pointer-events: none;
}

.login-card {
  position: relative;
  width: 100%;
  max-width: 400px;
  z-index: 1;
}

.login-card :deep(.el-card__header) {
  padding-bottom: 8px;
}

.login-card :deep(.el-card__body) {
  padding-top: 8px;
}

.login-header {
  text-align: center;
}

.login-title {
  display: block;
  margin: 0;
  font-size: 32px;
  font-weight: 700;
  color: #1f2937;
  letter-spacing: 4px;
}

.login-subtitle {
  display: block;
  margin-top: 8px;
  letter-spacing: 2px;
}

.login-form {
  margin-bottom: 0;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  padding: 4px 12px;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
}

.login-footer {
  display: block;
  text-align: center;
}
</style>
