<template>
  <view class="date-divider">
    {{ formattedDate }}
  </view>
</template>

<script lang="ts" setup>
  import { computed } from 'vue';
  import { formatDate } from '@/utils/tools';

  const props = defineProps({
    date: {
      type: [String, Date],
      required: true
    }
  });

  const formattedDate = computed(() => {
    return formatDate(props.date);
  });
</script>

<style scoped>
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
</style>