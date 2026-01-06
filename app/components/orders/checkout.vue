<template>
	<view class="checkout-container">
		<view class="checkout-content">
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
		justify-content: center;
		align-items: center;
	}
	
	.submit-btn {
		width: 100%;
		height: 40rpx;
		font-size: 32rpx;
		font-weight: bold;
		border-radius: 30rpx;
	}
</style>