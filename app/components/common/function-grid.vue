<template>
  <view class="function-grid">
    <view class="function-item" v-for="item in items" :key="item.orderTypeId"
      @click="handleItemClick(item)" :class="{ 'disabled': !item.enabled }">
      <view class="icon-wrapper" :style="{ backgroundColor: item.bgColor }">
        <text class="function-emoji">{{ item.emojiIcon }}</text>
      </view>
      <text class="function-text">{{ item.typeName }}</text>
    </view>
  </view>
</template>

<script lang="ts" setup>
  import { defineProps, defineEmits } from 'vue';

  interface FunctionGridItem {
    orderTypeId: string;
    typeName: string;
    emojiIcon: string;
    bgColor: string;
  }

  const props = defineProps<{
    items: FunctionGridItem[];
  }>();

  const emit = defineEmits<{
    (e: 'itemClick', item: FunctionGridItem): void;
  }>();

  const handleItemClick = (item: FunctionGridItem) => {
    emit('itemClick', item);
  };
</script>

<style scoped>
  .function-grid {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    padding: 20rpx;
    background-color: #fff;
    border-radius: 16rpx;
    box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  }

  .function-item {
    width: 20%;
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 30rpx;
    transition: all 0.3s ease;
  }

  .function-item:active {
    transform: scale(0.95);
  }

  .function-item.disabled {
    opacity: 0.6;
  }

  .icon-wrapper {
    width: 120rpx;
    height: 120rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 12rpx;
    border-radius: 20rpx;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
  }

  .function-emoji {
    font-size: 60rpx;
    line-height: 1;
    color: #333;
  }

  .function-text {
    font-size: 26rpx;
    color: #333;
    text-align: center;
    width: 100%;
    font-weight: 500;
  }

  .function-item.disabled .function-text {
    color: #999;
  }
</style>