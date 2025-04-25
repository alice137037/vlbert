package hit.exp3.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        // Ensure the secret key is long enough for HS512
        byte[] keyBytes = secret.getBytes();
        // HS512 requires a key of at least 512 bits (64 bytes)
        if (keyBytes.length < 64) {
            // Pad or throw an error, for simplicity padding with zeros
            byte[] paddedKeyBytes = new byte[64];
            System.arraycopy(keyBytes, 0, paddedKeyBytes, 0, keyBytes.length);
            keyBytes = paddedKeyBytes;
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 从 token 中提取用户名
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 从 token 中提取过期时间
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 从 token 中提取指定的 claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 解析 token 中的所有 claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 检查 token 是否过期
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 为指定用户生成 token
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        // 可以在 claims 中添加其他信息，如用户角色等
        return createToken(claims, username);
    }

    // 为 UserDetails 生成 token
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // 可以从 userDetails 中获取权限等信息放入 claims
        return createToken(claims, userDetails.getUsername());
    }


    // 创建 token
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // subject 通常是用户名
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // 使用 HS512
                .compact();
    }

    // 验证 token 是否有效
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // 验证 token 是否有效 (仅验证签名和过期时间)
    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) { // MalformedJwtException, ExpiredJwtException, etc.
            return false;
        }
    }
}