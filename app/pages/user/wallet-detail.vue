<template>
	<view class="detail-page">
		<view class="flow-tabs">
			<text v-for="(tab, i) in tabList" :key="i" class="flow-tab" :class="{ active: currentTab === i }"
				@click="currentTab = i">{{ tab.name }}</text>
		</view>
		<scroll-view class="flow-list" scroll-y :style="{ height: listHeight }">
			<view v-for="record in filteredRecords" :key="record.tid" class="flow-item">
				<view class="flow-item-left" :class="getRecordTypeClass(record.type)">
					<text class="flow-item-title">{{ getRecordTypeText(record.type) }}</text>
					<text class="flow-item-time">{{ formatTime(record.create_time) }}</text>
				</view>
				<text class="flow-item-amount" :class="getAmountClass(record.amount, record.type)">
					{{ getAmountPrefix(record.type) }}{{ formatAmount(record.amount) }}
				</text>
			</view>
			<view v-if="filteredRecords.length === 0" class="flow-empty">暂无记录</view>
		</scroll-view>
		<u-loading-page :loading="loading" />
	</view>
</template>

<script setup lang="ts">
import { ref, computed, Ref } from 'vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { onShow } from '@dcloudio/uni-app'

const userInfo = useUserStore().info
const loading: Ref<boolean> = ref(false)
const transactionRecords: Ref<any[]> = ref([])
const currentTab = ref(0)
const listHeight = '75vh'

const tabList = [{ name: '全部' }, { name: '收入' }, { name: '支出' }]

const recordTypeMap: Record<string, { text: string; isIncome: boolean }> = {
	'RECHARGE': { text: '充值', isIncome: true },
	'WITHDRAWAL': { text: '提现', isIncome: false },
	'ORDER': { text: '订单支付', isIncome: false },
	'COMPLETE_ORDER': { text: '订单收入', isIncome: true },
	'CANCEL_ORDER': { text: '订单退款', isIncome: true }
}

onShow(() => {
	loadList()
})

function loadList() {
	loading.value = true
	request({ url: '/transaction/list?uid=' + userInfo.uid })
		.then((res: any) => {
			if (res.data && Array.isArray(res.data)) {
				transactionRecords.value = res.data.sort((a: any, b: any) =>
					new Date(b.create_time).getTime() - new Date(a.create_time).getTime()
				)
			} else {
				transactionRecords.value = []
			}
		})
		.catch(() => {
			transactionRecords.value = []
		})
		.finally(() => {
			loading.value = false
		})
}

const formatAmount = (amount: number) => Math.abs(amount).toFixed(2)

function formatTime(timeStr: string) {
	if (!timeStr) return ''
	const d = new Date(timeStr)
	const M = (d.getMonth() + 1).toString().padStart(2, '0')
	const D = d.getDate().toString().padStart(2, '0')
	const h = d.getHours().toString().padStart(2, '0')
	const m = d.getMinutes().toString().padStart(2, '0')
	return `${M}-${D} ${h}:${m}`
}

function getRecordTypeText(type: string) {
	return recordTypeMap[type]?.text ?? type
}

function getRecordTypeClass(type: string) {
	const map: Record<string, string> = {
		'RECHARGE': 'recharge',
		'WITHDRAWAL': 'withdraw',
		'ORDER': 'shopping',
		'COMPLETE_ORDER': 'transfer',
		'CANCEL_ORDER': 'refund'
	}
	return map[type] ?? 'default'
}

function getAmountPrefix(type: string) {
	return recordTypeMap[type]?.isIncome ? '+' : '-'
}

function getAmountClass(_amount: number, type: string) {
	return recordTypeMap[type]?.isIncome ? 'income' : 'expense'
}

const filteredRecords = computed(() => {
	const list = transactionRecords.value
	if (currentTab.value === 1) return list.filter((r: any) => recordTypeMap[r.type]?.isIncome)
	if (currentTab.value === 2) return list.filter((r: any) => !recordTypeMap[r.type]?.isIncome)
	return list
})
</script>

<style scoped>
	.detail-page {
		min-height: 100vh;
		background: #f5f5f5;
		padding-top: 24rpx;
	}

	.flow-tabs {
		display: flex;
		gap: 32rpx;
		margin: 0 24rpx 16rpx;
		padding-bottom: 16rpx;
		border-bottom: 1rpx solid #eee;
		background: #fff;
		padding: 24rpx 24rpx 16rpx;
		border-radius: 12rpx 12rpx 0 0;
	}

	.flow-tab {
		font-size: 28rpx;
		color: #888;
	}

	.flow-tab.active {
		color: #1a1a1a;
		font-weight: 600;
		border-bottom: 3rpx solid #1a1a1a;
		padding-bottom: 4rpx;
		margin-bottom: -20rpx;
	}

	.flow-list {
		background: #fff;
		padding: 0 24rpx;
	}

	.flow-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 28rpx 0;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.flow-item-left {
		padding-left: 20rpx;
		border-left: 4rpx solid #e0e0e0;
	}

	.flow-item-left.recharge { border-left-color: #22c55e; }
	.flow-item-left.withdraw { border-left-color: #ef4444; }
	.flow-item-left.transfer { border-left-color: #8b5cf6; }
	.flow-item-left.refund { border-left-color: #f59e0b; }
	.flow-item-left.shopping { border-left-color: #ec4899; }

	.flow-item-title {
		display: block;
		font-size: 28rpx;
		color: #1a1a1a;
		margin-bottom: 6rpx;
	}

	.flow-item-time {
		display: block;
		font-size: 22rpx;
		color: #999;
	}

	.flow-item-amount {
		font-size: 30rpx;
		font-weight: 600;
	}

	.flow-item-amount.income { color: #22c55e; }
	.flow-item-amount.expense { color: #ef4444; }

	.flow-empty {
		text-align: center;
		padding: 80rpx 0;
		font-size: 28rpx;
		color: #999;
	}
</style>
