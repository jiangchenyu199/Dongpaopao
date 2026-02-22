<template>
  <div class="banner-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>首页轮播 Banner</span>
          <div class="header-actions">
            <el-button type="primary" :icon="Plus" @click="openForm()">新增</el-button>
            <el-button type="primary" :icon="Refresh" :loading="loading" @click="loadList">刷新</el-button>
          </div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" stripe style="width: 100%">
        <el-table-column label="图片">
          <template #default="{ row }">
            <div class="table-img-wrap">
              <el-image v-if="row.image" :src="row.image" fit="cover" class="table-img" />
              <span v-else class="text-placeholder">—</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openForm(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && list.length === 0" description="暂无轮播，点击新增添加" />

      <el-dialog v-model="formVisible" :title="formRow ? '编辑轮播' : '新增轮播'" width="500" @close="resetForm">
        <el-form ref="formRef" :model="form" :rules="formRules" label-width="90">
          <el-form-item label="图片" prop="image">
            <div class="cover-upload-wrap">
              <div class="cover-thumb-box">
                <el-image v-if="form.image" :src="form.image" fit="cover" class="cover-thumb" />
                <div v-else class="cover-thumb placeholder">暂无图片</div>
              </div>
              <div class="cover-actions">
                <div class="cover-row">
                  <el-input v-model="form.image" placeholder="图片 URL" clearable size="small" class="cover-url-input" />
                  <el-upload :show-file-list="false" accept="image/*" :http-request="handleImageUpload">
                    <el-button size="small">上传</el-button>
                  </el-upload>
                  <el-button v-if="form.image" size="small" type="danger" plain @click="form.image = ''">移除</el-button>
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="排序" prop="sort">
            <el-input-number v-model="form.sort" :min="0" style="width: 120px" />
            <span class="form-tip">数值越小越靠前</span>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio :value="1">启用</el-radio>
              <el-radio :value="0">停用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="formVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getBannerList, saveBanner, deleteBanner, uploadFile } from '@/api/app'
import { Refresh, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const list = ref([])
const formVisible = ref(false)
const formRow = ref(null)
const submitLoading = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  image: '',
  sort: 0,
  status: 1
})
const formRules = {
  image: [{ required: true, message: '请填写或上传图片地址', trigger: 'blur' }]
}

const loadList = async () => {
  loading.value = true
  try {
    const res = await getBannerList()
    list.value = res?.data ?? []
  } catch {
    list.value = []
  } finally {
    loading.value = false
  }
}

const openForm = (row = null) => {
  formRow.value = row
  form.id = row?.id ?? null
  form.image = row?.image ?? ''
  form.sort = row?.sort ?? 0
  form.status = row?.status ?? 1
  formVisible.value = true
}

const resetForm = () => {
  formRow.value = null
  form.id = null
  form.image = ''
  form.sort = 0
  form.status = 1
  formRef.value?.resetFields()
}

const handleImageUpload = async ({ file }) => {
  try {
    const res = await uploadFile(file, 'banner')
    const url = res?.data
    if (url) form.image = url
    else ElMessage.error('上传失败')
  } catch {
    ElMessage.error('上传失败')
  }
}

const submitForm = async () => {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }
  submitLoading.value = true
  try {
    await saveBanner({
      id: form.id,
      image: form.image,
      sort: form.sort,
      status: form.status
    })
    ElMessage.success(formRow.value ? '修改成功' : '新增成功')
    formVisible.value = false
    loadList()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该条轮播吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteBanner(row.id)
    ElMessage.success('删除成功')
    loadList()
  }).catch(() => { })
}

onMounted(loadList)
</script>

<style scoped>
.banner-page {
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

.cover-upload-wrap {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.cover-thumb-box {
  flex-shrink: 0;
}

.cover-thumb {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  display: block;
  background: var(--el-fill-color-light);
}

.cover-thumb.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: var(--el-text-color-placeholder);
}

.cover-actions {
  flex: 1;
  min-width: 0;
}

.cover-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.cover-url-input {
  flex: 1;
  min-width: 0;
}

.form-tip {
  margin-left: 8px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.text-placeholder {
  color: var(--el-text-color-placeholder);
  font-size: 12px;
}

.table-img-wrap {
  display: inline-block;
  width: 200px;
  height: 112px;
  overflow: hidden;
  border-radius: 8px;
  line-height: 0;
}

.table-img {
  width: 200px;
  height: 112px;
  border-radius: 8px;
  display: block;
}
</style>
