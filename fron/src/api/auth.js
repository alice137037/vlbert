import apiClient from './index'

/**
 * 登录请求
 * @param {object} credentials - 包含 username 和 password 的对象
 * @returns {Promise<object>} - 后端返回的 Result 对象
 */
export const login = (credentials) => {
  return apiClient.post('/auth/login', credentials)
}

// 如果有注册功能
// export const register = (userData) => {
//   return apiClient.post('/auth/register', userData)
// }