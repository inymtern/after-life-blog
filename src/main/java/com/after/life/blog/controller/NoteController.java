package com.after.life.blog.controller;

import com.after.life.blog.bean.BlogNote;
import com.after.life.blog.dto.TimeLineItem;
import com.after.life.blog.service.BlogNoteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Lostceleste
 * @Version 1.0
 * @Date 2023-08-21 17:06
 */
@RestController
@RequestMapping("/api/v1/note")
public class NoteController {

    @Resource
    private BlogNoteService blogNoteService;

    @PostMapping()
    public ResponseEntity<?> update(@RequestBody BlogNote blogNote) {
        String id = blogNoteService.insertOrUpdate(blogNote);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(String id) {
        blogNoteService.removeById(id);
        return ResponseEntity.ok("成功");
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(String id) {
        BlogNote one = blogNoteService.getOne(new LambdaQueryWrapper<BlogNote>()
                .eq(BlogNote::getId, id)
                .select(BlogNote::getId, BlogNote::getText, BlogNote::getTags, BlogNote::getCreateTime));
        return ResponseEntity.ok(one);
    }

    @GetMapping("/index")
    public ResponseEntity<?> indexList(@RequestParam(defaultValue = "1") Integer page) {
        List<BlogNote> list = blogNoteService.list(new LambdaQueryWrapper<BlogNote>()
                              .last("limit " +  (page-1)*10 + ", 10")
                              .select(BlogNote::getId,
                                     BlogNote::getTitle,
                                     BlogNote::getCover,
                                     BlogNote::getIntro,
                                     BlogNote::getCreateTime,
                                     BlogNote::getTags)
                              .orderByDesc(BlogNote::getCreateTime));
        List<TimeLineItem> build = TimeLineItem.build(list);
        return ResponseEntity.ok(build);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(String keyword) {
        return ResponseEntity.ok("成功");
    }
}
