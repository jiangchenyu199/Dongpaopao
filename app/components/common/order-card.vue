<template>
  <view class="order-card" @click="handleClick">
    <view class="order-header">
      <text class="order-title">{{ title }}</text>
    </view>
    <text class="order-desc">{{ description }}</text>
    <view class="order-footer">
    <text class="order-time" v-if="showTime">{{ timeLabel }}{{ time }}</text>
    <text class="order-price" v-if="showPrice">¥{{ price }}</text>
  </view>
  </view>
</template>

<script lang="ts" setup>
  import { defineProps, defineEmits, computed } from 'vue';

  interface Order {
    type_name?: string;
    typeName?: string;
    detail?: string;
    amount?: number | string;
    expect_time?: string;
    createTime?: string;
  }

  const props = defineProps<{
    order: Order;
    showTime?: boolean;
    showPrice?: boolean;
    timeLabel?: string;
  }>();

  const emit = defineEmits<{
    (e: 'click', order: Order): void;
  }>();

  const handleClick = () => {
    emit('click', props.order);
  };

  const title = computed(() => {
    return props.order.type_name || props.order.typeName || '订单';
  });

  const description = computed(() => {
    if (props.order.detail) {
      try {
        const detailObj = JSON.parse(props.order.detail);
        if (detailObj.code) {
          return `取件码：${detailObj.code}`;
        }
        if (detailObj.remark) {
          return `订单备注：${detailObj.remark || '无'}`;
        }
      } catch (e) {
        return props.order.detail;
      }
    }
    return '订单详情';
  });

  const price = computed(() => {
    return props.order.amount || 0;
  });

  const time = computed(() => {
    return props.order.expect_time || props.order.createTime || '';
  });
</script>

<style scoped>
  .order-card {
    background-color: #f9f9f9;
    border-radius: 12rpx;
    padding: 25rpx;
    position: relative;
    margin-bottom: 20rpx;
  }

  .order-card:last-child {
    margin-bottom: 0;
  }

  .order-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 15rpx;
  }

  .order-title {
    font-size: 28rpx;
    font-weight: bold;
    color: #333;
  }

  .order-desc {
    font-size: 26rpx;
    color: #666;
    margin-bottom: 20rpx;
    display: block;
  }

  .order-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .order-time {
    font-size: 24rpx;
    color: #999;
  }

  .order-price {
    font-size: 28rpx;
    font-weight: bold;
    color: #1a73e8;
  }
</style>