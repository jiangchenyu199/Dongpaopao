<template>
	<view class="order-detail-page">
		<!-- 自定义导航栏：状态 + 服务 + 订单号 -->
		<view class="detail-navbar-wrap" :style="{ borderBottomColor: statusBorderColor }">
			<u-navbar
				:title="''"
				leftIcon="arrow-left"
				leftText="返回"
				:autoBack="true"
				:fixed="true"
				:placeholder="true"
				:border="false"
				:height="navbarContentHeight"
				bgColor="#fff"
			>
				<template #center>
					<view class="navbar-center">
						<view class="navbar-row">
							<u-tag :text="orderDetail.status_name || '未知状态'" :type="statusTagType" size="mini" />
							<text class="navbar-service">{{ orderDetail.service_name || '未知服务' }}</text>
						</view>
						<text class="navbar-oid" v-if="orderDetail.oid">订单号：{{ orderDetail.oid }}</text>
					</view>
				</template>
			</u-navbar>
		</view>

		<view class="detail-content">
			<!-- 订单基本信息 -->
			<view class="info-card">
				<u-title>订单信息</u-title>
				<view class="info-item">
					<text class="label">下单时间</text>
					<text class="value">{{ formatTimeDisplay(orderDetail.create_time) }}</text>
				</view>
				<view class="info-item">
					<text class="label">订单金额</text>
					<text class="value price">¥{{ orderDetail.amount }}</text>
				</view>
				<view class="info-item" v-if="orderDetail.accept_time">
					<text class="label">接单时间</text>
					<text class="value">{{ formatTimeDisplay(orderDetail.accept_time) }}</text>
				</view>
				<view class="info-item" v-if="orderDetail.complete_time">
					<text class="label">完成时间</text>
					<text class="value">{{ formatTimeDisplay(orderDetail.complete_time) }}</text>
				</view>
				<view class="info-item" v-if="orderDetail.expect_time">
					<text class="label">期望送达时间</text>
					<text class="value">{{ formatTimeDisplay(orderDetail.expect_time) }}</text>
				</view>
				<view class="info-item" v-if="orderDetail.service_name">
					<text class="label">服务类型</text>
					<text class="value">{{ orderDetail.service_name }}</text>
				</view>
			</view>

			<!-- 动态表单业务详情信息 -->
			<view class="info-card" v-if="formList.length > 0 && Object.keys(businessDetail).length > 0">
				<u-title>{{ orderDetail.service_name || '服务' }}详情</u-title>
				<!-- 动态渲染表单数据 -->
				<view class="info-item" v-for="(item, index) in formList" :key="index">
					<text class="label">{{ item.label }}</text>
					<!-- 处理私密字段：只有满足权限才显示，否则显示*** -->
					<text class="value" v-if="!item.private || showSensitiveInfo">
						{{ businessDetail[item.name] || '无' }}
					</text>
					<text class="value" v-else>***</text>
				</view>
			</view>

			<!-- 配送信息 -->
			<view class="info-card" v-if="addressInfo">
				<u-title>配送信息</u-title>
				<view class="info-item">
					<text class="label">收件人</text>
					<text class="value">{{ addressInfo.sjr }}</text>
				</view>
				<view class="info-item">
					<text class="label">联系电话</text>
					<text class="value">{{ addressInfo.phone }}</text>
				</view>
				<view class="info-item">
					<text class="label">配送地址</text>
					<text class="value">{{ addressInfo.detail }}</text>
				</view>
			</view>

			<!-- 订单评价：仅已完成且当前用户为下单人时展示 -->
			<view class="info-card" v-if="showRatingSection">
				<u-title>订单评价</u-title>
				<view class="rating-row">
					<text class="label">服务评分</text>
					<view class="rating-wrap">
						<d-rate
							v-if="isRated"
							:modelValue="displayRating"
							:count="5"
							:disabled="true"
							activeColor="#ff9900"
							inactiveColor="#e5e7eb"
							:size="20"
						/>
						<template v-else>
							<d-rate
								v-model="rateValue"
								:count="5"
								activeColor="#ff9900"
								inactiveColor="#e5e7eb"
								:size="20"
							/>
							<u-button
								class="rating-submit-btn"
								type="primary"
								size="small"
								:loading="rateLoading"
								:disabled="rateValue <= 0"
								@click="submitRating"
							>
								提交评价
							</u-button>
						</template>
					</view>
				</view>
			</view>

			<!-- 操作按钮 -->
			<view class="action-buttons" v-if="showActionButtons">
				<u-button v-if="orderDetail.status === 'J'" type="success" shape="circle"
					@click="goToPrivateChat(conversationId)">私聊</u-button>
				<u-button v-if="orderDetail.status === 'D' && orderDetail.xdr != userInfo.uid" type="primary"
					shape="circle" :loading="acceptLoading" @click="acceptOrder">接单</u-button>
				<u-button v-if="orderDetail.status === 'J'" type="warning" shape="circle" :loading="completeLoading"
					@click="completeOrder">完成订单</u-button>
				<u-button v-if="orderDetail.status === 'D' && orderDetail.xdr == userInfo.uid" type="error"
					shape="circle" plain :loading="cancelLoading" @click="cancelOrder">取消订单</u-button>
			</view>
		</view>

		<!-- 加载状态 -->
		<u-loading-page :loading="loading" bgColor="#f5f5f5" />
	</view>
