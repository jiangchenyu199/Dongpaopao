<template>
	<view class="wallet-page">
		<view class="wallet-main">
			<u-icon name="rmb-circle-fill" size="64" color="orange" />
			<text class="wallet-label">钱包余额</text>
			<text class="wallet-amount">￥{{ formatAmount(totalBalance) }}</text>
			<view class="wallet-btns">
				<u-button size="large" type="success" class="wallet-btn" text="充值" @click="handleAction('recharge')" />
				<u-button size="large" class="wallet-btn" text="提现" @click="handleAction('withdraw')" />
			</view>
		</view>
		<view class="wallet-footer" @click="goToDetail">
			<text class="wallet-footer-text">交易记录</text>
		</view>

		<!-- 充值 -->
		<u-popup :show="showRecharge" mode="bottom" round="16" closeable @close="hideRechargeDialog">
			<view class="sheet">
				<text class="sheet-title">充值</text>
				<view class="sheet-field">
					<text class="sheet-label">金额</text>
					<view class="sheet-input-row">
						<text class="sheet-currency">¥</text>
						<u-input v-model="rechargeAmount" placeholder="0.00" type="digit" border="none"
							customStyle="flex:1; padding: 24rpx 0; font-size: 40rpx;" @input="validateRechargeAmount" />
					</view>
					<text v-if="rechargeError" class="sheet-error">{{ rechargeError }}</text>
				</view>
				<view class="sheet-field">
					<text class="sheet-label">选金额</text>
					<view class="sheet-chips">
						<text v-for="amount in quickAmounts" :key="amount" class="sheet-chip"
							:class="{ on: rechargeAmount === String(amount) }" @click="setRechargeAmount(amount)">¥{{
								amount }}</text>
					</view>
				</view>
				<view class="sheet-pay">
					<text class="sheet-pay-name">微信支付</text>
				</view>
				<view class="sheet-footer">
					<u-button text="充值" :loading="rechargeLoading" color="#1a1a1a"
						customStyle="height: 96rpx; font-size: 32rpx; border-radius: 0;"
						@click="confirmRecharge"></u-button>
				</view>
			</view>
		</u-popup>

		<!-- 提现 -->
		<u-popup :show="showWithdraw" mode="bottom" round="16" closeable @close="hideWithdrawDialog">
			<view class="sheet">
				<text class="sheet-title">提现</text>
				<view class="sheet-hint">可提现 ¥{{ formatAmount(totalBalance) }}</view>
				<view class="sheet-field">
					<text class="sheet-label">金额</text>
					<view class="sheet-input-row">
						<text class="sheet-currency">¥</text>
						<u-input v-model="withdrawAmount" placeholder="0.00" type="digit" border="none"
							customStyle="flex:1; padding: 24rpx 0; font-size: 40rpx;" @input="validateWithdrawAmount" />
					</view>
					<text v-if="withdrawError" class="sheet-error">{{ withdrawError }}</text>
				</view>
				<view class="sheet-to">
					<text class="sheet-to-label">到账</text>
					<text class="sheet-to-value">微信零钱</text>
				</view>
				<view class="sheet-rules">
					<text>2 小时内到账 · 单笔限 5 万 · 每日 3 次</text>
				</view>
				<view class="sheet-footer">
					<u-button text="提现" :loading="withdrawLoading" color="#1a1a1a"
						customStyle="height: 96rpx; font-size: 32rpx; border-radius: 0;"
						@click="confirmWithdraw"></u-button>
				</view>
			</view>
		</u-popup>

		<u-loading-page :loading="loading" />
	</view>
</template>

<script setup lang="ts">
import { ref, Ref } from 'vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'
import { onShow } from '@dcloudio/uni-app'

const userInfo = useUserStore().info

// 响应式数据
const totalBalance: Ref<number> = ref(0)
const showRecharge = ref(false)
const showWithdraw = ref(false)
const loading: Ref<boolean> = ref(false)
const rechargeLoading = ref(false)
const withdrawLoading = ref(false)

