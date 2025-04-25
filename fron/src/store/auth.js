import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as apiLogin } from '@/api/auth' // 引入登录 API
import router from '@/router' // 引入 router

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('authToken') || null)
  const username = ref(localStorage.getItem('username') || null)
  const isAuthenticated = ref(!!token.value)

  function setAuth(newToken, newUsername) {
    token.value = newToken
    username.value = newUsername
    isAuthenticated.value = true
    localStorage.setItem('authToken', newToken)
    localStorage.setItem('username', newUsername)
  }

  function clearAuth() {
    token.value = null
    username.value = null
    isAuthenticated.value = false
    localStorage.removeItem('authToken')
    localStorage.removeItem('username')
  }

  async function login(credentials) {
    try {
      const response = await apiLogin(credentials)
      if (response.code === 200 && response.data.token) {
        setAuth(response.data.token, response.data.username)
        return true // 登录成功
      } else {
        console.error('Login failed:', response.message)
        clearAuth()
        return false // 登录失败
      }
    } catch (error) {
      console.error('Login error:', error)
      clearAuth()
      return false // 登录异常
    }
  }

  function logout() {
    clearAuth()
    // 跳转到登录页，并清除查询参数，避免无限重定向
     router.push({ path: '/login', query: {} }).catch(err => {
       if (err.name !== 'NavigationDuplicated') {
         console.error(err);
       }
     });
  }

  return { token, username, isAuthenticated, login, logout, setAuth, clearAuth }
})