package hit.exp3.service;

import hit.exp3.entity.Student;
import com.github.pagehelper.PageInfo;

public interface StudentService {
    PageInfo<Student> findStudents(String query, int pageNum, int pageSize);
    Student findById(String id);
    void addStudent(Student student);
    void updateStudent(Student student);
    void deleteStudent(String id);
}