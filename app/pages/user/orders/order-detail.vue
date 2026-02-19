<template>
	<view class="order-detail-page">
		<view class="detail-navbar-wrap" :style="{ borderBottomColor: statusBorderColor }">
			<u-navbar :title="''" leftIcon="arrow-left" leftText="返回" :autoBack="true" :fixed="true" :placeholder="true"
				:border="false" :height="navbarContentHeight" bgColor="#fff">
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

			<view class="info-card" v-if="formList.length > 0 && Object.keys(businessDetail).length > 0">
				<u-title>{{ orderDetail.service_name || '服务' }}详情</u-title>
				<view class="info-item" v-for="(item, index) in formList" :key="index">
					<text class="label">{{ item.label }}</text>
					<text class="value" v-if="!item.private || showSensitiveInfo">
						{{ businessDetail[item.name] || '无' }}
					</text>
					<text class="value" v-else>***</text>
				</view>
			</view>

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

			<view class="info-card" v-if="showRatingSection">
				<u-title>订单评价</u-title>
				<view class="rating-row">
					<text class="label">服务评分</text>
					<view class="rating-wrap">
						<d-rate v-if="isRated" :modelValue="displayRating" :count="5" :disabled="true"
							activeColor="#ff9900" inactiveColor="#e5e7eb" :size="20" />
						<template v-else>
							<d-rate v-model="rateValue" :count="5" activeColor="#ff9900" inactiveColor="#e5e7eb"
								:size="20" />
							<u-button class="rating-submit-btn" type="primary" size="small" :loading="rateLoading"
								:disabled="rateValue <= 0" @click="submitRating">
								提交评价
							</u-button>
						</template>
					</view>
				</view>
			</view>

			<!-- 操作按钮 -->
			<view class="action-buttons" v-if="showActionButtons">
				<u-button v-if="orderDetail.status === 'J'" type="success"
					@click="goToPrivateChat(conversationId)">私聊</u-button>
				<u-button v-if="orderDetail.status === 'D' && orderDetail.xdr != userInfo.uid" type="primary"
					:loading="acceptLoading" @click="acceptOrder">接单</u-button>
				<u-button v-if="orderDetail.status === 'J'" type="warning" :loading="completeLoading"
					@click="completeOrder">完成订单</u-button>
				<u-button v-if="orderDetail.status === 'D' && orderDetail.xdr == userInfo.uid" type="error" plain
					:loading="cancelLoading" @click="cancelOrder">取消订单</u-button>
			</view>
		</view>

		<u-loading-page :loading="loading" bgColor="#f5f5f5" />
	</view>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import request from '@/utils/request.js';
import { useUserStore } from '@/stores/user';
import { formatTimeDisplay } from '@/utils/date.js';
import DRate from '@/components/common/d-rate.vue';

const loading = ref(false);
const acceptLoading = ref(false);
const completeLoading = ref(false);
const cancelLoading = ref(false);
const rateLoading = ref(false);
const rateValue = ref(0);

const userInfo = useUserStore().info;

const orderDetail = ref({
	oid: '',
	xdr: '',
	create_time: '',
	expect_time: null,
	complete_time: null,
	accept_time: null,
	service_id: '',
	aid: '',
	detail: '',
	amount: 0,
	status: 'D',
	status_name: '',
	jdr: '',
	form: '',
	service_name: '',
	description: '',
	rating: null
});
const businessDetail = ref({});
const formList = ref([]);
const addressInfo = ref(null);
const conversationId = ref(null);

const showActionButtons = computed(() => ['D', 'J'].includes(orderDetail.value.status));

const showSensitiveInfo = computed(() => {
	return ['J', 'S'].includes(orderDetail.value.status) || orderDetail.value.xdr === userInfo.uid;
});

const statusTagType = computed(() => {
	const map = { D: 'warning', J: 'primary', S: 'success', C: 'error' };
	return map[orderDetail.value.status] || 'info';
});

const statusBorderColor = computed(() => {
	const map = { D: '#f59e0b', J: '#3b82f6', S: '#22c55e', C: '#ef4444' };
	return map[orderDetail.value.status] || '#94a3b8';
});

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
		uni.showToast({ title: '订单ID不存在', icon: 'none' });
		uni.navigateBack();
	}
});

