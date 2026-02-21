<template>
	<view class="profile-page">
		<!-- 头像区 -->
		<view class="profile-avatar-card">
			<u-avatar class="profile-avatar" size="120" :src="userInfo.avatar" mode="aspectFill" />
			<button class="profile-avatar-btn" open-type="chooseAvatar" @chooseavatar="changeAvatar">更换头像</button>
		</view>

		<!-- 基本信息 -->
		<view class="profile-card">
			<view class="profile-row">
				<text class="profile-label">昵称</text>
				<input class="profile-input" v-model="userInfo.nickname" placeholder="请输入昵称"
					placeholder-class="profile-placeholder" />
			</view>
			<view class="profile-row profile-row--readonly">
				<text class="profile-label">ID</text>
				<text class="profile-value">{{ userInfo.uid }}</text>
			</view>
			<view class="profile-row">
				<text class="profile-label">性别</text>
				<picker class="profile-picker" mode="selector" :range="genderOptions" range-key="text"
					:value="genderIndex" @change="genderChange">
					<view class="profile-picker-value">{{ genderOptions[genderIndex]?.text }}</view>
				</picker>
				<u-icon name="arrow-right" size="14" color="#999" />
			</view>
			<view class="profile-row">
				<text class="profile-label">学校</text>
				<picker class="profile-picker" mode="selector" :range="schoolList" range-key="sname"
					:value="schoolIndex" @change="schoolChange">
					<view class="profile-picker-value">{{ schoolList[schoolIndex]?.sname || '请选择' }}</view>
				</picker>
				<u-icon name="arrow-right" size="14" color="#999" />
			</view>
		</view>

		<!-- 账号安全 -->
		<view class="profile-card">
			<view class="profile-row profile-row--readonly">
				<text class="profile-label">手机号</text>
				<text v-if="userInfo.phone" class="profile-value">{{ userInfo.phone }}</text>
				<button v-else class="profile-auth-btn" open-type="getPhoneNumber"
					@getphonenumber="getPhoneNumber">授权手机号</button>
			</view>
		</view>

		<view class="profile-actions">
			<u-button type="primary" text="保存修改" customStyle="height: 96rpx; font-size: 32rpx; border-radius: 12rpx;"
				@click="saveInfo" />
		</view>

	</view>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { useUserStore } from '@/stores/user';
import { onLoad } from '@dcloudio/uni-app';
import request from '@/utils/request';

const userStore = useUserStore();
const userInfo = userStore.info;

const genderOptions = [
	{ text: '男', value: 1 },
	{ text: '女', value: 0 },
	{ text: '保密', value: -1 }
];
const genderIndex = ref(0);

const schoolList = ref<{ sid?: string; sname: string }[]>([{ sname: '加载中...' }]);
const schoolIndex = ref(0);

function genderChange(e: any) {
	genderIndex.value = Number(e.detail.value) || 0;
}

function schoolChange(e: any) {
	schoolIndex.value = Number(e.detail.value) || 0;
}

onLoad(() => {
	request({ url: '/school/list' }).then((res: any) => {
		schoolList.value = res.data || [];
		const sid = userInfo.sid;
		if (sid && schoolList.value.length) {
			const i = schoolList.value.findIndex((s: any) => String(s.sid) === String(sid));
			if (i >= 0) schoolIndex.value = i;
		}
	});
	const gNum = typeof userInfo.sex === 'number' ? userInfo.sex : Number(userInfo.sex);
	const gi = genderOptions.findIndex((o) => o.value === gNum);
	if (gi >= 0) genderIndex.value = gi;
});

function changeAvatar(res: any) {
	const tempAvatarUrl = res.detail?.avatarUrl;
	if (!tempAvatarUrl) return;
	uni.uploadFile({
		url: 'https://localhost:8181/api/user/avatar?uid=' + userInfo.uid,
		filePath: tempAvatarUrl,
		name: 'file',
		success(res) {
			uni.showToast({ title: '上传成功', icon: 'none' });
			const data = JSON.parse((res as any).data);
			userInfo.avatar = (data.data || '') + '?t=' + Date.now();
		}
	});
}

function getPhoneNumber(res: any) {
	const code = res.detail?.code;
	if (!code) return;
	request({ url: '/auth/getPhoneNumber?code=' + code + '&uid=' + userInfo.uid }).then((r: any) => {
		if (r.data) userInfo.phone = r.data;
	});
}

function saveInfo() {
	const school = schoolList.value[schoolIndex.value];
	request({
		url: '/user/update',
		method: 'PUT',
		data: {
			user: {
				...userInfo,
				sex: genderOptions[genderIndex.value].value,
				sid: school?.sid
			}
		}
	}).then(() => {
		uni.showToast({ title: '保存成功', icon: 'success' });
		setTimeout(() => uni.navigateBack(), 1200);
	});
}
</script>

<style scoped>
.profile-page {
	min-height: 100vh;
	background-color: #f5f5f5;
	padding: 24rpx 20rpx 48rpx;
}

.profile-avatar-card {
	background-color: #fff;
	border-radius: 20rpx;
	padding: 48rpx;
	margin-bottom: 24rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
}

.profile-avatar {
	width: 120rpx;
	height: 120rpx;
	border-radius: 50%;
	background-color: #f0f0f0;
}

.profile-avatar-btn {
	margin-top: 24rpx;
	font-size: 28rpx;
	color: #1a73e8;
	background: none;
	border: none;
	padding: 0;
	line-height: 1.4;
}

.profile-avatar-btn::after {
	border: none;
}

.profile-card {
	background-color: #fff;
	border-radius: 20rpx;
	margin-bottom: 24rpx;
	overflow: hidden;
}

.profile-row {
	display: flex;
	align-items: center;
	padding: 32rpx 28rpx;
	border-bottom: 1rpx solid #f5f5f5;
}

.profile-row:last-child {
	border-bottom: none;
}

.profile-row--readonly .profile-value {
	color: #999;
}

.profile-row--clickable {
	position: relative;
}

.profile-label {
	width: 140rpx;
	flex-shrink: 0;
	font-size: 28rpx;
	color: #333;
}

.profile-input {
	flex: 1;
	font-size: 28rpx;
	color: #1a1a1a;
	text-align: right;
}

.profile-placeholder {
	color: #bbb;
}

.profile-value {
	flex: 1;
	font-size: 28rpx;
	color: #1a1a1a;
	text-align: right;
}

.profile-picker {
	flex: 1;
	text-align: right;
}

.profile-picker-value {
	font-size: 28rpx;
	color: #1a1a1a;
}

.profile-auth-btn {
	flex: 1;
	font-size: 28rpx;
	color: #1a73e8;
	background: none;
	border: none;
	padding: 0;
	text-align: right;
}

.profile-auth-btn::after {
	border: none;
}

.profile-actions {
	padding: 24rpx 0 0;
}

.profile-actions .u-button {
	width: 100%;
}
</style>
