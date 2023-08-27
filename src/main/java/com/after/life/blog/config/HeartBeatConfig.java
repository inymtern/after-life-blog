package com.after.life.blog.config;

import com.after.life.blog.bean.BlogNote;
import com.after.life.blog.service.BlogNoteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @Author Lostceleste
 * @Version 1.0
 * @Date 2023-08-27 12:08
 */
@Configuration
public class HeartBeatConfig {

    @Resource
    private BlogNoteService blogNoteService;

    @Scheduled(fixedRate = 10000)
    public void ping() {
        BlogNote one = blogNoteService.getOne(new LambdaQueryWrapper<BlogNote>()
                .eq(BlogNote::getId, 1)
                .select(BlogNote::getId));
//        System.err.println("ping is ok");
    }
}
