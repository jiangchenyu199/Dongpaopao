<template>
	<view class="chat-container">
		<view :style="{ paddingTop: statusBarHeight + 'px' }" class="chat-header">
			<u-navbar leftIcon="arrow-left" leftText="返回" :autoBack="true" @leftClick="handleBack">
				<template #center>
					<view style="display: flex; align-items: center;">
						<text>{{ otherUserName }}</text>
						<u-badge isDot :type="isOnline && isConnected? 'success' : 'error'" style="margin-left: 4px;" />
					</view>
				</template>
			</u-navbar>
		</view>

		<scroll-view class="chat-content" scroll-y :scroll-top="scrollTop" :refresher-triggered="refresherTriggered"
			@refresherrefresh="onRefresherRefresh" @scrolltolower="loadMoreMessages" ref="scrollViewRef"
			:style="{ paddingTop: navbarHeight + 'px' }">

			<!-- 订单信息 -->

			<!-- 动态日期分隔线：根据消息时间自动插入 -->
			<template v-for="(message, index) in messages" :key="message.id">
				<!-- 只在第一条消息或跨天消息前显示日期 -->
				<chat-date-divider v-if="index === 0 || !isSameDay(message.createTime, messages[index-1].createTime)"
					:date="message.createTime" />

				<chat-message-bubble :message="message" @showMessageAction="showMessageAction"
					@resendMessage="resendMessage" @previewImage="previewImage" />
			</template>

			<view class="loading-more" v-if="isLoadingMore">
				<text>加载更多历史消息...</text>
			</view>
		</scroll-view>

		<chat-input :isConnected="isConnected" @sendMessage="sendMessage" />

		<chat-message-menu ref="messageMenuRef" :currentMessage="currentMessage" @copyMessage="copyMessage"
			@forwardMessage="forwardMessage" @deleteMessage="deleteMessage" @close="closeMessageMenu" />
	</view>
</template>

<script lang="ts" setup>
	import { ref, onMounted, onUnmounted, nextTick, computed, getCurrentInstance } from 'vue';
