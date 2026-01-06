<template>
	<view class="business-promotion">
		<view class="promotion-list">
			<view class="promotion-item" v-for="(item, index) in businesses" :key="index" @click="showDetails(item)">
				<image class="promotion-image" :src="item.image" mode="aspectFill" />
				<view class="promotion-info">
					<text class="promotion-name">{{ item.name }}</text>
					<text class="promotion-desc">{{ item.description }}</text>
				</view>
			</view>
		</view>
		
		<!-- 详细信息弹窗 -->
		<u-popup v-model:show="popupShow" mode="center" border-radius="16rpx" :width="700">
			<view class="popup-content" v-if="selectedBusiness">
				<image class="popup-image" :src="selectedBusiness.image" mode="aspectFill" />
				<view class="popup-info">
					<text class="popup-name">{{ selectedBusiness.name }}</text>
					<text class="popup-desc">{{ selectedBusiness.description }}</text>
					<view class="popup-footer">
						<text class="popup-tag" v-for="(tag, tagIndex) in selectedBusiness.tags" :key="tagIndex">{{ tag }}</text>
						<text class="popup-price">{{ selectedBusiness.minPrice }}</text>
					</view>
					<u-button type="primary" size="default" class="popup-btn" @click="popupShow = false">
						关闭
					</u-button>
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script lang="ts" setup>
	import { ref, defineProps } from 'vue';

	interface BusinessItem {
		id: string;
		name: string;
		description: string;
		image: string;
		tags: string[];
		minPrice: string;
	}

	const props = defineProps<{
		businesses: BusinessItem[];
	}>();

	// 弹窗状态
	const popupShow = ref(false);
	const selectedBusiness = ref<BusinessItem | null>(null);

	// 显示详情弹窗
	const showDetails = (business: BusinessItem) => {
		selectedBusiness.value = business;
		popupShow.value = true;
	};
</script>

<style>
	.business-promotion {
		background-color: #fff;
		border-radius: 16rpx;
		padding: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.promotion-list {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.promotion-item {
		display: flex;
		align-items: center;
		padding: 20rpx;
		background-color: #f8f9fa;
		border-radius: 12rpx;
		transition: all 0.3s ease;
	}

	.promotion-item:active {
		transform: scale(0.98);
		opacity: 0.8;
	}

	.promotion-image {
		width: 120rpx;
		height: 120rpx;
		border-radius: 8rpx;
		margin-right: 20rpx;
	}

	.promotion-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
	}

	.promotion-name {
		font-size: 28rpx;
		font-weight: 600;
		color: #333;
	}

	.promotion-desc {
		font-size: 24rpx;
		color: #666;
		line-height: 32rpx;
	}

	/* 弹窗样式 */
	.popup-content {
		padding: 32rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 24rpx;
	}

	.popup-image {
		width: 100%;
		height: 300rpx;
		border-radius: 12rpx;
	}

	.popup-info {
		width: 100%;
		display: flex;
		flex-direction: column;
		gap: 16rpx;
	}

	.popup-name {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		text-align: center;
	}

	.popup-desc {
		font-size: 26rpx;
		color: #666;
		line-height: 36rpx;
		text-align: center;
	}

	.popup-footer {
		display: flex;
		align-items: center;
		gap: 12rpx;
		margin-top: 16rpx;
		flex-wrap: wrap;
		justify-content: center;
	}

	.popup-tag {
		font-size: 20rpx;
		color: #ff6b35;
		background-color: #fff0e6;
		padding: 4rpx 12rpx;
		border-radius: 12rpx;
	}

	.popup-price {
		font-size: 24rpx;
		font-weight: 600;
		color: #ff4757;
		margin-left: 12rpx;
	}

	.popup-btn {
		margin-top: 32rpx;
	}
</style>
