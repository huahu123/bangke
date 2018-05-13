package com.neuq.info.web;

import com.neuq.info.enums.OrderEnum;
import com.neuq.info.dto.ResultResponse;
import com.neuq.info.entity.Order;
import com.neuq.info.entity.User;
import com.neuq.info.service.OrderService;
import com.neuq.info.service.UserService;
import com.neuq.info.service.WxPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @AUTHOR lindexiang
 * @DATE 下午6:16
 */
@Controller
@RequestMapping("/personal")
@Api(value = "个人中心相关api")
public class UserController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private WxPayService wxPayService;

    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @RequestMapping(value = "/info", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ApiOperation(notes = "获取个人信息", httpMethod = "GET", value = "获取个人信息")
    @ResponseBody
    public ResultResponse getUnReadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.queryUserByUserId(userId);
        if (user == null) {
            return new ResultResponse(-1, "用户不存在");
        }
        return new ResultResponse(1, user);
    }

    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @ApiOperation(notes = "获取客户全部订单", httpMethod = "GET", value = "获取客户全部订单")
    @RequestMapping(value = "/Customer/listOrder", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultResponse myCustomerOrder(@RequestParam("orderStatus") Integer orderStatus,
                            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        Order query = Order.builder()
                .customerId(userId)
                .orderStatus(orderStatus)
                .build();
        List<Order> orders = orderService.queryAll(query);
        return new ResultResponse(0, orders);
    }


    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @ApiOperation(notes = "获取帮客全部订单", httpMethod = "GET", value = "获取帮客全部订单")
    @RequestMapping(value = "/Provider/listOrder", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultResponse myProviderOrder(@RequestParam("orderStatus") Integer orderStatus,
                                          HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        Order query = Order.builder()
                .providerId(userId)
                .orderStatus(orderStatus)
                .build();
        List<Order> orders = orderService.queryAll(query);
        return new ResultResponse(0, orders);
    }

    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @ApiOperation(notes = "客户取消订单", httpMethod = "GET", value = "客户取消订单")
    @RequestMapping(value = "/Customer/CancelOrder", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultResponse customerCancelOrder(@RequestParam(value = "orderId", required = true) String orderId,
                                              HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.queryUserByUserId(userId);

        Order order = orderService.findOrderByOrderId(orderId);
        if (null == order)
            return new ResultResponse(-1, "订单不存在，无法取消");

        if (order.getOrderStatus() == OrderEnum.YwcOrderStatus.getValue()) {
            return new ResultResponse(-1, "订单已完成，无法取消");
        }
        if (order.getOrderStatus() == OrderEnum.YqxOrderStatus.getValue()) {
            return new ResultResponse(-1, "订单已取消，无法再次取消");
        }
        if (!order.getCustomerId().equals(userId)) {
            return new ResultResponse(-1, "无效订单，无法取消");
        }

        boolean needRefund = true;
        if (order.getOrderStatus() == OrderEnum.WzfOrderStatus.getValue())
            needRefund = false;

        if (needRefund) {
            String ret = wxPayService.refund(order);
            if (ret == "")
                return new ResultResponse(-1, "订单退款失败，无法取消");
        }

        orderService.cancelOrder(order);
        return new ResultResponse(0, "取消订单成功");
    }

    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @ApiOperation(value = "帮客完成订单后上传payCode验证")
    @RequestMapping(value = "/Provider/finishOrder", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultResponse myProviderOrder(@RequestParam String orderId, @RequestParam String payCode,
                                          HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        User user = userService.queryUserByUserId(userId);
        if (payCode.isEmpty())
            return new ResultResponse(-1, "验证码不准为空");
        Order query = Order.builder()
                .orderId(orderId)
                .providerId(userId)
                .build();
        List<Order> orders = orderService.queryAll(query);
        if (null == orders || orders.size() == 0)
            return new ResultResponse(-1, "订单不存在");
        Order savedOrder = orders.get(0);
        if (!payCode.equals(savedOrder.getPayCode()))
            return new ResultResponse(-1, "验证码不对，请重试");
        savedOrder.setOrderStatus(OrderEnum.YwcOrderStatus.getValue());
        orderService.editOrder(savedOrder);
        //TODO 微信支付，设置个人的钱包
        Float fee = (float)((savedOrder.getExtraFee() + savedOrder.getFee()));
        user.setMoney(user.getMoney() + fee);
        userService.updateUser(user);//付钱到电子钱包
        return new ResultResponse(0, "完成订单");
    }

}
