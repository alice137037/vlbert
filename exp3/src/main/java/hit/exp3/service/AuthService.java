package hit.exp3.service;

import hit.exp3.dto.LoginRequest;
import hit.exp3.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    // 可以添加注册方法
    // void register(User user);
}
