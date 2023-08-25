package com.after.life.blog.service;

import com.after.life.blog.bean.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Lostceleste
* @description 针对表【user】的数据库操作Service
* @createDate 2023-08-21 15:20:07
*/
public interface UserService extends IService<User> {

    String login(String username, String password);


}
