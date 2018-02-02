package com.mmall.controller.backend;

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
@RequestMapping("/manage/user/")
public class UserManageController {
    @Autowired
    private UserServiceInterface userServiceInterface;

    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    public @ResponseBody ServletResponse<User> Login(String username, String password, HttpSession session){
        ServletResponse<User> response = userServiceInterface.Login(username, password);
        if(response.isSuccess()){
            User user = response.getData();
            if(Constant.Role.ROLE_ADMIN == user.getRole()){
                session.setAttribute(Constant.Constant_User,user);
                return response;
            }
            else{
                return ServletResponse.createByErrorMessage("该用户非管理员，无法登录");
            }
        }
        return response;
    }
}
