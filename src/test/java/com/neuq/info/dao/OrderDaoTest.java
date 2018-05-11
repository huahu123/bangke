package com.neuq.info.dao;

import com.neuq.info.common.utils.DateTimeUtil;
import com.neuq.info.common.utils.OrderUtil;
import com.neuq.info.entity.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * created by lindexiang
 * on 下午9:22
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class OrderDaoTest {
    @Autowired
    private OrderDao orderDao;

    @Test
    public void testInsert() throws Exception {
        for (int i = 0; i < 10 ; i++) {
            Order order = new Order();
            order.setOrderId(OrderUtil.getOrderIdByUUId());
            order.setRestaurantName("外婆家");
            order.setRestaurantLocation("杭州");
            order.setLongitude(new BigDecimal(0.000000));
            order.setLatitude(new BigDecimal(0.0000));
            order.setRestaurantPeople(1);
            order.setQueueType(0);
            order.setStartTime(DateTimeUtil.parseDateTime("2018-9-12 3:50",
                    DateTimeUtil.DEFAULT_DATE_TIME_HHmm_FORMAT_PATTERN).toDate());
            order.setArriveTime(DateTimeUtil.parseDateTime("2018-9-12 3:51",
                    DateTimeUtil.DEFAULT_DATE_TIME_HHmm_FORMAT_PATTERN).toDate());
            order.setContactName("林德相");
            order.setPhoneNum("13506876963");
            order.setGender((byte)1);
            order.setComment("备注");
            order.setFee(0.01);
            order.setExtraFee(0.01);
            order.setCustomerId(123L);
            order.setProviderId(123L);
            order.setOrderStatus(0);
            order.setCreateTime(DateTimeUtil.now().toDate());
            order.setUpdateTime(DateTimeUtil.now().toDate());
            System.out.println(orderDao.insert(order)); //插入成功返回1 失败返回0
        }
        System.out.println("插入成功");
    }

    @Test
    public void testSelectByOrderId() throws Exception {
        Order order = orderDao.selectByOrderId("1000001435951089");
        System.out.println(order.toString());
        System.out.println("查找成功");

    }
    @Test
    public void testUpdate() throws Exception {
        Order order = orderDao.selectByOrderId("1000001435951089");
        order.setRestaurantName("林德相");
        System.out.println(orderDao.updateByOrderId(order));
        System.out.println("修改成功");
    }

    @Test
    public void testDelete() throws Exception {
        String orderId = "1000002041591743";
        orderDao.deleteByOrderId(orderId);
        System.out.println("删除成功");
    }

    @Test public void testfindNeighPositionByCondition() {

        double latitude = 0;
        double longitude = 0;
        //先计算查询点的经纬度范围
        double r = 6371;//地球半径千米
        double dis = 1;//0.5千米距离
        double dlng =  2*Math.asin(Math.sin(dis/(2*r))/Math.cos(latitude*Math.PI/180));
        dlng = Math.abs(dlng*180/Math.PI);//角度转为弧度
        double dlat = dis/r;
        dlat = dlat*180/Math.PI;
        double minlat =latitude-dlat;
        double maxlat = latitude+dlat;
        double minlng = longitude - dlng;
        double maxlng = longitude + dlng;
        HashMap hashMap = new HashMap();
        hashMap.put("minlng", BigDecimal.valueOf(minlng));
        hashMap.put("maxlng", BigDecimal.valueOf(maxlng));
        hashMap.put("minlat", BigDecimal.valueOf(minlat));
        hashMap.put("maxlat", BigDecimal.valueOf(maxlat));
       // List<Order> orders = orderDao.findNeighPositionByCondition(BigDecimal.valueOf(minlng), BigDecimal.valueOf(maxlng), BigDecimal.valueOf(minlat), BigDecimal.valueOf(maxlat));
        List<Order> orders = orderDao.findNeighPositionByCondition(hashMap);

        System.out.println(orders.size());
    }

    @Test
    public void testlistOrderByCondition() {
        HashMap map = new HashMap();
        map.put("customerId", 123);
        map.put("orderStatus", 0);
        List<Order> orders = orderDao.listOrderByCondition(map);
        System.out.println(orders.size());
    }


    @Test
    public void queryAll() {
        Order query = Order.builder()
                .autoId(127L)
                .build();
        List<Order> orders = orderDao.queryAll(query);
        System.out.println(123);
    }

}
