package com.neuq.info.dao;

import com.neuq.info.common.utils.DateTimeUtil;
import com.neuq.info.entity.CustomerUser;
import com.neuq.info.entity.ProviderUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by lihang on 2017/4/4.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class ProviderUserDaoTest {
    @Resource
    private ProviderUserDao providerUserDao;

    @Test
    public void insertUser() throws Exception {
        for (int i = 0; i < 10; i++) {
            ProviderUser providerUser = new ProviderUser();
            providerUser.setOpenId("312312");
            providerUser.setAvatarurl("https://");
            providerUser.setNickName("测试");
            providerUser.setGender((byte)1);
            providerUser.setPhoneNum("12345677");
            providerUser.setCreditValue(0);
            providerUser.setUnionId("0");
            providerUser.setCreateTime(DateTimeUtil.now().toDate());
            providerUser.setUpdateTime(DateTimeUtil.now().toDate());
            providerUserDao.insert(providerUser);
        }
    }

    @Test
    public void queryUserById() throws Exception {
        ProviderUser user = providerUserDao.queryUserById(1);
        System.out.println(user);
    }

//    @Test
//    public void queryAllUserByPage() throws Exception {
//        Page page = new Page();
//        page.setCurrentPage(1);
//        page.setTotalNumber(userDao.queryAllUserCount());
//        List<User> list = userDao.queryAllUserByPage(page);
//        System.out.println(list.size());
//    }
//
//    @Test
//    public void queryUserByOpenid() throws Exception {
//        CustomerUser user = customerUserDao.queryUserByOpenId("312312");
//        System.out.println(user);
//    }
//
//    @Test
//    public void queryUserByUnionid() throws Exception {
//        User user = userDao.queryUserByUnionId("111");
//        System.out.println(user);
//    }
//
//    @Test
//    public void queryAllUserByGender() throws Exception {
//        List<User> list = userDao.queryAllUserByGender("1");
//        System.out.println(list.size());
//    }
//
//    @Test
//    public void updateUser() throws Exception {
//        User user = new User();
//        ObjectMapper objectMapper = new ObjectMapper();
//        user = objectMapper.readValue("{\n" +
//                "    \"province\": \"Hebei\",\n" +
//                "    \"openId\": \"oCC_80BgpK_JZy06GIcy3cAUQnNM\",\n" +
//                "    \"language\": \"zh_CN\",\n" +
//                "    \"city\": \"Qinhuangdao\",\n" +
//                "    \"gender\": 1,\n" +
//                "    \"avatarUrl\": \"http://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKicBVNZ9cq6cLSAyjbDR1rSRnNpkNZNW3x9PSRAxYJFtsykBBuia6RDqrkJS6UA778QbDwCsdjlfrg/0\",\n" +
//                "    \"watermark\": {\n" +
//                "        \"timestamp\": 1492792057,\n" +
//                "        \"appid\": \"wx22c990cbb6b3c918\"\n" +
//                "    },\n" +
//                "    \"country\": \"CN\",\n" +
//                "    \"nickName\": \"生活总要向前看11\"\n" +
//                "}", User.class);
//        int res = userDao.updateUser(user);
//        System.out.println(res);
//    }

}