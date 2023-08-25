package com.after.life.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.after.life.blog.bean.BlogNote;
import com.after.life.blog.service.BlogNoteService;
import com.after.life.blog.mapper.BlogNoteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
* @author Lostceleste
* @description 针对表【blog_note】的数据库操作Service实现
* @createDate 2023-08-21 17:09:49
*/
@Service
public class BlogNoteServiceImpl extends ServiceImpl<BlogNoteMapper, BlogNote>
    implements BlogNoteService{

    @Override
    @Transactional
    public String insertOrUpdate(BlogNote blogNote) {
        String id = blogNote.getId();
        if(id == null || id.equals("0")) {
            blogNote.setTags("DATE");
            blogNote.setCreateTime(new Date());
            blogNote.setId(null);
        }else {
            if(StrUtil.isBlank(blogNote.getCover())) {
                blogNote.setCover(null);
            }
            blogNote.setUpdateTime(new Date());
        }
        if(StrUtil.isBlank(blogNote.getTitle())) {
            blogNote.setTitle("DEFAULT TITLE");
        }
        saveOrUpdate(blogNote);
        return blogNote.getId();
    }
}




