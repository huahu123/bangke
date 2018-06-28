package com.neuq.info.dao;

import com.neuq.info.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {

    int insertUser(@Param("user") User user);

    User queryUserByOpenId(@Param("openId")String openId);

    int updateUser(@Param("user") User user);

    List<User> queryAll(@Param("user")User user);

}