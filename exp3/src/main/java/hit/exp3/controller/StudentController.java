package hit.exp3.controller;

import hit.exp3.dto.Result;
import hit.exp3.entity.Student;
import hit.exp3.service.StudentService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // 需要开启方法级安全注解
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
// @PreAuthorize("isAuthenticated()") // 要求所有接口都需要认证 (或者在 SecurityConfig 中配置)
public class StudentController {

    @Autowired
    private StudentService studentService;

    // 获取学生列表 (分页 + 模糊查询)
    @GetMapping
    public ResponseEntity<Result<PageInfo<Student>>> getStudents(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false, defaultValue = "1") int pageNum,
            @RequestParam(required = false, defaultValue = "10") int pageSize) {
        try {
            PageInfo<Student> pageInfo = studentService.findStudents(query, pageNum, pageSize);
            return ResponseEntity.ok(Result.success(pageInfo));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Result.error("获取学生列表失败: " + e.getMessage()));
        }
    }

    // 根据 ID 获取学生信息
    @GetMapping("/{id}")
    public ResponseEntity<Result<Student>> getStudentById(@PathVariable String id) {
        try {
            Student student = studentService.findById(id);
            if (student != null) {
                return ResponseEntity.ok(Result.success(student));
            } else {
                return ResponseEntity.status(404).body(Result.error(404, "未找到学号为 " + id + " 的学生"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Result.error("获取学生信息失败: " + e.getMessage()));
        }
    }

    // 添加学生
    @PostMapping
    public ResponseEntity<Result<Void>> addStudent(@RequestBody Student student) {
        try {
            studentService.addStudent(student);
            return ResponseEntity.ok(Result.success("添加学生成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Result.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Result.error("添加学生失败: " + e.getMessage()));
        }
    }

    // 修改学生信息
    @PutMapping("/{id}")
    public ResponseEntity<Result<Void>> updateStudent(@PathVariable String id, @RequestBody Student student) {
        if (!id.equals(student.getId())) {
            return ResponseEntity.badRequest().body(Result.error(400, "路径 ID 与请求体 ID 不匹配"));
        }
        try {
            studentService.updateStudent(student);
            return ResponseEntity.ok(Result.success("修改学生信息成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Result.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Result.error("修改学生信息失败: " + e.getMessage()));
        }
    }

    // 删除学生
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteStudent(@PathVariable String id) {
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.ok(Result.success("删除学生成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Result.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Result.error("删除学生失败: " + e.getMessage()));
        }
    }
}