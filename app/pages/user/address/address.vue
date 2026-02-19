<template>
	<view class="page-container">
		<view class="page-content">
			<view class="add-card" @click="addAddress">
				<u-icon name="plus" size="16" color="#9ca3af"></u-icon>
				<text class="add-text">添加新地址</text>
			</view>

			<view v-if="addressList.length === 0" class="empty-wrap">
				<u-icon name="map" size="120" color="#d1d5db"></u-icon>
				<text class="empty-text">暂无收货地址</text>
				<text class="empty-hint">点击上方添加</text>
			</view>

			<view class="address-list" v-else>
				<view
					v-for="(item, index) in addressList"
					:key="item.aid || index"
					class="address-card"
					:class="{ 'is-default': item.isDefault }"
				>
					<view class="card-body">
						<view class="row name-row">
							<text class="name">{{ item.sjr }}</text>
							<text class="phone">{{ item.phone }}</text>
							<u-tag v-if="item.isDefault" text="默认" type="primary" size="mini" plain />
						</view>
						<text class="detail">{{ item.detail }}</text>
					</view>
					<view class="card-actions">
						<view class="action-btn" @click="editAddress(index)">
							<u-icon name="edit-pen" size="20" color="#6b7280"></u-icon>
							<text>编辑</text>
						</view>
						<view class="action-btn danger" @click="deleteAddress(index)">
							<u-icon name="trash" size="20" color="#ef4444"></u-icon>
							<text>删除</text>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>

	<u-popup :show="showPopup" mode="bottom" round="24" @close="closePopup">
		<view class="popup-inner">
			<view class="popup-title">{{ isEdit ? '编辑地址' : '新增地址' }}</view>
			<u-form labelWidth="140" labelPosition="left" class="address-form">
				<u-form-item label="收货人" borderBottom>
					<u-input v-model="formData.sjr" placeholder="请输入收货人姓名" border="none" />
				</u-form-item>
				<u-form-item label="手机号" borderBottom>
					<u-input v-model="formData.phone" type="number" placeholder="请输入手机号" border="none" />
				</u-form-item>
				<u-form-item label="详细地址" borderBottom>
					<u-input v-model="formData.detail" type="textarea" placeholder="请输入详细地址" border="none" />
				</u-form-item>
				<u-form-item label="设为默认">
					<template #right>
						<u-switch v-model="formData.isDefault" size="22" />
					</template>
				</u-form-item>
			</u-form>
			<u-button type="primary" @click="saveAddress(isEdit)" :loading="loading" customStyle="margin-top: 24rpx;">
				{{ isEdit ? '保存修改' : '添加地址' }}
			</u-button>
		</view>
	</u-popup>
</template>

<script lang="ts" setup>
	import { ref, reactive } from 'vue';
	import { onLoad } from '@dcloudio/uni-app';
	import request from '@/utils/request';
	import { useUserStore } from '@/stores/user';

	const userInfo = useUserStore().info;

	const showPopup = ref(false);
	const isEdit = ref(false);
	const editingIndex = ref(-1);
	const loading = ref(false);

	const formData = reactive({
		aid: '',
		sjr: '',
		phone: '',
		detail: '',
		isDefault: false,
		uid: userInfo.uid
	});

	onLoad(() => {
		fetchAddress();
	});

	const fetchAddress = () => {
		request({
			url: '/address/list?uid=' + userInfo.uid
		}).then((res) => {
			addressList.value = res.data || [];
		});
	};

	const addressList = ref<any[]>([]);

	const addAddress = () => {
		isEdit.value = false;
		editingIndex.value = -1;
		resetForm();
		showPopup.value = true;
	};

	const editAddress = (index: number) => {
		isEdit.value = true;
		editingIndex.value = index;
		const address = addressList.value[index];
		formData.aid = address.aid || '';
		formData.sjr = address.sjr || '';
		formData.phone = address.phone || '';
		formData.detail = address.detail || '';
		formData.isDefault = !!address.isDefault;
		showPopup.value = true;
	};

	const deleteAddress = (index: number) => {
		uni.showModal({
			title: '提示',
			content: '确定要删除该地址吗？',
			success: async (res) => {
				if (res.confirm) {
					await request({
						url: '/address/delete',
						data: { aid: addressList.value[index].aid },
						method: 'DELETE'
					});
					addressList.value.splice(index, 1);
					uni.showToast({ title: '删除成功', icon: 'success' });
				}
			}
		});
	};

	const saveAddress = async (editing: boolean) => {
		loading.value = true;
		try {
			if (editing) {
				await request({ url: '/address/update', method: 'PUT', data: formData });
				uni.showToast({ title: '更新成功', icon: 'success' });
			} else {
				const res = await request({ url: '/address/add', method: 'POST', data: formData });
				uni.showToast({ title: res.errCode === 0 ? '添加成功' : (res.msg || '添加失败'), icon: res.errCode === 0 ? 'success' : 'none' });
			}
			closePopup();
			resetForm();
			fetchAddress();
		} finally {
			loading.value = false;
		}
	};

	const closePopup = () => {
		showPopup.value = false;
		resetForm();
	};

	const resetForm = () => {
		formData.aid = '';
		formData.sjr = '';
		formData.phone = '';
		formData.detail = '';
		formData.isDefault = false;
	};
