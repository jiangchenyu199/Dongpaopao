<template>
  <view class="user-info user-info--card" @click="handleClick" :style="{ cursor: clickable ? 'pointer' : 'default' }">
    <view class="user-info__avatar-wrap">
      <u-avatar size="120" class="user-info__avatar" :src="user.avatar" mode="aspectFill"/>
    </view>
    <text class="user-info__name">{{ user.nickname }}</text>
    <text class="user-info__uid">ID {{ user.uid }}</text>
    <uni-icons v-if="clickable" class="user-info__arrow" type="arrowright" size="18" color="#999"/>
  </view>
</template>

<script lang="ts" setup>
  import { defineProps, defineEmits, withDefaults } from 'vue';

  interface UserInfo {
    uid: string;
    nickname: string;
    avatar: string;
  }

  const props = withDefaults(defineProps<{
    user: UserInfo;
    clickable?: boolean;
  }>(), {
    clickable: true
  });

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
    -webkit-tap-highlight-color: transparent;
    tap-highlight-color: transparent;
  }

  .user-info--card {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 48rpx 32rpx 40rpx;
    margin: 20rpx 20rpx 24rpx;
    background-color: #fff;
    border-radius: 20rpx;
    position: relative;
  }

  .user-info__avatar-wrap {
    margin-bottom: 24rpx;
  }

  .user-info__avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 50%;
    background-color: #f0f0f0;
  }

  .user-info__name {
    font-size: 36rpx;
    font-weight: bold;
    color: #1a1a1a;
    margin-bottom: 8rpx;
  }

  .user-info__uid {
    font-size: 24rpx;
    color: #999;
    margin-bottom: 16rpx;
  }

  .user-info__arrow {
    position: absolute;
    right: 24rpx;
    top: 50%;
    transform: translateY(-50%);
  }
</style>