// 微信用户信息
const wechatInfo = ref({
	nickname: '微信用户',
	avatar: ''
})

// 充值相关
const rechargeAmount = ref('')
const rechargeError = ref('')
const quickAmounts = [100, 200, 500, 1000, 2000]

// 提现相关
const withdrawAmount = ref('')
const withdrawError = ref('')

function goToDetail() {
	uni.navigateTo({ url: '/pages/user/wallet-detail' })
}

onShow(async () => {
	await loadWalletData()
})

// 加载钱包数据
const loadWalletData = async () => {
	loading.value = true
	try {
		await request({
			url: "/user/balance?uid=" + userInfo.uid
		}).then((res) => {
			totalBalance.value = res.data || 0
		})
	} catch (error) {
		uni.showToast({
			title: '数据加载失败',
			icon: 'error'
		})
	} finally {
		loading.value = false
	}
}

// 格式化金额
const formatAmount = (amount: number) => {
	return Math.abs(amount).toFixed(2)
}

// 操作处理
const handleAction = (type: string) => {
	switch (type) {
		case 'recharge':
			showRechargeDialog()
			break
		case 'withdraw':
			showWithdrawDialog()
			break
	}
}

// 充值功能
const showRechargeDialog = () => {
	showRecharge.value = true
	rechargeAmount.value = ''
	rechargeError.value = ''
}

const hideRechargeDialog = () => {
	showRecharge.value = false
}

const setRechargeAmount = (amount: number) => {
	rechargeAmount.value = amount.toString()
	validateRechargeAmount()
}

const validateRechargeAmount = () => {
	const amount = parseFloat(rechargeAmount.value)
	if (!amount || amount <= 0) {
		rechargeError.value = '请输入有效金额'
	} else if (amount < 1) {
		rechargeError.value = '充值金额不能少于1元'
	} else if (amount > 50000) {
		rechargeError.value = '单笔充值不能超过50,000元'
	} else {
		rechargeError.value = ''
	}
}

const confirmRecharge = async () => {
	if (!rechargeAmount.value || rechargeError.value) return

	rechargeLoading.value = true

	try {
		const amount = parseFloat(rechargeAmount.value)

		const res = await request({
			url: '/user/recharge',
			method: 'PUT',
			data: {
				uid: userInfo.uid,
				amount: amount
			}
		})

		if (res.code === 200) {
			uni.showToast({
				title: `充值成功 ¥${rechargeAmount.value}`,
				icon: 'success'
			})

			totalBalance.value += amount
			hideRechargeDialog()
		} else {
			uni.showToast({
				title: res.msg || '充值失败',
				icon: 'error'
			})
		}
	} catch (error) {
		uni.showToast({
			title: '充值失败，请重试',
			icon: 'error'
		})
	} finally {
		rechargeLoading.value = false
	}
}

// 提现功能
const showWithdrawDialog = () => {
	showWithdraw.value = true
	withdrawAmount.value = ''
	withdrawError.value = ''
}

const hideWithdrawDialog = () => {
	showWithdraw.value = false
}

const validateWithdrawAmount = () => {
	const amount = parseFloat(withdrawAmount.value)
	if (!amount || amount <= 0) {
		withdrawError.value = '请输入有效金额'
	} else if (amount < 1) {
		withdrawError.value = '提现金额不能少于1元'
	} else if (amount > totalBalance.value) {
		withdrawError.value = '提现金额不能超过总余额'
	} else if (amount > 50000) {
		withdrawError.value = '单笔提现不能超过50,000元'
	} else {
		withdrawError.value = ''
	}
}