</template>

<script lang="ts" setup>
	import { ref, computed } from 'vue';
	import { onLoad } from '@dcloudio/uni-app'
	import request from '@/utils/request.js'
	import { useUserStore } from '@/stores/user';
	import { formatTimeDisplay } from '@/utils/date.js';
	import DRate from '@/components/common/d-rate.vue';

	const loading = ref(false);
	const acceptLoading = ref(false);
	const completeLoading = ref(false);
	const cancelLoading = ref(false);
	const rateLoading = ref(false);
	const rateValue = ref(0);

	const userInfo = useUserStore().info

	// 订单详情（适配后端新增的status_name、jdr、accept_time等字段）
	const orderDetail = ref({
		oid: '',
		xdr: '',
		create_time: '',
		expect_time: null,
		complete_time: null,
		accept_time: null, // 接单时间
		service_id: '',
		aid: '',
		detail: '',
		amount: 0,
		status: 'D', // 状态标识（如D/J/S/C）
		status_name: '', // 后端返回的状态名称（如待接单、进行中）
		jdr: '', // 接单者ID
		form: '', // 表单结构JSON字符串
		service_name: '', // 服务名称（如快递代取、外卖代取）
		description: '',
		rating: null // 评分 0~5，仅已完成订单可能有值
	});
	const businessDetail = ref({}); // 解析后的表单数据
	const formList = ref([]); // 解析后的表单结构数组
	const addressInfo = ref(null);
	const conversationId = ref(null) // 接收后端返回的会话ID

	// 计算属性：是否显示操作按钮
	const showActionButtons = computed(() => {
		return ['D', 'J'].includes(orderDetail.value.status);
	});

	// 是否显示敏感信息（私密字段，如取餐码、取件码）
	const showSensitiveInfo = computed(() => {
		return ['J', 'S'].includes(orderDetail.value.status) || orderDetail.value.xdr === userInfo.uid;
	});

	// 状态标签类型：D 待接单 warning，J 进行中 primary，S 已完成 success，C 已取消 error
	const statusTagType = computed(() => {
		const map = { D: 'warning', J: 'primary', S: 'success', C: 'error' };
		return map[orderDetail.value.status] || 'info';
	});

	// 导航栏底部线条颜色（随状态）
	const statusBorderColor = computed(() => {
		const map = { D: '#f59e0b', J: '#3b82f6', S: '#22c55e', C: '#ef4444' };
		return map[orderDetail.value.status] || '#94a3b8';
	});

	// 仅已完成且当前用户为下单人时显示评价区域
	const showRatingSection = computed(() => {
		return orderDetail.value.status === 'S' && orderDetail.value.xdr === userInfo.uid;
	});
	const isRated = computed(() => {
		const r = orderDetail.value.rating;
		return r != null && Number(r) > 0;
	});
	const displayRating = computed(() => Number(orderDetail.value.rating) || 0);

	const navbarContentHeight = 56;

	onLoad((options) => {
		const { oid } = options;
		if (oid) {
			loadOrderDetail(oid);
		} else {
			uni.showToast({
				title: '订单ID不存在',
				icon: 'none'
			});
			uni.navigateBack();
		}
	});

	// 加载订单详情
	const loadOrderDetail = async (oid: string) => {
		loading.value = true;
		try {
			const res = await request({
				url: `/order/detail?oid=${oid}`,
				method: 'GET'
			});

			if (res.errCode === 0) {
				// 设置订单详情（包含后端新增的status_name、jdr等字段）
				orderDetail.value = res.data.order;

				// 设置地址信息
				addressInfo.value = res.data.address;

				// 接收后端返回的会话ID
				conversationId.value = res.data.conversationId;

				// 1. 解析业务详情数据（detail字段）
				if (orderDetail.value.detail) {
					try {
						businessDetail.value = typeof orderDetail.value.detail === 'string'
							? JSON.parse(orderDetail.value.detail)
							: orderDetail.value.detail;
					} catch (e) {
						businessDetail.value = {};
					}
				}

				// 2. 解析表单结构（form字段）
				if (orderDetail.value.form) {
					try {
						formList.value = typeof orderDetail.value.form === 'string'
							? JSON.parse(orderDetail.value.form)
							: orderDetail.value.form;
					} catch (e) {
						formList.value = [];
					}
				}
			} else {
				throw new Error(res.msg || '获取订单详情失败');
			}
		} catch (error) {
			uni.showToast({
				title: '加载订单详情失败',
				icon: 'none'
			});
			setTimeout(() => {
				uni.navigateBack();
			}, 1500);
		} finally {
			loading.value = false;
		}
	};

	// 接单操作
	const acceptOrder = async () => {
		if (acceptLoading.value) return;

		acceptLoading.value = true;
		try {
			const uid = userInfo.uid

			const res = await request({
				url: '/order/accept',
				method: 'PUT',
				data: {
					uid: uid,
					oid: orderDetail.value.oid
				}
			});

			if (res.errCode === 0) {
				uni.showToast({
					title: '接单成功',
					icon: 'success'
				});
				// 更新订单状态（同步后端的状态标识和名称，可根据接口返回调整）
				orderDetail.value.status = 'J';
				orderDetail.value.status_name = '进行中';
				orderDetail.value.jdr = uid; // 赋值当前用户为接单者
				orderDetail.value.accept_time = new Date().toISOString();

				// 接单成功后创建私聊会话（如果后端不需要前端创建，可注释此逻辑）
				request({
					url: '/conversation/create',
					method: 'POST',
					data: {
						uid: uid,
						oid: orderDetail.value.oid
					},
				}).then((res) => {
					conversationId.value = res.data
				});
			} else if (res.errCode === 11) {
				// 抢单失败
				uni.showToast({
					title: '抢单失败，订单已被其他用户接取',
					icon: 'none',
					duration: 3000
				});
				// 刷新页面获取最新状态
				loadOrderDetail(orderDetail.value.oid);
			} else {
				throw new Error(res.msg || '接单失败');
			}
		} catch (error) {
			uni.showToast({
				title: error.message || '接单失败',
				icon: 'none'
			});
		} finally {
			acceptLoading.value = false;
		}
	};

	// 完成订单
	const completeOrder = async () => {
		if (completeLoading.value) return;

		try {
			uni.showModal({
				title: '确认完成',
				content: '确认订单已完成？',
				success: async (res) => {
					if (res.confirm) {
						completeLoading.value = true;

						try {
							const result = await request({
								url: '/order/status',
								method: 'PUT',
								data: {
									oid: orderDetail.value.oid,
									status: 'S'
								}
							});

							if (result.errCode === 0) {
								uni.showToast({
									title: '订单已完成',
									icon: 'success'
								});
								// 更新订单状态（同步后端的状态标识和名称）
								orderDetail.value.status = 'S';
								orderDetail.value.status_name = '已完成';
								orderDetail.value.complete_time = new Date().toISOString();
							} else {
								throw new Error(result.msg || '操作失败');
							}
						} catch (error) {
							uni.showToast({
								title: error.message || '操作失败',
								icon: 'none'
							});
						} finally {
							completeLoading.value = false;
						}
					}
				}
			});
		} catch (error) {
			uni.showToast({
				title: error.message || '操作失败',
				icon: 'none'
			});
		}
	};

	// 取消订单
	const cancelOrder = async () => {
		if (cancelLoading.value) return;

		try {
			uni.showModal({
				title: '确认取消',
				content: '确定要取消这个订单吗？',
				success: async (res) => {
					if (res.confirm) {
						cancelLoading.value = true;

						try {
							const result = await request({
								url: '/order/status',
								method: 'PUT',
								data: {
									oid: orderDetail.value.oid,
									status: 'C'
								}
							});

							if (result.errCode === 0) {
								uni.showToast({
									title: '订单已取消',
									icon: 'success'
								});
								// 更新订单状态（同步后端的状态标识和名称）
								orderDetail.value.status = 'C';
								orderDetail.value.status_name = '已取消';
							} else {
								throw new Error(result.msg || '取消失败');
							}
						} catch (error) {
							console.error('取消订单失败:', error);
							uni.showToast({
								title: error.message || '取消失败',
								icon: 'none'
							});
						} finally {
							cancelLoading.value = false;
						}
					}
				}
			});
		} catch (error) {
			console.error('取消订单失败:', error);
			uni.showToast({
				title: error.message || '取消失败',
				icon: 'none'
			});
		}
	};

	// 提交订单评价（PUT /order/rate）
	const submitRating = async () => {
		if (rateLoading.value || rateValue.value <= 0) return;
		rateLoading.value = true;
		try {
			const res = await request({
				url: '/order/rate',
				method: 'PUT',
				data: { oid: orderDetail.value.oid, rating: rateValue.value }
			});
			if (res.errCode === 0) {
				orderDetail.value.rating = rateValue.value;
				uni.showToast({ title: '评价成功', icon: 'success' });
			} else {
				uni.showToast({ title: res.msg || '评价失败', icon: 'none' });
			}
		} catch (e) {
			uni.showToast({ title: '评价失败', icon: 'none' });
		} finally {
			rateLoading.value = false;
		}
	};

	// 私聊
	const goToPrivateChat = (cid) => {
		if (!cid) {
			uni.showToast({ title: '会话ID不存在', icon: 'none' });
			return;
		}
		const od = orderDetail.value;
		const otherUid = userInfo.uid === od.xdr ? od.jdr : od.xdr;
		const otherName = od.other_user_name || od.otherUserNickname || '用户';
		const otherAvatar = od.other_user_avatar || od.otherUserAvatar || '';
		const q = (k, v) => `${k}=${encodeURIComponent(v || '')}`;
		uni.navigateTo({
			url: `/pages/messages/private-chat?${q('conversationId', cid)}&${q('otherUserId', otherUid)}&${q('otherUserName', otherName)}&${q('otherUserAvatar', otherAvatar)}&${q('orderId', od.oid)}`
		});
	};
