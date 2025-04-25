package hit.exp3.filter;

import hit.exp3.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 1. 检查 Authorization Header 是否存在且以 "Bearer " 开头
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // 如果没有 Token，直接放行给下一个 Filter
            return;
        }

        // 2. 提取 JWT Token
        jwt = authHeader.substring(7); // "Bearer ".length() == 7

        try {
            // 3. 从 Token 中提取用户名
            username = jwtUtil.extractUsername(jwt);

            // 4. 检查用户名是否存在且当前 SecurityContext 中没有认证信息
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 5. 根据用户名加载 UserDetails
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 6. 验证 Token 是否有效
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // 7. 创建 AuthenticationToken
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // 我们不需要凭证 (密码)，因为 Token 已经验证过了
                            userDetails.getAuthorities() // 设置用户权限
                    );
                    // 8. 设置认证细节
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    // 9. 将 AuthenticationToken 设置到 SecurityContext 中，表示用户已认证
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token 解析或验证失败，记录日志，但不中断请求，让后续的 Security 机制处理未认证状态
            logger.warn("JWT Token validation failed: " + e.getMessage());
            // SecurityContextHolder.clearContext(); // 可以选择清除上下文
        }


        // 10. 继续执行过滤器链中的下一个 Filter
        filterChain.doFilter(request, response);
    }
}