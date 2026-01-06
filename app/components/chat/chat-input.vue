<template>
  <view class="chat-input-container" :class="{ 'with-emoji': showEmoji }">
    <view class="chat-input-wrapper">
      <view class="input-actions">
        <uni-icons type="smile-o" size="30" color="#666" @click="toggleEmojiPanel" class="action-btn" />
      </view>
      
      <view class="input-content">
        <input 
          class="message-input" 
          v-model="inputText" 
          placeholder="输入消息..." 
          placeholder-style="color:#999"
          @confirm="sendMessage" 
          @input="onInput" 
          :style="{ height: inputHeight }" 
          maxlength="500" 
          @focus="onInputFocus"
          @blur="onInputBlur" 
        />
      </view>
      
      <view class="input-send">
        <button 
          class="send-button" 
          :disabled="!inputText.trim() || !isConnected" 
          @click="sendMessage"
        >
          发送
        </button>
      </view>
    </view>
  </view>

  <view class="emoji-panel" v-if="showEmoji">
    <view class="emoji-header">
      <text class="emoji-title">表情</text>
      <uni-icons type="close" size="28" color="#666" @click="toggleEmojiPanel" class="close-btn" />
    </view>
    <view class="emoji-list">
      <text class="emoji-item" v-for="emoji in emojis" :key="emoji" @click="addEmoji(emoji)">{{ emoji }}</text>
    </view>
  </view>
</template>

<script lang="ts" setup>
import { ref } from 'vue';

const props = defineProps({
  isConnected: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['sendMessage']);

const inputText = ref('');
const showEmoji = ref(false);
const inputHeight = ref('60rpx');

const emojis = ['😀', '😁', '😂', '🤣', '😃', '😄', '😅', '😆', '😉', '😊', '🙂', '🙃', '😋', '😎', '😍', '😘', '🥰', '😗', '😙', '👍', '👎', '👊', '✌️', '🤝', '🙏', '🎉', '🎊', '🔥', '🥳', '😢', '😭', '😱', '😡', '🤔', '🤫', '🤭', '😴', '🤗', '🤩', '🤔', '😬', '😴', '🤓', '😇', '😷', '🤒', '🤕', '🤢', '🤮', '🤧', '🥵', '🥶', '🥳', '🥺', '🤠', '🥰', '😎', '🤓', '🧐', '😕', '😟', '🙁', '😮', '😯', '😲', '😳', '🥺', '😦', '😧', '😨', '😰', '😥', '😢', '😭', '😱', '😖', '😣', '😞', '😓', '😩', '😫', '🥱', '😤', '😠', '😡', '🤬', '🤯', '😳', '😵', '😵‍💫', '🤢', '🤮', '🤧', '🥴', '😷', '🤒', '🤕', '🤑', '🤠', '😈', '👿', '👹', '👺', '🤡', '💩', '👻', '💀', '☠️', '👽', '👾', '🤖', '🎃', '😺', '😸', '😹', '😻', '😼', '😽', '🙀', '😿', '😾'];

const toggleEmojiPanel = () => {
  showEmoji.value = !showEmoji.value;
  if (!showEmoji.value) {
    resetInputHeight();
  }
};

const addEmoji = (emoji) => {
  inputText.value += emoji;
};

const sendMessage = () => {
  const content = inputText.value.trim();
  if (!content) return;

  emit('sendMessage', content);
  inputText.value = '';
  resetInputHeight();
};

const resetInputHeight = () => {
  inputHeight.value = '60rpx';
};

const onInput = (e) => {
  const value = e.detail.value;
  inputText.value = value;
  const lineHeight = 30;
  const maxLines = 4;
  const textLength = value.length;
  const lines = Math.ceil(textLength / 18);
  const newHeight = Math.min(lines * lineHeight + 16, maxLines * lineHeight + 16) + 'rpx';
  inputHeight.value = newHeight;
};

const onInputFocus = () => {
  showEmoji.value = false;
};

const onInputBlur = () => { };
</script>

<style scoped>
.chat-input-container {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  border-top: 1px solid #e5e5e5;
  z-index: 999;
  padding-bottom: 15rpx;
}

.chat-input-wrapper {
  display: flex;
  align-items: center;
  padding: 10rpx 20rpx;
  gap: 12rpx;
}

.input-actions {
  display: flex;
  align-items: center;
}

.action-btn {
  padding: 8rpx;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.action-btn:hover {
  background-color: #f0f0f0;
}

.input-content {
  flex: 1;
  display: flex;
  align-items: center;
  min-width: 0;
}

.message-input {
  width: 100%;
  min-height: 60rpx;
  max-height: 180rpx;
  padding: 14rpx 20rpx;
  background-color: #f5f5f5;
  border-radius: 30rpx;
  font-size: 28rpx;
  line-height: 36rpx;
  overflow-y: auto;
  box-sizing: border-box;
  border: none;
  transition: all 0.2s ease;
  resize: none;
}

.message-input:focus {
  background-color: #f0f0f0;
  outline: none;
}

.input-send {
  display: flex;
  align-items: center;
}

.send-button {
  min-width: 80rpx;
  height: 60rpx;
  border-radius: 30rpx;
  background-color: #07c160;
  color: #fff;
  font-size: 26rpx;
  font-weight: 500;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  padding: 0 20rpx;
}

.send-button:hover:not(:disabled) {
  background-color: #05ae53;
  transform: translateY(-1rpx);
}

.send-button:disabled {
  background-color: #e0e0e0 !important;
  color: #999 !important;
}

.chat-input-container.with-emoji {
  border-top: none;
}

.emoji-panel {
  position: fixed;
  bottom: 90rpx;
  left: 0;
  right: 0;
  background-color: #fff;
  border-top: 1px solid #e5e5e5;
  z-index: 998;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.emoji-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12rpx 20rpx;
  border-bottom: 1px solid #f0f0f0;
  background-color: #fafafa;
}

.emoji-title {
  font-size: 28rpx;
  font-weight: 500;
  color: #333;
}

.close-btn {
  padding: 6rpx;
  border-radius: 50%;
  transition: background-color 0.2s ease;
}

.close-btn:hover {
  background-color: #f0f0f0;
}

.emoji-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
  padding: 15rpx;
  max-height: 350rpx;
  overflow-y: auto;
}

.emoji-item {
  font-size: 40rpx;
  width: 65rpx;
  height: 65rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8rpx;
  transition: all 0.2s ease;
}

.emoji-item:hover {
  background-color: #f0f0f0;
  transform: scale(1.1);
}
</style>