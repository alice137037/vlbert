<template>
  <div class="student-form-container">
    <h2>添加学生信息</h2>
    <el-card>
       <el-form ref="studentFormRef" :model="studentForm" :rules="studentRules" label-width="100px">
        <el-form-item label="学号" prop="id">
          <el-input v-model="studentForm.id" placeholder="请输入学号"></el-input>
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
          <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { addStudent } from '@/api/student'
import { ElMessage } from 'element-plus'

const router = useRouter()
const studentFormRef = ref(null)
const loading = ref(false)

const studentForm = reactive({
  id: '',
  name: '',
  gender: 'm', // 默认性别
  age: null
})

// 校验规则
const studentRules = reactive({
  id: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9]+$/, message: '学号只能包含字母和数字', trigger: 'blur' },
    { max: 10, message: '学号长度不能超过10个字符', trigger: 'blur' }
  ],
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


const handleSubmit = async () => {
  if (!studentFormRef.value) return
  await studentFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const response = await addStudent(studentForm)
        if (response.code === 200) {
          ElMessage.success('添加学生成功')
          router.push('/students') // 添加成功后返回列表页
        }
        // 失败消息由拦截器处理
      } catch (error) {
        console.error('添加学生失败:', error)
         // 失败消息由拦截器处理
      } finally {
        loading.value = false
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
}
h2 {
    text-align: center;
    margin-bottom: 20px;
}
</style>