package com.after.life.blog.except;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * @Author Lostceleste
 * @Version 1.0
 * @Date 2023-08-21 15:15
 */
@RestControllerAdvice
public class ExceptionConfig {

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<?> commonException(CommonException e) {
        return ResponseEntity.status(500).body(e.getMessage());
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> authenticationException(AuthenticationException e) {
        e.printStackTrace();
        return ResponseEntity.status(501).body("认证失败");
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(500).body("请求方式不支持");
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> MaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(500).body("文件大小超过限制");
    }
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> expiredJwtException(ExpiredJwtException e) {
        e.printStackTrace();
        return ResponseEntity.status(501).body("身份认证过期，请重新登录！");
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(500).body("Required request body is missing");
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(500).body("request params is not valid");
    }
    @ExceptionHandler(OAuth2AuthenticationException.class)
    public ResponseEntity<?> oAuth2AuthenticationException(OAuth2AuthenticationException e) {
        e.printStackTrace();
        OAuth2Error error = e.getError();
        return ResponseEntity.status(500).body(error.getDescription());
    }

}
