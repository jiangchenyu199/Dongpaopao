<template>
	<view class="order-page">
		<!-- 顶部选项卡 -->
		<view class="tab-header">
			<u-tabs 
				:list="tabList" 
				:current="currentTab" 
				@change="changeTab"
				bar-active-color="#1a73e8"
				active-color="#1a73e8"
				inactive-color="#666"
			>
				<template #right>
					<view class="tab-actions">
						<u-icon 
							name="list" 
							size="21" 
							bold 
							@click="openFilterPopup"
							class="filter-icon"
						/>
					</view>
				</template>
			</u-tabs>
		</view>

		<!-- 订单列表 -->
		<view class="order-list">
			<view 
				v-for="order in filteredOrders" 
				:key="order.oid" 
				class="order-card"
				@click="navigateToDetail(order)"
			>
				<!-- 订单头部 -->
				<view class="order-header">
					<view class="order-header-left">
						<text class="order-id">订单号: {{ order.oid }}</text>
						<view 
							class="order-role-tag"
							:class="order.role === 'xdr' ? 'sender-tag' : 'receiver-tag'"
						>
							{{ order.role === 'xdr' ? '我下单的' : '我接单的' }}
						</view>
					</view>
					<view 
						class="order-status"
						:style="{ color: order.statusColor || '#666' }"
					>
						{{ order.statusName || '' }}
					</view>
				</view>

				<!-- 订单内容 -->
				<view class="order-content">
					<view class="order-info">
						<text class="order-title">{{ getOrderTypeName(order) }}</text>
						<text class="order-desc">{{ getOrderDesc(order) }}</text>
						<text class="order-price">¥{{ order.amount }}</text>
					</view>
				</view>

				<!-- 订单底部 -->
				<view class="order-footer">
					<text class="order-time">
						<text v-if="order.status === 'S'">完成时间：{{ formatTimeDisplay(order.completeTime) }}</text>
						<text v-else-if="order.status === 'C'">已取消</text>
						<text v-else>期望送达时间：{{ formatTimeDisplay(order.expectTime) }}</text>
					</text>
					<view class="order-actions">
						<view 
							v-if="order.status === 'D' && order.role === 'jsr'"
							class="action-btn primary-btn"
							@click.stop="handleAcceptOrder(order)"
						>
							立即接单
						</view>
						<view v-else class="order-arrow">
							<uni-icons type="arrow-right" size="20" color="#999" />
						</view>
					</view>
				</view>
			</view>

			<!-- 空状态 -->
			<view class="empty-state" v-if="filteredOrders.length === 0 && !loading">
				<view class="empty-icon">
					<u-icon name="order" size="120" color="#e0e0e0"></u-icon>
				</view>
				<text class="empty-text">暂无订单</text>
				<text class="empty-subtext">去发布或接取一个订单吧</text>
				<view class="empty-actions">
					<view class="empty-btn primary-btn" @click="navigateToPublish">
						发布订单
					</view>
					<view class="empty-btn secondary-btn" @click="navigateToHall">
						订单大厅
					</view>
				</view>
			</view>
		</view>

		<!-- 筛选弹窗 -->
		<u-popup :show="showFilter" mode="bottom" @close="showFilter = false">
			<view class="filter-popup">
				<view class="filter-header">
					<text class="filter-title">高级筛选</text>
					<u-icon name="close" size="20" @click="showFilter = false" />
				</view>

				<view class="filter-section">
					<text class="filter-label">订单状态</text>
					<view class="filter-options">
						<view class="filter-option" :class="{ active: tempFilterStatus === '' }"
							@click="tempFilterStatus = ''">
							全部状态
						</view>
						<view class="filter-option" :class="{ active: tempFilterStatus === 'D' }"
							@click="tempFilterStatus = 'D'">
							待接单
						</view>
						<view class="filter-option" :class="{ active: tempFilterStatus === 'J' }"
							@click="tempFilterStatus = 'J'">
							进行中
						</view>
						<view class="filter-option" :class="{ active: tempFilterStatus === 'S' }"
							@click="tempFilterStatus = 'S'">
							已完成
						</view>
						<view class="filter-option" :class="{ active: tempFilterStatus === 'C' }"
							@click="tempFilterStatus = 'C'">
							已取消
						</view>
					</view>
				</view>

				<view class="filter-section">
					<text class="filter-label">订单类型</text>
					<view class="filter-options">
						<view class="filter-option" :class="{ active: tempFilterType === '' }"
							@click="tempFilterType = ''">
							全部类型
						</view>
						<view class="filter-option" :class="{ active: tempFilterType === 'E' }"
							@click="tempFilterType = 'E'">
							快递代取
						</view>
						<view class="filter-option" :class="{ active: tempFilterType === 'T' }"
							@click="tempFilterType = 'T'">
							外卖代取
						</view>
						<view class="filter-option" :class="{ active: tempFilterType === 'C' }"
							@click="tempFilterType = 'C'">
							物品搬运
						</view>
						<view class="filter-option" :class="{ active: tempFilterType === 'P' }"
							@click="tempFilterType = 'P'">
							商品代购
						</view>
					</view>
				</view>

				<view class="filter-section">
					<text class="filter-label">订单角色</text>
					<view class="filter-options">
						<view class="filter-option" :class="{ active: tempFilterRole === '' }"
							@click="tempFilterRole = ''">
							全部角色
						</view>
						<view class="filter-option" :class="{ active: tempFilterRole === 'xdr' }"
							@click="tempFilterRole = 'xdr'">
							我下单的
						</view>
						<view class="filter-option" :class="{ active: tempFilterRole === 'jsr' }"
							@click="tempFilterRole = 'jsr'">
							我接单的
						</view>
					</view>
				</view>

				<view class="filter-actions">
					<view class="filter-btn reset" @click="handleResetFilter">
						重置
					</view>
					<view class="filter-btn confirm" @click="handleConfirmFilter">
						确认筛选
					</view>
				</view>
			</view>
		</u-popup>

		<!-- 加载状态 -->
		<u-loading-page :loading="loading" bgColor="#f5f5f5"></u-loading-page>
	</view>
