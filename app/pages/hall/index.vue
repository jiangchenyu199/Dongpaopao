<template>
	<view class="order-page">
		<!-- 选项卡 -->
		<u-sticky>
			<u-tabs :list="serviceList" :scrollable="false" keyName="typeName" :current="currentTab"
				@change="changeTab">
				<template #right>
					<u-icon name="reload" size="21" bold @click="handleRefresh" />
				</template>
			</u-tabs>

		</u-sticky>

		<!-- 订单列表 -->
		<view class="order-list">
			<view class="order-item" v-for="order in orders" :key="order.oid" @click="navigateToDetail(order)">
				<view class="order-header">
					<text class="order-id">订单号：{{ order.oid }}</text>
					<text class="order-status" style="color: #FF9800;">待接单</text>
				</view>
				<view class="order-content">
					<!-- 左侧预留图片位置 -->
					<image v-if="order.img" :src="order.img" class="order-image" width="150rpx" height="150rpx" />

					<!-- 右侧内容 -->
					<view class="order-right-content">
						<text class="detail-item" style="font-weight: bolder;">{{ order.typeName }}</text>
						<text class="detail-item">{{ JSON.parse(order.detail).remark || '暂无备注' }}</text>
					</view>
				</view>
				<view class="order-footer">
					<text class="order-time">{{ formatTime(order.createTime) }}</text>
					<view class="order-actions">
						<u-icon name="arrow-right-double" />
					</view>
				</view>
			</view>

			<u-empty v-if="orders.length === 0" mode="order" text=" 暂无待接订单" />
		</view>

		<!-- 下拉刷新 -->
		<u-loading-page :loading="loading" bgColor="#f5f5f5" />
	</view>
</template>

<script lang="ts" setup>
	import { ref, computed } from 'vue';
	import { onLoad, onShow, onPullDownRefresh } from '@dcloudio/uni-app'
	import request from '@/utils/request.js'
	import { useUserStore } from '@/stores/user';

	const currentTab = ref(0);
	const loading = ref(false);
	const orders = ref([]);

	const userInfo = useUserStore().info

	const serviceList : Array = ref([])

	const loadServices = async () => {
		await request({
			url: '/order-type/list',
			method: 'GET'
		}).then((res) => {
			// 添加'全部'分类到列表开头
			serviceList.value = [
				{ orderTypeId: '', typeName: '全部', bgColor: '#1a73e8' }
			].concat(res.data);
		});
	}

	// 加载订单列表
	const loadOrders = async (orderTypeId: string) => {
		loading.value = true;
		try {
			// 构建请求URL，当orderTypeId为空时不添加该参数
			let url = '/order/hall?uid=' + userInfo.uid;
			if (orderTypeId) {
				url += "&orderTypeId=" + orderTypeId;
			}
			
			await request({
				url: url,
				method: 'GET'
			}).then((res) => {
				orders.value = res.data
			})
		} catch (error) {
			uni.showToast({
				title: '加载订单失败',
				icon: 'none'
			});
		} finally {
			loading.value = false;
			uni.stopPullDownRefresh();
		}
	};

	// 格式化时间
	const formatTime = (time) => {
		const date = new Date(time);
		return `${date.getMonth() + 1}月${date.getDate()}日 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
	};

	// 切换选项卡
	const changeTab = (item) => {
		currentTab.value = item.index;
		loadOrders(item.orderTypeId)
	};

	// 刷新数据
	const handleRefresh = () => {
		// 刷新时使用当前选中的分类
		const currentService = serviceList.value[currentTab.value];
		loadOrders(currentService.orderTypeId);
		uni.showToast({
			title: '刷新中...',
			icon: 'loading',
			duration: 1000
		});
	};

	// 跳转到订单详情页 - 传递订单类型和订单号
	const navigateToDetail = (order : any) => {
		const orderType = order.order_type || order.inferredType;
		uni.navigateTo({
			url: `/pages/common/order-detail/order-detail?oid=${order.oid}&orderType=${orderType}`
		});
	};

	/* 页面加载 */
	onLoad(async () => {
		loadServices();
	})

	/* 页面展示 */
	onShow(async (orderTypeId) => {
		loadOrders(orderTypeId);
	});
</script>

<style scoped>
	.order-page {
		display: flex;
		flex-direction: column;
		height: 100vh;
		background-color: #f5f5f5;
	}

	.order-list {
		flex: 1;
		overflow-y: auto;
		padding: 20rpx;
	}

	.order-item {
		background-color: #fff;
		border-radius: 16rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.order-header {
		display: flex;
		justify-content: space-between;
		margin-bottom: 20rpx;
	}

	.order-id {
		font-size: 26rpx;
		color: #999;
	}

	.order-status {
		font-size: 26rpx;
		font-weight: bold;
	}

	.order-content {
		display: flex;
		margin-bottom: 20rpx;
		align-items: center;
	}

	.order-image {
		width: 160rpx;
		height: 160rpx;
		border-radius: 12rpx;
		margin-right: 20rpx;
		flex-shrink: 0;
	}

	/* 右侧内容容器 */
	.order-right-content {
		flex: 1;
		display: flex;
		flex-direction: column;
	}

	/* 右侧上部分：标签区域 */
	.order-tag-section {
		margin-bottom: 12rpx;
	}

	/* 右侧下部分：详情行 */
	.order-details-row {
		display: flex;
		flex-wrap: wrap;
		align-items: center;
		gap: 16rpx;
	}

	/* 详情项样式 */
	.detail-item {
		font-size: 26rpx;
		color: #333;
		line-height: 2;
	}

	/* 位置信息样式 */
	.detail-item:first-child {
		color: #666;
	}

	/* 备注信息样式 */
	.detail-item:last-child {
		color: #999;
		/* 当备注内容较长时自动换行 */
		word-break: break-word;
		flex: 1;
	}

	.order-title {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 10rpx;
		display: -webkit-box;
		-webkit-line-clamp: 1;
		-webkit-box-orient: vertical;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.order-desc {
		font-size: 26rpx;
		color: #666;
		margin-bottom: 10rpx;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.order-price {
		font-size: 28rpx;
		font-weight: bold;
		color: #1a73e8;
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

	.order-actions {
		display: flex;
	}
</style>