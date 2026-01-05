<template>
    <div class="users-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>用户管理</span>
                </div>
            </template>

            <div class="mb-5 flex justify-between items-center">
                <div class="flex gap-2.5">
                    <el-button type="primary" @click="handleAdd" class="add-button">
                        <el-icon>
                            <Plus />
                        </el-icon>
                        新增用户
                    </el-button>
                    <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">
                        <el-icon>
                            <Delete />
                        </el-icon>
                        批量删除
                    </el-button>
                    <!-- 暂时注释掉用户状态功能
                    <el-button type="warning" @click="handleBatchToggleStatus" :disabled="selectedRows.length === 0">
                        <el-icon>
                            <Refresh />
                        </el-icon>
                        批量{{ selectedRows[0]?.status === true ? '停用' : '启用' }}
                    </el-button>
                    -->
                </div>
                <div class="flex gap-2.5">
                    <el-input v-model="searchKeyword" placeholder="搜索用户名、手机号、邮箱" clearable style="width: 300px"
                        @clear="handleSearch">
                        <template #prefix>
                            <el-icon>
                                <Search />
                            </el-icon>
                        </template>
                    </el-input>
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                </div>
            </div>

            <el-table :data="tableData" border stripe style="width: 100%" v-loading="loading"
                @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" />
                <el-table-column prop="username" label="用户名" width="120" />
                <el-table-column prop="nickname" label="昵称" width="120" />
                <el-table-column prop="phone" label="手机号" />
                <el-table-column prop="email" label="邮箱" />
                <!-- 暂时注释掉用户状态列
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="row.status === true ? 'success' : 'info'">
                            {{ row.status === true ? '启用' : '停用' }}
                        </el-tag>
                    </template>
                </el-table-column>
                -->
                <el-table-column prop="createTime" label="创建时间" width="180" />
                <el-table-column label="操作" width="150" align="center" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" size="small" @click="handleEdit(row)">
                            修改
                        </el-button>
                        <el-button type="danger" size="small" @click="handleDelete(row)">
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-container">
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total"
                    layout="prev, pager, next, sizes" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
            </div>
        </el-card>

        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
            <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="form.username" placeholder="请输入用户名" />
                </el-form-item>
                <el-form-item label="昵称" prop="nickname">
                    <el-input v-model="form.nickname" placeholder="请输入昵称" />
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                    <el-input v-model="form.phone" placeholder="请输入手机号" />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                    <el-input v-model="form.email" placeholder="请输入邮箱" />
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSubmit">确定</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Delete, Refresh } from '@element-plus/icons-vue'
import { getUserList, addUser, updateUser, deleteUser, batchDeleteUsers, batchToggleUserStatus } from '@/api/user'

const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const formRef = ref(null)
const selectedRows = ref([])

const form = reactive({
    id: null,
    username: '',
    nickname: '',
    phone: '',
    email: '',
    password: ''
})

const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
    ],
    nickname: [
        { required: true, message: '请输入昵称', trigger: 'blur' }
    ],
    phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
    ],
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
    ]
}

const tableData = ref([])

const fetchUserList = async () => {
    loading.value = true
    try {
        const response = await getUserList({
            page: currentPage.value,
            size: pageSize.value,
            keyword: searchKeyword.value
        })

        if (response.errCode === 0) {
            tableData.value = response.data.records
            total.value = response.data.total
        }
    } catch (error) {
        console.error('获取用户列表失败:', error)
        ElMessage.error('获取用户列表失败')
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    fetchUserList()
})

const handleSearch = () => {
    currentPage.value = 1
    fetchUserList()
}

const handleSizeChange = (val) => {
    pageSize.value = val
    currentPage.value = 1
    fetchUserList()
}

const handleCurrentChange = (val) => {
    currentPage.value = val
    fetchUserList()
}

const handleAdd = () => {
    dialogTitle.value = '新增用户'
    Object.assign(form, {
        id: null,
        username: '',
        nickname: '',
        phone: '',
        email: '',
        password: ''
    })
    dialogVisible.value = true
}

const handleEdit = (row) => {
    dialogTitle.value = '修改用户'
    Object.assign(form, { ...row, password: '' })
    dialogVisible.value = true
}

const handleDelete = (row) => {
    ElMessageBox.confirm(
        `确定要删除用户 "${row.nickname}" 吗？`,
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        try {
            const response = await deleteUser(row.id)
            if (response.errCode === 0) {
                ElMessage.success('删除成功')
                fetchUserList()
            }
        } catch (error) {
            console.error('删除失败:', error)
            ElMessage.error('删除失败')
        }
    }).catch(() => { })
}

const handleSubmit = () => {
    formRef.value.validate(async (valid) => {
        if (valid) {
            try {
                let response
                if (form.id) {
                    response = await updateUser(form)
                } else {
                    response = await addUser(form)
                }

                if (response.errCode === 0) {
                    ElMessage.success(form.id ? '修改成功' : '新增成功')
                    dialogVisible.value = false
                    fetchUserList()
                }
            } catch (error) {
                console.error('操作失败:', error)
                ElMessage.error('操作失败')
            }
        }
    })
}

const handleSelectionChange = (selection) => {
    selectedRows.value = selection
}

const handleBatchDelete = () => {
    if (selectedRows.value.length === 0) {
        ElMessage.warning('请选择要删除的用户')
        return
    }

    ElMessageBox.confirm(
        `确定要删除选中的 ${selectedRows.value.length} 个用户吗？`,
        '批量删除',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        try {
            const ids = selectedRows.value.map(row => row.id)
            const response = await batchDeleteUsers(ids)
            if (response.errCode === 0) {
                selectedRows.value = []
                ElMessage.success('批量删除成功')
                fetchUserList()
            }
        } catch (error) {
            console.error('批量删除失败:', error)
            ElMessage.error('批量删除失败')
        }
    }).catch(() => { })
}

// 暂时注释掉批量切换状态功能
/*
const handleBatchToggleStatus = () => {
    if (selectedRows.value.length === 0) {
        ElMessage.warning('请选择要操作的用户')
        return
    }

    const firstStatus = selectedRows.value[0].status
    const newStatus = firstStatus === '启用' ? '禁用' : '启用'

    ElMessageBox.confirm(
        `确定要批量${newStatus}选中的 ${selectedRows.value.length} 个用户吗？`,
        `批量${newStatus}`,
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        try {
            const ids = selectedRows.value.map(row => row.id)
            const response = await batchToggleUserStatus(ids, newStatus)
            if (response.errCode === 0) {
                selectedRows.value = []
                ElMessage.success(`批量${newStatus}成功`)
                fetchUserList()
            }
        } catch (error) {
            console.error('批量操作失败:', error)
            ElMessage.error('批量操作失败')
        }
    }).catch(() => { })
}
*/
</script>

<style scoped>
.users-container {
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

.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}
</style>
