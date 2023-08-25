package com.after.life.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Author Lostceleste
 * @Version 1.0
 * @Date 2023-08-21 15:27
 */
@Data
public class LoginDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}
