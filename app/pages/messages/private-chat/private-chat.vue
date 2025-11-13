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
				<view class="date-divider"
					v-if="index === 0 || !isSameDay(message.createTime, messages[index-1].createTime)">
					{{ formatDate(message.createTime) }}
				</view>

				<view :class="['message-item', message.isMe ? 'message-right' : 'message-left']"
					:id="`msg-${message.id}`">
					<image v-if="!message.isMe" class="avatar"
						:src="message.avatar || otherUserAvatar || defaultAvatar" />

					<view class="message-content">
						<view class="message-bubble" :class="{
							'message-error': message.status === 'error',
							'image-message': message.isImage,
							'text-message': !message.isImage
						}" :style="getBubbleStyle(message)" @longpress="showMessageAction(message, index)">

							<template v-if="message.isImage">
								<image class="message-image" :src="message.imageUrl" mode="widthFix"
									@click="previewImage(message.imageUrl)" />
							</template>

							<template v-else>
								<text class="message-text">{{ message.content }}</text>
							</template>

							<view v-if="message.isMe" class="message-status">
								<u-loading-icon v-if="message.status === 'sending'" size="16" color="#ccc" />
								<uni-icons v-else-if="message.status === 'error'" type="refresh" size="16"
									color="#f53f3f" @click="resendMessage(index)" />
							</view>
						</view>

						<view class="message-meta">
							<!-- 显示精确到分钟的时间 -->
							<text class="message-time">{{ formatMessageTime(message.createTime) }}</text>
							<view v-if="message.isMe" class="read-status">
								<uni-icons type="checkmark" size="16" color="rgba(153,153,153,0.8)"
									v-if="!message.read" />
								<uni-icons type="checkmark-double" size="16" color="#1989fa" v-if="message.read" />
							</view>
						</view>
					</view>

					<image v-if="message.isMe" class="avatar self-avatar" :src="myAvatar" />
				</view>
			</template>

			<view class="loading-more" v-if="isLoadingMore">
				<text>加载更多历史消息...</text>
			</view>
		</scroll-view>

		<view class="input-area">
			<view class="input-left">
				<uni-icons type="image" size="28" color="#666" @click="chooseImage" />
				<uni-icons type="smile-o" size="28" color="#666" @click="showEmojiPanel" />
			</view>

			<view class="input-main">
				<input class="input-box" v-model="inputText" placeholder="输入消息..." placeholder-style="color:#999"
					@confirm="sendMessage" @input="onInput" :style="{height: inputHeight}" maxlength="500"
					@focus="onInputFocus" @blur="onInputBlur" />
			</view>

			<view class="input-right">
				<u-button type="primary" size="mini" :disabled="!inputText.trim() || !isConnected" @click="sendMessage"
					class="send-btn">
					{{ isConnected ? '发送' : '连接中' }}
				</u-button>
			</view>
		</view>

		<view class="emoji-panel" v-if="showEmoji">
			<view class="emoji-list">
				<text class="emoji-item" v-for="emoji in emojis" :key="emoji"
					@click="addEmoji(emoji)">{{ emoji }}</text>
			</view>
			<view class="emoji-footer">
				<uni-button type="text" @click="showEmoji = false">完成</uni-button>
			</view>
		</view>

		<uni-popup ref="messagePopup" type="bottom" :mask-click="true">
			<view class="message-menu">
				<view class="menu-item" @click="copyMessage">
					<uni-icons type="copy" size="20" color="#666" />
					<text>复制</text>
				</view>
				<view class="menu-item" @click="forwardMessage" v-if="currentMessage && !currentMessage.isMe">
					<uni-icons type="forward" size="20" color="#666" />
					<text>转发</text>
				</view>
				<view class="menu-item" @click="deleteMessage" v-if="currentMessage && currentMessage.isMe">
					<uni-icons type="trash" size="20" color="#666" />
					<text>删除</text>
				</view>
				<view class="menu-item cancel" @click="closeMessageMenu">
					<text>取消</text>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script lang="ts" setup>
	import { ref, onMounted, onUnmounted, nextTick, computed, getCurrentInstance } from 'vue';
	import { onLoad, onHide, onShow, onUnload } from '@dcloudio/uni-app'
	import { useUserStore } from '@/stores/user'
	import request from '@/utils/request';

	const userStore = useUserStore();
	const userInfo = userStore.info;

	const conversationId = ref('');
	const otherUserId = ref('');
	const otherUserName = ref('');
	const otherUserAvatar = ref('');
	const orderId = ref('');

	const orderInfo = ref(null);

	const messages = ref([]);
	const inputText = ref('');
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
	const showEmoji = ref(false);
	const inputHeight = ref('80rpx');
	const refresherTriggered = ref(false);
	const currentDate = ref('');
	const messagePopup = ref(null);
	const currentMessage = ref(null);
	const currentMessageIndex = ref(-1);
	const scrollViewRef = ref(null);

	// 新增：状态栏和导航栏高度
	const statusBarHeight = ref(0);
	const navbarHeight = ref(0);

	const defaultAvatar = '/static/images/default-avatar.png';
	const myAvatar = userInfo.avatar;
	const emojis = ['😀', '😁', '😂', '🤣', '😃', '😄', '😅', '😆', '😉', '😊', '🙂', '🙃', '😋', '😎', '😍', '😘', '🥰', '😗', '😙', '👍', '👎', '👊', '✌️', '🤝', '🙏', '🎉', '🎊', '🔥', '🥳', '😢', '😭', '😱', '😡', '🤔', '🤫', '🤭', '😴'];

	let hasInitWebSocket = false;
	let scrollTimer = null;

	// 消息气泡样式
	const getBubbleStyle = (message) => {
		if (message.isImage) {
			return {};
		}

		const content = message.content || '';
		const length = content.length;

		let maxWidth = '600rpx';

		if (length <= 5) {
			maxWidth = '200rpx';
		} else if (length <= 10) {
			maxWidth = '300rpx';
		} else if (length <= 20) {
			maxWidth = '400rpx';
		} else if (length <= 30) {
			maxWidth = '500rpx';
		}

		return {
			'max-width': maxWidth,
			'min-width': '120rpx'
		};
	};

	onLoad(async (options) => {
		// 获取状态栏和导航栏高度
		const systemInfo = uni.getSystemInfoSync();
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
		if (!orderId.value) return;
		const res = await request({
			url: `/order/detail?oid=${orderId.value}`,
			method: 'GET'
		});

		if (res.errCode === 0) {
			orderInfo.value = res.data;
		}
	};

	const getStatusClass = (status) => {
		const statusMap = {
			'D': 'status-waiting',
			'J': 'status-processing',
			'S': 'status-completed',
			'C': 'status-cancelled'
		};
		return statusMap[status] || 'status-waiting';
	};

	const getStatusText = (status) => {
		const statusMap = {
			'D': '等待接单',
			'J': '进行中',
			'S': '已完成',
			'C': '已取消'
		};
		return statusMap[status] || '未知状态';
	};

	const formatOrderTime = (time) => {
		if (!time) return '';
		const date = new Date(time);
		return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
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

		const wsUrl = `wss://localhost:8181/api/ws`;

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

	const sendMessage = async () => {
		const content = inputText.value.trim();
		if (!content) return;

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
		inputText.value = '';
		resetInputHeight();

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

	const resendMessage = async (index) => {
		if (index < 0 || index >= messages.value.length) return;

		const message = messages.value[index];
		if (!message.isMe || message.status !== 'error') return;

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

	// 格式化消息时间为"HH:MM"
	const formatMessageTime = (date) => {
		if (!(date instanceof Date)) date = new Date(date);
		const hours = date.getHours().toString().padStart(2, '0');
		const minutes = date.getMinutes().toString().padStart(2, '0');
		return `${hours}:${minutes}`;
	};

	// 格式化日期为"YYYY年MM月DD日 星期X"
	const formatDate = (date) => {
		if (!(date instanceof Date)) date = new Date(date);
		const year = date.getFullYear();
		const month = (date.getMonth() + 1).toString().padStart(2, '0');
		const day = date.getDate().toString().padStart(2, '0');
		const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
		const weekDay = weekDays[date.getDay()];
		return `${year}年${month}月${day}日 ${weekDay}`;
	};

	// 判断两个日期是否为同一天
	const isSameDay = (date1, date2) => {
		if (!(date1 instanceof Date)) date1 = new Date(date1);
		if (!(date2 instanceof Date)) date2 = new Date(date2);
		return date1.getFullYear() === date2.getFullYear() &&
			date1.getMonth() === date2.getMonth() &&
			date1.getDate() === date2.getDate();
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

	const resetInputHeight = () => {
		inputHeight.value = '80rpx';
	};

	const handleBack = () => {
		closeWebSocket();
		uni.navigateBack();
	};

	const onRefresherRefresh = () => {
		refresherTriggered.value = true;
		loadHistoryMessages(true);
	};

	const onInput = (e) => {
		const value = e.detail.value;
		inputText.value = value;
		const lineHeight = 32;
		const maxLines = 5;
		const textLength = value.length;
		const lines = Math.ceil(textLength / 15);
		const newHeight = Math.min(lines * lineHeight + 16, maxLines * lineHeight + 16) + 'rpx';
		inputHeight.value = newHeight;
	};

	const onInputFocus = () => {
		setTimeout(() => {
			scrollToBottom();
		}, 300);
	};

	const onInputBlur = () => { };

	const chooseImage = () => {
		uni.chooseImage({
			count: 1,
			sizeType: ['original', 'compressed'],
			sourceType: ['album', 'camera'],
			success: (res) => {
				const tempCreateTime = new Date();
				const tempMessage = {
					id: 'temp_' + Date.now(),
					senderId: userInfo.uid,
					imageUrl: res.tempFilePaths[0],
					messageType: 2,
					createTime: tempCreateTime,
					status: 2,
					isMe: true,
					isImage: true
				};

				messages.value.push(formatMessage(tempMessage));
				scrollToBottom();
			}
		});
	};

	const previewImage = (url) => {
		uni.previewImage({
			urls: [url]
		});
	};

	const showEmojiPanel = () => {
		showEmoji.value = !showEmoji.value;
	};

	const addEmoji = (emoji) => {
		inputText.value += emoji;
	};

	const showMessageAction = (message, index) => {
		currentMessage.value = message;
		currentMessageIndex.value = index;
		messagePopup.value.open();
	};

	const closeMessageMenu = () => {
		messagePopup.value.close();
		currentMessage.value = null;
		currentMessageIndex.value = -1;
	};

	const copyMessage = () => {
		if (!currentMessage.value) return;
		let text = '';
		if (currentMessage.value.isImage) {
			text = '[图片消息]';
		} else {
			text = currentMessage.value.content || '';
		}
		uni.setClipboardData({
			data: text,
			success: () => {
				uni.showToast({ title: '复制成功', icon: 'none' });
				closeMessageMenu();
			}
		});
	};

	const forwardMessage = () => {
		if (!currentMessage.value) return;
		uni.showToast({ title: '转发功能待实现', icon: 'none' });
		closeMessageMenu();
	};

	const deleteMessage = () => {
		if (!currentMessage.value || currentMessageIndex.value === -1) return;
		messages.value.splice(currentMessageIndex.value, 1);
		closeMessageMenu();
		uni.showToast({ title: '已删除', icon: 'none' });
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
		/* padding: 10rpx; */
		background-color: #f5f5f5;
		overflow-y: auto;
		padding-bottom: 140rpx;
		/* 确保内容从导航栏下方开始 */
		margin-top: 88rpx;
		/* 导航栏高度，可根据实际情况调整 */
	}

	.date-divider {
		text-align: center;
		font-size: 24rpx;
		color: #999;
		margin: 30rpx 0;
		padding: 12rpx 30rpx;
		background-color: rgba(0, 0, 0, 0.05);
		border-radius: 20rpx;
		display: inline-block;
		position: relative;
		left: 50%;
		transform: translateX(-50%);
	}

	.message-item {
		display: flex;
		margin-bottom: 30rpx;
		align-items: flex-start;
	}

	.message-left {
		justify-content: flex-start;
	}

	.message-right {
		justify-content: flex-end;
	}

	.avatar {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
		flex-shrink: 0;
	}

	.message-left .avatar {
		margin-right: 20rpx;
	}

	.message-right .avatar {
		margin-left: 20rpx;
		order: 2;
	}

	.message-content {
		flex: 1;
		display: flex;
		flex-direction: column;
		max-width: 70%;
	}

	.message-right .message-content {
		align-items: flex-end;
	}

	.message-left .message-content {
		align-items: flex-start;
	}

	.message-bubble {
		padding: 20rpx 24rpx;
		border-radius: 20rpx;
		position: relative;
		word-break: break-word;
		min-width: 120rpx;
		width: fit-content;
		max-width: 600rpx;
	}

	.message-left .message-bubble {
		background-color: #fff;
		border-top-left-radius: 8rpx;
		box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
	}

	.message-right .message-bubble {
		background-color: #1989fa;
		border-top-right-radius: 8rpx;
	}

	.message-text {
		font-size: 32rpx;
		line-height: 1.5;
		word-break: break-word;
	}

	.message-left .message-text {
		color: #333;
	}

	.message-right .message-text {
		color: #fff;
	}

	.message-meta {
		display: flex;
		align-items: center;
		margin-top: 8rpx;
		gap: 10rpx;
	}

	.message-time {
		font-size: 22rpx;
		color: #999;
	}

	.read-status {
		display: flex;
		align-items: center;
	}

	.input-area {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		display: flex;
		align-items: flex-end;
		padding: 20rpx 30rpx;
		background-color: #fff;
		border-top: 1px solid #eee;
		gap: 20rpx;
		z-index: 999;
		box-sizing: border-box;
		min-height: 120rpx;
	}

	.input-left {
		display: flex;
		align-items: center;
		gap: 20rpx;
		flex-shrink: 0;
	}

	.input-main {
		flex: 1;
		display: flex;
		align-items: center;
		min-width: 0;
	}

	.input-right {
		display: flex;
		align-items: center;
		flex-shrink: 0;
	}

	.input-box {
		width: 100%;
		padding: 16rpx 24rpx;
		background-color: #f5f5f5;
		border-radius: 40rpx;
		font-size: 28rpx;
		line-height: 1.4;
		height: 80rpx;
		min-height: 80rpx;
		max-height: 200rpx;
		box-sizing: border-box;
	}

	.send-btn {
		height: 70rpx;
		border-radius: 35rpx;
		line-height: 70rpx;
		padding: 0 30rpx;
		min-width: 120rpx;
		flex-shrink: 0;
		margin: 0;
	}

	.emoji-panel {
		background-color: #fff;
		border-top: 1px solid #eee;
		padding: 20rpx;
		position: fixed;
		bottom: 120rpx;
		left: 0;
		right: 0;
		z-index: 998;
		max-height: 400rpx;
	}

	.emoji-list {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
		max-height: 300rpx;
		overflow-y: auto;
	}

	.emoji-item {
		font-size: 40rpx;
		padding: 10rpx;
		border-radius: 8rpx;
		line-height: 1;
	}

	.emoji-item:active {
		background-color: #f1f1f1;
	}

	.emoji-footer {
		display: flex;
		justify-content: flex-end;
		margin-top: 20rpx;
		padding-top: 20rpx;
		border-top: 1px solid #eee;
	}

	.loading-more {
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 20rpx 0;
		font-size: 24rpx;
		color: #999;
	}

	.message-menu {
		background-color: #fff;
		border-radius: 16rpx 16rpx 0 0;
		padding: 20rpx 0;
	}

	.menu-item {
		display: flex;
		align-items: center;
		justify-content: center;
		height: 100rpx;
		border-bottom: 1px solid #eee;
		font-size: 28rpx;
		gap: 15rpx;
	}

	.menu-item.cancel {
		color: #f53f3f;
		margin-top: 15rpx;
		border-bottom: none;
		border-top: 1px solid #eee;
	}

	.message-status {
		position: absolute;
		right: -40rpx;
		bottom: 10rpx;
		width: 32rpx;
		height: 32rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.message-error .message-status {
		right: -35rpx;
	}

	.message-error {
		background-color: #fff3f3;
		border: 1px solid #ffcdcd;
	}

	.message-error .message-text {
		color: #f53f3f;
	}

	.message-image {
		max-width: 300rpx;
		border-radius: 8rpx;
		display: block;
	}

	.image-message {
		padding: 10rpx;
		background: transparent !important;
	}

	/* 修复滚动条样式 */
	.chat-content ::-webkit-scrollbar {
		width: 0;
		height: 0;
		color: transparent;
	}

	/* 确保图标按钮有合适的点击区域 */
	.input-left .uni-icons {
		padding: 10rpx;
	}
</style>