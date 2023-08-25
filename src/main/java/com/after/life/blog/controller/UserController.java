package com.after.life.blog.controller;

import com.after.life.blog.bean.User;
import com.after.life.blog.config.UserUtil;
import com.after.life.blog.service.UserService;
import jakarta.annotation.Resource;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Lostceleste
 * @Version 1.0
 * @Date 2023-08-21 15:32
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping()
    public ResponseEntity<?> userinfo() {
        User current = UserUtil.getCurrent();
        return ResponseEntity.ok(current.toSimple());
    }
}
