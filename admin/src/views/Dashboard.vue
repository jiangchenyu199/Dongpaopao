<template>
    <div class="dashboard-container">
        <el-row :gutter="20" class="stats-row">
            <el-col :span="6">
                <el-card class="stat-card">
                    <div class="stat-content">
                        <div class="stat-info">
                            <div class="stat-label">总用户数</div>
                            <div class="stat-number">
                                <n-number-animation :from="0" :to="1234" :duration="2000" :show-separator="true" />
                                <span class="stat-suffix">人</span>
                            </div>
                            <div class="stat-trend up">
                                <el-icon>
                                    <Top />
                                </el-icon>
                                <span>12.5%</span>
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
                                <n-number-animation :from="0" :to="5678" :duration="2000" :show-separator="true" />
                                <span class="stat-suffix">单</span>
                            </div>
                            <div class="stat-trend up">
                                <el-icon>
                                    <Top />
                                </el-icon>
                                <span>8.3%</span>
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
                            <div class="stat-label">商品总数</div>
                            <div class="stat-number">
                                <n-number-animation :from="0" :to="892" :duration="2000" :show-separator="true" />
                                <span class="stat-suffix">件</span>
                            </div>
                            <div class="stat-trend down">
                                <el-icon>
                                    <Bottom />
                                </el-icon>
                                <span>2.1%</span>
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
                            <div class="stat-label">总销售额</div>
                            <div class="stat-number">
                                <span class="stat-prefix">¥</span>
                                <n-number-animation :from="0" :to="234567" :duration="2000" :show-separator="true" />
                            </div>
                            <div class="stat-trend up">
                                <el-icon>
                                    <Top />
                                </el-icon>
                                <span>15.7%</span>
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
                            <span>销售趋势</span>
                        </div>
                    </template>
                    <v-chart class="chart" :option="lineChartOption" autoresize />
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card class="chart-card">
                    <template #header>
                        <div class="card-header">
                            <span>商品分类占比</span>
                        </div>
                    </template>
                    <v-chart class="chart" :option="pieChartOption" autoresize />
                </el-card>
            </el-col>
        </el-row>

        <el-row :gutter="20" class="charts-row">
            <el-col :span="12">
                <el-card class="chart-card">
                    <template #header>
                        <div class="card-header">
                            <span>月度订单统计</span>
                        </div>
                    </template>
                    <v-chart class="chart" :option="barChartOption" autoresize />
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card class="chart-card">
                    <template #header>
                        <div class="card-header">
                            <span>用户增长趋势</span>
                        </div>
                    </template>
                    <v-chart class="chart" :option="areaChartOption" autoresize />
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref } from 'vue'
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

const lineChartOption = ref({
    tooltip: {
        trigger: 'axis'
    },
    legend: {
        data: ['销售额', '订单数'],
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
        data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月']
    },
    yAxis: {
        type: 'value'
    },
    series: [
        {
            name: '销售额',
            type: 'line',
            data: [200, 300, 150, 250, 400, 200, 350],
            smooth: true
        },
        {
            name: '订单数',
            type: 'line',
            data: [220, 182, 191, 234, 290, 330, 310],
            smooth: true
        }
    ]
})

const pieChartOption = ref({
    tooltip: {
        trigger: 'item'
    },
    legend: {
        orient: 'vertical',
        left: 'left'
    },
    series: [
        {
            name: '商品分类',
            type: 'pie',
            radius: '50%',
            data: [
                { value: 1048, name: '电子产品' },
                { value: 735, name: '服装' },
                { value: 580, name: '食品' },
                { value: 484, name: '家居' },
                { value: 300, name: '其他' }
            ],
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }
    ]
})

const barChartOption = ref({
    tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'shadow'
        }
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis: {
        type: 'category',
        data: ['1月', '2月', '3月', '4月', '5月', '6月']
    },
    yAxis: {
        type: 'value'
    },
    series: [
        {
            name: '订单数',
            type: 'bar',
            data: [320, 332, 301, 334, 390, 330]
        }
    ]
})

const areaChartOption = ref({
    tooltip: {
        trigger: 'axis'
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月']
    },
    yAxis: {
        type: 'value'
    },
    series: [
        {
            name: '新增用户',
            type: 'line',
            stack: 'Total',
            areaStyle: {},
            emphasis: {
                focus: 'series'
            },
            data: [120, 132, 101, 134, 90, 230, 210]
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
