package com.mmall.service;

import com.mmall.common.ServletResponse;
import com.mmall.pojo.User;

public interface UserServiceInterface
{
    ServletResponse<User> Login(String username, String password);

    ServletResponse<String> Register(User user);

    ServletResponse<String> checkValid(String str, String type);

    ServletResponse<String> selectForgetQuestion(String username);

    ServletResponse<String> checkAnswer(String username, String question, String answer);

    ServletResponse<String> forgetResetPassword(String username, String passwordNew, String token);

    ServletResponse<String> resetPassword(String oldPassword, String newPassword, User user);

    ServletResponse<User> updateInformation(User user);

    ServletResponse<User> getInformation(Integer id);
}
