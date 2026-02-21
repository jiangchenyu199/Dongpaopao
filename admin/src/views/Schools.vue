<template>
  <div class="schools-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>学校管理</span>
          <div class="header-actions">
            <el-button type="primary" :icon="Plus" @click="openForm()">新增</el-button>
            <el-button type="primary" :icon="Refresh" :loading="loading" @click="loadSchools">刷新</el-button>
          </div>
        </div>
      </template>
      <el-table v-loading="loading" :data="schools" stripe style="width: 100%">
        <el-table-column prop="sid" label="ID" width="80" />
        <el-table-column prop="sname" label="学校名称" min-width="200" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openForm(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && schools.length === 0" description="暂无学校数据" />
    </el-card>

    <el-dialog v-model="formVisible" :title="formRow ? '编辑学校' : '新增学校'" width="400" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80">
        <el-form-item label="学校名称" prop="sname">
          <el-input v-model="form.sname" placeholder="请输入学校名称" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getSchoolList, saveSchool, deleteSchool } from '@/api/school'
import { Refresh, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const schools = ref([])
const formVisible = ref(false)
const formRow = ref(null)
const submitLoading = ref(false)
const formRef = ref(null)

const form = reactive({ sid: null, sname: '' })
const formRules = {
  sname: [{ required: true, message: '请输入学校名称', trigger: 'blur' }]
}

const loadSchools = async () => {
  loading.value = true
  try {
    const res = await getSchoolList()
    schools.value = res?.data ?? []
  } catch {
    schools.value = []
  } finally {
    loading.value = false
  }
}

const openForm = (row = null) => {
  formRow.value = row
  form.sid = row?.sid ?? null
  form.sname = row?.sname ?? ''
  formVisible.value = true
}

const resetForm = () => {
  formRow.value = null
  form.sid = null
  form.sname = ''
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
    await saveSchool({ sid: form.sid, sname: form.sname })
    ElMessage.success(formRow.value ? '修改成功' : '新增成功')
    formVisible.value = false
    loadSchools()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除学校「${row.sname}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteSchool(row.sid)
    ElMessage.success('删除成功')
    loadSchools()
  }).catch(() => {})
}

onMounted(loadSchools)
</script>

<style scoped>
.schools-page {
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
</style>
