<template>
	<view class="checkout-container">
		<view class="checkout-content">
			<view class="total-info">
				<text class="label">合计</text>
				<text class="value">¥{{ totalAmount }}</text>
			</view>
			<u-button type="primary" size="large" @click="handleSubmit" class="submit-btn">
				确认下单
			</u-button>
		</view>
	</view>
</template>

<script lang="ts" setup>
	import { ref, computed } from 'vue';
	
	const emit = defineEmits<{
		(e: 'submit'): void;
	}>();
	
	const props = defineProps<{
		totalAmount: string
	}>();
	
	const handleSubmit = () => {
		emit('submit');
	};
</script>

<style scoped>
	.checkout-container {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		padding: 20rpx 30rpx;
		background-color: #fff;
		border-top: 1rpx solid #f0f0f0;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
		z-index: 100;
	}
	
	.checkout-content {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}
	
	.total-info {
		flex: 1;
		margin-right: 20rpx;
	}
	
	.label {
		font-size: 24rpx;
		color: #666;
		margin-right: 8rpx;
	}
	
	.value {
		font-size: 32rpx;
		font-weight: bold;
		color: #ff6600;
	}
	
	.submit-btn {
		flex: 2;
		height: 40rpx;
		font-size: 32rpx;
		font-weight: bold;
		border-radius: 30rpx;
	}
</style>