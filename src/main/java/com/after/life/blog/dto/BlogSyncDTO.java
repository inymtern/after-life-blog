package com.after.life.blog.dto;

import com.after.life.blog.bean.BlogNote;
import lombok.Data;

import java.util.Date;

/**
 * @Author Lostceleste
 * @Version 1.0
 * @Date 2023-08-25 13:49
 */
@Data
public class BlogSyncDTO {

    private String title;
    private String intro;
    private String text;
    private String cover;

    public BlogNote convert() {
        BlogNote blogNote = new BlogNote();
        blogNote.setCreateTime(new Date());
        blogNote.setTitle(title);
        blogNote.setIntro(intro);
        blogNote.setText(text);
        blogNote.setTags("DATE");
        blogNote.setCover(cover);
        return blogNote;
    }
}
