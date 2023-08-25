package com.after.life.blog.service.impl;

import com.after.life.blog.config.JwtUtils;
import com.after.life.blog.except.CommonException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.after.life.blog.bean.User;
import com.after.life.blog.service.UserService;
import com.after.life.blog.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.stereotype.Service;

/**
 * @author Lostceleste
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2023-08-21 15:20:07
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    public String login(String username, String password) {
        Authentication authenticate = null;
        authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        Object principal = authenticate.getPrincipal();
        if (principal instanceof User) {
            return jwtUtils.generateToken((User) principal);
        }
        throw new CommonException("登录失败");
    }
}




