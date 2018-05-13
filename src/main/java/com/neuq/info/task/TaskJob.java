package com.neuq.info.task;

import com.neuq.info.dao.OrderDao;
import com.neuq.info.entity.Order;
import com.neuq.info.enums.OrderEnum;
import com.neuq.info.enums.PayEnum;
import com.neuq.info.service.OrderService;
import com.neuq.info.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Lin Dexiang
 * @date 2018/5/5
 */

@Slf4j
@Component("taskJob")
public class TaskJob {

    int i=0;

    @Autowired
    private OrderService orderService;

    @Autowired
    private WxPayService wxPayService;

    @Autowired

    //20s捞一次表的数据
    @Scheduled(fixedDelay = 20000)
    public void job1() {
        Order query = new Order();
        //捞出所有订单
        List<Order> orders = orderService.queryAll(query);
        long currentTime = System.currentTimeMillis();
        Date nowDate = new Date(currentTime);
        Date delayTimeDate = new Date(currentTime + 30 * 60 * 1000);
        for (Order order : orders) {
            //订单一直未付款，当前时间超过开始时间 取消订单
            if (order.getPayStatus() == PayEnum.UnPayStatus.getValue() &&
                    order.getOrderStatus() == OrderEnum.WzfOrderStatus.getValue() &&
                    order.getStartTime().before(nowDate)) {
                orderService.cancelOrder(order);
                break;
            }
            //已支付 当前时间超过开始时间，无人接单
            if (order.getOrderStatus() == OrderEnum.DjdOrderStatus.getValue() &&
                    order.getStartTime().before(nowDate) &&
                    order.getProviderId().equals(0)) {
                wxPayService.refund(order);
                orderService.cancelOrder(order);
                break;
            }

            //已支付 已接单，arriveTime，取消订单
            if (order.getOrderStatus() == OrderEnum.YjdOrderStatus.getValue() &&
                    order.getArriveTime().before(delayTimeDate)) {
                wxPayService.refund(order);
                orderService.cancelOrder(order);
            }
        }
        i++;
        log.info("---Scheduled--- : " + i);
    }
}
