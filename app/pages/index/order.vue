<template>
	<view class="page-container">
		<!-- 根据订单类型显示不同的业务组件 -->
		<Express  ref="businessRef" />
		<Takeaway ref="businessRef" />
		<Carry ref="businessRef" />
		<Purchasing  ref="businessRef" />

		<!-- 配送信息区域 -->
		<DeliverInfo ref="deliverInfoRef" />

		<!-- 费用信息区域 -->
		<FeeInfo ref="feeInfoRef" @submit="handleSubmit" />
	</view>
</template>

<script lang="ts" setup>
	import { ref } from 'vue';
	import { onLoad } from '@dcloudio/uni-app'

	import { useUserStore } from '@/stores/user';

	// 导入各种订单类型的组件
	import Express from '@/components/orders/categories/express.vue';
	import Takeaway from '@/components/orders/categories/takeaway.vue';
	import Carry from '@/components/orders/categories/carry.vue'
	import Purchasing from '@/components/orders/categories/purchasing.vue'

	import DeliverInfo from '@/components/orders/deliverInfo.vue';
	import FeeInfo from '@/components/orders/feeInfo.vue';
	import request from '@/utils/request';

	const type = ref('')

	const userStore = useUserStore()

	const formData = ref({})

	onLoad((options) => {
		// 初始化表单
		request({
			url: '/service/detail?serviceId=' + options.serviceId
		}).then((res) => {
			formData.value = res.data
		})
	})

	const businessRef = ref(null);
	const deliverInfoRef = ref(null);
	const feeInfoRef = ref(null);

	// 获取所有表单数据
	const getAllFormData = () => {
		const deliverInfo = deliverInfoRef.value?.getFormData ? deliverInfoRef.value.getFormData() : {};
		const feeInfo = feeInfoRef.value?.getFeeData ? feeInfoRef.value.getFeeData() : {};

		// 获取业务数据（如果存在）
		const businessInfo = businessRef.value?.getBusinessData ? businessRef.value.getBusinessData() : {};

		return {
			type: type.value,
			uid: null,
			businessInfo,
			deliverInfo,
			feeInfo
		};
	};

	// 验证表单
	const validateForm = (formData : any) => {
		// 验证业务表单
		if (businessRef.value?.validateForm) {
			const businessValid = businessRef.value.validateForm();
			if (!businessValid) return false;
		}

		// 验证配送信息
		if (deliverInfoRef.value?.validateForm) {
			const deliverValid = deliverInfoRef.value.validateForm();
			if (!deliverValid) return false;
		}

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
</style>