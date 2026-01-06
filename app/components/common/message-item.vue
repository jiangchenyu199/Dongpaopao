<template>
  <view class="message-item" @click="handleClick">
    <view class="message-avatar">
      <image class="avatar-image" :src="avatar" mode="aspectFill"></image>
    </view>
    <view class="message-content">
      <view class="message-header">
        <text class="message-name">{{ name }}</text>
        <text class="message-time">{{ formattedTime }}</text>
      </view>
      <text class="message-preview">{{ preview }}</text>
      <view class="message-badge" v-if="unreadCount > 0">{{ unreadCount > 99 ? '99+' : unreadCount }}</view>
    </view>
  </view>
</template>

<script lang="ts" setup>
  import { defineProps, defineEmits, computed } from 'vue';

  interface Conversation {
    otherUserAvatar?: string;
    otherUserNickname?: string;
    unreadCount?: number;
    lastMessageTime?: string | Date;
    lastMessageContent?: string;
    orderType?: string;
    orderStatus?: string;
  }

  const props = defineProps<{
    conversation: Conversation;
  }>();

  const emit = defineEmits<{
    (e: 'click', conversation: Conversation): void;
  }>();

  const handleClick = () => {
    emit('click', props.conversation);
  };

  const ORDER_TYPE_MAP: Record<string, string> = {
    'E': '快递代取',
    'T': '外卖代取', 
    'C': '物品搬运',
    'P': '商品代购'
  };

  const avatar = computed(() => {
    return props.conversation.otherUserAvatar || '/static/images/default-avatar.png';
  });

  const name = computed(() => {
    return props.conversation.otherUserNickname || '用户';
  });

  const unreadCount = computed(() => {
    return props.conversation.unreadCount || 0;
  });

  const formattedTime = computed(() => {
    return formatTime(props.conversation.lastMessageTime);
  });

  const preview = computed(() => {
    if (props.conversation.lastMessageContent) {
      return props.conversation.lastMessageContent;
    }
    
    const orderTypeText = ORDER_TYPE_MAP[props.conversation.orderType || ''] || '订单';
    
    if (props.conversation.orderStatus === 'D') {
      return `${orderTypeText}等待接单中`;
    } else if (props.conversation.orderStatus === 'J') {
      return `${orderTypeText}进行中`;
    } else if (props.conversation.orderStatus === 'S') {
      return `${orderTypeText}已完成`;
    } else {
      return `您有一个${orderTypeText}订单`;
    }
  });

  const formatTime = (time?: string | Date) => {
    if (!time) return '';
    
    const date = new Date(time);
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor(diff / (1000 * 60 * 60));
    const minutes = Math.floor(diff / (1000 * 60));
    
    if (minutes < 1) {
      return '刚刚';
    } else if (hours < 1) {
      return `${minutes}分钟前`;
    } else if (days === 0) {
      return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    } else if (days === 1) {
      return '昨天';
    } else if (days < 7) {
      return `${days}天前`;
    } else {
      return `${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
    }
  };
</script>

<style scoped>
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
    min-width: 0;
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
</style>