import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'https://localhost:8181/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('admin_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (err) => Promise.reject(err)
)

request.interceptors.response.use(
  (res) => {
    const { data } = res
    if (data?.errCode === 0 || data?.errCode === undefined) {
      return data
    }
    ElMessage.error(data?.msg || '请求失败')
    return Promise.reject(new Error(data?.msg || '请求失败'))
  },
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('admin_token')
      window.location.href = '/login'
    } else {
      ElMessage.error(err.message || '网络异常')
    }
    return Promise.reject(err)
  }
)

export default request
