<template>
	<view class="page-container">
		<!-- 用户信息区域 -->
		<view class="user-info" @click="navigateTo('/pages/user/profile/profile')">
			<view class="user-avatar">
				<!-- <image class="avatar-image" :src="userInfo.avatar" mode="aspectFill"/> -->
				<u-avatar size="60" class="avatar-image" :src="userInfo.avatar" mode="aspectFill"/>
			</view>
			<view class="user-detail">
				<text class="user-name">{{ userInfo.nickname }}</text>
				<text class="user-id">{{ userInfo.uid }}</text>
			</view>
			<uni-icons type="arrowright" size="20" color="#999"/>
		</view>

		<!-- 功能列表 -->
		<view class="function-list">
			<view class="function-item" v-for="item in functionList" :key="item.id" @click="navigateTo(item.path)">
				<uni-icons :type="item.icon" size="24" color="#666"/>
				<text class="function-name">{{ item.name }}</text>
				<uni-icons type="arrowright" size="20" color="#999"/>
			</view>
		</view>
	</view>
</template>
<script lang="ts" setup>
	import { reactive, ref } from 'vue';
	import { useUserStore } from '@/stores/user';
	import { onShow } from '@dcloudio/uni-app';

	import request from '../../utils/request';

	const userStore = useUserStore()

	let userInfo = ref({
		avatar: null,
		nickname: "未知用户",
		uid: "null"
	})

	onShow(() => {
		request({
			url: '/user/info',
			data: {
				uid: userStore.info.uid
			}
		}).then((res) => {
			userStore.info = userInfo.value = res.data
		})
	})

	// 功能列表
	const functionList = ref([
		{ id: 1, name: '我的订单', icon: 'list', path: '/pages/user/orders/my-orders' },
		{ id: 2, name: '我的钱包', icon: 'wallet', path: '/pages/user/wallet' },
		{ id: 3, name: '我的地址', icon: 'location', path: '/pages/user/address/address' },
		{ id: 4, name: '设置', icon: 'gear', path: '/pages/settings/index' }
	]);

	const goToProfile = () => {

	}

	const navigateTo = (path : string) => {
		uni.navigateTo({
			url: path
		});
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
		/* 给tabbar留出空间 */
	}

	/* 用户信息区域 */
	.user-info {
		display: flex;
		align-items: center;
		padding: 40rpx 30rpx;
		background-color: #fff;
		margin-bottom: 20rpx;
	}

	.user-avatar {
		width: 120rpx;
		height: 120rpx;
		margin-right: 30rpx;
		flex-shrink: 0;
	}

	.avatar-image {
		width: 100%;
		height: 100%;
		border-radius: 50%;
	}

	.user-detail {
		flex: 1;
		display: flex;
		flex-direction: column;
	}

	.user-name {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 10rpx;
	}

	.user-id {
		font-size: 24rpx;
		color: #999;
	}

	/* 功能列表 */
	.function-list {
		background-color: #fff;
		border-radius: 16rpx;
		overflow: hidden;
		margin: 0 20rpx;
	}

	.function-item {
		display: flex;
		align-items: center;
		padding: 30rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.function-item:last-child {
		border-bottom: none;
	}

	.function-name {
		flex: 1;
		font-size: 28rpx;
		color: #333;
		margin-left: 20rpx;
	}
</style>