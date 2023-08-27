package com.after.life.blog.controller;

import cn.hutool.core.map.MapBuilder;
import com.after.life.blog.bean.BlogNote;
import com.after.life.blog.bean.User;
import com.after.life.blog.config.UserUtil;
import com.after.life.blog.dto.LoginDTO;
import com.after.life.blog.service.BlogNoteService;
import com.after.life.blog.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author Lostceleste
 * @Version 1.0
 * @Date 2023-08-21 15:23
 */
@RestController
@RequestMapping("/api/v1/auth")
public class LoginController {
    @Resource
    private UserService userService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated LoginDTO loginDTO ) {
        String token = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
        User.SimpleUser simpleUser = UserUtil.getCurrent().toSimple();
        Map<Object, Object> build = MapBuilder.create()
                .put("token", token)
                .put("userinfo", simpleUser)
                .build();
        return ResponseEntity.ok(build);
    }





}
