package com.after.life.blog.service;

import com.after.life.blog.bean.BlogNote;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Lostceleste
* @description 针对表【blog_note】的数据库操作Service
* @createDate 2023-08-21 17:09:49
*/
public interface BlogNoteService extends IService<BlogNote> {

    String insertOrUpdate(BlogNote blogNote);
}
