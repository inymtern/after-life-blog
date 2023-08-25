package com.after.life.blog.controller;

import com.after.life.blog.bean.BlogNote;
import com.after.life.blog.bean.User;
import com.after.life.blog.dto.BlogSyncDTO;
import com.after.life.blog.service.BlogNoteService;
import jakarta.annotation.Resource;
import jakarta.servlet.Filter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Lostceleste
 * @Version 1.0
 * @Date 2023-08-24 14:08
 */
@RestController
@RequestMapping("/oauth2/resource")
public class AuthorizationResourceController {

    private final BlogNoteService blogNoteService;
    private final UserDetailsService userDetailsService;

    public AuthorizationResourceController(BlogNoteService blogNoteService, UserDetailsService userDetailsService) {
        this.blogNoteService = blogNoteService;
        this.userDetailsService = userDetailsService;
    }

    @PreAuthorize("hasAuthority('SCOPE_user.info')")
    @GetMapping("/userinfo")
    public ResponseEntity<?> userinfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Object principal = authentication.getPrincipal();
        String username = authentication.getName();
        User userDetails = (User)userDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            return ResponseEntity.status(500).body("subject not found");
        }
        User.SimpleUser simple = userDetails.toSimple();
        return ResponseEntity.ok(simple);
    }

    @PreAuthorize("hasAuthority('SCOPE_user.info')")
    @PostMapping("/blogSync")
    public ResponseEntity<?> blogSync(@RequestBody BlogSyncDTO blogSyncDTO) {
        BlogNote convert = blogSyncDTO.convert();
        blogNoteService.save(convert);
        return ResponseEntity.ok("成功");
    }


    @Resource
    private SecurityFilterChain filterChain;
    @GetMapping("/test")
    public Object test() {
        List<Filter> filters = filterChain.getFilters();
        for (Filter filter : filters) {
            System.err.println(filter.toString());
        }
        return "ok";
    }

}
