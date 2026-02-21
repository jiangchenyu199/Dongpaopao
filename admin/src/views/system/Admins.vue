<template>
  <div class="admins-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>后台账号管理</span>
          <div class="header-actions">
            <el-button type="primary" :icon="Plus" @click="openForm()">新增</el-button>
            <el-button type="primary" :icon="Refresh" :loading="loading" @click="loadList">刷新</el-button>
          </div>
        </div>
      </template>
      <div class="filter-row">
        <el-input v-model="keyword" placeholder="搜索用户名/昵称" clearable style="width: 200px" :prefix-icon="Search"
          @keyup.enter="loadList" />
        <el-button type="primary" @click="loadList">查询</el-button>
      </div>
      <el-table v-loading="loading" :data="list" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openForm(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next" @size-change="loadList" @current-change="loadList" />
      </div>
    </el-card>

    <el-dialog v-model="formVisible" :title="formRow ? '编辑管理员' : '新增管理员'" width="480" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80">
        <el-form-item v-if="formRow" label="ID">
          <el-input v-model="form.id" disabled />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="登录用户名" clearable :disabled="!!formRow" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="留空则不修改" clearable show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="显示名称" clearable />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleId" placeholder="选择一个角色" clearable style="width: 100%">
            <el-option v-for="r in roles" :key="r.id" :label="r.roleName" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="选填" />
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
import { getSysUserList, getSysUserDetail, saveSysUser, deleteSysUser } from '@/api/sysUser'
import { getRoleList } from '@/api/role'
import { Refresh, Plus, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')
const formVisible = ref(false)
const formRow = ref(null)
const submitLoading = ref(false)
const formRef = ref(null)
const roles = ref([])

const form = reactive({
  id: null,
  username: '',
  password: '',
  nickname: '',
  status: 1,
  remark: '',
  roleId: null
})
const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }]
}

const loadList = async () => {
  loading.value = true
  try {
    const res = await getSysUserList({ page: page.value, size: size.value, keyword: keyword.value })
    const d = res?.data ?? res
    list.value = d?.records ?? []
    total.value = d?.total ?? 0
  } catch {
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const loadRoles = async () => {
  try {
    const res = await getRoleList()
    roles.value = res?.data ?? []
  } catch {
    roles.value = []
  }
}

const openForm = async (row = null) => {
  formRow.value = row
  await loadRoles()
  if (row) {
    const detail = await getSysUserDetail(row.id)
    const d = detail?.data
    form.id = d?.id
    form.username = d?.username ?? ''
    form.password = ''
    form.nickname = d?.nickname ?? ''
    form.status = d?.status ?? 1
    form.remark = d?.remark ?? ''
    form.roleId = (d?.roleIds && d.roleIds.length) ? d.roleIds[0] : null
  } else {
    form.id = null
    form.username = ''
    form.password = ''
    form.nickname = ''
    form.status = 1
    form.remark = ''
    form.roleId = null
  }
  formVisible.value = true
}

const resetForm = () => {
  formRow.value = null
  formRef.value?.resetFields()
}

const submitForm = async () => {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }
  if (!formRow.value && !form.password) {
    ElMessage.warning('新增时请填写密码')
    return
  }
  submitLoading.value = true
  try {
    const payload = {
      id: form.id,
      username: form.username,
      nickname: form.nickname,
      status: form.status,
      remark: form.remark,
      roleIds: form.roleId != null ? [form.roleId] : []
    }
    if (form.password) payload.password = form.password
    await saveSysUser(payload)
    ElMessage.success(formRow.value ? '修改成功' : '新增成功')
    formVisible.value = false
    loadList()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除管理员「${row.username}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteSysUser(row.id)
    ElMessage.success('删除成功')
    loadList()
  }).catch(() => {})
}

onMounted(loadList)
</script>

<style scoped>
.admins-page {
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

.filter-row {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
