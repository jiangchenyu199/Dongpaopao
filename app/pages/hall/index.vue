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
		<view class="order-list" @scrolltolower="handleLoadMore">
			<order-item v-for="order in formattedOrders" :key="order.oid" :order="order"
				@click="navigateToDetail(order)" />

			<u-empty v-if="orders.length === 0" mode="order" text=" 暂无待接订单" />
			
			<!-- 加载更多 -->
			<u-loadmore v-if="orders.length > 0" :status="loadMoreStatus" :loadingText="'加载中...'" :noMoreText="'没有更多了'" />
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
	import OrderItem from '@/components/common/order-item.vue';

	const currentTab = ref(0);
	const loading = ref(false);
	const orders = ref([]);
	const pageNum = ref(1);
	const pageSize = ref(10);
	const hasMore = ref(true);
	const loadMoreStatus = ref('loadmore');

	const userInfo = useUserStore().info

	// 类型名称到类型代码的映射
	const typeNameToOrderType = {
		'快递代取': 'E',
		'外卖代取': 'T',
		'物品搬运': 'C',
		'商品代购': 'P'
	};

	// 格式化订单数据以匹配 order-item 组件的期望格式
	const formattedOrders = computed(() => {
		return orders.value.map(order => {
			// 解析 detail 字段
			let detailObj = {};
			try {
				detailObj = JSON.parse(order.detail);
			} catch (e) {
				detailObj = {};
			}

			return {
				...order,
				status: 'D', // 待接单状态
				role: 'receiver', // 我接单角色
				typeName: order.typeName, // 订单类型名称
				orderType: typeNameToOrderType[order.typeName] || '', // 订单类型代码
				detailObj: detailObj, // 解析后的详情对象
				amount: order.amount || 0, // 订单金额
				expectTime: order.expectTime || order.createTime, // 期望送达时间
				createTime: order.createTime // 创建时间
			};
		});
	});

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
	const loadOrders = async (orderTypeId : string, isLoadMore = false) => {
		if (!isLoadMore) {
			loading.value = true;
			pageNum.value = 1;
			hasMore.value = true;
		} else if (!hasMore.value) {
			return;
		}

		try {
			// 构建请求URL，当orderTypeId为空时不添加该参数
			let url = `/order/hall?uid=${userInfo.uid}&pageNum=${pageNum.value}&pageSize=${pageSize.value}`;
			if (orderTypeId) {
				url += `&orderTypeId=${orderTypeId}`;
			}

			await request({
				url: url,
				method: 'GET'
			}).then((res) => {
				const newOrders = res.data.records || [];
				
				if (!isLoadMore) {
					orders.value = newOrders;
				} else {
					orders.value = orders.value.concat(newOrders);
				}

				// 判断是否还有更多数据
				hasMore.value = newOrders.length === pageSize.value;
				
				// 更新加载状态
				if (isLoadMore) {
					loadMoreStatus.value = hasMore.value ? 'loadmore' : 'nomore';
				}
			})
		} catch (error) {
			uni.showToast({
				title: '加载订单失败',
				icon: 'none'
			});
			
			if (isLoadMore) {
				loadMoreStatus.value = 'loadmore';
			}
		} finally {
			loading.value = false;
			uni.stopPullDownRefresh();
		}
	};

	// 处理加载更多
	const handleLoadMore = () => {
		if (loading.value || !hasMore.value) return;
		
		loadMoreStatus.value = 'loading';
		pageNum.value++;
		
		const currentService = serviceList.value[currentTab.value];
		loadOrders(currentService.orderTypeId, true);
	};

	// 切换选项卡
	const changeTab = (item) => {
		currentTab.value = item.index;
		loadOrders(item.orderTypeId);
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
		const orderType = order.order_type || order.inferredType || order.orderType;
		uni.navigateTo({
			url: `/pages/common/order-detail/order-detail?oid=${order.oid}&orderType=${orderType}`
		});
	};

	/* 页面加载 */
	onLoad(async () => {
		loadServices();
	})

	/* 页面展示 */
	onShow(async () => {
		// 确保服务列表已加载，然后加载对应分类的订单
		if (serviceList.value.length === 0) {
			await loadServices();
		}
		const currentService = serviceList.value[currentTab.value];
		loadOrders(currentService.orderTypeId);
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
</style>