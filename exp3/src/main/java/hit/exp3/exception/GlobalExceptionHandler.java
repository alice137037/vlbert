package hit.exp3.exception;

import hit.exp3.dto.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 声明这是一个全局异常处理类
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 处理业务运行时异常 (例如 Service 层抛出的)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<Void>> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: ", e);
        // 通常业务异常会带有明确信息，可以直接返回
        // 对于一些意外的 RuntimeException，返回通用错误信息
        String message = e.getMessage() != null ? e.getMessage() : "服务器内部错误，请联系管理员";
        // 根据异常类型判断状态码，这里简化处理，都用 500 或 400
        if (message.contains("已存在") || message.contains("不存在")) {
            return ResponseEntity.badRequest().body(Result.error(400, message));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.error(500, message));
    }

    // 处理 Spring Security 认证异常
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Result<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证失败: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.error(HttpStatus.UNAUTHORIZED.value(), "认证失败: " + e.getMessage()));
    }

    // 处理 Spring Security 授权异常 (权限不足)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("权限不足: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Result.error(HttpStatus.FORBIDDEN.value(), "权限不足，无法访问该资源"));
    }

    // 处理数据校验异常 (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("数据校验失败: {}", ex.getMessage());
        // 可以提取更详细的校验错误信息返回给前端
        String defaultMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(Result.error(HttpStatus.BAD_REQUEST.value(), "参数校验失败: " + defaultMessage));
    }


    // 处理所有其他未捕获的异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleGenericException(Exception e) {
        log.error("未捕获异常: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器发生未知错误"));
    }
}