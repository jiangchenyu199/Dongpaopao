<template>
  <div class="roles-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <div class="header-actions">
            <el-button type="primary" :icon="Plus" @click="openForm()">新增</el-button>
            <el-button type="primary" :icon="Refresh" :loading="loading" @click="loadRoles">刷新</el-button>
          </div>
        </div>
      </template>
      <el-table v-loading="loading" :data="roles" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" min-width="120" />
        <el-table-column prop="roleKey" label="角色标识" width="120" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openForm(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && roles.length === 0" description="暂无角色" />
    </el-card>

    <el-dialog v-model="formVisible" :title="formRow ? '编辑角色' : '新增角色'" width="560" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="90">
        <el-form-item v-if="formRow" label="角色ID">
          <el-input v-model="form.id" disabled />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item label="角色标识" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="如 admin、operator" clearable />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="选填" />
        </el-form-item>
        <el-form-item label="菜单权限">
          <el-tree ref="menuTreeRef" :data="menuTreeData" show-checkbox node-key="id" :props="{ label: 'name', children: 'children' }"
            :default-checked-keys="form.menuIds" class="menu-tree" />
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
import { getRoleList, getRoleDetail, saveRole, deleteRole } from '@/api/role'
import { getMenuTree } from '@/api/menu'
import { Refresh, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const roles = ref([])
const formVisible = ref(false)
const formRow = ref(null)
const submitLoading = ref(false)
const formRef = ref(null)
const menuTreeRef = ref(null)
const menuTreeData = ref([])

const form = reactive({
  id: null,
  roleName: '',
  roleKey: '',
  sort: 0,
  status: 1,
  remark: '',
  menuIds: []
})
const formRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleKey: [{ required: true, message: '请输入角色标识', trigger: 'blur' }]
}

const loadRoles = async () => {
  loading.value = true
  try {
    const res = await getRoleList()
    roles.value = res?.data ?? []
  } catch {
    roles.value = []
  } finally {
    loading.value = false
  }
}

const loadMenuTree = async () => {
  try {
    const res = await getMenuTree()
    menuTreeData.value = res?.data ?? []
  } catch {
    menuTreeData.value = []
  }
}

const openForm = async (row = null) => {
  formRow.value = row
  await loadMenuTree()
  if (row) {
    const detail = await getRoleDetail(row.id)
    const d = detail?.data
    form.id = d?.id
    form.roleName = d?.roleName ?? ''
    form.roleKey = d?.roleKey ?? ''
    form.sort = d?.sort ?? 0
    form.status = d?.status ?? 1
    form.remark = d?.remark ?? ''
    form.menuIds = d?.menuIds ?? []
  } else {
    form.id = null
    form.roleName = ''
    form.roleKey = ''
    form.sort = 0
    form.status = 1
    form.remark = ''
    form.menuIds = []
  }
  formVisible.value = true
  setTimeout(() => {
    menuTreeRef.value?.setCheckedKeys(form.menuIds ?? [])
  }, 100)
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
  submitLoading.value = true
  try {
    const checked = menuTreeRef.value?.getCheckedKeys() ?? []
    const halfChecked = menuTreeRef.value?.getHalfCheckedKeys() ?? []
    const menuIds = [...new Set([...checked, ...halfChecked])]
    const payload = {
      id: form.id,
      roleName: form.roleName,
      roleKey: form.roleKey,
      sort: form.sort,
      status: form.status,
      remark: form.remark,
      menuIds
    }
    await saveRole(payload)
    ElMessage.success(formRow.value ? '修改成功' : '新增成功')
    formVisible.value = false
    loadRoles()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除角色「${row.roleName}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadRoles()
  }).catch(() => {})
}

onMounted(loadRoles)
</script>

<style scoped>
.roles-page {
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

.menu-tree {
  max-height: 320px;
  overflow-y: auto;
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  padding: 8px;
}
</style>
