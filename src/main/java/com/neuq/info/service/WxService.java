package com.neuq.info.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuq.info.common.utils.wxPayUtil.HttpUtil;
import com.neuq.info.config.WxPayConfig;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Lin Dexiang
 * @date 2018/5/11
 */
@Log4j
@Service
public class WxService {

//    @Autowired
//    private RedisDao redisDao;
    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private HashMap<String, String> wxConfig;

    public Map<String, Object> getWxSession(String wxCode) {
        StringBuffer sb = new StringBuffer();
        sb.append("appid=").append(wxConfig.get("appId"));
        sb.append("&secret=").append(wxConfig.get("secret"));
        sb.append("&js_code=").append(wxCode);
        sb.append("&grant_type=").append(wxConfig.get("grantType"));
        String res = HttpUtil.sendGet((String)wxConfig.get("sessionHost"), sb.toString());
        if (res == null || res.equals("")) {
            return null;
        }
        try {
            return new ObjectMapper().readValue(res, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAccessToken() {
        String accessToken = (String) redisTemplate.opsForValue().get(WxPayConfig.ACCESS_TOKEN_REDIS_KEY);
        if (null == accessToken || accessToken.length() == 0) {
            StringBuffer sb = new StringBuffer();
            sb.append("grant_type=").append("client_credential");
            sb.append("&appid=").append(WxPayConfig.APP_ID);
            sb.append("&secret=").append(WxPayConfig.APP_SECRET);
            String res = HttpUtil.sendGet(WxPayConfig.URL_ACCESS_TOKEN, sb.toString());
            if (res == null || res.equals("")) {
                return null;
            }
            try {
                Map map = new ObjectMapper().readValue(res, Map.class);
                accessToken = (String) map.get("access_token");
                Long expires_in = Long.valueOf(map.get("expires_in").toString());
                log.info("获取的access_token=" + accessToken + ",expires_in=" + expires_in);
                redisTemplate.opsForValue().set(WxPayConfig.ACCESS_TOKEN_REDIS_KEY, accessToken, expires_in, TimeUnit.SECONDS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return accessToken;
    }

    public String create3rdSession(String wxOpenId, String wxSessionKey, Long expires) {
        String thirdSessionKey = RandomStringUtils.randomAlphanumeric(64);
        StringBuffer sb = new StringBuffer();
        sb.append(wxSessionKey).append("#").append(wxOpenId);
        redisTemplate.opsForValue().set(thirdSessionKey, sb.toString(), expires, TimeUnit.SECONDS);
        log.info(sb.toString());
        return thirdSessionKey;
    }
}
