<template>
    <div class="dashboard-container">
        <el-row :gutter="20" class="stats-row">
            <el-col :span="6">
                <el-card class="stat-card">
                    <div class="stat-content">
                        <div class="stat-info">
                            <div class="stat-label">总用户数</div>
                            <div class="stat-number">
                                <n-number-animation :from="0" :to="statistics?.totalUsers || 0" :duration="2000"
                                    :show-separator="true" />
                                <span class="stat-suffix">人</span>
                            </div>
                            <div class="stat-trend" :class="getUserTrend()">
                                <el-icon>
                                    <Top v-if="getUserTrend() === 'up'" />
                                    <Bottom v-else />
                                </el-icon>
                                <span>{{ getUserGrowth() }}%</span>
                                <span class="trend-text">较上月</span>
                            </div>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card class="stat-card">
                    <div class="stat-content">
                        <div class="stat-info">
                            <div class="stat-label">总订单数</div>
                            <div class="stat-number">
                                <n-number-animation :from="0" :to="statistics?.totalOrders || 0" :duration="2000"
                                    :show-separator="true" />
                                <span class="stat-suffix">单</span>
                            </div>
                            <div class="stat-trend" :class="getOrderTrend()">
                                <el-icon>
                                    <Top v-if="getOrderTrend() === 'up'" />
                                    <Bottom v-else />
                                </el-icon>
                                <span>{{ getOrderGrowth() }}%</span>
                                <span class="trend-text">较上月</span>
                            </div>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card class="stat-card">
                    <div class="stat-content">
                        <div class="stat-info">
                            <div class="stat-label">上月新增用户</div>
                            <div class="stat-number">
                                <n-number-animation :from="0" :to="statistics?.lastMonthUsers || 0" :duration="2000"
                                    :show-separator="true" />
                                <span class="stat-suffix">人</span>
                            </div>
                            <div class="stat-trend">
                                <span class="trend-text">{{ statistics?.month || '-' }}</span>
                            </div>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card class="stat-card">
                    <div class="stat-content">
                        <div class="stat-info">
                            <div class="stat-label">总交易金额</div>
                            <div class="stat-number">
                                <span class="stat-prefix">¥</span>
                                <n-number-animation :from="0" :to="statistics?.totalAmount || 0" :duration="2000"
                                    :show-separator="true" />
                            </div>
                            <div class="stat-trend" :class="getAmountTrend()">
                                <el-icon>
                                    <Top v-if="getAmountTrend() === 'up'" />
                                    <Bottom v-else />
                                </el-icon>
                                <span>{{ getAmountGrowth() }}%</span>
                                <span class="trend-text">较上月</span>
                            </div>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <el-row :gutter="20" class="charts-row">
            <el-col :span="12">
                <el-card class="chart-card">
                    <template #header>
                        <div class="card-header">
                            <span>近七天交易额趋势</span>
                        </div>
                    </template>
                    <v-chart class="chart" :option="lineChartOption" autoresize />
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card class="chart-card">
                    <template #header>
                        <div class="card-header">
                            <span>订单类型分布</span>
                        </div>
                    </template>
                    <v-chart class="chart" :option="pieChartOption" autoresize />
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart, PieChart } from 'echarts/charts'
import {
    TitleComponent,
    TooltipComponent,
    LegendComponent,
    GridComponent
} from 'echarts/components'
import { Top, Bottom } from '@element-plus/icons-vue'
import { NNumberAnimation } from 'naive-ui'
import { getLatestStatistics, getDashboardCharts } from '@/api/statistics'

use([
    CanvasRenderer,
    LineChart,
    BarChart,
    PieChart,
    TitleComponent,
    TooltipComponent,
    LegendComponent,
    GridComponent
])

const statistics = ref(null)

const loadStatistics = async () => {
    try {
        const response = await getLatestStatistics()
        if (response.data) {
            statistics.value = response.data
        }
    } catch (error) {
        console.error('Failed to load statistics:', error)
    }
}

const loadDashboardCharts = async () => {
    try {
        const response = await getDashboardCharts()
        if (response.data) {
            const data = response.data

            if (data.transactionTrend) {
                lineChartOption.value.xAxis.data = data.transactionTrend.dates
                lineChartOption.value.series[0].data = data.transactionTrend.amounts
            }

            if (data.orderTypes) {
                pieChartOption.value.series[0].data = data.orderTypes.map(item => ({
                    ...item,
                    itemStyle: { color: item.bgColor || '#909399' }
                }))
            }
        }
    } catch (error) {
        console.error('Failed to load dashboard charts:', error)
    }
}

