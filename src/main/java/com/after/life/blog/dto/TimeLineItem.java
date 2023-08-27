package com.after.life.blog.dto;

import cn.hutool.core.util.RandomUtil;
import com.after.life.blog.bean.BlogNote;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Author Lostceleste
 * @Version 1.0
 * @Date 2023-08-21 18:09
 */
@Data
public class TimeLineItem {
    private String id_l;
    private String tag_l;
    private String intro_l;
    private String title_l;
    private String img_l;

    private String id_r;
    private String tag_r;
    private String intro_r;
    private String title_r;
    private String img_r;

    private int gap = 30;

    public TimeLineItem(BlogNote first, BlogNote last) {
        this.id_l = first.getId();
        this.tag_l = first.getTags() + _shu + sdf.format(first.getCreateTime());
        this.intro_l = first.getIntro();
        this.title_l = first.getTitle();
        this.img_l = first.getCover();
        if(last != null) {
            this.id_r = last.getId();
            this.tag_r = last.getTags() + _shu + sdf.format(last.getCreateTime());
            this.intro_r = last.getIntro();
            this.title_r = last.getTitle();
            this.img_r = last.getCover();
        }
        this.setGap(RandomUtil.randomInt(100, 150));
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final String _shu = " | ";
    public static List<TimeLineItem> build(List<BlogNote> list) {
        int size = list.size();
        if(size == 0) return Collections.emptyList();
        list.sort(Comparator.comparing(BlogNote::getCreateTime).reversed());
        int c = 0;
        BlogNote last = null;
        ArrayList<TimeLineItem> timeLineItems = new ArrayList<>();
        for (BlogNote blogNote : list) {
            c++;
            if(c == 2) {
                c = 0;
                TimeLineItem timeLineItem = new TimeLineItem(last, blogNote);
                timeLineItems.add(timeLineItem);
            } else {
                last = blogNote;
                if(last.getId().equals(list.get(size-1).getId())) {
                    // 最后
                    TimeLineItem timeLineItem = new TimeLineItem(last, null);
                    timeLineItems.add(timeLineItem);
                }
            }
        }
        return timeLineItems;
    }
}
