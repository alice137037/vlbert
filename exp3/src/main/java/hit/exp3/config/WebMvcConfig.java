package hit.exp3.config;

// import hit.exp3.interceptor.AuthInterceptor; // 如果使用自定义拦截器
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // @Autowired
    // private AuthInterceptor authInterceptor; // 注入自定义拦截器

    // @Override
    // public void addInterceptors(InterceptorRegistry registry) {
    // 使用 Spring Security 后，通常不再需要自定义认证拦截器
    // 如果有其他拦截需求可以在这里添加
    // registry.addInterceptor(authInterceptor)
    //         .addPathPatterns("/api/**") // 拦截 /api 下的所有路径
    //         .excludePathPatterns("/api/auth/login", "/api/auth/register"); // 排除登录和注册接口
    // }
}