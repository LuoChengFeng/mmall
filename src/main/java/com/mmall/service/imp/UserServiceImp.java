package com.mmall.service.imp;

import com.mmall.common.Constant;
import com.mmall.common.ServletResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.UserServiceInterface;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.provider.MD5;

import javax.servlet.Servlet;
import java.util.Date;
import java.util.UUID;

@Service("userServiceInterface")
public class UserServiceImp implements UserServiceInterface
{
    @Autowired
    private UserMapper userMapper;

    /**
     * 登录 判断用户名是否存在，密码加密后在进行查询数据库是否存在
     * @param username
     * @param password
     * @return
     */
    @Override
    public ServletResponse<User> Login(String username, String password) {

        ServletResponse<String> validresponse = checkValid(username,Constant.USERNAME);

        if(validresponse.isSuccess()){
            return ServletResponse.createByErrorMessage("用户名不存在");
        }

        //密码MD5加密
        String md5password = MD5Util.MD5EncodeUtf8(password);

        User user = userMapper.selectUser(username,md5password);

        if(user == null){
            return ServletResponse.createByErrorMessage("密码不正确");
        }
        else{
            return ServletResponse.createBySuccess("登录成功",user);
        }

    }

    /**
     * 注册  判断用户名、邮箱是否被注册，设置账户类型并将密码加密添加至数据库中
     * @param user
     * @return
     */
    @Override
    public ServletResponse<String> Register(User user) {

        ServletResponse<String> validresponse = checkValid(user.getUsername(),Constant.USERNAME);

        if(!validresponse.isSuccess()){
            return ServletResponse.createByErrorMessage("注册失败,该帐号已被注册");
        }

        validresponse = checkValid(user.getEmail(),Constant.EMAIL);
        if(!validresponse.isSuccess()){
            return ServletResponse.createByErrorMessage("注册失败,该邮箱已被注册");
        }

        //设置账户类型
        user.setRole(Constant.Role.ROLE_USER);

        //加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        int i = userMapper.insertSelective(user);

        if(i == 1){
            return ServletResponse.createBySuccessMessage("注册成功");
        }
        else{
            return ServletResponse.createByErrorMessage("注册失败");
        }

    }

    /**
     * 校验帐号邮箱
     * @param str
     * @param type
     * @return
     */
    @Override
    public ServletResponse<String> checkValid(String str, String type){
        if(StringUtils.isNoneBlank(type)){
            if(type.equals(Constant.USERNAME)){
                int check = userMapper.checkUserByUsername(str);
                if(check > 0){
                    return ServletResponse.createByErrorMessage("注册失败,该帐号已被注册");
                }
            }
            if(type.equals(Constant.EMAIL)){
                int check = userMapper.checkUserByEmail(str);
                if(check > 0){
                    return ServletResponse.createByErrorMessage("注册失败,该邮箱已被注册");
                }
            }
        }
        else{
            return ServletResponse.createByErrorMessage("参数错误");
        }
        return ServletResponse.createBySuccessMessage("校验成功");
    }

    /**
     * 查询修改密码的问题   判断账户是否存在，再判断问题是否为空
     * @param username
     * @return
     */
    public ServletResponse<String> selectForgetQuestion(String username){
        ServletResponse response = checkValid(username,Constant.USERNAME);
        if(response.isSuccess()){
           return ServletResponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.selectForgetQuestion(username);
        if(StringUtils.isNoneBlank(question)){
            return ServletResponse.createBySuccess(question);
        }

        return ServletResponse.createByErrorMessage("问题为空");


    }

    /**
     * 根据帐号，问题，答案 查询是否存在
     * 存在则生成Token并存入本地缓存方便调用
     * @param username
     * @param question
     * @param answer
     * @return
     */
    public ServletResponse<String> checkAnswer(String username, String question, String answer){
        int check = userMapper.selectAnswer(username,question,answer);
        if(check > 0){
            //使用UUID生成唯一token
            String token = UUID.randomUUID().toString();
            //将值存入缓存中
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,token);
            return ServletResponse.createBySuccess(token);
        }
        return ServletResponse.createByErrorMessage("回答的答案错误");

    }

    /**
     * 未登录状态重置密码  判断请求时发送的token、用户名是否为空，判断本地cache所存储的token是否过期，判断请求时的token和本地token并将密码加密及重置
     * @param username
     * @param passwordNew
     * @param token
     * @return
     */
    public ServletResponse<String> forgetResetPassword(String username, String passwordNew, String token){
        if(StringUtils.isBlank(token)){
            return ServletResponse.createByErrorMessage("参数错误,token未传递");
        }
        ServletResponse<String> validresponse = checkValid(username,Constant.USERNAME);

        if(validresponse.isSuccess()){
            return ServletResponse.createByErrorMessage("用户名不存在");
        }
        String localToken = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(localToken)){
            ServletResponse.createByErrorMessage("Token无效或已过期");
        }
        if(StringUtils.equals(localToken,token)){
            //加密
            String MD5password = MD5Util.MD5EncodeUtf8(passwordNew);
            //修改密码
            int row = userMapper.updatePasswordByUsername(username, MD5password);
            if(row > 0){
                return ServletResponse.createBySuccessMessage("修改密码成功");
            }
        }
        else{
            return ServletResponse.createByErrorMessage("请重新获取修改密码的token");
        }


        return ServletResponse.createByErrorMessage("修改密码失败");
    }

    /**
     * 登录状态下重置密码   通过userid判断用户输入的旧密码是和数据库的密码是否一致，防止横向越权，然后更新密码
     * @param oldPassword
     * @param newPassword
     * @param user
     * @return
     */
    public ServletResponse<String> resetPassword(String oldPassword, String newPassword, User user){
        int checkPassword = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(oldPassword),user.getId());
        if(checkPassword == 0){
            return ServletResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
        int updateInt = userMapper.updateByPrimaryKeySelective(user);
        if(updateInt > 0){
            return ServletResponse.createBySuccessMessage("修改密码成功");
        }
        return ServletResponse.createByErrorMessage("修改密码失败");
    }

    /**
     * 更新个人信息  用户名不进行更新，校验邮箱是否重复
     * @param user
     * @return
     */
    public ServletResponse<User> updateInformation(User user){
        int check = userMapper.checkEmailById(user.getId(),user.getEmail());
        if(check > 0){
            return ServletResponse.createByErrorMessage("更新失败，请更换Email再尝试更新");
        }
        User userinfo = new User();
        userinfo.setId(user.getId());
        userinfo.setEmail(user.getEmail());
        userinfo.setPhone(user.getPhone());
        userinfo.setQuestion(user.getQuestion());
        userinfo.setAnswer(user.getAnswer());

        int updateInt = userMapper.updateByPrimaryKeySelective(userinfo);
        if(updateInt > 0){
            return ServletResponse.createBySuccess("更新个人信息成功",user);
        }
        return ServletResponse.createByErrorMessage("更新个人信息失败");

    }

    /**
     * 获取用户ID并吧密码置空
     * @param id
     * @return
     */
    public ServletResponse<User> getInformation(Integer id){

        User user = userMapper.selectByPrimaryKey(id);
        if(user == null){
            return ServletResponse.createByErrorMessage("找不到当前用户");
        }
        //todo ???
        user.setPassword(StringUtils.EMPTY);

        return ServletResponse.createBySuccess(user);
    }

}
