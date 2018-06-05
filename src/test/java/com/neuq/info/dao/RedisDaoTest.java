package com.neuq.info.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuq.info.common.utils.wxPayUtil.HttpUtil;
import com.neuq.info.config.WxPayConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by lihang on 2017/4/4.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {

//    @Resource(name = "redisTemplate")
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testSpringRedis() {
        //stringRedisTemplate的操作
        // String读写
        redisTemplate.delete("myStr");
        redisTemplate.opsForValue().set("myStr", "skyLine");
        System.out.println(redisTemplate.opsForValue().get("myStr"));
        System.out.println("---------------");

        // List读写
        redisTemplate.delete("myList");
        redisTemplate.opsForList().rightPush("myList", "T");
        redisTemplate.opsForList().rightPush("myList", "L");
        redisTemplate.opsForList().leftPush("myList", "A");
        List<String> listCache = redisTemplate.opsForList().range(
                "myList", 0, -1);
        for (String s : listCache) {
            System.out.println(s);
        }
        System.out.println("---------------");

        // Set读写
        redisTemplate.delete("mySet");
        redisTemplate.opsForSet().add("mySet", "A");
        redisTemplate.opsForSet().add("mySet", "B");
        redisTemplate.opsForSet().add("mySet", "C");
        Set<String> setCache = redisTemplate.opsForSet().members(
                "mySet");
        for (String s : setCache) {
            System.out.println(s);
        }
        System.out.println("---------------");

        // Hash读写
        redisTemplate.delete("myHash");
        redisTemplate.opsForHash().put("myHash", "BJ", "北京");
        redisTemplate.opsForHash().put("myHash", "SH", "上海");
        redisTemplate.opsForHash().put("myHash", "HN", "河南");
        Map<String, String> hashCache = redisTemplate.opsForHash()
                .entries("myHash");
        for (Map.Entry entry : hashCache.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
        System.out.println("---------------");
    }

    @Test
    public void getAccessTokenTest() {
        String accessToken = (String)redisTemplate.opsForValue().get(WxPayConfig.ACCESS_TOKEN_REDIS_KEY);
        if (null == accessToken || accessToken.length() == 0) {
            StringBuffer sb = new StringBuffer();
            sb.append("grant_type=").append("client_credential");
            sb.append("&appid=").append(WxPayConfig.APP_ID);
            sb.append("&secret=").append(WxPayConfig.APP_SECRET);
            String res = HttpUtil.sendGet(WxPayConfig.URL_ACCESS_TOKEN, sb.toString());
            if (res == null || res.equals("")) {
                return;
            }
            try {
                Map map = new ObjectMapper().readValue(res, Map.class);
                accessToken = (String) map.get("access_token");
                Long expires_in = Long.valueOf(map.get("expires_in").toString());
                redisTemplate.opsForValue().set(WxPayConfig.ACCESS_TOKEN_REDIS_KEY, accessToken, expires_in, TimeUnit.SECONDS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}