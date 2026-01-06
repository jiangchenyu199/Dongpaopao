<template>
	<view class="card">
		<template #title>
			<u-title>费用信息</u-title>
		</template>
		<view class="fee-info">
			<view class="info-item">
				<text class="label">订单金额</text>
				<text class="value">¥{{ orderAmount }}</text>
			</view>
			<view class="info-item">
				<text class="label">配送费</text>
				<text class="value">¥{{ deliveryFee }}</text>
			</view>
			<view class="info-item">
				<text class="label">小费</text>
				<text class="value">¥{{ tip }}</text>
			</view>
			<view class="info-item total">
				<text class="label">总计</text>
				<text class="value total-value">¥{{ totalAmount }}</text>
			</view>
		</view>
	</view>
</template>

<script lang="ts" setup>
	import { withDefaults } from 'vue'
	
	const props = withDefaults(defineProps<{
		orderAmount: string
		deliveryFee: string
		tip: string
		totalAmount: string
	}>(), {
		orderAmount: '0.00',
		deliveryFee: '0.00',
		tip: '0.00',
		totalAmount: '0.00'
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
</style>