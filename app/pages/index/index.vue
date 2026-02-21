<template>
	<u-notice-bar v-if="noticeText" :text="noticeText" speed="150" />
	<view class="page-container">
		<!-- Banner区 -->
		<view class="banner-section">
			<common-banner :images="bannerImages" />
		</view>

		<!-- 功能图标区 -->
		<view class="function-section">
			<view class="function-header">
				<text class="function-title">服务项目</text>
				<text class="function-more">查看全部</text>
			</view>
			<function-grid :items="functionList" @item-click="navigateTo" />
		</view>

		<!-- 商家推广区 -->
		<view class="business-section">
			<view class="function-header">
				<text class="function-title">商家推广</text>
				<text class="function-more" @click="handleViewAllBusinesses">查看全部</text>
			</view>
			<business-promotion :businesses="businessList" @item-click="handleBusinessClick" />
		</view>
	</view>
</template>

<script lang="ts" setup>
	import { ref } from 'vue';
	import request from '@/utils/request.js'
	import { useUserStore } from '@/stores/user.js'
	import { onShow } from '@dcloudio/uni-app'
	import CommonBanner from '@/components/common/banner.vue'
	import FunctionGrid from '@/components/common/function-grid.vue'
	import BusinessPromotion from '@/components/common/business-promotion.vue'

	const userInfo = useUserStore().info

	// 首页顶部滚动通知
	const noticeText = ref('')
	request({ url: '/app/notices' }).then((res: any) => {
		const list = res?.data || []
		if (list.length > 0) {
			noticeText.value = list.map((n: any) => n.content).join('    ')
		}
	}).catch(() => {})

	// Banner 图片数据
	const bannerImages = ref([
		'https://ai-public.mastergo.com/ai/img_res/6945a6b7e8506df1abcd0efce30213d7.jpg',
		'https://ai-public.mastergo.com/ai/img_res/ea7587e8643368749c2e819938953839.jpg',
		'https://ai-public.mastergo.com/ai/img_res/120fce42fd9e45edcd603cc750ec7054.jpg'
	]);

	// 商家推广数据
	const businessList = ref([
		{
			id: '1',
			name: '快捷搬家服务',
			description: '全市范围内专业搬家，2小时内响应，价格透明',
			image: 'https://img.xjh.me/random_img.php?type=bg&ctype=nature&return=302',
			tags: ['专业', '快捷', '实惠'],
			minPrice: '¥100起'
		},
		{
			id: '2',
			name: '洁净家政保洁',
			description: '专业保洁团队，使用环保清洁剂，让家焕然一新',
			image: 'https://img.xjh.me/random_img.php?type=bg&ctype=nature&return=302',
			tags: ['专业', '环保', '细致'],
			minPrice: '¥80起'
		},
		{
			id: '3',
			name: '家电维修专家',
			description: '专业维修各类家电，30年经验技师，上门服务',
			image: 'https://img.xjh.me/random_img.php?type=bg&ctype=nature&return=302',
			tags: ['专业', '快速', '保修'],
			minPrice: '¥50起'
		}
	]);

	// Uniapp 生命周期
	onShow(() => {
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

	const navigateTo = (item : any) => {
		item.enabled ?
			uni.navigateTo({
				url: `/pages/index/order?orderTypeId=${item.orderTypeId}`
			}) :
			uni.showToast({
				title: '敬请期待',
				icon: 'error'
			});
	}

	// 商家推广点击处理
	const handleBusinessClick = (business : any) => {
		uni.showToast({
			title: `查看商家: ${business.name}`,
			icon: 'none'
		});
		// 这里可以添加跳转到商家详情页的逻辑
	};

	// 查看全部商家
	const handleViewAllBusinesses = () => {
		uni.showToast({
			title: '查看全部商家',
			icon: 'none'
		});
		// 这里可以添加跳转到商家列表页的逻辑
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

	.banner-section {
		width: 100%;
	}

	.function-section {
		padding: 0 20rpx 20rpx;
		margin-top: 20rpx;
	}
	
	.function-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 0 20rpx;
		margin-bottom: 16rpx;
	}
	
	.function-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}
	
	.function-more {
		font-size: 24rpx;
		color: #999;
	}

	.business-section {
		padding: 0 20rpx 20rpx;
		margin-top: 20rpx;
	}
</style>
