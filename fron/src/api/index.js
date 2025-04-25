import axios from 'axios'
import { useAuthStore } from '@/store/auth'
import { ElMessage } from 'element-plus'

// 从环境变量读取 API 基础 URL，如果定义了的话
// const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'; // 如果使用代理，可以直接用 /api
const baseURL = '/api'; // 配合 vite proxy

const apiClient = axios.create({
  baseURL: baseURL,
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json',
  }
})

// 请求拦截器：添加 Token
apiClient.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers['Authorization'] = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器：处理错误和 Token 失效
apiClient.interceptors.response.use(
  (response) => {
    // 后端返回的 Result 对象在 data 中
    const res = response.data
    // 如果 code 不是 200，或者没有 code (直接返回数据的情况)，则判断为错误或直接返回
     if (res && res.code && res.code !== 200) {
         ElMessage({
             message: res.message || 'Error',
             type: 'error',
             duration: 5 * 1000
         })

         // 401: 未认证 (Token 无效或过期)
         if (res.code === 401 || res.code === 403) {
             const authStore = useAuthStore()
             authStore.logout() // 清除本地 Token 并跳转到登录页
         }
         // 返回一个被拒绝的 Promise，阻止后续 then 的执行
         return Promise.reject(new Error(res.message || 'Error'))
     } else {
         // 如果 code 是 200 或没有 code 字段，直接返回 data (可能是 Result 对象或直接的数据)
         return res; // 直接返回后端 Result 对象，让调用方处理 data
         // 或者 return res.data; // 只返回 Result 中的 data 部分
     }
  },
  (error) => {
    console.error('API Error:', error) // for debug

    let message = '请求失败，请检查网络或联系管理员';
     if (error.response) {
         // 请求已发出，但服务器响应状态码不在 2xx 范围
         console.error('Error Response:', error.response);
         message = `请求错误 ${error.response.status}: ${error.response.data?.message || error.response.statusText}`;
         if (error.response.status === 401 || error.response.status === 403) {
             message = '认证失败或权限不足，请重新登录';
             const authStore = useAuthStore()
             authStore.logout()
         }
     } else if (error.request) {
         // 请求已发出但没有收到响应
         console.error('Error Request:', error.request);
         message = '服务器无响应，请稍后重试';
     } else {
         // 发送请求时触发了一些错误
         console.error('Error Message:', error.message);
         message = error.message;
     }

    ElMessage({
      message: message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default apiClient