<template>
    <div class="users-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>用户管理</span>
                </div>
            </template>

            <div class="search-bar">
                <el-button type="primary" @click="handleAdd" class="add-button">
                    <el-icon><Plus /></el-icon>
                    新增用户
                </el-button>
                <el-button
                    type="danger"
                    @click="handleBatchDelete"
                    :disabled="selectedRows.length === 0"
                >
                    <el-icon><Delete /></el-icon>
                    批量删除
                </el-button>
                <el-button
                    type="warning"
                    @click="handleBatchToggleStatus"
                    :disabled="selectedRows.length === 0"
                >
                    <el-icon><Refresh /></el-icon>
                    批量{{ selectedRows[0]?.status === '启用' ? '停用' : '启用' }}
                </el-button>
                <el-input
                    v-model="searchKeyword"
                    placeholder="搜索用户名、手机号、邮箱"
                    clearable
                    style="width: 300px"
                    @clear="handleSearch"
                >
                    <template #prefix>
                        <el-icon><Search /></el-icon>
                    </template>
                </el-input>
                <el-button type="primary" @click="handleSearch">搜索</el-button>
            </div>

            <el-table
                :data="tableData"
                border
                stripe
                style="width: 100%"
                v-loading="loading"
                @selection-change="handleSelectionChange"
            >
                <el-table-column type="selection" width="55" />
                <el-table-column type="index" label="序号" width="60" align="center" />
                <el-table-column prop="username" label="用户名" width="120" />
                <el-table-column prop="nickname" label="昵称" width="120" />
                <el-table-column prop="phone" label="手机号" width="130" />
                <el-table-column prop="email" label="邮箱" width="180" />
                <el-table-column prop="role" label="角色" width="100">
                    <template #default="{ row }">
                        <el-tag :type="row.role === '管理员' ? 'danger' : 'primary'">
                            {{ row.role }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="row.status === '启用' ? 'success' : 'info'">
                            {{ row.status }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="180" />
                <el-table-column label="操作" width="150" align="center" fixed="right">
                    <template #default="{ row }">
                        <el-button
                            type="primary"
                            size="small"
                            @click="handleEdit(row)"
                        >
                            修改
                        </el-button>
                        <el-button
                            type="danger"
                            size="small"
                            @click="handleDelete(row)"
                        >
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-container">
                <el-pagination
                    v-model:current-page="currentPage"
                    v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 50, 100]"
                    :total="total"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                />
            </div>
        </el-card>

        <el-dialog
            v-model="dialogVisible"
            :title="dialogTitle"
            width="500px"
        >
            <el-form
                ref="formRef"
                :model="form"
                :rules="rules"
                label-width="80px"
            >
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
                <el-form-item label="角色" prop="role">
                    <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
                        <el-option label="管理员" value="管理员" />
                        <el-option label="普通用户" value="普通用户" />
                    </el-select>
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                        <el-option label="启用" value="启用" />
                        <el-option label="禁用" value="禁用" />
                    </el-select>
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
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Delete, Refresh } from '@element-plus/icons-vue'

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
    role: '普通用户',
    status: '启用'
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
    role: [
        { required: true, message: '请选择角色', trigger: 'change' }
    ],
    status: [
        { required: true, message: '请选择状态', trigger: 'change' }
    ]
}

