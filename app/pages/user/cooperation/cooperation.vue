<template>
	<view class="cooperation-page">
		<view class="page-content">
			<section-card title="微信赞赏码">
				<view class="qrcode-wrap">
					<image v-if="showQrcode" class="qrcode-image" :src="qrcodeImageUrl" mode="widthFix"
						@error="onImageError" @longpress="saveQrcodeToAlbum" />
					<text v-if="showQrcode" class="save-hint">长按图片保存到相册</text>
					<text v-else class="qrcode-placeholder">图片加载失败，请检查路径</text>
				</view>
			</section-card>

			<section-card title="备注">
				<text class="remark">{{ remark }}</text>
			</section-card>
		</view>
	</view>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import SectionCard from '@/components/common/section-card.vue';

const qrcodeImageUrl = ref('/static/images/cooperation/WeixinAppreciation.jpg');
const showQrcode = ref(true);
const onImageError = () => {
	showQrcode.value = false;
};

const saveQrcodeToAlbum = () => {
	const src = qrcodeImageUrl.value;
	uni.getImageInfo({
		src,
		success: (res) => {
			uni.saveImageToPhotosAlbum({
				filePath: res.path,
				success: () => {
					uni.showToast({ title: '已保存到相册', icon: 'success' });
				},
				fail: (err) => {
					if (err.errMsg && err.errMsg.indexOf('auth deny') !== -1) {
						uni.showModal({
							title: '提示',
							content: '需要您授权保存图片到相册',
							confirmText: '去设置',
							success: (modalRes) => {
								if (modalRes.confirm) {
									uni.openSetting();
								}
							}
						});
					} else {
						uni.showToast({ title: err.errMsg || '保存失败', icon: 'none' });
					}
				}
			});
		},
		fail: () => {
			uni.showToast({ title: '图片信息获取失败', icon: 'none' });
		}
	});
};

const remark = ref('感谢支持，商务合作请备注联系方式。');
</script>

<style scoped>
.cooperation-page {
	min-height: 100vh;
	background-color: #f5f5f5;
	padding-bottom: 60rpx;
}

.page-content {
	padding: 24rpx;
}

.qrcode-wrap {
	display: flex;
	flex-direction: column;
	align-items: center;
	min-height: 280rpx;
	background: #ffffff;
	border-radius: 16rpx;
	padding: 24rpx;
}

.qrcode-image {
	width: 100%;
	max-width: 420rpx;
	height: auto;
	border-radius: 12rpx;
	display: block;
}

.save-hint {
	margin-top: 20rpx;
	font-size: 24rpx;
	color: #9ca3af;
}

.qrcode-placeholder {
	font-size: 26rpx;
	color: #9ca3af;
	text-align: center;
	padding: 24rpx;
}

.remark {
	font-size: 28rpx;
	color: #374151;
	line-height: 1.6;
	display: block;
	margin-top: 20rpx;
	text-align: center;
}
</style>
