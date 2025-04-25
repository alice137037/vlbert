<template>
  <div class="student-list-container">
    <h2>学生信息管理</h2>

    <!-- 搜索和添加区域 -->
    <el-row :gutter="20" class="toolbar">
      <el-col :span="8">
        <el-input v-model="searchQuery" placeholder="按学号或姓名模糊查询" clearable @clear="fetchStudents" @keyup.enter="fetchStudents(1)">
          <template #append>
            <el-button :icon="Search" @click="fetchStudents(1)"></el-button>
          </template>
        </el-input>
      </el-col>
      <el-col :span="4">
        <el-button type="primary" :icon="Plus" @click="goToAddPage">添加学生</el-button>
      </el-col>
       <el-col :span="12" style="text-align: right;">
         <span v-if="authStore.username" style="margin-right: 10px;">欢迎, {{ authStore.username }}</span>
        <el-button type="danger" :icon="SwitchButton" @click="handleLogout">退出登录</el-button>
      </el-col>
    </el-row>

    <!-- 学生列表表格 -->
    <el-table :data="studentList" v-loading="loading" style="width: 100%" border stripe>
      <el-table-column prop="id" label="学号" width="150"></el-table-column>
      <el-table-column prop="name" label="姓名" width="150"></el-table-column>
      <el-table-column prop="gender" label="性别" width="100">
        <template #default="scope">
          {{ scope.row.gender === 'm' ? '男' : scope.row.gender === 'f' ? '女' : '未知' }}
        </template>
      </el-table-column>
      <el-table-column prop="age" label="年龄" width="100"></el-table-column>
      <el-table-column label="操作" fixed="right" min-width="180">
        <template #default="scope">
          <el-button size="small" type="primary" :icon="Edit" @click="goToEditPage(scope.row.id)">修改</el-button>
          <el-button size="small" type="danger" :icon="Delete" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
       <template #empty>
        <el-empty description="暂无数据" />
      </template>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-if="total > 0"
      class="pagination-container"
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      :current-page="currentPage"
      :page-size="pageSize"
      :page-sizes="[10, 20, 50, 100]"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    >
    </el-pagination>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { getStudents, deleteStudent } from '@/api/student'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete, SwitchButton } from '@element-plus/icons-vue'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const authStore = useAuthStore()

const studentList = ref([])
const loading = ref(false)
const searchQuery = ref('')
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 获取学生列表数据
const fetchStudents = async (page = currentPage.value) => {
  loading.value = true
  try {
    const params = {
      query: searchQuery.value,
      pageNum: page,
      pageSize: pageSize.value
    }
    const response = await getStudents(params)
    if (response.code === 200 && response.data) {
      studentList.value = response.data.list || []
      total.value = response.data.total || 0
      currentPage.value = response.data.pageNum || page
      pageSize.value = response.data.pageSize || pageSize.value // 更新 pageSize 以防万一
    } else {
      studentList.value = []
      total.value = 0
      // 错误消息已由 API 拦截器处理
    }
  } catch (error) {
    console.error('获取学生列表失败:', error)
    studentList.value = []
    total.value = 0
     // 错误消息已由 API 拦截器处理
  } finally {
    loading.value = false
  }
}

// 组件挂载后加载数据
onMounted(() => {
  fetchStudents()
})

// 页码改变
const handleCurrentChange = (newPage) => {
  fetchStudents(newPage)
}

// 每页数量改变
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  fetchStudents(1) // 页大小改变后，通常回到第一页
}

// 跳转到添加页面
const goToAddPage = () => {
  router.push('/students/add')
}

// 跳转到修改页面
const goToEditPage = (id) => {
  router.push(`/students/edit/${id}`)
}

// 处理删除
const handleDelete = (id) => {
  ElMessageBox.confirm(
    '确定要删除该学生记录吗？此操作不可撤销。',
    '警告',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      loading.value = true; // 开始加载状态
      try {
        const response = await deleteStudent(id)
         if (response.code === 200) {
            ElMessage({
                type: 'success',
                message: '删除成功',
            })
            // 删除成功后刷新列表
            // 如果删除的是当前页最后一条数据，且不是第一页，可能需要请求前一页
             if (studentList.value.length === 1 && currentPage.value > 1) {
               fetchStudents(currentPage.value - 1);
             } else {
               fetchStudents(); // 否则刷新当前页
             }
         }
        // 失败消息由拦截器处理
      } catch (error) {
        console.error('删除失败:', error)
        // 失败消息由拦截器处理
      } finally {
          loading.value = false; // 结束加载状态
      }
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消删除',
      })
    })
}

// 处理退出登录
const handleLogout = () => {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(() => {
        authStore.logout();
        ElMessage.success('已退出登录');
    }).catch(() => {
        // 用户取消退出
    });
};

</script>

<style scoped>
.student-list-container {
  padding: 20px;
}
.toolbar {
  margin-bottom: 20px;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end; /* 右对齐 */
}
</style>