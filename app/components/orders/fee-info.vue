<template>
	<view class="card">
		<template #title>
			<u-title>费用信息</u-title>
		</template>
		<view class="fee-info">
			<view class="info-item">
				<text class="label">订单金额</text>
				<u-input v-model="orderAmount" type="number" placeholder="请输入订单金额" prefix="¥" class="amount-input" />
			</view>
		</view>
	</view>
</template>

<script lang="ts" setup>
	import { ref, computed } from 'vue'
	
	const orderAmount = ref('')
	
	const totalAmount = computed(() => {
		const order = parseFloat(orderAmount.value) || 0
		return order.toFixed(2)
	})
	
	// 实现getFeeData方法，用于获取费用数据
	const getFeeData = () => {
		return {
			orderAmount: orderAmount.value,
			totalAmount: totalAmount.value
		}
	}
	
	// 暴露方法给父组件
	defineExpose({
		getFeeData,
		totalAmount
	})
</script>

<style scoped>
	.card {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
	}
	
	.fee-info {
		width: 100%;
	}
	
	.info-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 15rpx 0;
		border-bottom: 1rpx solid #f0f0f0;
	}
	
	.info-item:last-child {
		border-bottom: none;
	}
	
	.label {
		font-size: 26rpx;
		color: #666;
	}
	
	.value {
		font-size: 26rpx;
		color: #333;
	}
	
	.total {
		margin-top: 10rpx;
		padding-top: 15rpx;
		border-top: 1rpx solid #f0f0f0;
	}
	
	.total-value {
		font-size: 30rpx;
		font-weight: bold;
		color: #ff6600;
	}
	
	.amount-input {
		width: 200rpx;
		text-align: right;
	}
</style>