<template>
  <view :class="['message-item', message.isMe ? 'message-right' : 'message-left']" :id="`msg-${message.id}`">
    <image v-if="!message.isMe" class="avatar" :src="message.avatar" />
    
    <view class="message-content">
      <view class="message-bubble" :class="{
        'message-error': message.status === 'error',
        'image-message': message.isImage,
        'text-message': !message.isImage
      }" :style="getBubbleStyle(message)" @longpress="showMessageAction(message)">
        
        <template v-if="message.isImage">
          <image class="message-image" :src="message.imageUrl" mode="widthFix" @click="previewImage(message.imageUrl)" />
        </template>
        
        <template v-else>
          <text class="message-text">{{ message.content }}</text>
        </template>
        
        <view v-if="message.isMe" class="message-status">
          <u-loading-icon v-if="message.status === 'sending'" size="16" color="#ccc" />
          <uni-icons v-else-if="message.status === 'error'" type="refresh" size="16" color="#f53f3f" @click="resendMessage(message)" />
        </view>
      </view>
      
      <view class="message-meta">
        <text class="message-time">{{ message.time }}</text>
        <view v-if="message.isMe" class="read-status">
          <uni-icons type="checkmark" size="16" color="rgba(153,153,153,0.8)" v-if="!message.read" />
          <uni-icons type="checkmark-double" size="16" color="#1989fa" v-if="message.read" />
        </view>
      </view>
    </view>
    
    <image v-if="message.isMe" class="avatar self-avatar" :src="message.avatar" />
  </view>
</template>

<script lang="ts" setup>
  import { computed } from 'vue';

  const props = defineProps({
    message: {
      type: Object,
      required: true
    }
  });

  const emit = defineEmits(['showMessageAction', 'resendMessage', 'previewImage']);

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

  const showMessageAction = (message) => {
    emit('showMessageAction', message);
  };

  const resendMessage = (message) => {
    emit('resendMessage', message);
  };

  const previewImage = (imageUrl) => {
    emit('previewImage', imageUrl);
  };
</script>

<style scoped>
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

  .message-image {
    width: 300rpx;
    height: auto;
    border-radius: 12rpx;
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

  .message-status {
    margin-left: 10rpx;
    display: inline-flex;
    align-items: center;
  }
</style>