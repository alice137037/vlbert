package hit.exp3.mapper;


import hit.exp3.entity.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface StudentMapper {

    // 使用 XML 进行复杂查询 (分页和模糊查询)
    List<Student> findStudents(@Param("query") String query);

    @Select("SELECT * FROM student WHERE id = #{id}")
    Student findById(@Param("id") String id);

    @Insert("INSERT INTO student(id, name, gender, age) VALUES(#{id}, #{name}, #{gender}, #{age})")
    int insert(Student student);

    @Update("UPDATE student SET name = #{name}, gender = #{gender}, age = #{age} WHERE id = #{id}")
    int update(Student student);

    @Delete("DELETE FROM student WHERE id = #{id}")
    int deleteById(@Param("id") String id);

    @Select("SELECT COUNT(*) FROM student WHERE id = #{id}")
    boolean existsById(@Param("id") String id);
}