const allUsers = ref([
    {
        id: 1,
        username: 'admin',
        nickname: '超级管理员',
        phone: '13800138000',
        email: 'admin@example.com',
        role: '管理员',
        status: '启用',
        createTime: '2024-01-01 10:00:00'
    },
    {
        id: 2,
        username: 'user001',
        nickname: '张三',
        phone: '13900139000',
        email: 'zhangsan@example.com',
        role: '普通用户',
        status: '启用',
        createTime: '2024-01-02 14:30:00'
    },
    {
        id: 3,
        username: 'user002',
        nickname: '李四',
        phone: '13700137000',
        email: 'lisi@example.com',
        role: '普通用户',
        status: '启用',
        createTime: '2024-01-03 09:15:00'
    },
    {
        id: 4,
        username: 'user003',
        nickname: '王五',
        phone: '13600136000',
        email: 'wangwu@example.com',
        role: '普通用户',
        status: '禁用',
        createTime: '2024-01-04 16:45:00'
    },
    {
        id: 5,
        username: 'user004',
        nickname: '赵六',
        phone: '13500135000',
        email: 'zhaoliu@example.com',
        role: '普通用户',
        status: '启用',
        createTime: '2024-01-05 11:20:00'
    },
    {
        id: 6,
        username: 'user005',
        nickname: '钱七',
        phone: '13400134000',
        email: 'qianqi@example.com',
        role: '普通用户',
        status: '启用',
        createTime: '2024-01-06 15:00:00'
    },
    {
        id: 7,
        username: 'user006',
        nickname: '孙八',
        phone: '13300133000',
        email: 'sunba@example.com',
        role: '普通用户',
        status: '启用',
        createTime: '2024-01-07 13:30:00'
    },
    {
        id: 8,
        username: 'user007',
        nickname: '周九',
        phone: '13200132000',
        email: 'zhoujiu@example.com',
        role: '普通用户',
        status: '禁用',
        createTime: '2024-01-08 10:45:00'
    },
    {
        id: 9,
        username: 'user008',
        nickname: '吴十',
        phone: '13100131000',
        email: 'wushi@example.com',
        role: '普通用户',
        status: '启用',
        createTime: '2024-01-09 14:20:00'
    },
    {
        id: 10,
        username: 'user009',
        nickname: '郑十一',
        phone: '13000130000',
        email: 'zhengshiyi@example.com',
        role: '普通用户',
        status: '启用',
        createTime: '2024-01-10 09:00:00'
    },
    {
        id: 11,
        username: 'user010',
        nickname: '王十二',
        phone: '15800158000',
        email: 'wangshier@example.com',
        role: '普通用户',
        status: '启用',
        createTime: '2024-01-11 16:30:00'
    },
    {
        id: 12,
        username: 'user011',
        nickname: '冯十三',
        phone: '15900159000',
        email: 'fengshisan@example.com',
        role: '普通用户',
        status: '启用',
        createTime: '2024-01-12 11:15:00'
    }
])

const tableData = computed(() => {
    let filteredUsers = allUsers.value

    if (searchKeyword.value) {
        const keyword = searchKeyword.value.toLowerCase()
        filteredUsers = filteredUsers.filter(user =>
            user.username.toLowerCase().includes(keyword) ||
            user.phone.includes(keyword) ||
            user.email.toLowerCase().includes(keyword)
        )
    }

    total.value = filteredUsers.length

    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return filteredUsers.slice(start, end)
})

const handleSearch = () => {
    currentPage.value = 1
}

const handleSizeChange = (val) => {
    pageSize.value = val
    currentPage.value = 1
}

const handleCurrentChange = (val) => {
    currentPage.value = val
}

const handleAdd = () => {
    dialogTitle.value = '新增用户'
    Object.assign(form, {
        id: null,
        username: '',
        nickname: '',
        phone: '',
        email: '',
        role: '普通用户',
        status: '启用'
    })
    dialogVisible.value = true
}

const handleEdit = (row) => {
    dialogTitle.value = '修改用户'
    Object.assign(form, { ...row })
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
    ).then(() => {
        const index = allUsers.value.findIndex(user => user.id === row.id)
        if (index !== -1) {
            allUsers.value.splice(index, 1)
            ElMessage.success('删除成功')
        }
    }).catch(() => {})
}

const handleSubmit = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            if (form.id) {
                const index = allUsers.value.findIndex(user => user.id === form.id)
                if (index !== -1) {
                    allUsers.value[index] = { ...form }
                    ElMessage.success('修改成功')
                }
            } else {
                const newUser = {
                    ...form,
                    id: Date.now(),
                    createTime: new Date().toLocaleString('zh-CN', {
                        year: 'numeric',
                        month: '2-digit',
                        day: '2-digit',
                        hour: '2-digit',
                        minute: '2-digit',
                        second: '2-digit',
                        hour12: false
                    }).replace(/\//g, '-')
                }
                allUsers.value.unshift(newUser)
                ElMessage.success('新增成功')
            }
            dialogVisible.value = false
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
    ).then(() => {
        const selectedIds = selectedRows.value.map(row => row.id)
        allUsers.value = allUsers.value.filter(user => !selectedIds.includes(user.id))
        selectedRows.value = []
        ElMessage.success('批量删除成功')
    }).catch(() => {})
}

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
    ).then(() => {
        const selectedIds = selectedRows.value.map(row => row.id)
        allUsers.value = allUsers.value.map(user => {
            if (selectedIds.includes(user.id)) {
                return { ...user, status: newStatus }
            }
            return user
        })
        selectedRows.value = []
        ElMessage.success(`批量${newStatus}成功`)
    }).catch(() => {})
}
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

.search-bar {
    margin-bottom: 20px;
    display: flex;
    gap: 10px;
}

.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}
</style>
