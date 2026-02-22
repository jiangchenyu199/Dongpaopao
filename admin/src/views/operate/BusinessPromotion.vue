<template>
  <div class="business-promotion-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>商家推广（首页展示）</span>
          <div class="header-actions">
            <el-button type="primary" :icon="Plus" @click="openForm()">新增</el-button>
            <el-button type="primary" :icon="Refresh" :loading="loading" @click="loadList">刷新</el-button>
          </div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" stripe style="width: 100%">
        <el-table-column label="封面" width="90">
          <template #default="{ row }">
            <el-image
              v-if="row.image"
              :src="row.image"
              fit="cover"
              style="width: 56px; height: 56px; border-radius: 8px"
            />
            <span v-else class="text-placeholder">—</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" min-width="140" show-overflow-tooltip />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="标签" width="160">
          <template #default="{ row }">
            <template v-if="tagList(row).length">
              <el-tag v-for="(t, i) in tagList(row)" :key="i" size="small" class="tag-mr">{{ t }}</el-tag>
            </template>
            <span v-else class="text-placeholder">—</span>
          </template>
        </el-table-column>
        <el-table-column prop="minPrice" label="最低价" width="100" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="90">
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
      <el-empty v-if="!loading && list.length === 0" description="暂无商家推广，点击新增添加" />

      <el-dialog v-model="formVisible" :title="formRow ? '编辑商家推广' : '新增商家推广'" width="520" @close="resetForm">
        <el-form ref="formRef" :model="form" :rules="formRules" label-width="90">
          <el-form-item label="名称" prop="name">
            <el-input v-model="form.name" placeholder="如：快捷搬家服务" maxlength="100" show-word-limit clearable />
          </el-form-item>
          <el-form-item label="描述" prop="description">
            <el-input v-model="form.description" type="textarea" :rows="2" placeholder="简短描述" maxlength="500" show-word-limit />
          </el-form-item>
          <el-form-item label="封面图">
            <div class="cover-upload-wrap">
              <div class="cover-thumb-box">
                <el-image
                  v-if="form.image"
                  :src="form.image"
                  fit="cover"
                  class="cover-thumb"
                />
                <div v-else class="cover-thumb placeholder">暂无封面</div>
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
          <el-form-item label="标签">
            <div class="tags-editor">
              <el-tag
                v-for="(t, i) in form.tags"
                :key="i"
                closable
                size="small"
                class="tag-mr"
                @close="form.tags.splice(i, 1)"
              >
                {{ t }}
              </el-tag>
              <el-input
                v-if="tagInputVisible"
                ref="tagInputRef"
                v-model="tagInputValue"
                size="small"
                class="tag-input"
                @blur="confirmTag"
                @keyup.enter="confirmTag"
              />
              <el-button v-else size="small" type="primary" plain @click="showTagInput">+ 标签</el-button>
            </div>
          </el-form-item>
          <el-form-item label="最低价展示">
            <el-input v-model="form.minPrice" placeholder="如：¥100起" maxlength="50" clearable style="width: 160px" />
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
import { ref, reactive, onMounted, nextTick } from 'vue'
import { getBusinessPromotionList, saveBusinessPromotion, deleteBusinessPromotion, uploadFile } from '@/api/app'
import { Refresh, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const list = ref([])
const formVisible = ref(false)
const formRow = ref(null)
const submitLoading = ref(false)
const formRef = ref(null)
const tagInputVisible = ref(false)
const tagInputValue = ref('')
const tagInputRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  description: '',
  image: '',
  tags: [],
  minPrice: '',
  sort: 0,
  status: 1
})
const formRules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }]
}

function tagList(row) {
  if (!row || !row.tags) return []
  try {
    const arr = JSON.parse(row.tags)
    return Array.isArray(arr) ? arr : []
  } catch {
    return []
  }
}

const loadList = async () => {
  loading.value = true
  try {
    const res = await getBusinessPromotionList()
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
  form.name = row?.name ?? ''
  form.description = row?.description ?? ''
  form.image = row?.image ?? ''
  form.tags = tagList(row).slice()
  form.minPrice = row?.minPrice ?? ''
  form.sort = row?.sort ?? 0
  form.status = row?.status ?? 1
  formVisible.value = true
}

const resetForm = () => {
  formRow.value = null
  form.id = null
  form.name = ''
  form.description = ''
  form.image = ''
  form.tags = []
  form.minPrice = ''
  form.sort = 0
  form.status = 1
  tagInputVisible.value = false
  tagInputValue.value = ''
  formRef.value?.resetFields()
}

const showTagInput = () => {
  tagInputVisible.value = true
  nextTick(() => tagInputRef.value?.focus())
}

const confirmTag = () => {
  const v = (tagInputValue.value || '').trim()
  if (v && !form.tags.includes(v)) form.tags.push(v)
  tagInputValue.value = ''
  tagInputVisible.value = false
}

const handleImageUpload = async ({ file }) => {
  try {
    const res = await uploadFile(file, 'business-promotion')
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
    const payload = {
      id: form.id,
      name: form.name,
      description: form.description,
      image: form.image,
      tags: JSON.stringify(form.tags),
      minPrice: form.minPrice,
      sort: form.sort,
      status: form.status
    }
    await saveBusinessPromotion(payload)
    ElMessage.success(formRow.value ? '修改成功' : '新增成功')
    formVisible.value = false
    loadList()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该条商家推广吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteBusinessPromotion(row.id)
    ElMessage.success('删除成功')
    loadList()
  }).catch(() => {})
}

onMounted(loadList)
</script>

<style scoped>
.business-promotion-page {
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
.tag-mr {
  margin-right: 6px;
  margin-bottom: 4px;
}
.tags-editor {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 4px;
}
.tag-input {
  width: 100px;
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
</style>
