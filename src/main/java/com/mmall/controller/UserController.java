package com.mmall.controller;

import com.mmall.common.Constant;
import com.mmall.common.ServletResponse;
import com.mmall.pojo.User;
import com.mmall.service.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value="/user/")
public class UserController
{

    @Autowired
    private UserServiceInterface userServiceInterface;

    @RequestMapping(value="login.do",method = RequestMethod.POST)
    public @ResponseBody ServletResponse<User> Login(String username, String password, HttpSession session){

        ServletResponse<User> response = userServiceInterface.Login(username,password);

        if(response.isSuccess()){
            session.setAttribute(Constant.Constant_User,response.getData());
        }
        return response;
    }

}
