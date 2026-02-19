<template>
	<view class="card">
		<template #title>
			<u-title>配送信息</u-title>
		</template>
		<view class="deliver-info" @click="showAddressList">
			<view v-if="selectedAddress" class="address-card">
				<view class="contact-info">
					<text class="name">{{ selectedAddress.sjr }}</text>
					<text class="phone">{{ selectedAddress.phone }}</text>
				</view>
				<view class="address-detail">
					{{ selectedAddress.detail }}
				</view>
				<view class="deliver-time">
					期望配送时间：{{ deliverTime }}
				</view>
			</view>
			<view v-else class="no-address">
				<text>请选择配送地址</text>
			</view>
		</view>
	</view>

	<!-- 地址选择弹窗 -->
	<u-popup :show="addressListShow" safeAreaInsetBottom safeAreaInsetTop :closeable="true"
		@close="addressListShow = false">
		<view class="popup-content" @click.stop>
			<view class="popup-header">
				<text class="popup-title">选择地址</text>
				<text class="popup-close" @click="addressListShow = false">✕</text>
			</view>
			<view class="address-list">
				<view v-for="addr in addressList" :key="addr.aid" class="address-item"
					:class="{ active: selectedAddress && addr.aid === selectedAddress.aid }"
					@click="selectAddress(addr)">
					<view class="address-item-header">
						<text class="addr-name">{{ addr.sjr }}</text>
						<text class="addr-phone">{{ addr.phone }}</text>
						<text v-if="addr.isDefault" class="default-tag">默认</text>
					</view>
					<view class="addr-detail">{{ addr.detail }}</view>
				</view>
			</view>
		</view>
	</u-popup>
</template>

<script lang="ts" setup>
import { onLoad } from '@dcloudio/uni-app'
import { ref, onMounted, defineExpose } from 'vue';
import { useUserStore } from '@/stores/user';
import request from '@/utils/request';

// 状态管理
const userStore = useUserStore();

// 状态变量
const addressList = ref<any[]>([]);
const selectedAddress = ref<any>(null);
const deliverTime = ref('尽快配送');
const addressListShow = ref(false);

// 加载地址列表
const loadAddressList = async (autoSelect = false) => {
	const res = await request({
		url: `/address/list?uid=${userStore.info.uid}`
	});

	if (res?.data && Array.isArray(res.data)) {
		addressList.value = res.data;
		if (autoSelect) {
			const defaultAddr = res.data.find((addr: any) => addr.isDefault);
			if (defaultAddr) {
				selectedAddress.value = defaultAddr;
			}
		}
	}
};

// 选择地址
const selectAddress = (addr: any) => {
	selectedAddress.value = addr;
	addressListShow.value = false;
};

// 显示地址列表
const showAddressList = () => {
	loadAddressList();
	addressListShow.value = true;
};

// 组件挂载时加载地址
onMounted(() => {
	loadAddressList(true);
});

// 暴露方法给父组件
defineExpose({
	getFormData: () => {
		if (!selectedAddress.value) {
			return {
				deliverAddress: '',
				contactName: '',
				contactPhone: '',
				deliverTime: deliverTime.value,
				aid: '',
				expectTime: new Date().toISOString()
			};
		}

		return {
			deliverAddress: selectedAddress.value.detail,
			contactName: selectedAddress.value.sjr,
			contactPhone: selectedAddress.value.phone,
			deliverTime: deliverTime.value,
			aid: selectedAddress.value.aid,
			expectTime: new Date().toISOString()
		};
	}
});
</script>

<style scoped>
.card {
	background-color: #fff;
	border-radius: 12rpx;
	padding: 30rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.deliver-info {
	cursor: pointer;
}

.address-card {
	padding: 20rpx;
	border: 1rpx solid #f0f0f0;
	border-radius: 8rpx;
}

.contact-info {
	margin-bottom: 15rpx;
}

.name {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
	margin-right: 20rpx;
}

.phone {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
}

.address-detail {
	font-size: 26rpx;
	color: #666;
	line-height: 36rpx;
	margin-bottom: 15rpx;
}

.deliver-time {
	font-size: 24rpx;
	color: #999;
}

.no-address {
	padding: 40rpx 20rpx;
	text-align: center;
	color: #999;
	border: 1rpx dashed #e0e0e0;
	border-radius: 8rpx;
}

/* 弹窗样式 */
.popup-content {
	background-color: #fff;
	height: 100%;
}

.popup-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.popup-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
}

.popup-close {
	font-size: 36rpx;
	color: #999;
}

.address-list {
	padding: 20rpx;
	max-height: 500rpx;
	overflow-y: auto;
}

.address-item {
	padding: 20rpx;
	border: 1rpx solid #f0f0f0;
	border-radius: 8rpx;
	margin-bottom: 15rpx;
}

.address-item.active {
	border-color: #007aff;
	background-color: #f5f9ff;
}

.address-item-header {
	display: flex;
	align-items: center;
	margin-bottom: 10rpx;
}

.addr-name {
	font-size: 26rpx;
	font-weight: bold;
	color: #333;
	margin-right: 15rpx;
}

.addr-phone {
	font-size: 26rpx;
	color: #666;
	flex: 1;
}

.default-tag {
	font-size: 20rpx;
	color: #007aff;
	background-color: #e6f0ff;
	padding: 4rpx 12rpx;
	border-radius: 12rpx;
}

.addr-detail {
	font-size: 24rpx;
	color: #666;
	line-height: 32rpx;
}
</style>