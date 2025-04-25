// 文件路径：exp3/src/main/java/hit/exp3/dto/LoginResponse.java
package hit.exp3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String username;
    private String redirectUrl;

    // 新增的构造器
    public LoginResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }
}