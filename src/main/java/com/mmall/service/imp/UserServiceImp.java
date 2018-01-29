package com.mmall.service.imp;

import com.mmall.common.ServletResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userServiceInterface")
public class UserServiceImp implements UserServiceInterface
{
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServletResponse<User> Login(String username, String password) {

        if(userMapper.checkUserByUsername(username) == 0){
            return ServletResponse.createByErrorMessage("用户名不存在");
        }

        //todo 密码MD5加密

        User user = userMapper.selectUser(username,password);

        if(user == null){
            return ServletResponse.createByErrorMessage("密码不正确");
        }
        else{
            return ServletResponse.createBySuccess("登录成功",user);
        }

    }
}
