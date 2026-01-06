// config/index.js

// 基础配置
export const BASE_CONFIG = {
  // API基础URL
  API_BASE_URL: 'https://localhost:8181/api',
  
  // WebSocket基础URL (自动从API URL转换)
  get WS_BASE_URL() {
    return this.API_BASE_URL.replace('http://', 'ws://').replace('https://', 'wss://')
  }
}

// 获取WebSocket URL
export const getWebSocketUrl = (path = '/ws') => {
  return BASE_CONFIG.WS_BASE_URL + path
}

// 获取API URL
export const getApiUrl = (path = '') => {
  return BASE_CONFIG.API_BASE_URL + path
}
