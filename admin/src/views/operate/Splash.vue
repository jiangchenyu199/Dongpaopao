<template>
  <div class="splash-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>开屏设置</span>
          <div class="header-actions">
            <el-button type="primary" :icon="Refresh" :loading="loading" @click="load">刷新</el-button>
          </div>
        </div>
      </template>
      <el-form v-loading="loading" ref="formRef" :model="form" :rules="formRules" label-width="120" style="max-width: 520px">
        <el-form-item label="Logo 图片" prop="logoUrl">
          <div class="logo-upload">
            <el-image v-if="form.logoUrl" :src="form.logoUrl" fit="contain" style="width: 120px; height: 120px; border-radius: 8px" />
            <el-upload
              v-else
              class="logo-uploader"
              :auto-upload="false"
              :show-file-list="false"
              accept="image/*"
              @change="onLogoChange"
            >
              <el-icon class="uploader-icon"><Plus /></el-icon>
              <template #tip><span class="el-upload__tip">点击上传 Logo</span></template>
            </el-upload>
            <div v-if="form.logoUrl" class="logo-actions">
              <el-button type="primary" link size="small" @click="triggerUpload">更换</el-button>
              <el-button type="danger" link size="small" @click="form.logoUrl = ''">移除</el-button>
            </div>
          </div>
          <input ref="logoInputRef" type="file" accept="image/*" style="display: none" @change="onLogoFile" />
        </el-form-item>
        <el-form-item label="开屏文案" prop="slogan">
          <el-input v-model="form.slogan" placeholder="如：让科技改变生活" clearable maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="背景色">
          <el-input v-model="form.backgroundColor" placeholder="#f9f9f9" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item label="主色">
          <el-input v-model="form.primaryColor" placeholder="#666666" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item label="文案字号(px)">
          <el-input-number v-model="form.sloganFontSize" :min="12" :max="32" placeholder="留空默认" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item label="文案颜色">
          <el-input v-model="form.sloganColor" placeholder="留空用主色" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item label="显示底部版权">
          <el-radio-group v-model="form.showCopyright">
            <el-radio :value="1">显示</el-radio>
            <el-radio :value="0">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitLoading" @click="submitForm">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getSplash, saveSplash, uploadFile } from '@/api/app'
import { Refresh, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const logoInputRef = ref(null)

const form = reactive({
  id: null,
  logoUrl: '',
  slogan: '',
  backgroundColor: '#f9f9f9',
  primaryColor: '#666666',
  sloganFontSize: null,
  sloganColor: '',
  showCopyright: 1,
  status: 1
})
const formRules = {}

const load = async () => {
  loading.value = true
  try {
    const res = await getSplash()
    const d = res?.data
    if (d) {
      form.id = d.id
      form.logoUrl = d.logoUrl ?? ''
      form.slogan = d.slogan ?? ''
      form.backgroundColor = d.backgroundColor ?? '#f9f9f9'
      form.primaryColor = d.primaryColor ?? '#666666'
      form.sloganFontSize = d.sloganFontSize ?? null
      form.sloganColor = d.sloganColor ?? ''
      form.showCopyright = d.showCopyright ?? 1
      form.status = d.status ?? 1
    } else {
      form.id = null
      form.logoUrl = ''
      form.slogan = ''
      form.backgroundColor = '#f9f9f9'
      form.primaryColor = '#666666'
      form.sloganFontSize = null
      form.sloganColor = ''
      form.showCopyright = 1
      form.status = 1
    }
  } catch {
    form.id = null
    form.logoUrl = ''
    form.slogan = ''
    form.backgroundColor = '#f9f9f9'
    form.primaryColor = '#666666'
    form.showCopyright = 1
    form.status = 1
  } finally {
    loading.value = false
  }
}

const triggerUpload = () => {
  logoInputRef.value?.click()
}
const onLogoChange = (uploadFileObj) => {
  const file = uploadFileObj?.raw
  if (file) doUploadLogo(file)
}
const onLogoFile = (e) => {
  const file = e.target?.files?.[0]
  if (file) doUploadLogo(file)
  e.target.value = ''
}
const doUploadLogo = async (file) => {
  try {
    const res = await uploadFile(file)
    const url = res?.data
    if (url) {
      form.logoUrl = url
      ElMessage.success('Logo 上传成功')
      await saveSplash({
        id: form.id,
        logoUrl: form.logoUrl || null,
        slogan: form.slogan || null,
        backgroundColor: form.backgroundColor || null,
        primaryColor: form.primaryColor || null,
        sloganFontSize: form.sloganFontSize ?? null,
        sloganColor: form.sloganColor || null,
        showCopyright: form.showCopyright ?? 1,
        status: form.status ?? 1
      })
      load()
    }
  } catch {
    ElMessage.error('Logo 上传失败')
  }
}

const submitForm = async () => {
  submitLoading.value = true
  try {
    await saveSplash({
      id: form.id,
      logoUrl: form.logoUrl || null,
      slogan: form.slogan || null,
      backgroundColor: form.backgroundColor || null,
      primaryColor: form.primaryColor || null,
      sloganFontSize: form.sloganFontSize ?? null,
      sloganColor: form.sloganColor || null,
      showCopyright: form.showCopyright ?? 1,
      status: form.status ?? 1
    })
    ElMessage.success('保存成功')
    load()
  } finally {
    submitLoading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.splash-page {
  width: 100%;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-actions {
  display: flex;
  gap: 12px;
}
.logo-upload {
  display: flex;
  align-items: center;
  gap: 12px;
}
.logo-uploader {
  border: 1px dashed var(--el-border-color);
  border-radius: 8px;
  width: 120px;
  height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.uploader-icon {
  font-size: 28px;
  color: var(--el-text-color-secondary);
}
.logo-actions {
  display: flex;
  gap: 8px;
}
</style>