onMounted(() => {
    loadStatistics()
    loadDashboardCharts()
})

const getUserTrend = () => {
    if (!statistics.value || statistics.value.lastMonthUsers === 0) return 'up'
    return 'up'
}

const getUserGrowth = () => {
    if (!statistics.value || statistics.value.lastMonthUsers === 0) return '0'
    const growth = ((statistics.value.lastMonthUsers / (statistics.value.totalUsers - statistics.value.lastMonthUsers)) * 100).toFixed(1)
    return growth
}

const getOrderTrend = () => {
    if (!statistics.value || statistics.value.lastMonthOrders === 0) return 'up'
    return 'up'
}

const getOrderGrowth = () => {
    if (!statistics.value || statistics.value.lastMonthOrders === 0) return '0'
    const growth = ((statistics.value.lastMonthOrders / (statistics.value.totalOrders - statistics.value.lastMonthOrders)) * 100).toFixed(1)
    return growth
}

const getAmountTrend = () => {
    if (!statistics.value || statistics.value.lastMonthAmount === 0) return 'up'
    return 'up'
}

const getAmountGrowth = () => {
    if (!statistics.value || statistics.value.lastMonthAmount === 0) return '0'
    const growth = ((statistics.value.lastMonthAmount / (statistics.value.totalAmount - statistics.value.lastMonthAmount)) * 100).toFixed(1)
    return growth
}

const lineChartOption = ref({
    tooltip: {
        trigger: 'axis'
    },
    legend: {
        data: ['交易额'],
        top: 0
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        top: '15%',
        containLabel: true
    },
    xAxis: {
        type: 'category',
        boundaryGap: false,
        data: []
    },
    yAxis: {
        type: 'value',
        name: '金额（元）'
    },
    series: [
        {
            name: '交易额',
            type: 'line',
            data: [],
            smooth: true,
            areaStyle: {
                color: {
                    type: 'linear',
                    x: 0,
                    y: 0,
                    x2: 0,
                    y2: 1,
                    colorStops: [
                        { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                        { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
                    ]
                }
            },
            itemStyle: {
                color: '#409eff'
            }
        }
    ]
})

const pieChartOption = ref({
    tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
        orient: 'vertical',
        left: 'left',
        top: 'middle'
    },
    series: [
        {
            name: '订单类型',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['60%', '50%'],
            avoidLabelOverlap: false,
            itemStyle: {
                borderRadius: 10,
                borderColor: '#fff',
                borderWidth: 2
            },
            label: {
                show: false,
                position: 'center'
            },
            emphasis: {
                label: {
                    show: true,
                    fontSize: 20,
                    fontWeight: 'bold'
                }
            },
            labelLine: {
                show: false
            },
            data: []
        }
    ]
})
</script>

<style scoped>
.dashboard-container {
    padding: 20px;
}

.stats-row {
    margin-bottom: 20px;
}

.stat-card {
    cursor: pointer;
    transition: all 0.3s;
}

.stat-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stat-content {
    padding: 10px 0;
}

.stat-info {
    flex: 1;
}

.stat-label {
    font-size: 14px;
    color: #909399;
    margin-bottom: 8px;
}

.stat-number {
    display: flex;
    align-items: baseline;
    margin-bottom: 8px;
    font-size: 28px;
    font-weight: bold;
    color: #303133;
}

.stat-prefix {
    font-size: 20px;
    color: #909399;
    margin-right: 4px;
}

.stat-suffix {
    font-size: 16px;
    color: #909399;
    margin-left: 4px;
}

.stat-trend {
    display: flex;
    align-items: center;
    font-size: 13px;
    gap: 4px;
}

.stat-trend.up {
    color: #67c23a;
}

.stat-trend.down {
    color: #f56c6c;
}

.trend-text {
    color: #909399;
    margin-left: 4px;
}

.charts-row {
    margin-bottom: 20px;
}

.chart-card {
    /* height: 600px; */
}

.card-header {
    font-size: 16px;
    font-weight: bold;
    color: #303133;
}

.chart {
    height: 320px;
    width: 100%;
}
</style>
