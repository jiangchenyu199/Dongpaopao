<template>
  <div class="order-types-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>订单类型</span>
          <div class="header-actions">
            <el-button type="primary" :icon="Plus" @click="openForm()">新增</el-button>
            <el-button type="primary" :icon="Refresh" :loading="loading" @click="loadTypes">刷新</el-button>
          </div>
        </div>
      </template>
      <el-table v-loading="loading" :data="types" stripe style="width: 100%">
        <el-table-column prop="orderTypeId" label="类型ID" min-width="120" show-overflow-tooltip />
        <el-table-column prop="typeName" label="名称" min-width="120" />
        <el-table-column prop="emojiIcon" label="图标" width="80" />
        <el-table-column prop="bgColor" label="背景色">
          <template #default="{ row }">
            <span v-if="row.bgColor" class="color-dot" :style="{ background: row.bgColor }" />
            {{ row.bgColor || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="enabled" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openForm(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && types.length === 0" description="暂无订单类型" />
    </el-card>

    <el-dialog v-model="formVisible" :title="formRow ? '编辑订单类型' : '新增订单类型'" width="520" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="90">
        <el-form-item v-if="formRow" label="类型ID">
          <el-input v-model="form.orderTypeId" disabled />
        </el-form-item>
        <el-form-item label="名称" prop="typeName">
          <el-input v-model="form.typeName" placeholder="请输入类型名称" clearable />
        </el-form-item>
        <el-form-item label="图标" prop="emojiIcon">
          <div class="emoji-picker">
            <span v-for="emoji in emojiOptions" :key="emoji" class="emoji-option"
              :class="{ active: form.emojiIcon === emoji }" :title="emoji" @click="form.emojiIcon = emoji">{{ emoji
              }}</span>
            <el-button v-if="form.emojiIcon" type="primary" link size="small"
              @click="form.emojiIcon = ''">清空</el-button>
          </div>
          <div v-if="form.emojiIcon" class="emoji-preview">当前：{{ form.emojiIcon }}</div>
        </el-form-item>
        <el-form-item label="背景色" prop="bgColor">
          <div class="color-picker-row">
            <el-color-picker v-model="form.bgColor" show-alpha />
            <el-input v-model="form.bgColor" placeholder="或输入十六进制颜色" clearable class="color-input" />
          </div>
        </el-form-item>
        <el-form-item label="表单配置" prop="form">
          <el-input v-model="form.form" type="textarea" :rows="6"
            placeholder="JSON 数组，如 [{&quot;name&quot;:&quot;field1&quot;,&quot;label&quot;:&quot;字段名&quot;,&quot;type&quot;:&quot;text&quot;}]" />
        </el-form-item>
        <el-form-item label="状态" prop="enabled">
          <el-switch v-model="form.enabled" active-text="启用" inactive-text="停用" />
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
import { getOrderTypeList, saveOrderType, deleteOrderType } from '@/api/orderType'
import { Refresh, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const types = ref([])
const formVisible = ref(false)
const formRow = ref(null)
const submitLoading = ref(false)
const formRef = ref(null)

const form = reactive({
  orderTypeId: '',
  typeName: '',
  emojiIcon: '',
  bgColor: '',
  form: '',
  enabled: true
})
const formRules = {
  typeName: [{ required: true, message: '请输入类型名称', trigger: 'blur' }]
}

const emojiOptions = ['📦', '🛵', '🍔', '☕', '📄', '🏃', '💼', '🎁', '🧹', '🐕', '🔧', '📮', '🛒', '🍜', '🏠', '✏️', '🗑️', '📋', '🚚']

const formatTime = (t) => {
  if (!t) return ''
  return new Date(t).toLocaleString('zh-CN')
}

const loadTypes = async () => {
  loading.value = true
  try {
    const res = await getOrderTypeList()
    types.value = res?.data ?? []
  } catch {
    types.value = []
  } finally {
    loading.value = false
  }
}

const openForm = (row = null) => {
  formRow.value = row
  if (row) {
    form.orderTypeId = row.orderTypeId ?? ''
    form.typeName = row.typeName ?? ''
    form.emojiIcon = row.emojiIcon ?? ''
    form.bgColor = row.bgColor ?? ''
    form.form = typeof row.form === 'string' ? row.form : (row.form ? JSON.stringify(row.form, null, 2) : '')
    form.enabled = row.enabled !== false
  } else {
    form.orderTypeId = ''
    form.typeName = ''
    form.emojiIcon = ''
    form.bgColor = ''
    form.form = ''
    form.enabled = true
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
  submitLoading.value = true
  try {
    const payload = {
      typeName: form.typeName,
      emojiIcon: form.emojiIcon || null,
      bgColor: form.bgColor || null,
      enabled: form.enabled
    }
    if (formRow.value) {
      payload.orderTypeId = form.orderTypeId
    }
    if (form.form && form.form.trim()) {
      try {
        JSON.parse(form.form.trim())
        payload.form = form.form.trim()
      } catch {
        ElMessage.warning('表单配置不是合法 JSON，请修正后再提交')
        submitLoading.value = false
        return
      }
    } else {
      payload.form = null
    }
    await saveOrderType(payload)
    ElMessage.success(formRow.value ? '修改成功' : '新增成功')
    formVisible.value = false
    loadTypes()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除订单类型「${row.typeName}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteOrderType(row.orderTypeId)
    ElMessage.success('删除成功')
    loadTypes()
  }).catch(() => { })
}

onMounted(loadTypes)
</script>

<style scoped>
.order-types-page {
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

.color-dot {
  display: inline-block;
  width: 14px;
  height: 14px;
  border-radius: 4px;
  margin-right: 6px;
  vertical-align: middle;
}

.emoji-picker {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.emoji-option {
  font-size: 1.5rem;
  padding: 6px;
  cursor: pointer;
  border-radius: 8px;
  transition: background 0.2s;
}

.emoji-option:hover {
  background: var(--el-fill-color-light);
}

.emoji-option.active {
  background: var(--el-color-primary-light-9);
  outline: 2px solid var(--el-color-primary);
}

.emoji-preview {
  margin-top: 6px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.color-picker-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.color-picker-row .color-input {
  flex: 1;
  max-width: 200px;
}
</style>
