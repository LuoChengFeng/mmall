package com.mmall.controller.portal;

import com.mmall.common.Constant;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServletResponse;
import com.mmall.pojo.User;
import com.mmall.service.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value="/user/")
public class UserController
{

    @Autowired
    private UserServiceInterface userServiceInterface;

    /**
     * 登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value="login.do",method = RequestMethod.POST)
    public @ResponseBody ServletResponse<User> Login(String username, String password, HttpSession session){

        ServletResponse<User> response = userServiceInterface.Login(username,password);

        if(response.isSuccess()){
            session.setAttribute(Constant.Constant_User,response.getData());
        }
        return response;
    }

    /**
     * 退出
     * @param session
     * @return
     */
    @RequestMapping(value="logout.do",method = RequestMethod.POST)
    public @ResponseBody ServletResponse<String> Logout(HttpSession session){

        session.removeAttribute(Constant.Constant_User);
        return ServletResponse.createBySuccessMessage("退出成功");
    }

    /**
     * 注册
     * @param user
     * @param session
     * @return
     */
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    public @ResponseBody ServletResponse<String> Register(@ModelAttribute User user, HttpSession session){

        ServletResponse<String> response = userServiceInterface.Register(user);
        return response;

    }

    /**
     * 查询帐号和邮箱是否存在
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "checkValid.do",method = RequestMethod.POST)
    public @ResponseBody ServletResponse<String> checkValid(String str,String type){
        return userServiceInterface.checkValid(str,type);
    }

    /**
     * 获取用户信息
     * @param username
     * @return
     */
    @RequestMapping(value = "getUserInfo.do",method = RequestMethod.POST)
    public @ResponseBody ServletResponse<User> getUserInfo(String username, HttpSession session){
        User user = (User)session.getAttribute(Constant.Constant_User);
        if(user != null){
            return ServletResponse.createBySuccess(user);
        }
        return ServletResponse.createByErrorMessage("用户未登录");
    }

    /**
     * 获取设置的问题
     * @param username
     * @return
     */
    @RequestMapping(value = "forgetQuestion.do",method = RequestMethod.POST)
    public @ResponseBody ServletResponse<String> forgetQuestion(String username){
        return userServiceInterface.selectForgetQuestion(username);
    }

    /**
     * 判断回答的问题是否正确
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "checkAnswer.do",method = RequestMethod.POST)
    public @ResponseBody ServletResponse<String> checkAnswer(String username, String question, String answer){
        return userServiceInterface.checkAnswer(username,question,answer);

    }

    /**
     * 重置密码
     * @param username
     * @param passwordNew
     * @param token
     * @return
     */
    @RequestMapping(value = "forgetResetPassword.do",method = RequestMethod.POST)
    public @ResponseBody ServletResponse<String> forgetResetPassword(String username, String passwordNew, String token){
        return userServiceInterface.forgetResetPassword(username,passwordNew,token);
    }

    /**
     * 登录状态下修改密码
     * @param oldPassword
     * @param newPassword
     * @param session
     * @return
     */
    @RequestMapping(value = "resetPassword.do",method = RequestMethod.POST)
    public @ResponseBody ServletResponse<String> resetPassword(String oldPassword, String newPassword, HttpSession session){
        User user = (User) session.getAttribute(Constant.Constant_User);
        if(user == null){
            return ServletResponse.createByErrorMessage("用户未登录");
        }
        return userServiceInterface.resetPassword(oldPassword,newPassword,user);
    }

    /**
     * 更新用户信息
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(value = "updateInformation.do", method = RequestMethod.POST)
    public @ResponseBody ServletResponse<User> updateInformation(HttpSession session, User user){
        User constantUser = (User) session.getAttribute(Constant.Constant_User);
        if(constantUser == null){
            return ServletResponse.createByErrorMessage("用户未登录");
        }
        user.setId(constantUser.getId());
        user.setUsername(constantUser.getUsername());

        ServletResponse<User> response = userServiceInterface.updateInformation(user);
        if(response.isSuccess()){
            session.setAttribute(Constant.Constant_User,response.getData());
        }

        return response;
    }

    /**
     * 获取用户信息
     * @param session
     * @return
     */
    @RequestMapping(value = "getInformation.do", method = RequestMethod.POST)
    public @ResponseBody ServletResponse<User> getInformation(HttpSession session){
        User user = (User) session.getAttribute(Constant.Constant_User);
        if(user == null){
            return ServletResponse.createByError(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录");
        }
        return userServiceInterface.getInformation(user.getId());
    }
}
