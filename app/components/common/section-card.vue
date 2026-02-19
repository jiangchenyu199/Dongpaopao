<template>
	<view class="section-card">
		<u-title>{{ title }}</u-title>
		<view class="section-card__body"><slot></slot></view>
	</view>
</template>

<script lang="ts" setup>
defineProps<{
	title: string;
}>();
</script>

<style scoped>
.section-card {
	background-color: #fff;
	border-radius: 20rpx;
	padding: 32rpx 28rpx;
	margin-bottom: 24rpx;
	box-shadow: 0 2rpx 16rpx rgba(0, 0, 0, 0.06);
}

.section-card__body {
	margin-top: 40rpx;
}
</style>
