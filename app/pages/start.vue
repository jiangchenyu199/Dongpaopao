<template>
	<view class="container" :style="{ backgroundColor: splashConfig.backgroundColor || '#f9f9f9' }">
		<view class="brand-area">
			<image v-if="splashConfig.logoUrl" class="logo" :src="splashConfig.logoUrl" mode="aspectFit" />
			<text
				class="slogan"
				:style="{
					fontSize: (splashConfig.sloganFontSize || 16) + 'px',
					color: splashConfig.sloganColor || splashConfig.primaryColor || '#666'
				}"
			>{{ splashConfig.slogan || '让科技改变生活' }}</text>
		</view>

		<view class="loading-area">
			<view class="loading-animation">
				<uni-icons type="spinner-cycle" size="40" :color="splashConfig.primaryColor || '#666'" />
			</view>
			<text class="loading-text" :style="{ color: splashConfig.primaryColor || '#999' }">{{ loadingText }}</text>
		</view>

		<view v-if="splashConfig.showCopyright !== 0" class="footer">
			<text class="copyright">©2025 科技公司 版权所有</text>
		</view>
	</view>
</template>

<script lang="ts" setup>
	import { onLoad } from '@dcloudio/uni-app'
	import request from '@/utils/request.js'
	import { useUserStore } from '@/stores/user.js'
	import { ref, reactive } from 'vue'

	const loadingText = ref("正在初始化应用...")
	const splashConfig = reactive({
		logoUrl: '',
		slogan: '',
		backgroundColor: '#f9f9f9',
		primaryColor: '#666666',
		sloganFontSize: null as number | null,
		sloganColor: '',
		showCopyright: 1
	})

	onLoad(() => {
		try {
			request({ url: '/app/splash' }).then((res: any) => {
				const d = res?.data
				if (d) {
					splashConfig.logoUrl = d.logoUrl || ''
					splashConfig.slogan = d.slogan || ''
					splashConfig.backgroundColor = d.backgroundColor || '#f9f9f9'
					splashConfig.primaryColor = d.primaryColor || '#666666'
					splashConfig.sloganFontSize = d.sloganFontSize ?? null
					splashConfig.sloganColor = d.sloganColor || ''
					splashConfig.showCopyright = d.showCopyright ?? 1
				}
			}).catch(() => {})

			const user = useUserStore()
			uni.login({
				success: (res) => {
					try {
						request({
							url: '/auth/login',
							data: { code: res.code },
							timeout: 3000,
							failed: () => {
								loadingText.value = "当前网络不通畅，请稍后重试"
							}
						}).then((res: any) => {
							if (res.errCode === 0) {
								user.setUserInfo(res.data)
								uni.switchTab({ url: "/pages/index/index" })
							} else {
								loadingText.value = res.msg || '登录失败，请重试'
							}
						}).catch(() => {
							loadingText.value = '登录失败，请重试'
						})
					} catch (e) {
						loadingText.value = '登录异常，请重试'
						console.error('login success callback:', e)
					}
				},
				fail: (err) => {
					loadingText.value = '微信登录失败，请重试'
					console.error('uni.login fail:', err)
				}
			})
		} catch (e) {
			loadingText.value = '初始化异常，请重试'
			console.error('start onLoad:', e)
		}
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
		from { transform: rotate(0deg); }
		to { transform: rotate(360deg); }
	}
</style>