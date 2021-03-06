package com.neuq.info.web;

import com.neuq.info.config.WxPayConfig;
import com.neuq.info.entity.WithdrawDeposit;
import com.neuq.info.enums.OrderEnum;
import com.neuq.info.dto.ResultResponse;
import com.neuq.info.entity.Order;
import com.neuq.info.entity.User;
import com.neuq.info.enums.TemplateMsgEnum;
import com.neuq.info.enums.WithdrawDespositEnum;
import com.neuq.info.service.OrderService;
import com.neuq.info.service.TemplateMsgService;
import com.neuq.info.service.UserService;
import com.neuq.info.service.WxPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.rmi.runtime.Log;

import javax.print.DocFlavor;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * @AUTHOR lindexiang
 * @DATE 下午6:16
 */
@Controller
@RequestMapping("/personal")
@Api(value = "个人中心相关api")
@Log4j
public class UserController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private TemplateMsgService templateMsgService;

    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @RequestMapping(value = "/info", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ApiOperation(notes = "获取个人信息", httpMethod = "GET", value = "获取个人信息")
    @ResponseBody
    public ResultResponse getUserInfo(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            User user = userService.queryUserByUserId(userId);
            if (user == null) {
                return new ResultResponse(-1, false, "用户不存在");
            }
            return new ResultResponse(0, true, user);
        } catch (Exception e) {
            log.error(String.format("error: %s", ExceptionUtils.getStackTrace(e)));
            return new ResultResponse(-1, false, e.getMessage());
        }
    }

    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @ApiOperation(notes = "获取客户全部订单", httpMethod = "GET", value = "获取客户全部订单")
    @RequestMapping(value = "/Customer/listOrder", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultResponse myCustomerOrder(@RequestParam("orderStatus") Integer orderStatus,
                            HttpServletRequest request) {

        try {
            Long userId = (Long) request.getAttribute("userId");
            Order query = Order.builder()
                    .customerId(userId)
                    .orderStatus(orderStatus)
                    .build();
            List<Order> orders = orderService.queryAll(query);
            return new ResultResponse(0, true, orders);
        } catch (Exception e) {
            log.error(String.format("error: %s", ExceptionUtils.getStackTrace(e)));
            return new ResultResponse(-1, false, e.getMessage());
        }
    }


    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @ApiOperation(notes = "获取帮客全部订单", httpMethod = "GET", value = "获取帮客全部订单")
    @RequestMapping(value = "/Provider/listOrder", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultResponse myProviderOrder(@RequestParam("orderStatus") Integer orderStatus,
                                          HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Order query = Order.builder()
                    .providerId(userId)
                    .orderStatus(orderStatus)
                    .build();
            List<Order> orders = orderService.queryAll(query);
            return new ResultResponse(0, true, orders);
        } catch (Exception e) {
            log.error(String.format("error: %s", ExceptionUtils.getStackTrace(e)));
            return new ResultResponse(-1, false, e.getMessage());
        }
    }

    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @ApiOperation(notes = "客户取消订单", httpMethod = "GET", value = "客户取消订单")
    @RequestMapping(value = "/Customer/CancelOrder", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultResponse customerCancelOrder(@RequestParam(value = "orderId", required = true) String orderId,
                                              HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            User user = userService.queryUserByUserId(userId);

            Order order = orderService.findOrderByOrderId(orderId);
            if (null == order)
                return new ResultResponse(-1, false, "订单不存在，无法取消");

            if (order.getOrderStatus() == OrderEnum.YwcOrderStatus.getValue()) {
                return new ResultResponse(-1, false, "订单已完成，无法取消");
            }
            if (order.getOrderStatus() == OrderEnum.YqxOrderStatus.getValue()) {
                return new ResultResponse(-1, false, "订单已取消，无法再次取消");
            }
            if (!order.getCustomerId().equals(userId)) {
                return new ResultResponse(-1, false, "无效订单，无法取消");
            }

            boolean needRefund = true;
            if (Objects.equals(order.getOrderStatus(), OrderEnum.WzfOrderStatus.getValue()))
                needRefund = false;

            if (needRefund) {
                String ret = wxPayService.refund(order);
                if (Objects.equals(ret, ""))
                    return new ResultResponse(-1, false, "订单退款失败，无法取消");
            }

            orderService.cancelOrder(order);
            return new ResultResponse(0, true, "取消订单成功");
        } catch (Exception e) {
            log.error(String.format("error: %s", ExceptionUtils.getStackTrace(e)));
            return new ResultResponse(-1, false, e.getMessage());
        }
    }

    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @ApiOperation(value = "帮客完成订单后上传payCode验证")
    @RequestMapping(value = "/Provider/finishOrder", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultResponse myProviderOrder(@RequestParam String orderId, @RequestParam String payCode,
                                          HttpServletRequest request) {

        try {
            Long userId = (Long) request.getAttribute("userId");
            User user = userService.queryUserByUserId(userId);
            if (payCode.isEmpty())
                return new ResultResponse(-1, false, "验证码不准为空");
            Order query = Order.builder()
                    .orderId(orderId)
                    .providerId(userId)
                    .build();
            List<Order> orders = orderService.queryAll(query);
            if (null == orders || orders.size() == 0)
                return new ResultResponse(-1,false, "订单不存在");
            Order savedOrder = orders.get(0);
            if (!payCode.equals(savedOrder.getPayCode()))
                return new ResultResponse(-1, false,"验证码不对，请重试");
            savedOrder.setOrderStatus(OrderEnum.YwcOrderStatus.getValue());
            orderService.editOrder(savedOrder);
            Float fee = (float)(savedOrder.getExtraFee() + savedOrder.getFee());
            user.setMoney(user.getMoney() + fee);
            userService.updateUser(user);//付钱到电子钱包
            //发送订单完成
            templateMsgService.sendMsg(savedOrder, user, TemplateMsgEnum.WCOrderStatus);
            return new ResultResponse(0, true, "完成订单");
        } catch (Exception e) {
            log.error(String.format("error: %s", ExceptionUtils.getStackTrace(e)));
            return new ResultResponse(-1, false, e.getMessage());
        }
    }

    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @ApiOperation(value = "帮客提现, 输入的金额以分为单位")
    @RequestMapping(value = "/Provider/withdrawDeposit", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    @Transactional
    public ResultResponse myProviderOrder(@RequestParam String cardId, @RequestParam String name, @RequestParam String bankId, @RequestParam Double money,
                                          HttpServletRequest request) {

        try {
            Long userId = (Long) request.getAttribute("userId");
            User user = userService.queryUserByUserId(userId);

            if (StringUtils.isEmpty(cardId))
                return new ResultResponse(-1, false, "卡号不准为空");
            if (StringUtils.isEmpty(bankId))
                return new ResultResponse(-1, false, "银行编号不准为空");
            if (!WxPayConfig.bank.containsValue(bankId))
                return new ResultResponse(-1, false, "不支持该银行提现");
            if (StringUtils.isEmpty(name))
                return new ResultResponse(-1, false, "名字不准为空");

            if (money == null || money <= 0 || money >= 1000)
                return new ResultResponse(-1, false, "金额为空或者负数或者超过1000");
            float amount = user.getMoney(); //余额
            double charge = money * 0.001;
            if (charge < 1)
                charge = 1;
            if (charge > 25)
                charge = 25;
            if (money + charge > amount)
                return new ResultResponse(-1, false, String.format("提现金额:%s, 手续费:%s, 账户余额:%s, 提现金额超过账户余下的钱，提现失败", money, charge, amount));
            WithdrawDeposit withdrawDeposit = WithdrawDeposit.builder()
                    .bankId(bankId)
                    .cardId(cardId)
                    .money(money + charge)
                    .name(name)
                    .userId(userId)
                    .build();

            wxPayService.withDrawDesposit(withdrawDeposit);
            if (Objects.equals(withdrawDeposit.getStatus(), WithdrawDespositEnum.Success.getValue())) {
                //转账成功，扣除账户余额
                user.setMoney((float)(amount - money - charge));
                userService.updateUser(user);
            }
            if (Objects.equals(withdrawDeposit.getStatus(), WithdrawDespositEnum.Fail.getValue())) {
                return new ResultResponse(-1, false, "转账失败");
            }
            return new ResultResponse(0, true, "转账成功");

        } catch (Exception e) {
            log.error(String.format("error: %s", ExceptionUtils.getStackTrace(e)));
            return new ResultResponse(-1, false, e.getMessage());
        }
    }

}
