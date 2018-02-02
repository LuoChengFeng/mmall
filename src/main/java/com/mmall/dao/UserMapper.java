package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserByUsername(String username);

    int checkUserByEmail(String email);

    User selectUser(@Param(value = "username") String username,@Param(value = "password") String password);

    String selectForgetQuestion(String username);

    int selectAnswer(@Param(value = "username") String username,@Param(value = "question") String question,@Param(value = "answer") String answer);

    int updatePasswordByUsername(@Param(value = "username") String username,@Param(value = "password") String passwordNew);
    //注解开发
    @Select("select count(1) from mmall_user where id=#{userid} and password=#{password}")
    int checkPassword(@Param(value = "password") String password,@Param(value = "userid") int userid);

    int checkEmailById(@Param("id") Integer id,@Param("email") String email);
}