</script>

<style scoped>
	.order-detail-page {
		min-height: 100vh;
		background-color: #f0f2f5;
		padding-bottom: 200rpx;
	}

	.detail-navbar-wrap {
		position: relative;
		border-bottom: 3rpx solid;
	}

	.navbar-center {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		gap: 4rpx;
		min-height: 56px;
	}

	.navbar-row {
		display: flex;
		align-items: center;
		gap: 12rpx;
	}

	.navbar-service {
		font-size: 28rpx;
		font-weight: 600;
		color: #1f2937;
	}

	.navbar-oid {
		font-size: 22rpx;
		color: #6b7280;
	}

	.detail-content {
		padding: 24rpx;
	}

	.info-card {
		background-color: #fff;
		border-radius: 20rpx;
		padding: 32rpx 28rpx;
		margin-bottom: 24rpx;
		box-shadow: 0 2rpx 16rpx rgba(0, 0, 0, 0.06);
	}

	.info-card :deep(.u-title) {
		margin-bottom: 28rpx;
		display: block;
	}

	.info-item {
		display: flex;
		justify-content: space-between;
		align-items: flex-start;
		padding: 20rpx 0;
		border-bottom: 1rpx solid #f5f5f5;
		min-height: 44rpx;
	}

	.info-item:last-child {
		border-bottom: none;
		padding-bottom: 0;
	}

	.rating-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 20rpx 0;
	}
	.rating-row .label {
		margin-bottom: 0;
	}
	.rating-wrap {
		display: flex;
		align-items: center;
		gap: 24rpx;
	}
	.rating-submit-btn {
		flex-shrink: 0;
	}

	.label {
		font-size: 28rpx;
		color: #6b7280;
		flex-shrink: 0;
		width: 200rpx;
		line-height: 1.5;
	}

	.value {
		font-size: 28rpx;
		color: #1f2937;
		flex: 1;
		text-align: right;
		word-break: break-all;
		line-height: 1.5;
	}

	.value.price {
		color: #dc2626;
		font-weight: 700;
		font-size: 32rpx;
	}

	.value.link {
		color: #1a73e8;
		text-decoration: underline;
	}

	.action-buttons {
		padding: 28rpx 24rpx;
		padding-bottom: calc(28rpx + env(safe-area-inset-bottom, 0));
		background-color: #fff;
		position: fixed;
		left: 0;
		right: 0;
		bottom: 0;
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
		box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.08);
	}
</style>