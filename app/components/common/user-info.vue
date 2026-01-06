<template>
  <view class="user-info" @click="handleClick" :style="{ cursor: clickable ? 'pointer' : 'default' }">
    <view class="user-avatar">
      <u-avatar size="60" class="avatar-image" :src="user.avatar" mode="aspectFill"/>
    </view>
    <view class="user-detail">
      <text class="user-name">{{ user.nickname }}</text>
      <text class="user-id">{{ user.uid }}</text>
    </view>
    <uni-icons v-if="clickable" type="arrowright" size="20" color="#999"/>
  </view>
</template>

<script lang="ts" setup>
  import { defineProps, defineEmits } from 'vue';

  interface UserInfo {
    uid: string;
    nickname: string;
    avatar: string;
  }

  const props = defineProps<{
    user: UserInfo;
    clickable?: boolean;
  }>();

  const emit = defineEmits<{
    (e: 'click', user: UserInfo): void;
  }>();

  const handleClick = () => {
    if (props.clickable !== false) {
      emit('click', props.user);
    }
  };
</script>

<style scoped>
  .user-info {
    display: flex;
    align-items: center;
    padding: 40rpx 30rpx;
    background-color: #fff;
    margin-bottom: 20rpx;
  }

  .user-avatar {
    width: 120rpx;
    height: 120rpx;
    margin-right: 30rpx;
    flex-shrink: 0;
  }

  .avatar-image {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    background-color: #f0f0f0;
  }

  .user-detail {
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  .user-name {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 10rpx;
  }

  .user-id {
    font-size: 24rpx;
    color: #999;
  }
</style>