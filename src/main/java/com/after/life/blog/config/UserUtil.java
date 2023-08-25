package com.after.life.blog.config;


import com.after.life.blog.bean.User;
import com.after.life.blog.except.LoginUserNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @Author Lostceleste
 * @Version 1.0
 * @Date 2023-08-10 18:28
 */
public class UserUtil {

    public static User getCurrent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if(principal == null) throw new LoginUserNotFoundException();
        return (User)principal;
    }
}