const confirmWithdraw = async () => {
	if (!withdrawAmount.value || withdrawError.value) return

	withdrawLoading.value = true

	try {
		const amount = parseFloat(withdrawAmount.value)

		const res = await request({
			url: '/user/withdraw',
			method: 'PUT',
			data: {
				uid: userInfo.uid,
				amount: amount
			}
		})

		if (res.code === 200) {
			uni.showToast({
				title: '提现申请已提交',
				icon: 'success'
			})

			totalBalance.value -= amount
			hideWithdrawDialog()
		} else {
			uni.showToast({
				title: res.msg || '提现失败',
				icon: 'error'
			})
		}
	} catch (error) {
		uni.showToast({
			title: '提现失败，请重试',
			icon: 'error'
		})
	} finally {
		withdrawLoading.value = false
	}
}
</script>

<style scoped>
.wallet-page {
	min-height: 100vh;
	background: #fff;
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 80rpx 48rpx 140rpx;
	box-sizing: border-box;
}

.wallet-main {
	display: flex;
	flex-direction: column;
	align-items: center;
	width: 100%;
	max-width: 560rpx;
	gap: 36rpx;
	margin-top: 192rpx;
}

.wallet-label {
	font-size: 30rpx;
	color: #333;
}

.wallet-amount {
	font-size: 64rpx;
	font-weight: bold;
	color: #1a1a1a;
}

.wallet-btns {
	margin-top: 192rpx;
	width: 100%;
	display: flex;
	flex-direction: column;
	gap: 36rpx;
}

.wallet-btn {
	width: 100%;
	height: 88rpx;
}

.wallet-footer {
	position: fixed;
	left: 0;
	right: 0;
	bottom: 40rpx;
	padding: 32rpx 0 32rpx;
	background: #fff;
	text-align: center;
}

.wallet-footer-text {
	font-size: 28rpx;
	color: #999;
}

.wallet-footer:active {
	opacity: 0.7;
}

/* 底部弹窗 */
.sheet {
	padding: 40rpx 32rpx env(safe-area-inset-bottom);
	background: #fff;
}

.sheet-title {
	display: block;
	font-size: 36rpx;
	font-weight: 600;
	color: #1a1a1a;
	margin-bottom: 32rpx;
}

.sheet-hint {
	font-size: 26rpx;
	color: #666;
	margin-bottom: 24rpx;
}

.sheet-field {
	margin-bottom: 28rpx;
}

.sheet-label {
	display: block;
	font-size: 26rpx;
	color: #666;
	margin-bottom: 16rpx;
}

.sheet-input-row {
	display: flex;
	align-items: center;
	border-bottom: 2rpx solid #e5e5e5;
	padding-bottom: 8rpx;
}

.sheet-currency {
	font-size: 36rpx;
	color: #1a1a1a;
	margin-right: 12rpx;
}

.sheet-error {
	display: block;
	font-size: 24rpx;
	color: #ef4444;
	margin-top: 12rpx;
}

.sheet-chips {
	display: flex;
	flex-wrap: wrap;
	gap: 16rpx;
}

.sheet-chip {
	padding: 16rpx 28rpx;
	font-size: 26rpx;
	color: #666;
	background: #f5f5f5;
	border-radius: 8rpx;
}

.sheet-chip.on {
	background: #1a1a1a;
	color: #fff;
}

.sheet-pay {
	padding: 24rpx 0;
	border-top: 1rpx solid #f0f0f0;
	margin-top: 8rpx;
}

.sheet-pay-name {
	font-size: 28rpx;
	color: #1a1a1a;
}

.sheet-to {
	margin-bottom: 20rpx;
}

.sheet-to-label {
	font-size: 26rpx;
	color: #666;
	margin-right: 12rpx;
}

.sheet-to-value {
	font-size: 28rpx;
	color: #1a1a1a;
}

.sheet-rules {
	font-size: 24rpx;
	color: #999;
	margin-bottom: 24rpx;
}

.sheet-footer {
	margin-top: 24rpx;
	padding-top: 24rpx;
	border-top: 1rpx solid #eee;
}
</style>