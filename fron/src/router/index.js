import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import StudentListView from '../views/StudentListView.vue'
import StudentAddView from '../views/StudentAddView.vue'
import StudentEditView from '../views/StudentEditView.vue'
import { useAuthStore } from '@/store/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { requiresAuth: false } // 登录页不需要认证
    },
    {
      path: '/',
      redirect: '/students' // 根路径重定向到学生列表
    },
    {
      path: '/students',
      name: 'student-list',
      component: StudentListView,
      meta: { requiresAuth: true } // 需要认证
    },
    {
      path: '/students/add',
      name: 'student-add',
      component: StudentAddView,
      meta: { requiresAuth: true } // 需要认证
    },
    {
      // 使用 :id 参数匹配学生 ID
      path: '/students/edit/:id',
      name: 'student-edit',
      component: StudentEditView,
      props: true, // 将路由参数作为 props 传递给组件
      meta: { requiresAuth: true } // 需要认证
    },
    // 可以添加 404 页面
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('../views/NotFoundView.vue') // 假设有一个 NotFoundView.vue
    }
  ]
})

// 全局前置守卫：检查认证状态
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.meta.requiresAuth

  if (requiresAuth && !authStore.isAuthenticated) {
    // 如果需要认证但用户未登录，重定向到登录页
    // 同时传递目标路径作为查询参数，以便登录后跳转回来
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else if (to.name === 'login' && authStore.isAuthenticated) {
    // 如果用户已登录但试图访问登录页，重定向到学生列表页
    next({ name: 'student-list' })
  } else {
    // 其他情况正常放行
    next()
  }
})

export default router