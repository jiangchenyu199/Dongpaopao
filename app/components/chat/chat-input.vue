<template>
  <view class="chat-input-container">
    <view class="chat-input-bar">
      <view class="input-wrap">
        <input
          class="message-input"
          v-model="inputText"
          placeholder="说点什么..."
          placeholder-style="color: #b0b0b0"
          @confirm="sendMessage"
          @input="onInput"
          :style="{ height: inputHeight }"
          maxlength="500"
        />
      </view>
      <view
        class="send-wrap"
        :class="{ active: canSend }"
        @click="sendMessage"
      >
        <u-icon name="arrow-up" size="20" :color="canSend ? '#fff' : '#c0c0c0'"></u-icon>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue';

const props = defineProps({
  isConnected: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['sendMessage']);

const inputText = ref('');
const canSend = computed(() => props.isConnected && inputText.value.trim().length > 0);
const inputHeight = ref('72rpx');

const sendMessage = () => {
  const content = inputText.value.trim();
  if (!content || !props.isConnected) return;
  emit('sendMessage', content);
  inputText.value = '';
  resetInputHeight();
};

const resetInputHeight = () => {
  inputHeight.value = '72rpx';
};

const onInput = (e) => {
  const value = e.detail.value;
  inputText.value = value;
  const lineHeight = 44;
  const maxLines = 4;
  const textLength = value.length;
  const lines = Math.max(1, Math.ceil(textLength / 16));
  const newHeight = Math.min(lines * lineHeight, maxLines * lineHeight) + 'rpx';
  inputHeight.value = newHeight;
};
</script>

<style scoped>
.chat-input-container {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #f5f5f5;
  border-top: 1rpx solid #eee;
  z-index: 999;
  padding: 20rpx 24rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom, 0px));
}

.chat-input-bar {
  display: flex;
  align-items: flex-end;
  gap: 16rpx;
}

.input-wrap {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
  min-height: 72rpx;
  padding: 0 24rpx;
  background: #f0f2f5;
  border-radius: 36rpx;
}

.message-input {
  width: 100%;
  min-height: 72rpx;
  max-height: 200rpx;
  padding: 20rpx 0;
  font-size: 30rpx;
  line-height: 1.4;
  color: #1f2937;
  background: transparent;
  border: none;
  box-sizing: border-box;
}

.message-input:focus {
  outline: none;
}

.send-wrap {
  flex-shrink: 0;
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s ease;
}

.send-wrap.active {
  background: #1a73e8;
}
</style>