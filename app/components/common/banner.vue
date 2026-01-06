<template>
	<view class="banner-container" :style="{ height: height }">
		<swiper 
			class="swiper" 
			:circular="circular" 
			:interval="interval" 
			:autoplay="autoplay"
			:indicator-dots="showIndicators"
			:indicator-active-color="indicatorActiveColor"
			:indicator-color="indicatorColor"
		>
			<swiper-item v-for="(item, index) in images" :key="index">
				<view 
					class="banner-item" 
					@click="handleBannerClick(item, index)"
					:style="{ height: '100%' }"
				>
					<image 
						class="banner-image" 
						:src="typeof item === 'string' ? item : item.image" 
						mode="widthFix"
						:style="{ height: '100%' }"
					/>
				</view>
			</swiper-item>
		</swiper>
	</view>
</template>
<script lang="ts" setup>
	import { defineProps, defineEmits } from 'vue';
	
	interface BannerItem {
		image: string;
		link?: string;
		id?: string;
	}
	
	type BannerImage = string | BannerItem;
	
	const props = defineProps<{
		images: BannerImage[];
		autoplay?: boolean;
		interval?: number;
		circular?: boolean;
		height?: string;
		showIndicators?: boolean;
		indicatorColor?: string;
		indicatorActiveColor?: string;
	}>();
	
	const emit = defineEmits<{
		(e: 'click', item: BannerImage, index: number): void;
	}>();
	
	const handleBannerClick = (item: BannerImage, index: number) => {
		emit('click', item, index);
	};
</script>
<style scoped>
	.banner-container {
		width: 100%;
		height: 300rpx;
		position: relative;
	}
	
	.swiper {
		width: 100%;
		height: 100%;
	}
	
	.banner-item {
		width: 100%;
		height: 100%;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.banner-image {
		width: 100%;
		height: 100%;
		border-radius: 0 0 16rpx 16rpx;
	}
</style>