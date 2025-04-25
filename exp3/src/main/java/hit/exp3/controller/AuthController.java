package hit.exp3.controller;

import hit.exp3.dto.LoginRequest;
import hit.exp3.dto.LoginResponse;
import hit.exp3.dto.Result;
import hit.exp3.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Result<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            response.setRedirectUrl("/home"); // 设置跳转的 URL
            return ResponseEntity.ok(Result.success("登录成功", response));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Result.error(HttpStatus.UNAUTHORIZED.value(), "用户名或密码错误"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.error("登录失败: " + e.getMessage()));
        }
    }

    // 可以添加注册接口
    // @PostMapping("/register")
    // public ResponseEntity<Result<Void>> register(@RequestBody User user) {
    //     try {
    //         authService.register(user);
    //         return ResponseEntity.ok(Result.success("注册成功"));
    //     } catch (RuntimeException e) {
    //          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.error("注册失败"));
    //     }
    // }
}