<template>
    <div class="not-found-container">
      <el-result
        icon="error"
        title="404 - 页面未找到"
        sub-title="抱歉，您访问的页面不存在或已被移动。"
      >
        <template #extra>
          <el-button type="primary" @click="goHome">返回首页</el-button>
        </template>
      </el-result>
    </div>
  </template>
  
  <script setup>
  import { useRouter } from 'vue-router';
  
  const router = useRouter();
  
  const goHome = () => {
    router.push('/'); // 或者 router.push('/students')
  };
  </script>
  
  <style scoped>
  .not-found-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 80vh; /* 占据大部分视口高度 */
  }
  </style>