import apiClient from './index'

/**
 * 获取学生列表 (分页 + 查询)
 * @param {object} params - 查询参数 { query, pageNum, pageSize }
 * @returns {Promise<object>} - 后端返回的 Result 对象，data 为 PageInfo
 */
export const getStudents = (params) => {
  return apiClient.get('/students', { params })
}

/**
 * 根据 ID 获取学生信息
 * @param {string} id - 学生 ID
 * @returns {Promise<object>} - 后端返回的 Result 对象，data 为 Student
 */
export const getStudentById = (id) => {
  return apiClient.get(`/students/${id}`)
}

/**
 * 添加学生
 * @param {object} studentData - 学生信息对象
 * @returns {Promise<object>} - 后端返回的 Result 对象
 */
export const addStudent = (studentData) => {
  return apiClient.post('/students', studentData)
}

/**
 * 更新学生信息
 * @param {string} id - 学生 ID
 * @param {object} studentData - 更新后的学生信息对象
 * @returns {Promise<object>} - 后端返回的 Result 对象
 */
export const updateStudent = (id, studentData) => {
  return apiClient.put(`/students/${id}`, studentData)
}

/**
 * 删除学生
 * @param {string} id - 学生 ID
 * @returns {Promise<object>} - 后端返回的 Result 对象
 */
export const deleteStudent = (id) => {
  return apiClient.delete(`/students/${id}`)
}