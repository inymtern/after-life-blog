package com.after.life.blog.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName blog_note
 */
@TableName(value ="blog_note")
@Data
public class BlogNote implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 封面
     */
    private String cover;

    private String userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 简介
     */
    private String intro;

    /**
     * 内容
     */
    private String text;

    /**
     * 关键词s
     */
    private String tags;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}