</template>

<script lang="ts" setup>
	import { ref, computed } from 'vue';
	import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'
	import request from '@/utils/request.js'
	import { useUserStore } from '@/stores/user.js'
	import { formatTimeDisplay } from '@/utils/date.js'

	const currentTab = ref(0);
	const loading = ref(false);
	const orders = ref([]);
	const showFilter = ref(false);
	const userInfo = useUserStore().info

	// 生效的筛选条件
	const filterStatus = ref('');
	const filterType = ref('');
	const filterRole = ref('');

	// 临时的筛选条件
	const tempFilterStatus = ref('');
	const tempFilterType = ref('');
	const tempFilterRole = ref('');

	// 选项卡配置
	const tabList = ref([
		{ name: '全部订单' },
		{ name: '待接单' },
		{ name: '进行中' },
		{ name: '已完成' },
		{ name: '已取消' }
	]);

	// 订单类型映射
	const orderTypeMap = {
		'E': { text: '快递代取', image: 'https://ai-public.mastergo.com/ai/img_res/53451dce5b9ad1abd6747fc75f050aa1.jpg' },
		'T': { text: '外卖代取', image: 'https://ai-public.mastergo.com/ai/img_res/7c254a7c6488aeb3d613034db4854725.jpg' },
		'C': { text: '物品搬运', image: 'https://ai-public.mastergo.com/ai/img_res/2a5e323ee652e3771b05ef5e72d4155c.jpg' },
		'P': { text: '商品代购', image: 'https://ai-public.mastergo.com/ai/img_res/dd794bd7156efaa64de362c009f59890.jpg' }
	};

	// 解析订单详情
	const parseOrderDetail = (detailStr) => {
		if (!detailStr) return {};

		try {
			// 如果已经是对象，直接返回
			if (typeof detailStr === 'object') return detailStr;

			// 处理格式错误的JSON字符串
			let fixedStr = detailStr;
			// 修复键名没有引号的问题
			fixedStr = fixedStr.replace(/(\w+)\s*:/g, '"$1":');
			// 修复值没有引号的问题
			fixedStr = fixedStr.replace(/:(\s*)([^"{}\[\],\s][^,}]*)(\s*[},])/g, ':"$2"$3');

			return JSON.parse(fixedStr);
		} catch (e) {
			// 尝试更简单的手动解析
			try {
				const result = {};
				// 移除大括号和空格
				const cleanStr = detailStr.replace(/[{}"]/g, '').replace(/\s/g, '');
				const pairs = cleanStr.split(',');

				for (const pair of pairs) {
					const [key, value] = pair.split(':');
					if (key && value) {
						result[key] = value;
					}
				}
				return result;
			} catch (e2) {
				return {};
			}
		}
	};

	// 判断订单角色
	const getOrderRole = (order) => {
		if (order.xdr === userInfo.uid) {
			return 'xdr';
		} else if (order.jdr === userInfo.uid) {
			return 'jsr';
		}
		return 'xdr'; // 默认
	};

	// 加载订单列表
	const loadOrders = async () => {
		loading.value = true;
		try {
			const res = await request({
				url: "/order/list",
				data: {
					uid: userInfo.uid,
					role: filterRole.value,
					status: filterStatus.value,
					type: filterType.value
				}
			});

			if (res.data && Array.isArray(res.data)) {
				// 处理订单数据，添加角色信息和解析详情
				orders.value = res.data.map(order => {
					const role = getOrderRole(order);
					const detailObj = parseOrderDetail(order.detail);

					return {
						...order,
						role,
						detailObj
					};
				});
			} else {
				orders.value = [];
			}

			loading.value = false;
		} catch (error) {
			loading.value = false;
			orders.value = [];
		}
	};

	// 订单类型名称
	const getOrderTypeName = (order) => {
		if (order.typeName) return order.typeName;
		const code = order.orderTypeId || order.orderType;
		return orderTypeMap[code]?.text || '其他服务';
	};

	// 订单描述
	const getOrderDesc = (order) => {
		const detailObj = order.detailObj || {};
		const remark = detailObj.remark;
		if (remark && remark !== '""' && remark !== '') {
			return remark;
		}
		return '无备注信息';
	};

	// 处理接单
	const handleAcceptOrder = (order) => {
		uni.showModal({
			title: '确认接单',
			content: `确定要接取订单「${getOrderTypeName(order)}」吗？`,
			success: async (res) => {
				if (res.confirm) {
					try {
						const response = await request({
							url: "/order/accept",
							method: "POST",
							data: {
								oid: order.oid,
								jdr: userInfo.uid
							}
						});
						if (response.code === 200) {
							uni.showToast({
								title: '接单成功',
								icon: 'success'
							});
							loadOrders();
						} else {
							uni.showToast({
								title: '接单失败',
								icon: 'error'
							});
						}
					} catch (error) {
						uni.showToast({
							title: '网络错误',
							icon: 'error'
						});
					}
				}
			}
		});
	};

	// 筛选订单
	const filteredOrders = computed(() => {
		let filtered = [...orders.value];

		// 按顶部选项卡筛选
		if (currentTab.value !== 0 && !filterStatus.value) {
			const tabStatusMap = {
				1: 'D', // 待接单
				2: 'J', // 进行中
				3: 'S', // 已完成
				4: 'C'  // 已取消
			};
			const targetStatus = tabStatusMap[currentTab.value];
			filtered = filtered.filter(order => order.status === targetStatus);
		}

		// 按生效的高级筛选条件筛选
		if (filterStatus.value) {
			filtered = filtered.filter(order => order.status === filterStatus.value);
		}

		if (filterType.value) {
			filtered = filtered.filter(order => order.orderType === filterType.value);
		}

		if (filterRole.value) {
			filtered = filtered.filter(order => order.role === filterRole.value);
		}

		return filtered;
	});

	// 切换选项卡
	const changeTab = (item) => {
		currentTab.value = item.index;
		// 切换到选项卡时清空高级筛选的状态条件
		filterStatus.value = '';
	};

	// 打开筛选弹窗
	const openFilterPopup = () => {
		tempFilterStatus.value = filterStatus.value;
		tempFilterType.value = filterType.value;
		tempFilterRole.value = filterRole.value;
		showFilter.value = true;
	};

	// 重置筛选
	const handleResetFilter = () => {
		tempFilterStatus.value = '';
		tempFilterType.value = '';
		tempFilterRole.value = '';
	};

	// 确认筛选
	const handleConfirmFilter = () => {
		filterStatus.value = tempFilterStatus.value;
		filterType.value = tempFilterType.value;
		filterRole.value = tempFilterRole.value;
		showFilter.value = false;
		// 应用高级筛选时，重置顶部选项卡到"全部订单"
		currentTab.value = 0;
		loadOrders();
	};

	// 跳转到订单详情页
	const navigateToDetail = (order) => {
		uni.navigateTo({
			url: `/pages/user/orders/order-detail?oid=${order.oid}&orderTypeId=${order.orderTypeId || order.orderType || ''}`
		});
	};

	// 跳转到发布订单页
	const navigateToPublish = () => {
		uni.navigateTo({
			url: '/pages/user/publish/publish'
		});
	};

	// 跳转到订单大厅
	const navigateToHall = () => {
		uni.navigateTo({
			url: '/pages/order/hall/hall'
		});
	};

	// 每次显示页面时拉取列表（首次进入、从详情/大厅返回、从后台切回）
	onShow(() => {
		loadOrders();
	});

	// 下拉刷新
	onPullDownRefresh(() => {
		loadOrders();
		setTimeout(() => {
			uni.stopPullDownRefresh();
		}, 1000);
	});
</script>

<style scoped>
	.order-page {
		display: flex;
		flex-direction: column;
		height: 100vh;
		background-color: #f5f5f5;
	}

	.tab-header {
		flex-shrink: 0;
		background-color: #fff;
	}

	.order-list {
		flex: 1;
		height: 100%;
	}

	.order-card {
		background-color: #fff;
		border-radius: 16rpx;
		padding: 30rpx;
		margin: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.order-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
		padding-bottom: 20rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.order-header-left {
		display: flex;
		align-items: center;
	}

	.order-id {
		font-size: 26rpx;
		color: #999;
	}

	.order-role-tag {
		margin-left: 16rpx;
		padding: 4rpx 12rpx;
		border-radius: 20rpx;
		font-size: 20rpx;
		font-weight: bold;
	}

	.sender-tag {
		background-color: #e3f2fd;
		color: #1976d2;
	}

	.receiver-tag {
		background-color: #e8f5e9;
		color: #388e3c;
	}

	.order-status {
		font-size: 26rpx;
		font-weight: bold;
	}

	.order-content {
		margin-bottom: 20rpx;
	}

	.order-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
	}

	.order-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 10rpx;
	}

	.order-desc {
		font-size: 24rpx;
		color: #666;
		margin-bottom: 10rpx;
	}

	.order-price {
		font-size: 28rpx;
		font-weight: bold;
		color: #ff4d4f;
	}

	.order-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding-top: 20rpx;
		border-top: 1rpx solid #f0f0f0;
	}

	.order-time {
		font-size: 24rpx;
		color: #999;
		flex: 1;
	}

	.order-actions {
		display: flex;
		align-items: center;
		margin-left: 20rpx;
	}

	.order-arrow {
		display: inline-block;
	}

	.action-btn {
		padding: 12rpx 24rpx;
		border-radius: 8rpx;
		font-size: 24rpx;
		font-weight: 500;
		margin-left: 12rpx;
	}

	.primary-btn {
		background-color: #1a73e8;
		color: #fff;
	}

	.secondary-btn {
		background-color: #f5f5f5;
		color: #666;
		border: 2rpx solid #e0e0e0;
	}

	/* 空状态样式 */
	.empty-state {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		height: 100%;
		padding: 120rpx 40rpx;
		text-align: center;
	}

	.empty-icon {
		margin-bottom: 40rpx;
	}

	.empty-text {
		font-size: 32rpx;
		color: #999;
		margin-bottom: 16rpx;
	}

	.empty-subtext {
		font-size: 26rpx;
		color: #ccc;
		margin-bottom: 40rpx;
	}

	.empty-actions {
		display: flex;
		gap: 20rpx;
	}

	.empty-btn {
		padding: 20rpx 40rpx;
		border-radius: 8rpx;
		font-size: 26rpx;
		font-weight: 500;
	}

	/* 筛选弹窗样式 */
	.filter-popup {
		padding: 40rpx;
		background: #fff;
		border-radius: 24rpx 24rpx 0 0;
	}

	.filter-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 40rpx;
	}

	.filter-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.filter-section {
		margin-bottom: 40rpx;
	}

	.filter-label {
		display: block;
		font-size: 28rpx;
		color: #333;
		margin-bottom: 20rpx;
		font-weight: 500;
	}

	.filter-options {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
	}

	.filter-option {
		padding: 16rpx 24rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 30rpx;
		font-size: 26rpx;
		color: #666;
		background: #f8f8f8;
		transition: all 0.3s;
	}

	.filter-option.active {
		background: #1a73e8;
		color: #fff;
		border-color: #1a73e8;
	}

	.filter-actions {
		display: flex;
		gap: 20rpx;
		margin-top: 40rpx;
	}

	.filter-btn {
		flex: 1;
		padding: 24rpx;
		border-radius: 12rpx;
		font-size: 28rpx;
		font-weight: 500;
		text-align: center;
	}

	.filter-btn.reset {
		background: #f5f5f5;
		color: #666;
		border: 2rpx solid #e0e0e0;
	}

	.filter-btn.confirm {
		background: #1a73e8;
		color: #fff;
	}
</style>