package com.neuq.info.service;

import com.neuq.info.entity.User;
import com.neuq.info.enums.OrderEnum;
import com.neuq.info.dao.OrderDao;
import com.neuq.info.entity.Order;
import com.neuq.info.enums.TemplateMsgEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private TemplateMsgService templateMsgService;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private UserService userService;

    public int createOrder(Order order) {
        return orderDao.insert(order);
    }

    public int editOrder(Order order) {
        return orderDao.updateByOrderId(order);
    }

    public Order findOrderByOrderId(String orderId) {
        Order order = orderDao.selectByOrderId(orderId);
        return order;
    }

    public int DeleteOrder(String orderId) {
        return orderDao.deleteByOrderId(orderId);
    }

    public List<Order> listOrderForUser(HashMap conditionMap) {
        List<Order> orders = orderDao.listOrderByCondition(conditionMap);
        return orders;
    }

    public List<Order> queryAll(Order order) {
        return orderDao.queryAll(order);
    }

    //TODO
    public void cancelOrder(Order order) {
        order.setOrderStatus(OrderEnum.YqxOrderStatus.getValue());
        orderDao.updateByOrderId(order);
        User user = userService.queryUserByUserId(order.getCustomerId());
        //发送取消订单的模版
        templateMsgService.sendMsg(order, user, TemplateMsgEnum.QXOrderStatus);
    }

}


