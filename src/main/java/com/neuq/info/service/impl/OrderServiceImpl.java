//package com.neuq.info.service.impl;
//
//import com.neuq.info.common.utils.DateTimeUtil;
//import com.neuq.info.common.utils.OrderUtil;
//import com.neuq.info.dao.OrderDao;
//import com.neuq.info.dto.ResultModel;
//import com.neuq.info.entity.Comment;
//import com.neuq.info.entity.Order;
//import com.neuq.info.entity.Post;
//import com.neuq.info.enums.ResultStatus;
//import com.neuq.info.service.OrderService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.util.HashMap;
//import java.util.List;
//
//@Service("orderService")
//public class OrderServiceImpl implements OrderService {
//
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    @Resource
//    private OrderDao orderDao;
//
//    @Override
//    public int createOrder(Order order) {
//        return orderDao.insert(order);
//    }
//
//    //修改订单的最终入口 同时要更改update的时间
//    @Override
//    public int editOrder(Order order) {
//        return orderDao.updateByOrderId(order);
//    }
//
//    //查找订单
//    @Override
//    public Order findOrderByOrderId(String orderId) {
//        Order order = orderDao.selectByOrderId(orderId);
//        return order;
//    }
//
//    //删除订单
//    @Override
//    public int DeleteOrder(String orderId) {
//       return orderDao.deleteByOrderId(orderId);
//    }
//
//    @Override
//    public List<Order> listOrderForUser(HashMap conditionMap) {
//        List<Order> orders = orderDao.listOrderByCondition(conditionMap);
//        return orders;
//    }
//}