import { onLoad, onHide, onShow, onUnload } from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request';
import { getWebSocketUrl } from '@/config/index.js';
import { formatMessageTime, formatDate, isSameDay } from '@/utils/tools';
import ChatMessageBubble from '@/components/chat/message-bubble.vue';
import ChatInput from '@/components/chat/chat-input.vue';
import ChatDateDivider from '@/components/chat/date-divider.vue';
import ChatMessageMenu from '@/components/chat/message-menu.vue';

	const userStore = useUserStore();
	const userInfo = userStore.info;

	const conversationId = ref('');
	const otherUserId = ref('');
	const otherUserName = ref('');
	const otherUserAvatar = ref('');
	const orderId = ref('');

	const orderInfo = ref(null);

	const messages = ref([]);
	const isOnline = ref(true);
	const isLoadingMore = ref(false);
	const hasMore = ref(true);
	const page = ref(1);
	const pageSize = 20;

	const socketTask = ref(null);
	const isConnected = ref(false);
	const reconnectTimer = ref(null);
	const reconnectCount = ref(0);
	const maxReconnectCount = 5;

	const scrollTop = ref(0);
	const refresherTriggered = ref(false);
	const currentMessage = ref(null);
	const currentMessageIndex = ref(-1);
	const scrollViewRef = ref(null);
	const messageMenuRef = ref(null);

	// 新增：状态栏和导航栏高度
	const statusBarHeight = ref(0);
	const navbarHeight = ref(0);

	const defaultAvatar = '/static/images/default-avatar.png';
	const myAvatar = userInfo.avatar;

	let hasInitWebSocket = false;
	let scrollTimer = null;

	onLoad(async (options) => {
		// 获取状态栏和导航栏高度
		const systemInfo = uni.getWindowInfo();
		statusBarHeight.value = systemInfo.statusBarHeight || 0;
		navbarHeight.value = 44; // 标准导航栏高度，可根据实际情况调整

		conversationId.value = options.conversationId || '';
		otherUserId.value = options.otherUserId || '';
		otherUserName.value = decodeURIComponent(options.otherUserName || '用户');
		otherUserAvatar.value = decodeURIComponent(options.otherUserAvatar || '');
		orderId.value = options.orderId || '';

		await loadOrderInfo();
		await loadHistoryMessages();

		if (!hasInitWebSocket) {
			initWebSocket();
			hasInitWebSocket = true;
		}
	});

	onShow(() => {
		if (!isConnected.value && !hasInitWebSocket) {
			initWebSocket();
			hasInitWebSocket = true;
		}
	});

	onHide(() => {
		closeWebSocket();
		hasInitWebSocket = false;
	});

	onUnload(() => {
		closeWebSocket();
		hasInitWebSocket = false;
	});

	const loadOrderInfo = async () => {
		if (!orderId.value || orderId.value === 'undefined') return;
		const res = await request({
			url: `/order/detail?oid=${orderId.value}`,
			method: 'GET'
		});

		if (res.errCode === 0) {
			orderInfo.value = res.data;
		}
	};

	const loadHistoryMessages = async (loadMore = false) => {
		if (loadMore) {
			if (!hasMore.value || isLoadingMore.value) return;
			isLoadingMore.value = true;
			page.value += 1;
		} else {
			page.value = 1;
			messages.value = [];
		}

		try {
			const res = await request({
				url: `/message/history?cid=` + conversationId.value + `&page=${page.value}&pageSize=${pageSize}`,
				method: 'GET'
			});

			if (res.errCode === 0) {
				const newMessages = res.data || [];
				const formattedMessages = newMessages.map(msg => formatMessage(msg));

				if (loadMore) {
					messages.value = [...formattedMessages, ...messages.value];
				} else {
					messages.value = formattedMessages;
					// 等待DOM更新后滚动到底部
					await nextTick();
					setTimeout(() => {
						scrollToBottom();
					}, 300);
				}

				hasMore.value = formattedMessages.length >= pageSize;
			} else {
				uni.showToast({
					title: res.msg || '加载消息失败',
					icon: 'none'
				});
			}
		} catch (error) {
			uni.showToast({
				title: '加载消息失败',
				icon: 'none'
			});
		} finally {
			isLoadingMore.value = false;
			refresherTriggered.value = false;
		}
	};

	// 格式化消息
	const formatMessage = (msg) => {
		const isMe = msg.sender === userInfo.uid;
		const messageType = msg.type || 'TEXT';
		// 处理时间格式，确保是Date对象
		let msgCreateTime;
		if (msg.sendTime) {
			if (typeof msg.sendTime === 'string' && msg.sendTime.includes('T')) {
				msgCreateTime = new Date(msg.sendTime.replace('T', ' '));
			} else {
				msgCreateTime = new Date(msg.sendTime);
			}
		} else {
			msgCreateTime = new Date();
		}

		return {
			id: msg.mid || msg.id,
			content: msg.content,
			time: formatMessageTime(msgCreateTime),
			isMe: isMe,
			avatar: isMe ? myAvatar : otherUserAvatar.value,
			status: msg.status === 'SENT' ? 'sent' : (msg.status === 'sending' ? 'sending' : 'error'),
			read: msg.readTime !== null || (isMe && msg.status === 'SENT'),
			messageType: messageType,
			isImage: messageType === 'IMAGE',
			imageUrl: msg.fileUrl || '',
			createTime: msgCreateTime
		};
	};

	const initWebSocket = () => {
		if (!userInfo?.uid || !conversationId.value || socketTask.value) {
			return;
		}

		const wsUrl = getWebSocketUrl('/ws');

		socketTask.value = uni.connectSocket({
			url: wsUrl,
			success: () => {
				isConnected.value = true;
				reconnectCount.value = 0;
				sendJoinMessage();
			},
			fail: () => {
				isConnected.value = false;
				handleReconnect();
			}
		});

		socketTask.value.onOpen(() => {
			isConnected.value = true;
			reconnectCount.value = 0;
			sendJoinMessage();
		});

		socketTask.value.onMessage((res) => {
			handleWebSocketMessage(res.data);
		});

		socketTask.value.onClose(() => {
			isConnected.value = false;
			handleReconnect();
		});

		socketTask.value.onError(() => {
			isConnected.value = false;
			handleReconnect();
		});
	};

	const handleWebSocketMessage = (data) => {
		const message = typeof data === 'string' ? JSON.parse(data) : data;
		switch (message.type) {
			case 'CHAT':
				handleChatMessage(message);
				break;
			case 'JOIN':
				handleUserJoin(message);
				break;
			case 'READ':
				handleMessageRead(message);
				break;
			case 'TYPING':
				handleTyping(message);
				break;
			case 'ONLINE':
				handleOnlineStatus(message);
				break;
			default:
				handleChatMessage(message);
				break;
		}
	};

	const handleChatMessage = (message) => {
		const msgCreateTime = message.timestamp ? new Date(message.timestamp) : new Date();
		const formattedMessage = formatMessage({
			mid: message.id || message.messageId || Date.now(),
			sender: message.senderId || message.sender,
			content: message.content,
			sendTime: msgCreateTime,
			type: message.messageType === 1 ? 'TEXT' : 'IMAGE',
			status: 'SENT',
			fileUrl: message.fileUrl
		});

		const existingIndex = messages.value.findIndex(msg =>
			msg.id === formattedMessage.id ||
			(msg.content === formattedMessage.content &&
				Math.abs(new Date(msg.createTime).getTime() - new Date(formattedMessage.createTime).getTime()) < 5000)
		);

		if (existingIndex === -1) {
			messages.value.push(formattedMessage);
			nextTick(() => {
				scrollToBottom();
			});

			if (formattedMessage.isMe === false) {
				uni.vibrateShort();
				sendReadReceipt(formattedMessage.id);
			}
		} else if (messages.value[existingIndex].status === 'sending') {
			messages.value[existingIndex].status = 'sent';
			messages.value[existingIndex].read = true;
			messages.value[existingIndex].id = formattedMessage.id;
		}
	};

	const sendJoinMessage = () => {
		if (!isConnected.value) return;

		const joinMessage = {
			type: 'JOIN',
			conversationId: conversationId.value,
			userId: userInfo.uid,
			timestamp: Date.now()
		};

		sendWebSocketMessage(joinMessage);
	};

	const sendWebSocketMessage = (message) => {
		if (!isConnected.value || !socketTask.value) {
			return false;
		}

		socketTask.value.send({
			data: JSON.stringify(message)
		});
		return true;

	};

	// 修复滚动到底部功能 - 简单有效的方法
	const scrollToBottom = () => {
		if (scrollTimer) {
			clearTimeout(scrollTimer);
		}

		scrollTimer = setTimeout(() => {
			scrollTop.value = 9999999;
			// 双重保险，再设置一次
			setTimeout(() => {
				scrollTop.value = 9999999;
			}, 50);
		}, 100);
	};

	const sendMessage = async (content) => {
		if (!content.trim()) return;

		if (!isConnected.value) {
			await sendMessageByHttp(content);
			return;
		}

		const tempId = 'temp_' + Date.now();
		const tempCreateTime = new Date();
		const tempMessage = {
			id: tempId,
			content: content,
			time: formatMessageTime(tempCreateTime),
			isMe: true,
			avatar: myAvatar,
			status: 'sending',
			read: false,
			isImage: false,
			createTime: tempCreateTime
		};

		messages.value.push(tempMessage);

		// 立即滚动到底部
		scrollToBottom();

		const chatMessage = {
			type: 'TEXT',
			cid: conversationId.value,
			sender: userInfo.uid,
			receiver: otherUserId.value,
			content: content,
			messageType: 'TEXT',
			timestamp: Date.now()
		};

		const success = sendWebSocketMessage(chatMessage);
		if (!success) {
			updateMessageStatus(tempId, 'error');
			await sendMessageByHttp(content, tempId);
		}
	};

	const sendMessageByHttp = async (content, tempId = null) => {
		try {
			const res = await request({
				url: '/message/send',
				method: 'POST',
				data: {
					cid: conversationId.value,
					sender: userInfo.uid,
					receiver: otherUserId.value,
					type: 'TEXT',
					content: content
				}
			});

			if (res.errCode === 0) {
				if (tempId) {
					updateMessageStatus(tempId, 'sent');
					const tempIndex = messages.value.findIndex(msg => msg.id === tempId);
					if (tempIndex !== -1) {
						messages.value[tempIndex].createTime = new Date();
					}
					scrollToBottom();
				} else {
					const newMessage = formatMessage({
						mid: res.data?.messageId || Date.now(),
						content: content,
						sender: userInfo.uid,
						sendTime: new Date(),
						type: 'TEXT',
						status: 'SENT'
					});
					messages.value.push(newMessage);
					scrollToBottom();
				}

				uni.showToast({
					title: '发送成功',
					icon: 'success'
				});
			} else {
				throw new Error(res.msg || '发送失败');
			}
		} catch (error) {
			if (tempId) {
				updateMessageStatus(tempId, 'error');
			}
			uni.showToast({
				title: '发送失败，请检查网络',
				icon: 'none'
			});
		}
	};

	const resendMessage = async (message) => {
		if (!message.isMe || message.status !== 'error') return;

		const index = messages.value.findIndex(msg => msg.id === message.id);
		if (index === -1) return;

		messages.value[index].status = 'sending';
		await sendMessageByHttp(message.content, message.id);
	};

	const sendReadReceipt = (messageId) => {
		if (!isConnected.value || !messageId) return;

		const readMessage = {
			type: 'READ',
			conversationId: conversationId.value,
			userId: userInfo.uid,
			messageId: messageId,
			timestamp: Date.now()
		};

		sendWebSocketMessage(readMessage);
	};

	const closeWebSocket = () => {
		if (reconnectTimer.value) {
			clearTimeout(reconnectTimer.value);
			reconnectTimer.value = null;
		}

		if (socketTask.value) {
			socketTask.value.close();
			socketTask.value = null;
		}

		isConnected.value = false;
	};

	const handleReconnect = () => {
		if (reconnectCount.value >= maxReconnectCount) {
			uni.showToast({
				title: '连接已断开',
				icon: 'none'
			});
			return;
		}

		reconnectCount.value++;
		const delay = Math.min(2000 * reconnectCount.value, 10000);

		reconnectTimer.value = setTimeout(() => {
			initWebSocket();
		}, delay);
	};

	const updateMessageStatus = (messageId, status) => {
		const index = messages.value.findIndex(msg => msg.id === messageId);
		if (index !== -1) {
			messages.value[index].status = status;
			if (status === 'sent') {
				messages.value[index].read = true;
			}
		}
	};

	const handleBack = () => {
		closeWebSocket();
		uni.navigateBack();
	};

	const onRefresherRefresh = () => {
		refresherTriggered.value = true;
		loadHistoryMessages(true);
	};

	const loadMoreMessages = () => {
		loadHistoryMessages(true);
	};



	const previewImage = (url) => {
		uni.previewImage({
			urls: [url]
		});
	};

	const showMessageAction = (message) => {
		currentMessage.value = message;
		currentMessageIndex.value = messages.value.findIndex(msg => msg.id === message.id);
		messageMenuRef.value.open();
	};

	const closeMessageMenu = () => {
		currentMessage.value = null;
		currentMessageIndex.value = -1;
	};

	const copyMessage = (message) => {
		if (!message) return;
		let text = '';
		if (message.isImage) {
			text = '[图片消息]';
		} else {
			text = message.content || '';
		}
		uni.setClipboardData({
			data: text,
			success: () => {
				uni.showToast({ title: '复制成功', icon: 'none' });
			}
		});
	};

	const forwardMessage = (message) => {
		if (!message) return;
		uni.showToast({ title: '转发功能待实现', icon: 'none' });
	};

	const deleteMessage = (message) => {
		if (!message) return;
		const index = messages.value.findIndex(msg => msg.id === message.id);
		if (index !== -1) {
			messages.value.splice(index, 1);
			uni.showToast({ title: '已删除', icon: 'none' });
		}
	};

	const handleUserJoin = (message) => {
		console.log('用户加入:', message);
	};

	const handleMessageRead = (message) => {
		const index = messages.value.findIndex(msg => msg.id === message.messageId);
		if (index !== -1) {
			messages.value[index].read = true;
		}
	};

	const handleTyping = (message) => {
		console.log('对方正在输入:', message);
	};

	const handleOnlineStatus = (message) => {
		isOnline.value = message.online || false;
	};
</script>

<style scoped>
	.chat-container {
		display: flex;
		flex-direction: column;
		height: 100vh;
		background-color: #f5f5f5;
	}

	.chat-header {
		flex-shrink: 0;
		background-color: #fff;
		border-bottom: 1px solid #eee;
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		z-index: 1000;
	}

	.chat-content {
		flex: 1;
		background-color: #f5f5f5;
		overflow-y: auto;
		padding-bottom: 140rpx;
		/* 确保内容从导航栏下方开始 */
		margin-top: 88rpx;
		/* 导航栏高度，可根据实际情况调整 */
	}

	.loading-more {
		text-align: center;
		padding: 20rpx;
		font-size: 28rpx;
		color: #999;
	}
</style>