<template>
    <div class="orders-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>订单管理</span>
                </div>
            </template>

            <div class="search-bar">
                <el-input v-model="searchKeyword" placeholder="搜索订单号" clearable style="width: 250px"
                    @clear="handleSearch">
                    <template #prefix>
                        <el-icon>
                            <Search />
                        </el-icon>
                    </template>
                </el-input>
                <el-button type="primary" @click="handleSearch">搜索</el-button>
            </div>

            <el-table :data="tableData" border stripe style="width: 100%" v-loading="loading">
                <el-table-column prop="orderNo" label="订单号" width="200px" />
                <el-table-column prop="buyer" label="下单人" />
                <el-table-column prop="receiver" label="接单人" />
                <el-table-column prop="typeName" label="订单类型" width="100px" />
                <el-table-column prop="amount" label="订单金额" align="right" width="100px">
                    <template #default="{ row }">
                        <span style="color: #f56c6c; font-weight: bold">¥{{ row.amount.toFixed(2) }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="订单状态" width="120px">
                    <template #default="{ row }">
                        <el-text :style="{ color: row.color }">
                            {{ row.status }}
                        </el-text>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="下单时间" width="180px">
                    <template #default="{ row }">
                        {{ row.createTime || '-' }}
                    </template>
                </el-table-column>
                <el-table-column prop="acceptTime" label="接单时间" width="180px">
                    <template #default="{ row }">
                        {{ row.acceptTime || '-' }}
                    </template>
                </el-table-column>
                <el-table-column prop="expectedDeliveryTime" label="期望送达时间" width="180px">
                    <template #default="{ row }">
                        {{ row.expectedDeliveryTime || '-' }}
                    </template>
                </el-table-column>
                <el-table-column prop="finishTime" label="订单完成时间" width="180px">
                    <template #default="{ row }">
                        {{ row.finishTime || '-' }}
                    </template>
                </el-table-column>
                <el-table-column label="送达情况" width="120px">
                    <template #default="{ row }">
                        <span v-if="row.finishTime && row.expectedDeliveryTime"
                            :style="{ color: getDeliveryStatusColor(row.finishTime, row.expectedDeliveryTime) }">
                            {{ getDeliveryStatus(row.finishTime, row.expectedDeliveryTime) }}
                        </span>
                        <span v-else>-</span>
                    </template>
                </el-table-column>
                <el-table-column prop="rating" label="评分" width="200px" align="center">
                    <template #default="{ row }">
                        <span v-if="row.rating !== null && row.rating !== undefined">
                            <el-rate v-model="row.rating" disabled show-score text-color="#ff9900" />
                        </span>
                        <span v-else>-</span>
                    </template>
                </el-table-column>
                <el-table-column label="操作" align="center" fixed="right" width="120px">
                    <template #default="{ row }">
                        <el-link type="primary" @click="handleViewDetail(row)">
                            查看详情
                        </el-link>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-container">
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total"
                    layout="prev, pager, next" @current-change="handleCurrentChange" />
            </div>
        </el-card>

        <el-dialog v-model="detailDialogVisible" title="订单详情" width="600px">
            <el-descriptions :column="1" border>
                <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
                <el-descriptions-item label="下单人">{{ currentOrder.buyer }}</el-descriptions-item>
                <el-descriptions-item label="接单人">{{ currentOrder.receiver }}</el-descriptions-item>
                <el-descriptions-item label="订单金额">
                    <span style="color: #f56c6c; font-weight: bold; font-size: 18px">¥{{ currentOrder.amount.toFixed(2)
                        }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="订单状态">
                    <el-text :style="{ color: currentOrder.color }">
                        {{ currentOrder.status }}
                    </el-text>
                </el-descriptions-item>
                <el-descriptions-item label="联系电话">{{ currentOrder.phone }}</el-descriptions-item>
                <el-descriptions-item label="收货地址">{{ currentOrder.address }}</el-descriptions-item>
                <el-descriptions-item label="下单时间">{{ currentOrder.createTime || '-' }}</el-descriptions-item>
                <el-descriptions-item label="支付时间">{{ currentOrder.payTime || '-' }}</el-descriptions-item>
                <el-descriptions-item label="发货时间">{{ currentOrder.shipTime || '-' }}</el-descriptions-item>
                <el-descriptions-item label="完成时间">{{ currentOrder.finishTime || '-' }}</el-descriptions-item>
                <el-descriptions-item label="订单评分">
                    <span v-if="currentOrder.rating !== null && currentOrder.rating !== undefined">
                        <el-rate v-model="currentOrder.rating" disabled show-score text-color="#ff9900" />
                    </span>
                    <span v-else>-</span>
                </el-descriptions-item>
            </el-descriptions>
            <template #footer>
                <el-button @click="detailDialogVisible = false">关闭</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getOrderList, getOrderDetail } from '@/api/order'


const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailDialogVisible = ref(false)
const currentOrder = ref({})
const tableData = ref([])

const allOrders = ref([])



const loadOrderList = async () => {
    loading.value = true
    try {
        const params = {
            page: currentPage.value,
            size: pageSize.value
        }
        if (searchKeyword.value) {
            params.oid = searchKeyword.value
        }
        const response = await getOrderList(params)
        if (response && response.data) {
            tableData.value = response.data.records || []
            total.value = response.data.total || 0
        }
    } catch (error) {
        console.error('Failed to load order list:', error)
        ElMessage.error('加载订单列表失败')
    } finally {
        loading.value = false
    }
}



const getDeliveryStatus = (finishTime, expectedTime) => {
    const finish = new Date(finishTime).getTime()
    const expected = new Date(expectedTime).getTime()
    if (finish < expected) {
        return '提前完成'
    } else if (finish > expected) {
        return '延迟完成'
    } else {
        return '准时完成'
    }
}

const getDeliveryStatusColor = (finishTime, expectedTime) => {
    const finish = new Date(finishTime).getTime()
    const expected = new Date(expectedTime).getTime()
    if (finish < expected) {
        return '#67c23a'
    } else if (finish > expected) {
        return '#f56c6c'
    } else {
        return '#409eff'
    }
}

const handleSearch = () => {
    currentPage.value = 1
    loadOrderList()
}

const handleSizeChange = (val) => {
    pageSize.value = val
    currentPage.value = 1
    loadOrderList()
}

const handleCurrentChange = (val) => {
    currentPage.value = val
    loadOrderList()
}

const handleViewDetail = async (row) => {
    try {
        const response = await getOrderDetail(row.orderNo)
        if (response && response.data) {
            currentOrder.value = response.data
            detailDialogVisible.value = true
        }
    } catch (error) {
        console.error('Failed to load order detail:', error)
        ElMessage.error('加载订单详情失败')
    }
}

onMounted(() => {
    loadOrderList()
})
</script>

<style scoped>
.orders-container {
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
    justify-content: center;
}
</style>
