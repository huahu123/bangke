package com.neuq.info.service;

import com.neuq.info.entity.User;

/**
 * Created by lihang on 2017/4/23.
 */
public interface UserService {
     User queryUserByOpenId(String openid);
     User queryUserByUserId(Long userId);
     int updateUser(User user);
     int insertUser(User user);
     User decodeUserInfo(String encryptedData,String iv,String sessionKey);


}
