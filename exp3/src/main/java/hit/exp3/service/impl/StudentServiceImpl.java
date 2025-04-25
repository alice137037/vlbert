package hit.exp3.service.impl;




import hit.exp3.entity.Student;
import hit.exp3.mapper.StudentMapper;
import hit.exp3.service.StudentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public PageInfo<Student> findStudents(String query, int pageNum, int pageSize) {
        // 设置分页参数
        PageHelper.startPage(pageNum, pageSize);
        // 执行查询 (MyBatis 会自动处理分页)
        List<Student> students = studentMapper.findStudents(query);
        // 包装成 PageInfo 对象返回，包含了分页信息
        return new PageInfo<>(students);
    }

    @Override
    public Student findById(String id) {
        return studentMapper.findById(id);
    }

    @Override
    @Transactional // 添加事务管理
    public void addStudent(Student student) {
        // 检查 ID 是否已存在 (可选，但推荐)
        if (studentMapper.existsById(student.getId())) {
            throw new RuntimeException("学号 " + student.getId() + " 已存在");
        }
        studentMapper.insert(student);
    }

    @Override
    @Transactional
    public void updateStudent(Student student) {
        // 检查学生是否存在
        if (!studentMapper.existsById(student.getId())) {
            throw new RuntimeException("学号 " + student.getId() + " 不存在，无法更新");
        }
        studentMapper.update(student);
    }

    @Override
    @Transactional
    public void deleteStudent(String id) {
        // 检查学生是否存在
        if (!studentMapper.existsById(id)) {
            throw new RuntimeException("学号 " + id + " 不存在，无法删除");
        }
        studentMapper.deleteById(id);
    }
}