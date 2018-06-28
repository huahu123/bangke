package com.neuq.info.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuq.info.dto.Page;
import com.neuq.info.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.List;

/**
 * Created by lihang on 2017/4/4.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class UserDaoTest {
    @Resource
    private UserDao userDao;

    @Test
    public void insertUser() throws Exception {
        User user = new User();
        ObjectMapper objectMapper = new ObjectMapper();
        user = objectMapper.readValue("{\n" +
                "    \"province\": \"Hebei\",\n" +
                "    \"openid\": \"oCC_80BgpK_JZy06GIcy3cAUQnNM\",\n" +
                "    \"language\": \"zh_CN\",\n" +
                "    \"city\": \"Qinhuangdao\",\n" +
                "    \"gender\": 1,\n" +
                "    \"avatarurl\": \"http://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKicBVNZ9cq6cLSAyjbDR1rSRnNpkNZNW3x9PSRAxYJFtsykBBuia6RDqrkJS6UA778QbDwCsdjlfrg/0\",\n" +
                "    \"watermark\": {\n" +
                "        \"timestamp\": 1492792057,\n" +
                "        \"appid\": \"wx22c990cbb6b3c918\"\n" +
                "    },\n" +
                "    \"country\": \"CN\",\n" +
                "    \"nickname\": \"生活总要向前看\"\n" +
                "}", User.class);
        userDao.insertUser(user);
    }

    @Test
    public void queryAllTest() throws Exception {
        User query = User.builder()
                .userId(10l)
                .build();
        List<User> users = userDao.queryAll(query);
        System.out.println(users.size());
    }

    @Test
    public void testQueryUserByOpenId() {
        User user = userDao.queryUserByOpenId("oPkMC0c3pAPdBUyeB7Q6xWfZcSH0");
        System.out.println(user.toString());
    }


}