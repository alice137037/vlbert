import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

// 引入 ElementPlus (Vite 插件会自动处理按需导入)
// import ElementPlus from 'element-plus'  // 不需要手动全局引入
// import 'element-plus/dist/index.css' // 不需要手动全局引入 CSS

import App from './App.vue'
import router from './router'


const app = createApp(App)

app.use(createPinia()) // 启用 Pinia
app.use(router)
// app.use(ElementPlus) // 不需要手动全局 use

app.mount('#app')
