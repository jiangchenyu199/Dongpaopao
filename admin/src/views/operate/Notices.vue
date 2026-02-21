<template>
  <div class="notices-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>滚动通知（公告）</span>
          <div class="header-actions">
            <el-button type="primary" :icon="Plus" @click="openForm()">新增</el-button>
            <el-button type="primary" :icon="Refresh" :loading="loading" @click="loadList">刷新</el-button>
          </div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="content" label="内容" min-width="280" show-overflow-tooltip />
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
      <el-empty v-if="!loading && list.length === 0" description="暂无公告，点击新增添加" />

      <el-dialog v-model="formVisible" :title="formRow ? '编辑公告' : '新增公告'" width="480" @close="resetForm">
        <el-form ref="formRef" :model="form" :rules="formRules" label-width="80">
          <el-form-item label="内容" prop="content">
            <el-input v-model="form.content" type="textarea" :rows="3" placeholder="首页顶部滚动显示的通知内容" maxlength="500" show-word-limit />
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
import { getNoticeList, saveNotice, deleteNotice } from '@/api/app'
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
  content: '',
  sort: 0,
  status: 1
})
const formRules = {
  content: [{ required: true, message: '请输入通知内容', trigger: 'blur' }]
}

const loadList = async () => {
  loading.value = true
  try {
    const res = await getNoticeList()
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
  form.content = row?.content ?? ''
  form.sort = row?.sort ?? 0
  form.status = row?.status ?? 1
  formVisible.value = true
}

const resetForm = () => {
  formRow.value = null
  form.id = null
  form.content = ''
  form.sort = 0
  form.status = 1
  formRef.value?.resetFields()
}

const submitForm = async () => {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }
  submitLoading.value = true
  try {
    await saveNotice({ id: form.id, content: form.content, sort: form.sort, status: form.status })
    ElMessage.success(formRow.value ? '修改成功' : '新增成功')
    formVisible.value = false
    loadList()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除该条公告吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteNotice(row.id)
    ElMessage.success('删除成功')
    loadList()
  }).catch(() => {})
}

onMounted(loadList)
</script>

<style scoped>
.notices-page {
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
.form-tip {
  margin-left: 8px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
</style>
