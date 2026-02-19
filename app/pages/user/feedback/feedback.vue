<template>
	<view class="feedback-page">
		<view class="page-content">
			<u-alert title="订单问题？可以在订单详情发起申诉" type="warning" :show-icon="true" />

			<section-card title="反馈内容">
				<u-textarea v-model="content" placeholder="请描述您的问题或建议，我们会认真阅读并改进～" :height="70" maxlength="200"
					:show-confirm-bar="false" autoHeight border="none" :customStyle="{ padding: '0' }" />
				<text>{{ content.length }}/200</text>
			</section-card>

			<section-card title="联系方式（选填）">
				<u-input v-model="contact" placeholder="手机号或微信号，方便我们回复您" border="none"
					:customStyle="{ padding: '0' }" />
			</section-card>

			<u-button type="primary" shape="square" :loading="submitting" :disabled="!content.trim()"
				customStyle="margin-top: 24rpx; border-radius: 0;" @click="submit">
				提交反馈
			</u-button>
		</view>
	</view>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import request from '@/utils/request';
import { useUserStore } from '@/stores/user';
import SectionCard from '@/components/common/section-card.vue';

const userStore = useUserStore();
const content = ref('');
const contact = ref('');
const submitting = ref(false);

const submit = async () => {
	const text = content.value.trim();
	if (!text) {
		uni.showToast({ title: '请填写反馈内容', icon: 'none' });
		return;
	}
	submitting.value = true;
	try {
		const res = await request({
			url: '/feedback/submit',
			method: 'POST',
			data: {
				uid: userStore.info?.uid,
				content: text,
				contact: contact.value.trim()
			}
		});
		if (res.errCode === 0) {
			uni.showToast({ title: '感谢您的反馈', icon: 'success' });
			content.value = '';
			contact.value = '';
		} else {
			uni.showToast({ title: res.msg || '提交失败', icon: 'none' });
		}
	} catch (e) {
		uni.showToast({ title: '网络异常，请稍后重试', icon: 'none' });
	} finally {
		submitting.value = false;
	}
};
</script>

<style scoped>
.feedback-page {
	min-height: 100vh;
	background-color: #f5f5f5;
	padding-bottom: 60rpx;
}

.page-content {
	padding: 24rpx;
}
</style>
