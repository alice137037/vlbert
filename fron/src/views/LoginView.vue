<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <span>用户登录</span>
        </div>
      </template>
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-width="80px" @submit.prevent="handleLogin">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading">登录</el-button>
        </el-form-item>
      </el-form>
      <!-- 可以添加注册入口 -->
      <!-- <div class="register-link">
        没有账号？<router-link to="/register">立即注册</router-link>
      </div> -->
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useAuthStore } from '@/store/auth'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute() // 获取当前路由信息，用于登录后重定向

const loginFormRef = ref(null) // 表单引用
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
})

const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 调用 authStore 的 login 方法
        const response = await authStore.login(loginForm)
        if (response && response.redirectUrl) {
          ElMessage.success('登录成功')
          // 使用后端返回的 redirectUrl 进行跳转
          router.push(response.redirectUrl)
        } else {
          // 如果后端未返回 redirectUrl，使用默认路径
          const defaultPath = '/students'
          ElMessage.success('登录成功')
          router.push(defaultPath)
        }
      } catch (error) {
        // 错误已在 API 拦截器或 store 中处理
        console.error('Login component error:', error)
      } finally {
        loading.value = false
      }
    } else {
      console.log('表单验证失败')
      return false
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.login-card {
  width: 400px;
}

.card-header {
  text-align: center;
  font-size: 20px;
}
.register-link {
  margin-top: 10px;
  text-align: center;
  font-size: 14px;
}
</style>