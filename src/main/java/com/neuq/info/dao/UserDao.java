package com.neuq.info.dao;

import com.neuq.info.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {

    int insert(User record);

    int insertUser(User user);

    User queryUserByUserId(long userId);

    User queryUserByOpenId(String openId);

    int updateUser(@Param("user") User user);

    int selectCreateValue(Long autoId);

    List<User> queryAll(@Param("user")User user);

}