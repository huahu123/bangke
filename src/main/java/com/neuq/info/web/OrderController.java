package com.neuq.info.web;
import com.neuq.info.entity.User;
import com.neuq.info.enums.OrderEnum;
import com.neuq.info.common.utils.DateTimeUtil;
import com.neuq.info.common.utils.NeiborUtil;
import com.neuq.info.common.utils.OrderUtil;
import com.neuq.info.dto.ResultResponse;
import com.neuq.info.entity.Order;
import com.neuq.info.service.OrderService;
import com.neuq.info.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * created by lindexiang
 * on 下午9:39
 */

@Controller
@RequestMapping("/Order")
@Api(value = "订单相关的API")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @ApiOperation(value = "根据orderid获取order")
    @RequestMapping(value = "/OrderInfo", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultResponse orderInfo(@RequestParam(value = "orderId") String orderId, HttpServletRequest request) {
        Order order = orderService.findOrderByOrderId(orderId);
        if (order == null)
            return new ResultResponse(-1, "订单不存在");
        return new ResultResponse(0, order);
    }

    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @ApiOperation(value = "帮客查询附近订单")
    @RequestMapping(value = "/Provider/ListOrder", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultResponse listOrder(@RequestParam BigDecimal longitude,
                                 @RequestParam BigDecimal latitude,
                                 @RequestParam(defaultValue = "0") Integer orderStatus,
                                 @RequestParam Double dis, HttpServletRequest request ) {

        Long userId = (Long) request.getAttribute("userId");
        List<BigDecimal> pos = NeiborUtil.getNeiborPoi(longitude, latitude, dis);
        Order query = Order.builder()
                .providerId(userId)
                .orderStatus(orderStatus)
                .minlng(pos.get(0))
                .maxlng(pos.get(1))
                .minlat(pos.get(2))
                .maxlat(pos.get(3))
                .build();

        List<Order> orders = orderService.queryAll(query);
        return new ResultResponse(0, orders);
    }

    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @ApiOperation(value = "帮客接单")
    @RequestMapping(value = "/Provider/EnterOrder", method = RequestMethod.GET,produces =  {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultResponse submitOrder(@RequestParam String orderId, HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        Order order = orderService.findOrderByOrderId(orderId);
        //订单不存在
        if (order == null)
            return new ResultResponse(-1, "订单不存在");
        //订单不是等待接单的状态
        if (order.getOrderStatus() != 0)
            return new ResultResponse(-1, "订单不是待接单状态");
        //订单已经加入帮客了
        if (order.getProviderId() != 0)
            return  new ResultResponse(-1, "订单已被接单");
        //不能加入自己创建的订单
        if (order.getCustomerId() == userId)
            return new ResultResponse(-1, "不能接自己创建的订单");

        order.setProviderId(userId);
        order.setOrderStatus(1);
        orderService.editOrder(order);
        return new ResultResponse(0, order);
    }


    //创建订单
    @ApiOperation(value = "发布代排队订单")
    @ApiImplicitParam(name = "session", value = "session", paramType = "query", dataType = "string")
    @RequestMapping(value = "/CreateOrder", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultResponse createOrder(@RequestParam(required = true) String restaurantName,
                                      @RequestParam(required = true) String restaurantLocation,
                                      @RequestParam(required = true) BigDecimal longitude,
                                      @RequestParam(required = true) BigDecimal latitude,
                                      @RequestParam(required = true) Integer restaurantPeople,
                                      @RequestParam(required = true) String startTime,
                                      @RequestParam(required = true) String arriveTime,
                                      @RequestParam(required = true) Integer queueType,
                                      @RequestParam(required = true) String contactName,
                                      @RequestParam(required = true) String phoneNum,
                                      @RequestParam(required = true) Byte gender,
                                      @RequestParam(required = true) String comment,
                                      @RequestParam(required = true) Double fee,
                                      @RequestParam(required = true) Double extraFee,
                                      HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Date st, et;
        try {
            st = DateTimeUtil.parseDateTime(startTime,
                    DateTimeUtil.DEFAULT_DATE_TIME_HHmm_FORMAT_PATTERN).toDate();
            et = DateTimeUtil.parseDateTime(arriveTime,
                    DateTimeUtil.DEFAULT_DATE_TIME_HHmm_FORMAT_PATTERN).toDate();

            if (et.before(st))
                return new ResultResponse(-1, "到达时间不能早于开始时间");
            if (st.before(DateTime.now().toDate()) || et.before(DateTime.now().toDate()))
                return new ResultResponse(-1, "开始时间或者到达时间均不能早于当前时间");
        }catch (Exception e) {
            return new ResultResponse(-1, "时间转化失败");
        }

        Order order = new Order();
        order.setRestaurantName(restaurantName);
        order.setRestaurantLocation(restaurantLocation);
        order.setLongitude(longitude);
        order.setLatitude(latitude);
        order.setRestaurantPeople(restaurantPeople);
        order.setQueueType(queueType);
        order.setContactName(contactName);
        order.setPhoneNum(phoneNum);
        order.setGender(gender);
        order.setComment(comment);
        order.setFee(fee);
        order.setExtraFee(extraFee);
        order.setCustomerId(userId);
        order.setProviderId((long)0);//设置为0表示没有接单
        order.setOrderId(OrderUtil.getOrderIdByUUId());
        order.setStartTime(st);
        order.setArriveTime(et);
        order.setPayCode(OrderUtil.generatePayCode());
        orderService.createOrder(order);
        return new ResultResponse(0, order);
    }



}
