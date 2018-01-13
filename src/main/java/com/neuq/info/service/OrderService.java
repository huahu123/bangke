package com.neuq.info.service;

import com.neuq.info.dto.ResultModel;
import com.neuq.info.entity.Order;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public interface OrderService {
    //创建订单
    public int createOrder(Order order);

    //修改订单
    public int editOrder(Order order);

    //查找订单
    public Order findOrderByOrderId(String orderId);

    //删除订单
    public int DeleteOrder(String orderId);

    //根据条件查找所有的订单
    public List<Order> listOrderForUser(HashMap conditionMap);
}
