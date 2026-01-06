<template>
	<view class="page-container">
		<!-- 消息列表 -->
		<view class="message-list" @scrolltolower="loadMore">
			<message-item v-for="conversation in conversationList" :key="conversation.cid" :conversation="conversation" @click="viewMessageDetail" />
			
			<!-- 空状态 -->
			<view class="empty-state" v-if="conversationList.length === 0 && !loading">
				<text class="empty-text">暂无消息</text>
			</view>
			
			<!-- 加载状态 -->
			<view class="loading-state" v-if="loading">
				<text class="loading-text">加载中...</text>
			</view>
			
			<!-- 触底加载状态 -->
			<view class="load-more" v-if="conversationList.length > 0 && !loading">
				<text v-if="hasMore" class="load-more-text">上拉加载更多</text>
				<text v-else class="load-more-text">已加载全部</text>
			</view>
		</view>
	</view>
</template>

<script lang="ts" setup>
	import { ref, onMounted } from 'vue';
	import { onPullDownRefresh, onShow } from '@dcloudio/uni-app'
	import { useUserStore } from '@/stores/user'
	import request from '../../utils/request';
	import MessageItem from '@/components/common/message-item.vue';

	// 用户信息
	const userStore = useUserStore();
	const userInfo = userStore.info;

	// 会话列表数据
	const conversationList = ref([]);
	const loading = ref(false);
	const hasMore = ref(true);
	const pageNum = ref(1);
	const pageSize = ref(10);

	// 获取用户会话列表
	const fetchConversationList = async (isRefresh = false) => {
		if (isRefresh) {
			pageNum.value = 1;
			hasMore.value = true;
		}
		
		loading.value = true;
		try {
			const uid = userInfo?.uid;
			
			if (!uid) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				});
				return;
			}

			const res = await request({
				url: `/conversation/list?uid=${uid}&pageNum=${pageNum.value}&pageSize=${pageSize.value}`,
				method: 'GET'
			});
			
			if (res.errCode === 0) {
				const data = res.data || {};
				const records = data.records || [];
				
				if (isRefresh) {
					conversationList.value = records;
				} else {
					conversationList.value = [...conversationList.value, ...records];
				}
				
				// 判断是否还有更多数据
				hasMore.value = records.length === pageSize.value;
			} else {
				uni.showToast({
					title: res.msg || '加载失败',
					icon: 'none'
				});
			}
		} catch (error) {
			uni.showToast({
				title: '网络错误',
				icon: 'none'
			});
		} finally {
			loading.value = false;
			uni.stopPullDownRefresh();
		}
	};

	// 加载更多
	const loadMore = async () => {
		if (loading.value || !hasMore.value) return;
		
		pageNum.value++;
		await fetchConversationList(false);
	};

	// 查看消息详情
	const viewMessageDetail = (conversation) => {
		const orderId = conversation.oid || '';
		uni.navigateTo({
			url: `/pages/messages/private-chat?conversationId=${conversation.cid}&otherUserId=${conversation.otherUserId}&otherUserName=${encodeURIComponent(conversation.otherUserNickname || '用户')}&otherUserAvatar=${encodeURIComponent(conversation.otherUserAvatar || '')}&orderId=${orderId}`
		});
	};

	// 生命周期
	onMounted(() => {
		fetchConversationList(true);
	});

	onShow(() => {
		// 每次页面显示时刷新数据
		fetchConversationList(true);
	});

	onPullDownRefresh(() => {
		fetchConversationList(true);
	});
</script>

<style>
	page {
		height: 100%;
		background-color: #f5f5f5;
	}

	.page-container {
		display: flex;
		flex-direction: column;
		height: 100%;
		background-color: #f5f5f5;
		padding-bottom: 120rpx; /* 给tabbar留出空间 */
	}

	/* 消息列表样式 */
	.message-list {
		flex: 1;
		overflow-y: auto;
		padding: 20rpx;
	}

	.message-item {
		display: flex;
		padding: 25rpx;
		background-color: #fff;
		border-radius: 16rpx;
		margin-bottom: 20rpx;
		position: relative;
	}

	.message-avatar {
		width: 100rpx;
		height: 100rpx;
		margin-right: 20rpx;
		flex-shrink: 0;
	}

	.avatar-image {
		width: 100%;
		height: 100%;
		border-radius: 50%;
		background-color: #f0f0f0;
	}

	.message-content {
		flex: 1;
		display: flex;
		flex-direction: column;
		justify-content: center;
		min-width: 0; /* 防止内容溢出 */
	}

	.message-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 10rpx;
	}

	.message-name {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
		flex: 1;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		margin-right: 20rpx;
	}

	.message-time {
		font-size: 24rpx;
		color: #999;
		flex-shrink: 0;
	}

	.message-preview {
		font-size: 26rpx;
		color: #666;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.message-badge {
		position: absolute;
		right: 26rpx;
		top: 82rpx;
		min-width: 36rpx;
		height: 36rpx;
		line-height: 36rpx;
		text-align: center;
		background-color: #ff4d4f;
		color: #fff;
		font-size: 22rpx;
		border-radius: 18rpx;
		padding: 0 8rpx;
	}

	/* 空状态 */
	.empty-state {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 100rpx 0;
	}

	.empty-text {
		font-size: 28rpx;
		color: #999;
	}

	/* 加载状态 */
	.loading-state {
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 40rpx 0;
	}

	.loading-text {
		font-size: 28rpx;
		color: #999;
	}
	
	/* 加载更多状态 */
	.load-more {
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 30rpx 0;
	}
	
	.load-more-text {
		font-size: 26rpx;
		color: #999;
	}
</style>