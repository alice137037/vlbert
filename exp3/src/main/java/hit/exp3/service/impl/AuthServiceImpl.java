package hit.exp3.service.impl;

import hit.exp3.service.AuthService;
import hit.exp3.mapper.UserMapper;
import hit.exp3.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import hit.exp3.dto.LoginRequest;
import hit.exp3.dto.LoginResponse;
import hit.exp3.entity.User;
import hit.exp3.mapper.UserMapper;
import hit.exp3.service.AuthService;
import hit.exp3.util.JwtUtil; // 需要创建 JwtUtil
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager; // 来自 SecurityConfig

    @Autowired
    private UserDetailsService userDetailsService; // 来自 SecurityConfig

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder; // 来自 SecurityConfig

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // 1. 使用 AuthenticationManager 进行认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        // 2. 认证成功，将认证信息存入 SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. 从认证信息中获取 UserDetails
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 4. 生成 JWT Token
        String token = jwtUtil.generateToken(userDetails.getUsername());

        // 5. 返回 LoginResponse
        return new LoginResponse(token, userDetails.getUsername());
    }

    // 示例：添加注册方法 (需要在 UserMapper 添加 insert 方法)
    // @Override
    // public void register(User user) {
    //     User existingUser = userMapper.findByUsername(user.getUsername());
    //     if (existingUser != null) {
    //         throw new RuntimeException("用户名已存在");
    //     }
    //     user.setPassword(passwordEncoder.encode(user.getPassword())); // 加密密码
    //     userMapper.insert(user);
    // }
}