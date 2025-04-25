package hit.exp3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // 允许跨域访问的路径
                        .allowedOriginPatterns("*") // 允许所有来源 (在生产环境中应指定具体来源)
                        // 或者 .allowedOrigins("http://localhost:5173") // 指定前端开发服务器地址
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的方法
                        .allowedHeaders("*") // 允许的请求头
                        .allowCredentials(true) // 是否允许发送 Cookie
                        .maxAge(3600); // 预检请求的有效期
            }
        };
    }
}