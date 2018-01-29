package com.mmall.service;

import com.mmall.common.ServletResponse;
import com.mmall.pojo.User;

public interface UserServiceInterface
{
    ServletResponse<User> Login(String username, String password);
}
