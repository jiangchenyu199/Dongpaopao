<template>
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
</template>

<script lang="ts" setup>
  import { ref, watch } from 'vue';

  const props = defineProps({
    currentMessage: {
      type: Object,
      default: null
    }
  });

  const emit = defineEmits(['copyMessage', 'forwardMessage', 'deleteMessage', 'close']);

  const messagePopup = ref(null);

  const openMenu = () => {
    messagePopup.value.open();
  };

  const closeMessageMenu = () => {
    messagePopup.value.close();
    emit('close');
  };

  const copyMessage = () => {
    if (!props.currentMessage) return;
    emit('copyMessage', props.currentMessage);
    closeMessageMenu();
  };

  const forwardMessage = () => {
    if (!props.currentMessage) return;
    emit('forwardMessage', props.currentMessage);
    closeMessageMenu();
  };

  const deleteMessage = () => {
    if (!props.currentMessage) return;
    emit('deleteMessage', props.currentMessage);
    closeMessageMenu();
  };

  // Expose methods to parent component
  defineExpose({
    open: openMenu,
    close: closeMessageMenu
  });
</script>

<style scoped>
  .message-menu {
    background-color: #fff;
    border-radius: 20rpx 20rpx 0 0;
    padding: 20rpx;
  }

  .menu-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 20rpx;
    margin-bottom: 10rpx;
    border-radius: 12rpx;
  }

  .menu-item:hover {
    background-color: #f5f5f5;
  }

  .menu-item.cancel {
    margin-top: 20rpx;
    background-color: #f5f5f5;
  }

  .menu-item text {
    margin-top: 8rpx;
    font-size: 28rpx;
    color: #333;
  }

  .menu-item.cancel text {
    color: #666;
  }
</style>