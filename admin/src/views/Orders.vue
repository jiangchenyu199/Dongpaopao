<template>
    <div class="orders-container">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>订单管理</span>
                </div>
            </template>

            <div class="search-bar">
                <el-input v-model="searchKeyword" placeholder="搜索订单号、用户名" clearable style="width: 250px"
                    @clear="handleSearch">
                    <template #prefix>
                        <el-icon>
                            <Search />
                        </el-icon>
                    </template>
                </el-input>
                <el-select v-model="statusFilter" placeholder="订单状态" clearable style="width: 150px"
                    @change="handleSearch">
                    <el-option label="待支付" value="待支付" />
                    <el-option label="已支付" value="已支付" />
                    <el-option label="待发货" value="待发货" />
                    <el-option label="已发货" value="已发货" />
                    <el-option label="已完成" value="已完成" />
                    <el-option label="已取消" value="已取消" />
                </el-select>
                <el-button type="primary" @click="handleSearch">搜索</el-button>
            </div>

            <el-table :data="tableData" border stripe style="width: 100%" v-loading="loading">
                <el-table-column type="index" label="序号" width="60" align="center" />
                <el-table-column prop="orderNo" label="订单号" width="180" />
                <el-table-column prop="buyer" label="下单人" width="120" />
                <el-table-column prop="receiver" label="接单人" width="120" />
                <el-table-column prop="amount" label="订单金额" width="120" align="right">
                    <template #default="{ row }">
                        <span style="color: #f56c6c; font-weight: bold">¥{{ row.amount.toFixed(2) }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="订单状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="getStatusType(row.status)">
                            {{ row.status }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="下单时间" width="180" />
                <el-table-column label="操作" width="100" align="center">
                    <template #default="{ row }">
                        <el-button type="plain" size="small" @click="handleViewDetail(row)">
                            查看详情
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-container">
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 50, 100]" :total="total" layout="total, sizes, prev, pager, next, jumper"
                    @size-change="handleSizeChange" @current-change="handleCurrentChange" />
            </div>
        </el-card>

        <el-dialog v-model="detailDialogVisible" title="订单详情" width="600px">
            <el-descriptions :column="1" border>
                <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
                <el-descriptions-item label="下单人">{{ currentOrder.buyer }}</el-descriptions-item>
                <el-descriptions-item label="接单人">{{ currentOrder.receiver }}</el-descriptions-item>
                <el-descriptions-item label="订单金额">
                    <span style="color: #f56c6c; font-weight: bold; font-size: 18px">¥{{ currentOrder.amount.toFixed(2) }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="订单状态">
                    <el-tag :type="getStatusType(currentOrder.status)">{{ currentOrder.status }}</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="联系电话">{{ currentOrder.phone }}</el-descriptions-item>
                <el-descriptions-item label="收货地址">{{ currentOrder.address }}</el-descriptions-item>
                <el-descriptions-item label="下单时间">{{ currentOrder.createTime }}</el-descriptions-item>
                <el-descriptions-item label="支付时间">{{ currentOrder.payTime || '-' }}</el-descriptions-item>
                <el-descriptions-item label="发货时间">{{ currentOrder.shipTime || '-' }}</el-descriptions-item>
                <el-descriptions-item label="完成时间">{{ currentOrder.finishTime || '-' }}</el-descriptions-item>
            </el-descriptions>
            <template #footer>
                <el-button @click="detailDialogVisible = false">关闭</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const loading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailDialogVisible = ref(false)
const currentOrder = ref({})

const allOrders = ref([
    {
        id: 1,
        orderNo: 'ORD20240101001',
        buyer: '张三',
        receiver: '李师傅',
        amount: 9999.00,
        status: '已完成',
        phone: '13900139000',
        address: '北京市朝阳区xxx街道xxx号',
        createTime: '2024-01-01 10:00:00',
        payTime: '2024-01-01 10:05:00',
        shipTime: '2024-01-02 09:00:00',
        finishTime: '2024-01-05 14:30:00'
    },
    {
        id: 2,
        orderNo: 'ORD20240102001',
        buyer: '李四',
        receiver: '王师傅',
        amount: 14999.00,
        status: '已发货',
        phone: '13800138000',
        address: '上海市浦东新区xxx路xxx号',
        createTime: '2024-01-02 14:30:00',
        payTime: '2024-01-02 14:35:00',
        shipTime: '2024-01-03 10:00:00',
        finishTime: null
    },
    {
        id: 3,
        orderNo: 'ORD20240103001',
        buyer: '王五',
        receiver: '赵师傅',
        amount: 16998.00,
        status: '待发货',
        phone: '13700137000',
        address: '广州市天河区xxx大道xxx号',
        createTime: '2024-01-03 09:15:00',
        payTime: '2024-01-03 09:20:00',
        shipTime: null,
        finishTime: null
    },
    {
        id: 4,
        orderNo: 'ORD20240104001',
        buyer: '赵六',
        receiver: '钱师傅',
        amount: 1899.00,
        status: '已支付',
        phone: '13600136000',
        address: '深圳市南山区xxx路xxx号',
        createTime: '2024-01-04 16:45:00',
        payTime: '2024-01-04 16:50:00',
        shipTime: null,
        finishTime: null
    },
    {
        id: 5,
        orderNo: 'ORD20240105001',
        buyer: '钱七',
        receiver: '孙师傅',
        amount: 6299.00,
        status: '待支付',
        phone: '13500135000',
        address: '杭州市西湖区xxx街xxx号',
        createTime: '2024-01-05 11:20:00',
        payTime: null,
        shipTime: null,
        finishTime: null
    },
    {
        id: 6,
        orderNo: 'ORD20240106001',
        buyer: '孙八',
        receiver: '周师傅',
        amount: 4499.00,
        status: '已取消',
        phone: '13400134000',
        address: '成都市武侯区xxx路xxx号',
        createTime: '2024-01-06 15:00:00',
        payTime: null,
        shipTime: null,
        finishTime: null
    },
    {
        id: 7,
        orderNo: 'ORD20240107001',
        buyer: '周九',
        receiver: '吴师傅',
        amount: 11499.00,
        status: '已完成',
        phone: '13300133000',
        address: '武汉市江汉区xxx大道xxx号',
        createTime: '2024-01-07 13:30:00',
        payTime: '2024-01-07 13:35:00',
        shipTime: '2024-01-08 09:00:00',
        finishTime: '2024-01-10 16:00:00'
    },
    {
        id: 8,
        orderNo: 'ORD20240108001',
        buyer: '吴十',
        receiver: '郑师傅',
        amount: 899.00,
        status: '已发货',
        phone: '13200132000',
        address: '南京市鼓楼区xxx路xxx号',
        createTime: '2024-01-08 10:45:00',
        payTime: '2024-01-08 10:50:00',
        shipTime: '2024-01-09 11:00:00',
        finishTime: null
    },
    {
        id: 9,
        orderNo: 'ORD20240109001',
        buyer: '郑十一',
        receiver: '王师傅',
        amount: 1398.00,
        status: '待发货',
        phone: '13100131000',
        address: '西安市雁塔区xxx街xxx号',
        createTime: '2024-01-09 14:20:00',
        payTime: '2024-01-09 14:25:00',
        shipTime: null,
        finishTime: null
    },
    {
        id: 10,
        orderNo: 'ORD20240110001',
        buyer: '王十二',
        receiver: '冯师傅',
        amount: 897.00,
        status: '已支付',
        phone: '13000130000',
        address: '重庆市渝中区xxx路xxx号',
        createTime: '2024-01-10 09:00:00',
        payTime: '2024-01-10 09:05:00',
        shipTime: null,
        finishTime: null
    },
    {
        id: 11,
        orderNo: 'ORD20240111001',
        buyer: '冯十三',
        receiver: '陈师傅',
        amount: 799.00,
        status: '待支付',
        phone: '15800158000',
        address: '天津市和平区xxx道xxx号',
        createTime: '2024-01-11 16:30:00',
        payTime: null,
        shipTime: null,
        finishTime: null
    },
    {
        id: 12,
        orderNo: 'ORD20240112001',
        buyer: '陈十四',
        receiver: '刘师傅',
        amount: 1498.00,
        status: '已取消',
        phone: '15900159000',
        address: '苏州市姑苏区xxx路xxx号',
        createTime: '2024-01-12 11:15:00',
        payTime: null,
        shipTime: null,
        finishTime: null
    }
])

const tableData = computed(() => {
    let filteredOrders = allOrders.value

    if (searchKeyword.value) {
        const keyword = searchKeyword.value.toLowerCase()
        filteredOrders = filteredOrders.filter(order =>
            order.orderNo.toLowerCase().includes(keyword) ||
            order.buyer.toLowerCase().includes(keyword) ||
            order.receiver.toLowerCase().includes(keyword)
        )
    }

    if (statusFilter.value) {
        filteredOrders = filteredOrders.filter(order => order.status === statusFilter.value)
    }

    total.value = filteredOrders.length

    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return filteredOrders.slice(start, end)
})

const getStatusType = (status) => {
    const statusMap = {
        '待支付': 'warning',
        '已支付': 'primary',
        '待发货': 'info',
        '已发货': 'success',
        '已完成': '',
        '已取消': 'danger'
    }
    return statusMap[status] || ''
}

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

const handleViewDetail = (row) => {
    currentOrder.value = { ...row }
    detailDialogVisible.value = true
}
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
    justify-content: flex-end;
}
</style>
