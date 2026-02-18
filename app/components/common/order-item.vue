<template>
  <view class="order-item" @click="handleClick">
    <view class="order-header">
      <view class="order-header-left">
        <text class="order-id">订单号：{{ order.oid }}</text>
      </view>
      <text class="order-status" :style="{ color: statusColor }">
        {{ statusText }}
      </text>
    </view>
    <view class="order-content">
      <view class="order-info">
        <text class="order-title">{{ orderTypeText }}</text>
        <text class="order-desc">{{ orderDesc }}</text>
        <text class="order-price">¥{{ order.amount }}</text>
      </view>
    </view>
    <view class="order-footer">
      <text class="order-time">
        <text v-if="order.status === 'S'">
          完成时间：{{ formatTimeDisplay(order.completeTime) }}
        </text>
        <text v-else-if="order.status === 'C'">
          已取消
        </text>
        <text v-else>
          期望送达时间：{{ formatTimeDisplay(order.expectTime) }}
        </text>
      </text>
      <view class="order-actions">
        <uni-icons type="arrow-right" size="20" color="#999" />
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import { defineProps, defineEmits, computed } from 'vue';
import { formatTimeDisplay } from '@/utils/date.js';

interface OrderDetail {
  remark?: string;
  company?: string;
  location?: string;
  code?: string;
  description?: string;
  weight?: string;
}


interface Order {
  oid: string;
  role: string;
  status: string;
  orderType?: string;
  typeName?: string;
  statusName?: string;
  statusColor?: string;
  detailObj?: OrderDetail;
  amount: number;
  completeTime?: string | Date;
  expectTime?: string | Date;
}


const props = defineProps<{
  order: Order;
}>();

const emit = defineEmits<{
  (e: 'click', order: Order): void;
}>();

const handleClick = () => {
  emit('click', props.order);
};

const statusColor = computed(() => {
    return props.order.statusColor || '#ff9500';
  });
const statusText = computed(() => {
    return props.order.statusName || '待接单';
  });

const orderTypeText = computed(() => {
  return props.order.typeName || '其他服务';
});

const orderDesc = computed(() => {
  const { detailObj } = props.order;

  if (detailObj && detailObj.remark && detailObj.remark !== '""' && detailObj.remark !== '') {
    return detailObj.remark;
  }

  return '无备注信息';
});

</script>

<style scoped>
.order-item {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 25rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.order-header-left {
  display: flex;
  align-items: center;
}

.order-id {
  font-size: 24rpx;
  color: #999;
  margin-right: 15rpx;
}

.order-role-tag {
  font-size: 20rpx;
  padding: 4rpx 12rpx;
  border-radius: 12rpx;
  color: #fff;
}

.sender-tag {
  background-color: #1a73e8;
}

.receiver-tag {
  background-color: #4caf50;
}

.order-status {
  font-size: 26rpx;
  font-weight: bold;
}

.order-content {
  margin-bottom: 20rpx;
}

.order-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.order-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}

.order-desc {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 10rpx;
}

.order-price {
  font-size: 28rpx;
  font-weight: bold;
  color: #ff4d4f;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.order-time {
  font-size: 24rpx;
  color: #999;
  flex: 1;
}

.order-actions {
  display: inline-block;
  margin-left: 20rpx;
}
</style>