</script>

<style scoped>
	page {
		height: 100%;
		background-color: #f5f5f5;
	}

	.page-container {
		min-height: 100vh;
		background-color: #f5f5f5;
		padding-bottom: 60rpx;
	}

	.page-content {
		padding: 24rpx;
	}

	.add-card {
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 16rpx;
		height: 160rpx;
		background: #fff;
		border-radius: 20rpx;
		border: 2rpx dashed #d1d5db;
		margin-bottom: 24rpx;
	}

	.add-card:active {
		background: #f9fafb;
	}

	.add-text {
		font-size: 30rpx;
		color: #6b7280;
	}

	.empty-wrap {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 120rpx 40rpx;
	}

	.empty-text {
		font-size: 30rpx;
		color: #6b7280;
		margin-top: 24rpx;
	}

	.empty-hint {
		font-size: 26rpx;
		color: #9ca3af;
		margin-top: 12rpx;
	}

	.address-list {
		display: flex;
		flex-direction: column;
		gap: 24rpx;
	}

	.address-card {
		background-color: #fff;
		border-radius: 20rpx;
		padding: 28rpx 24rpx;
		box-shadow: 0 2rpx 16rpx rgba(0, 0, 0, 0.06);
		border-left: 6rpx solid transparent;
	}

	.address-card.is-default {
		border-left-color: #1a73e8;
	}

	.card-body {
		margin-bottom: 20rpx;
	}

	.name-row {
		display: flex;
		align-items: center;
		flex-wrap: wrap;
		gap: 16rpx;
		margin-bottom: 16rpx;
	}

	.name {
		font-size: 32rpx;
		font-weight: 600;
		color: #1f2937;
	}

	.phone {
		font-size: 28rpx;
		color: #6b7280;
	}

	.detail {
		font-size: 28rpx;
		color: #4b5563;
		line-height: 1.5;
		display: block;
		padding-left: 0;
	}

	.card-actions {
		display: flex;
		justify-content: flex-end;
		gap: 32rpx;
		padding-top: 20rpx;
		border-top: 1rpx solid #f3f4f6;
	}

	.action-btn {
		display: flex;
		align-items: center;
		gap: 8rpx;
		font-size: 26rpx;
		color: #6b7280;
	}

	.action-btn.danger {
		color: #ef4444;
	}

	.popup-inner {
		padding: 32rpx 24rpx;
		padding-bottom: calc(32rpx + env(safe-area-inset-bottom, 0px));
		background: #fff;
	}

	.popup-title {
		font-size: 34rpx;
		font-weight: 600;
		color: #1f2937;
		margin-bottom: 32rpx;
		text-align: center;
	}

	.address-form {
		margin-bottom: 8rpx;
	}
</style>
