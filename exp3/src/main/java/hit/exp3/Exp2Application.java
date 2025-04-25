package hit.exp3;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("hit.exp3.mapper") // 扫描 Mapper 接口
public class Exp2Application {

    public static void main(String[] args) {
        SpringApplication.run(Exp2Application.class, args);
    }

    // 如果没有注册功能，可以在这里初始化一个默认用户 (仅用于测试)
    // @Bean
    // public CommandLineRunner initData(UserMapper userMapper, PasswordEncoder passwordEncoder) {
    //     return args -> {
    //         String username = "admin";
    //         if (userMapper.findByUsername(username) == null) {
    //             String encodedPassword = passwordEncoder.encode("123456");
    //             User adminUser = new User(username, encodedPassword);
    //             userMapper.insert(adminUser);
    //             System.out.println("默认用户 'admin' 创建成功，密码 '123456'");
    //         }
    //     };
    // }
}