const loadOrderDetail = async (oid) => {
	loading.value = true;
	try {
		const res = await request({ url: `/order/detail?oid=${oid}`, method: 'GET' });
		if (res.errCode === 0) {
			orderDetail.value = res.data.order;
			addressInfo.value = res.data.address;
			conversationId.value = res.data.conversationId;
			if (orderDetail.value.detail) {
				try {
					businessDetail.value = typeof orderDetail.value.detail === 'string'
						? JSON.parse(orderDetail.value.detail) : orderDetail.value.detail;
				} catch (e) {
					businessDetail.value = {};
				}
			}
			if (orderDetail.value.form) {
				try {
					formList.value = typeof orderDetail.value.form === 'string'
						? JSON.parse(orderDetail.value.form) : orderDetail.value.form;
				} catch (e) {
					formList.value = [];
				}
			}
		} else {
			throw new Error(res.msg || '获取订单详情失败');
		}
	} catch (error) {
		uni.showToast({ title: '加载订单详情失败', icon: 'none' });
		setTimeout(() => uni.navigateBack(), 1500);
	} finally {
		loading.value = false;
	}
};

const acceptOrder = async () => {
	if (acceptLoading.value) return;
	acceptLoading.value = true;
	try {
		const res = await request({
			url: '/order/accept',
			method: 'PUT',
			data: { uid: userInfo.uid, oid: orderDetail.value.oid }
		});
		if (res.errCode === 0) {
			uni.showToast({ title: '接单成功', icon: 'success' });
			orderDetail.value.status = 'J';
			orderDetail.value.status_name = '进行中';
			orderDetail.value.jdr = userInfo.uid;
			orderDetail.value.accept_time = new Date().toISOString();
			request({
				url: '/conversation/create',
				method: 'POST',
				data: { uid: userInfo.uid, oid: orderDetail.value.oid }
			}).then((r) => { conversationId.value = r.data; });
		} else if (res.errCode === 11) {
			uni.showToast({ title: '抢单失败，订单已被其他用户接取', icon: 'none', duration: 3000 });
			loadOrderDetail(orderDetail.value.oid);
		} else {
			throw new Error(res.msg || '接单失败');
		}
	} catch (error) {
		uni.showToast({ title: error.message || '接单失败', icon: 'none' });
	} finally {
		acceptLoading.value = false;
	}
};

const completeOrder = async () => {
	if (completeLoading.value) return;
	uni.showModal({
		title: '确认完成',
		content: '确认订单已完成？',
		success: async (res) => {
			if (!res.confirm) return;
			completeLoading.value = true;
			try {
				const result = await request({
					url: '/order/status',
					method: 'PUT',
					data: { oid: orderDetail.value.oid, status: 'S' }
				});
				if (result.errCode === 0) {
					uni.showToast({ title: '订单已完成', icon: 'success' });
					orderDetail.value.status = 'S';
					orderDetail.value.status_name = '已完成';
					orderDetail.value.complete_time = new Date().toISOString();
				} else {
					throw new Error(result.msg || '操作失败');
				}
			} catch (error) {
				uni.showToast({ title: error.message || '操作失败', icon: 'none' });
			} finally {
				completeLoading.value = false;
			}
		}
	});
};

const cancelOrder = async () => {
	if (cancelLoading.value) return;
	uni.showModal({
		title: '确认取消',
		content: '确定要取消这个订单吗？',
		success: async (res) => {
			if (!res.confirm) return;
			cancelLoading.value = true;
			try {
				const result = await request({
					url: '/order/status',
					method: 'PUT',
					data: { oid: orderDetail.value.oid, status: 'C' }
				});
				if (result.errCode === 0) {
					uni.showToast({ title: '订单已取消', icon: 'success' });
					orderDetail.value.status = 'C';
					orderDetail.value.status_name = '已取消';
				} else {
					throw new Error(result.msg || '取消失败');
				}
			} catch (error) {
				uni.showToast({ title: error.message || '取消失败', icon: 'none' });
			} finally {
				cancelLoading.value = false;
			}
		}
	});
};

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
