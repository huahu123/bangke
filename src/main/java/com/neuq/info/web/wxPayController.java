package com.neuq.info.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuq.info.dto.ResultResponse;
import com.neuq.info.enums.OrderEnum;
import com.neuq.info.common.utils.wxPayUtil.*;
import com.neuq.info.config.WxPayConfig;
import com.neuq.info.entity.Order;
import com.neuq.info.entity.User;
import com.neuq.info.enums.PayEnum;
import com.neuq.info.enums.TemplateMsgEnum;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @AUTHOR lindexiang
 * @DATE 上午11:52
 */


@Controller
@RequestMapping("/wxPay")
@Api(value = "微信支付相关api")
@Log4j
public class wxPayController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private TemplateMsgService templateMsgService;

    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @ApiOperation(value = "订单支付接口")
    @RequestMapping(value = "/preOrderPay", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultResponse preOrderPay(@RequestParam(name = "orderId") String orderId,
                              @RequestParam(name = "totalFee") Integer totalFee,
                              HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            User user = userService.queryUserByUserId(userId);
            Order order = orderService.findOrderByOrderId(orderId);
            String content = null;
            ObjectMapper mapper = new ObjectMapper();

            String openId = user.getOpenId();
            //openid为空
            if(StringUtils.isBlank(openId)) {
                return new ResultResponse(-1, false, "获取到openId为空, 订单支付失败");
            }
            if (null == order) {
                return new ResultResponse(-1, false, "订单不存在，支付失败");
            }

            if(!Objects.equals(order.getOrderStatus(), PayEnum.UnPayStatus.getValue())) {
                return new ResultResponse(-1, false, String.format("订单的支付状态为%s, 支付失败", PayEnum.getPayEnum(order.getPayStatus()).getName()));
            }

            if (!Objects.equals(order.getOrderStatus(), OrderEnum.WzfOrderStatus.getValue())) {
                return new ResultResponse(-1, false, String.format("订单的状态为%s, 支付失败", OrderEnum.getOrderEnum(order.getOrderStatus()).getName()));
            }

            openId = openId.replace("\"", "").trim();
            String clientIP = CommonUtil.getClientIp(request);
            String randomNonceStr = RandomUtils.generateMixString(32);
            String prepayId = wxPayService.unifiedOrder(user, order, clientIP);
            log.info(String.format("获取orderId: %s的prepayId: %s", orderId, prepayId));

            if (StringUtils.isBlank(prepayId)) {
                return new ResultResponse(-1, false,"出错了，未获取到prepayId");
            }

            HashMap map = new HashMap<String, String>();
            map.put("package", "prepay_id=" + prepayId);
            map.put("nonceStr", randomNonceStr);
            Long timeStamp = System.currentTimeMillis() / 1000;
            map.put("timeStamp", timeStamp + "");
            String stringSignTemp = "appId=" + WxPayConfig.APP_ID
                    + "&nonceStr=" + randomNonceStr
                    + "&package=prepay_id=" + prepayId
                    + "&signType=MD5&timeStamp=" + timeStamp;
            String paySign = CommonUtil.sign(stringSignTemp, "&key=" + WxPayConfig.KEY_REFUND, "utf-8").toUpperCase();
            map.put("paySign", paySign);


            return new ResultResponse(0, true, "", map);
        } catch (Exception e) {
            log.error(String.format("error: %s", ExceptionUtils.getStackTrace(e)));
            return new ResultResponse(-1, false, e.getMessage());
        }
    }

    @ApiOperation(value = "支付成功接口")
    @RequestMapping(value = "/paySuccess", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultResponse paySuccess(@RequestParam(name = "orderId") String orderId,
                                     HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            User user = userService.queryUserByUserId(userId);
            Order order = orderService.findOrderByOrderId(orderId);
            if (null == order) {
                return new ResultResponse(-1, false, "订单不存在，支付失败");
            }

            if(!Objects.equals(order.getOrderStatus(), PayEnum.UnPayStatus.getValue())) {
                return new ResultResponse(-1, false, String.format("订单的支付状态为%s, 支付失败", PayEnum.getPayEnum(order.getPayStatus()).getName()));
            }

            if (!Objects.equals(order.getOrderStatus(), OrderEnum.WzfOrderStatus.getValue())) {
                return new ResultResponse(-1, false, String.format("订单的状态为%s, 支付失败", OrderEnum.getOrderEnum(order.getOrderStatus()).getName()));
            }
            //设置订单
            order.setPayStatus(PayEnum.AlreadyPayStatsu.getValue());
            order.setOrderStatus(OrderEnum.DjdOrderStatus.getValue());
            orderService.editOrder(order);
            //发送订单支付成功通知
            templateMsgService.sendMsg(order, user, TemplateMsgEnum.ZFCGOrderStatus);
            return new ResultResponse(0, true, "订单支付成功");
        } catch (Exception e) {
            log.error(String.format("error: %s", ExceptionUtils.getStackTrace(e)));
            return new ResultResponse(-1, false, e.getMessage());
        }
    }

}
