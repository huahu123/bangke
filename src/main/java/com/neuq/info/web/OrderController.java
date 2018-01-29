package com.neuq.info.web;
import com.neuq.info.common.utils.DateTimeUtil;
import com.neuq.info.common.utils.NeiborUtil;
import com.neuq.info.dto.Order1Dto;
import com.neuq.info.dto.OrderDto;
import com.neuq.info.dto.ResultModel;
import com.neuq.info.entity.Order;
import com.neuq.info.enums.ResultStatus;
import com.neuq.info.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
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


    //返回的值还需要修改 不需要将整个订单都返回
    @ApiOperation(value = "根据orderid获取order")
    @RequestMapping(value = "/OrderInfo", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultModel orderInfo(@RequestParam(value = "orderId") String orderId, HttpServletRequest request) {

        Order order = orderService.findOrderByOrderId(orderId);
        if (order == null)
            return new ResultModel(ResultStatus.ORDER_NOT_FOUND);
        return new ResultModel(ResultStatus.SUCCESS, order);
    }

    //查询订单
    @ApiOperation(value = "查询附近订单")
    @RequestMapping(value = "/listOrder", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultModel listOrder(@RequestParam(required = true) BigDecimal longitude,
                                 @RequestParam(required = true) BigDecimal latitude,
                                 @RequestParam(defaultValue = "0", required = true) Integer orderStatus,
                                 @RequestParam(required = true) Double dis) {

        List<Double> pos = NeiborUtil.getNeiborPoi(longitude, latitude, dis);
        HashMap condition = new HashMap();
        condition.put("minlng", pos.get(0));
        condition.put("maxlng", pos.get(1));
        condition.put("minlat", pos.get(2));
        condition.put("maxlat", pos.get(3));
        condition.put("orderStatus", orderStatus);
        List<Order> orders = orderService.listOrderForUser(condition);
        return new ResultModel(ResultStatus.SUCCESS, orders);
    }

    //加入订单  如果是买家发布的订单 或者卖家加入的订单
    @ApiOperation(value = "卖家接单")
    @RequestMapping(value = "/ProviderSubmitOrder", method = RequestMethod.GET,produces =  {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultModel submitOrder(@RequestParam String orderId,
                                   HttpServletRequest request) {
        //卖家的userId  因为在拦截器中已经验证过了
        // 这边这个user肯定存在 而且是唯一的 因为是openid验证的
        // opendi是唯一的
        Long userId = (Long) request.getAttribute("userId");
        Order order = orderService.findOrderByOrderId(orderId);
        //订单不存在
        if (order == null)
            return new ResultModel(ResultStatus.ORDER_NOT_FOUND);
        //订单不是等待接单的状态
        if (order.getOrderStatus() != 0)
            return new ResultModel(ResultStatus.ORDER_SUBMIT_FAIL);
        //订单已经加入帮客了
        if (order.getProviderId() == 0)
            return  new ResultModel(ResultStatus.ORDER_SUBMIT_FAIL);
        //不能加入自己创建的订单
        if (order.getCustomerId() == userId)
            return new ResultModel(ResultStatus.ORDER_SUBMIT_FAIL);

        order.setProviderId(userId);
        order.setOrderStatus(1); //设置订单为接单
        int flag = orderService.editOrder(order);
        if (flag == 0)
            return new ResultModel(ResultStatus.ORDER_SUBMIT_FAIL);
        return new ResultModel(ResultStatus.SUCCESS);
    }

    //客户提交订单
    @ApiOperation(value = "创建订单")
    @RequestMapping(value = "/CreateOrder", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultModel createOrder(@RequestParam String restaurantName,
                                   @RequestParam String restaurantLocation,
                                   @RequestParam BigDecimal longitude,
                                   @RequestParam BigDecimal latitude,
                                   @RequestParam Integer rerstaurantPeople,
                                   @RequestParam String startTime,
                                   @RequestParam String arriveTime,
                                   @RequestParam Integer queueType,
                                   @RequestParam String contactName,
                                   @RequestParam String phoneNum,
                                   @RequestParam Byte gender,
                                   @RequestParam String comment,
                                   @RequestParam Double fee,
                                   @RequestParam Double extraFee,
                                   @RequestParam(defaultValue = "0",required = true) Long customerId,
                                   @RequestParam(defaultValue = "0", required = true) Long providerId) {

        Order order = new Order();
        order.setRestaurantName(restaurantName);
        order.setRestaurantLocation(restaurantLocation);
        order.setLongitude(longitude);
        order.setLatitude(latitude);
        order.setRestaurantPeople(rerstaurantPeople);
        order.setQueueType(queueType);
        order.setContactName(contactName);
        order.setPhoneNum(phoneNum);
        order.setGender(gender);
        order.setComment(comment);
        order.setFee(fee);
        order.setExtraFee(extraFee);
        order.setCustomerId(customerId);
        order.setProviderId(providerId);
        try {
            order.setStartTime(DateTimeUtil.parseDateTime(startTime,
                    DateTimeUtil.DEFAULT_DATE_TIME_HHmm_FORMAT_PATTERN).toDate());
            order.setArriveTime(DateTimeUtil.parseDateTime(arriveTime,
                    DateTimeUtil.DEFAULT_DATE_TIME_HHmm_FORMAT_PATTERN).toDate());
        }catch (Exception e) {
            return new ResultModel(ResultStatus.ORDER_CREATE_FAIL);
        }

        //每次创建订单只允许出现一个0
        if ((customerId == 0 && providerId == 0) || (customerId != 0 && providerId != 0))
            return new ResultModel(ResultStatus.ORDER_CREATE_FAIL);

        int flag = orderService.createOrder(order);
        if (flag == 0)
            return new ResultModel(ResultStatus.ORDER_CREATE_FAIL);
        return new ResultModel(ResultStatus.SUCCESS);
    }

    @ApiOperation(value = "测试创建订单")
    @RequestMapping(value = "/testCreateOrder", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultModel createOrder(@RequestBody Order1Dto order1Dto) {

        System.out.println(order1Dto.orderId + " " + order1Dto.longitude);

        return new ResultModel(ResultStatus.SUCCESS);
    }

}
