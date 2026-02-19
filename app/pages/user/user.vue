<template>
	<view class="page-container">
		<!-- 自定义导航栏 -->
		<view class="navbar-wrap" :style="{ paddingTop: statusBarHeight + 'px' }">
			<u-navbar title="我的" :border="false" bgColor="#ffffff" titleColor="#1a1a1a" :placeholder="false">
			</u-navbar>
		</view>

		<view class="page-content" :style="{ paddingTop: (statusBarHeight + navbarHeight) + 'px' }">
			<!-- 用户信息区域：居中卡片风格 -->
			<user-info :user="userInfo" @click="() => uni.navigateTo({ url: '/pages/user/profile/profile' })" />

			<!-- 功能列表 -->
			<view class="function-list">
				<function-item v-for="item in functionList" :key="item.id" :item="item" @click="navigateTo(item.path)" />
			</view>

			<!-- 底部协议 -->
			<view class="page-footer">
				<text class="page-footer-link" @click="openAgreement('user')">《用户协议》</text>
				<text class="page-footer-sep"> </text>
				<text class="page-footer-link" @click="openAgreement('terms')">《服务条款》</text>
			</view>
		</view>
	</view>
</template>
<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue';
import { useUserStore } from '@/stores/user';
import { onShow } from '@dcloudio/uni-app';
import request from '../../utils/request';
import UserInfo from '@/components/common/user-info.vue';
import FunctionItem from '@/components/common/function-item.vue';

const userStore = useUserStore()

const statusBarHeight = ref(0)
const navbarHeight = ref(44)

onMounted(() => {
	const systemInfo = uni.getWindowInfo?.() || uni.getSystemInfoSync?.()
	statusBarHeight.value = systemInfo.statusBarHeight ?? 0
})

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
	{ id: 4, name: '意见反馈', icon: 'chat', path: '/pages/user/feedback/feedback' },
	{ id: 99, name: '商务合作', icon: 'contact', path: '/pages/user/cooperation/cooperation' },
]);

const navigateTo = (path: string) => {
	uni.navigateTo({
		url: path
	});
};

const openAgreement = (_type: string) => {
	uni.showToast({ title: '敬请期待', icon: 'none' });
	// 可后续改为跳转协议页：uni.navigateTo({ url: '/pages/common/agreement?type=' + _type });
};
</script>
<style scoped>
page {
	height: 100%;
}

.page-container {
	background-color: #f5f5f5;
	min-height: 100vh;
}

.navbar-wrap {
	position: fixed;
	left: 0;
	right: 0;
	top: 0;
	z-index: 980;
}

.page-content {
	display: flex;
	flex-direction: column;
	min-height: 100vh;
	padding: 0;
	box-sizing: border-box;
}

.page-footer {
	margin-top: auto;
	padding: 24rpx 32rpx 50px;
	text-align: center;
	background-color: #f5f5f5;
}

.page-footer-link {
	font-size: 24rpx;
	color: #999;
}

.page-footer-link:active {
	opacity: 0.8;
}

.page-footer-sep {
	font-size: 24rpx;
	color: #999;
}

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