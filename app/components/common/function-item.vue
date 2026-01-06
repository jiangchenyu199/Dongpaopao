<template>
  <view class="function-item" @click="handleClick">
    <uni-icons :type="item.icon" size="24" color="#666"/>
    <text class="function-name">{{ item.name }}</text>
    <uni-icons type="arrowright" size="20" color="#999"/>
  </view>
</template>

<script lang="ts" setup>
  import { defineProps, defineEmits } from 'vue';

  interface FunctionItem {
    icon: string;
    name: string;
    id?: string;
    link?: string;
  }

  const props = defineProps<{
    item: FunctionItem;
  }>();

  const emit = defineEmits<{
    (e: 'click', item: FunctionItem): void;
  }>();

  const handleClick = () => {
    emit('click', props.item);
  };
</script>

<style scoped>
  .function-item {
    display: flex;
    align-items: center;
    padding: 30rpx;
    background-color: #fff;
    margin-bottom: 1rpx;
  }

  .function-name {
    flex: 1;
    font-size: 28rpx;
    color: #333;
    margin: 0 20rpx;
  }
</style>