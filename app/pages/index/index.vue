<template>
	<u-notice-bar text="系统通知：本系统将于23:00~7:00进行维护。" speed="150" />
	<view class="page-container">
		<!-- Banner区 -->
		<u-sticky>
			<view class="banner-container">
				<swiper class="swiper" circular interval="1500" autoplay="true">
					<swiper-item>
						<image class="banner-image"
							src="https://ai-public.mastergo.com/ai/img_res/6945a6b7e8506df1abcd0efce30213d7.jpg"
							mode="widthFix" />
					</swiper-item>
					<swiper-item>
						<image class="banner-image"
							src="https://ai-public.mastergo.com/ai/img_res/ea7587e8643368749c2e819938953839.jpg"
							mode="widthFix" />
					</swiper-item>
					<swiper-item>
						<image class="banner-image"
							src="https://ai-public.mastergo.com/ai/img_res/120fce42fd9e45edcd603cc750ec7054.jpg"
							mode="widthFix" />
					</swiper-item>
				</swiper>
			</view>
		</u-sticky>

		<!-- 功能图标区 -->
		<view class="function-grid">
			<view class="function-item" v-for="item in functionList" :key="item.orderTypeId"
				@click="navigateTo(item.orderTypeId, item.enabled)">
				<view class="icon-wrapper" :style="{ backgroundColor: item.bgColor }">
					<text class="function-emoji">{{ item.emojiIcon }}</text>
				</view>
				<text class="function-text">{{ item.typeName }}</text>
			</view>
		</view>
		<!-- 进行中订单区 -->
		<view class="active-order-section">
			<view class="section-header">
				<view class="section-title">
					<text>进行中订单</text>
					<u-badge type="primary" :value="activeOrders.length" />
				</view>
				<view class="more-link" @click="goToUserOrders">
					<text>更多</text>
					<uni-icons type="arrowright" size="20" color="#999" />
				</view>
			</view>
			<view class="active-order-card" v-for="order in activeOrders" :key="order.oid"
				@click="viewOrderDetail(order.oid)">
				<view class="order-header">
					<text class="order-title">{{ order.type_name }}</text>
				</view>
				<text class="order-desc">{{ getOrderDetail(order.detail) }}</text>
				<view class="order-footer">
					<text class="order-time">期望送达时间：{{ order.expect_time }}</text>
					<text class="order-price">¥{{ order.amount }}</text>
				</view>
			</view>
			<u-empty mode="order" text="暂无进行中订单" v-if="activeOrders.length === 0" />
		</view>
	</view>
</template>
<script lang="ts" setup>
	import { ref } from 'vue';
	import request from '@/utils/request.js'
	import { useUserStore } from '@/stores/user.js'
	import { onShow } from '@dcloudio/uni-app'

	const userInfo = useUserStore().info

	// Uniapp 生命周期
	onShow(() => {
		fetchActiveOrders();
		fetchServiceList();
	})

	// 功能图标数据
	const functionList = ref([]);

	// 获取服务列表
	const fetchServiceList = async () => {
		const res = await request({
			url: '/order-type/list'
		});
		functionList.value = res.data
	};

	// 进行中订单数据
	const activeOrders = ref([]);

	// 获取进行中订单
	const fetchActiveOrders = async () => {
		request({
			url: '/order/progressing?type=jdr&uid=' + userInfo.uid,
		}).then((res) => {
			activeOrders.value = res.data
		})
	};

	const navigateTo = (orderTypeId : string, enabled : boolean) => {
		enabled ?
			uni.navigateTo({
				url: `/pages/index/order?orderTypeId=${orderTypeId}`
			}) :
			uni.showToast({
				title: '敬请期待',
				icon: 'error'
			});
	}
	const goToUserOrders = () => {
		uni.navigateTo({
			url: '/pages/user/orders/orders?tab=active'
		});
	};
	const viewOrderDetail = (oid : string) => {
		uni.navigateTo({
			url: `/pages/common/order-detail/order-detail?oid=${oid}`
		});
	};

	// 解析订单详情，优先显示code，否则显示remark
	const getOrderDetail = (detail) => {
		// 尝试解析detail为JSON对象
		const detailObj = JSON.parse(detail);
		// 如果包含code字段，则显示code
		if (detailObj.code) {
			return `取件码：${detailObj.code}`;
		}
		// 如果包含remark字段，则显示remark
		if (detailObj.remark) {
			return `订单备注：${detailObj.remark || '无'}`;
		}
		// 如果都没有，返回默认信息
		return '订单详情';
	};
</script>
<style>
	page {
		height: 100%;
	}

	.page-container {
		display: flex;
		flex-direction: column;
		height: 100%;
		background-color: #f5f5f5;
		padding-bottom: 120rpx;
	}

	/* Banner区样式 */
	.banner-container {
		width: 100%;
		height: 300rpx;
	}

	.banner-image {
		width: 100%;
		height: 100%;
		border-radius: 0 0 16rpx 16rpx;
	}

	/* 功能图标区样式 */
	.function-grid {
		display: flex;
		flex-wrap: wrap;
		justify-content: space-between;
		padding: 30rpx;
		background-color: #fff;
		border-radius: 16rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
		margin: -20rpx 20rpx 0;
		position: relative;
		z-index: 2;
	}

	.function-item {
		width: 20%;
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.icon-wrapper {
		width: 120rpx;
		height: 120rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 10rpx;
		border-radius: 16rpx;
	}

	.function-emoji {
		font-size: 56rpx;
		line-height: 1;
		color: #333;
	}

	.function-text {
		font-size: 24rpx;
		color: #333;
		text-align: center;
		width: 100%;
	}

	/* 进行中订单区样式 */
	.active-order-section {
		background-color: #fff;
		border-radius: 16rpx;
		padding: 30rpx;
		margin: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.section-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		display: inline-flex;
		align-items: center;
	}

	.active-order-card {
		background-color: #f9f9f9;
		border-radius: 12rpx;
		padding: 25rpx;
		position: relative;
		margin-bottom: 20rpx;
	}

	.active-order-card:last-child {
		margin-bottom: 0;
	}

	.order-header {
		display: flex;
		justify-content: space-between;
		margin-bottom: 15rpx;
	}

	.order-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
	}

	.order-desc {
		font-size: 26rpx;
		color: #666;
		margin-bottom: 20rpx;
		display: block;
	}

	.order-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.order-time {
		font-size: 24rpx;
		color: #999;
	}

	.order-price {
		font-size: 28rpx;
		font-weight: bold;
		color: #1a73e8;
	}

	.more-link {
		display: flex;
		align-items: center;
	}

	.more-link text {
		margin-right: 8rpx;
	}
</style>