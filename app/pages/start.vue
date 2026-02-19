<template>
	<view class="container">
		<view class="brand-area">
			<image class="logo" src="https://ai-public.mastergo.com/ai/img_res/c7241bce76bce134b3f269645a42162c.jpg"
				mode="aspectFit" />
			<text class="slogan">让科技改变生活</text>
		</view>

		<view class="loading-area">
			<view class="loading-animation">
				<uni-icons type="spinner-cycle" size="40" color="#666" />
			</view>
			<text class="loading-text">{{ loadingText }}</text>
		</view>

		<view class="footer">
			<text class="copyright">©2025 科技公司 版权所有</text>
		</view>
	</view>
</template>

<script lang="ts" setup>
	import { onLoad } from '@dcloudio/uni-app'
	import request from '@/utils/request.js'
	import {
		useUserStore
	} from '@/stores/user.js'
	import { ref } from 'vue'

	const loadingText = ref("正在初始化应用...")

	onLoad(() => {
		const user = useUserStore()
		uni.login({
			success: (res) => {
				request({
					url: '/auth/login',
					data: {
						code: res.code
					},
					timeout: 3000,
					failed: () => {
						loadingText.value = "当前网络不通畅，请稍后重试"
					}
				}).then((res) => {
					if (res.errCode === 0) {
						user.setUserInfo(res.data)
						uni.switchTab({
							url: "/pages/index/index"
						})
					} else {
						// errCode 11 等错误：request.js 已 showToast，此处更新加载文案
						loadingText.value = res.msg || '登录失败，请重试'
					}
				})
			}
		})
	})
</script>

<style>
	page {
		height: 100%;
	}

	.container {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		height: 100%;
		background-color: #f9f9f9;
	}

	.brand-area {
		flex: 3.5;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		width: 100%;
	}

	.logo {
		width: 200rpx;
		height: 200rpx;
		margin-bottom: 20rpx;
	}

	.slogan {
		font-size: 16px;
		color: #666;
		margin-top: 20rpx;
	}

	.loading-area {
		flex: 2;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: flex-start;
		width: 100%;
	}

	.loading-animation {
		width: 40px;
		height: 40px;
		margin-bottom: 20rpx;
		animation: rotate 1.5s linear infinite;
	}

	.loading-text {
		font-size: 14px;
		color: #999;
	}

	.footer {
		flex: 1;
		display: flex;
		align-items: flex-end;
		justify-content: center;
		width: 100%;
		padding-bottom: 40rpx;
	}

	.copyright {
		font-size: 12px;
		color: #ccc;
	}

	@keyframes rotate {
		from {
			transform: rotate(0deg);
		}

		to {
			transform: rotate(360deg);
		}
	}
</style>