package com.neuq.info.dao;

import com.neuq.info.entity.Order;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.List;

@Repository
public interface OrderDao {

    int insert(Order record);

    int updateByOrderId(Order record);

    List<Order> queryAll(@Param("order") Order order);

    Order selectByOrderId(String orderId);

    int deleteByOrderId(String orderId);

    List<Order> findNeighPositionByCondition(HashMap hashMap);

    List<Order> listOrderByCondition(HashMap hashmap);

}