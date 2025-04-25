<template>
  <div class="student-form-container">
    <h2>修改学生信息</h2>
    <el-card v-loading="loadingData">
       <el-form v-if="!loadingData && studentForm" ref="studentFormRef" :model="studentForm" :rules="studentRules" label-width="100px">
        <el-form-item label="学号" prop="id">
          <!-- 学号不允许修改 -->
          <el-input v-model="studentForm.id" disabled></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="studentForm.name" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="studentForm.gender">
            <el-radio label="m">男</el-radio>
            <el-radio label="f">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="studentForm.age" :min="1" :max="150" placeholder="请输入年龄"></el-input-number>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">提交修改</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
       <el-empty v-else-if="!loadingData && !studentForm" description="未找到学生信息或加载失败"></el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getStudentById, updateStudent } from '@/api/student'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const studentFormRef = ref(null)
const loadingData = ref(true) // 控制加载学生数据的状态
const submitting = ref(false) // 控制提交修改的状态

// 从路由参数获取学生 ID
const studentId = route.params.id

const studentForm = ref(null) // 初始化为 null，加载后再赋值

// 校验规则 (与添加时类似，但学号不需要校验了)
const studentRules = reactive({
  name: [
      { required: true, message: '请输入姓名', trigger: 'blur' },
      { max: 10, message: '姓名长度不能超过10个字符', trigger: 'blur' }
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  age: [
      { required: true, message: '请输入年龄', trigger: 'blur' },
      { type: 'integer', message: '年龄必须为整数', trigger: 'blur' },
      { min: 1, type: 'integer', message: '年龄不能小于1', trigger: 'blur'},
      { max: 150, type: 'integer', message: '年龄不能大于150', trigger: 'blur'}
  ]
})

// 加载学生数据
const fetchStudentData = async () => {
  loadingData.value = true
  try {
    const response = await getStudentById(studentId)
    if (response.code === 200 && response.data) {
      studentForm.value = reactive({ ...response.data }) // 使用 reactive 包装加载的数据
    } else {
      ElMessage.error('加载学生信息失败: ' + (response.message || '未知错误'))
      studentForm.value = null // 表示加载失败或未找到
      // 可以选择几秒后返回列表页
      // setTimeout(() => router.push('/students'), 3000);
    }
  } catch (error) {
    console.error('加载学生信息失败:', error)
     studentForm.value = null
     // 错误消息已由 API 拦截器处理
  } finally {
    loadingData.value = false
  }
}

// 组件挂载后加载数据
onMounted(() => {
  fetchStudentData()
})

const handleSubmit = async () => {
  if (!studentFormRef.value || !studentForm.value) return
  await studentFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        // 确保传递的是普通对象，而不是 Proxy
        const dataToSubmit = { ...studentForm.value };
        const response = await updateStudent(studentId, dataToSubmit)
         if (response.code === 200) {
            ElMessage.success('修改学生信息成功')
            router.push('/students') // 修改成功后返回列表页
         }
         // 失败消息由拦截器处理
      } catch (error) {
        console.error('修改学生信息失败:', error)
         // 失败消息由拦截器处理
      } finally {
        submitting.value = false
      }
    } else {
      console.log('表单验证失败')
      return false
    }
  })
}

const goBack = () => {
  router.go(-1) // 返回上一页
}
</script>

<style scoped>
.student-form-container {
  padding: 20px;
  max-width: 600px; /* 限制最大宽度 */
  margin: 0 auto; /* 居中 */
}
.el-card {
    padding: 20px;
    min-height: 200px; /* 给个最小高度，防止加载时闪烁 */
}
h2 {
    text-align: center;
    margin-bottom: 20px;
}
</style>