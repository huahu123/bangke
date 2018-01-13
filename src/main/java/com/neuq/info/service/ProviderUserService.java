package com.neuq.info.service;

import com.neuq.info.entity.User;

/**
 * @AUTHOR lindexiang
 * @DATE 下午9:23
 */
public interface ProviderUserService {

    User queryProviderUserByOpenId(String openid);
    int updateUser(User user);
    int insertUser(User user);
    User decodeUserInfo(String encryptedData,String iv,String sessionKey);

}
