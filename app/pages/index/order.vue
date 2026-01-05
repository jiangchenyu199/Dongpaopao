<template>
	<view class="page-container">
		<!-- 订单基本信息 -->
		<view class="card">
			<template #title>
				<u-title>基本信息</u-title>
			</template>
			<u-form :model="formData" :rules="formRules" label-position="top" label-width="auto">
				<u-form-item v-for="(item,index) in formTemplate" :label="item.label" :key="item.name">
					<u-input v-model="formData[item.name]" :type="item.inputType" :maxlength="item.maxlength"
						:prefixIcon="item.icon" :placeholder="item.placeholder" />
				</u-form-item>
			</u-form>
		</view>

		<!-- 配送信息区域 -->
		<DeliverInfo ref="deliverInfoRef" />

		<!-- 费用信息区域 -->
		<FeeInfo ref="feeInfoRef" @submit="handleSubmit" />

		<!-- TODO: 拆分结账模块 -->
		<Checkout @submit="handleSubmit" />
	</view>
</template>

<script lang="ts" setup>
	import { ref } from 'vue';
	import { onLoad } from '@dcloudio/uni-app'

	import { useUserStore } from '@/stores/user';

	import DeliverInfo from '@/components/orders/deliverInfo.vue';
	import FeeInfo from '@/components/orders/feeInfo.vue';
	import request from '@/utils/request';
	import form from '../../uni_modules/uview-plus/components/u-form/form';

	const userStore = useUserStore()

	/* 服务类型id */
	const orderTypeId : String = ref()

	/* 表单模板 */
	const formTemplate : Array = ref([])

	/* 表单数据 */
	const formData : Object = ref({})

	/* TODO: 表单验证 */
	const fromRules : Object = ref({})

	onLoad(async (options) => {
		orderTypeId.value = options.serviceId
		await request({
			url: '/order-type/detail?orderTypeId=' + orderTypeId.value
		}).then((res) => {
			formTemplate.value = JSON.parse(res.data)
		})
	})

	const deliverInfoRef = ref(null);
	const feeInfoRef = ref(null);

	// 获取所有表单数据
	const getAllFormData = () => {
		const deliverInfo = deliverInfoRef.value?.getFormData ? deliverInfoRef.value.getFormData() : {};
		const feeInfo = feeInfoRef.value?.getFeeData ? feeInfoRef.value.getFeeData() : {};

		return {
			orderTypeId: orderTypeId.value,
			uid: null,
			formData: formData.value,
			deliverInfo,
			feeInfo
		};
	};

	/* 验证表单 */
	const validateForm = (formData : any) => {
		return true;
	};

	/* 下单操作 */
	const handleSubmit = () => {
		const formData = getAllFormData();

		formData.uid = userStore.info.uid

		// 验证表单
		if (!validateForm(formData)) {
			return;
		}

		// 显示加载中
		uni.showLoading({
			title: '提交中...'
		});

		// 调用API提交订单
		request({
			url: "/order/create",
			data: formData,
			method: "POST",
			failed: () => {
				uni.showToast({
					title: "下单失败",
					icon: "error"
				})
			}
		}).then((res) => {
			uni.hideLoading();

			res.errCode === 0 ?
				uni.showToast({
					title: "下单成功",
					icon: 'success'
				})
				:
				uni.showToast({
					title: res.msg,
					icon: 'error'
				});

			// 跳转到订单详情或订单列表页面
			setTimeout(() => {
				uni.navigateBack();
			}, 1500)
		})
	}
</script>

<style scoped>
	.page-container {
		display: flex;
		flex-direction: column;
		height: 100%;
		background-color: #f7f7f7;
	}

	.card {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
	}
</style>