package com.neuq.info.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuq.info.enums.OrderEnum;
import com.neuq.info.common.utils.wxPayUtil.*;
import com.neuq.info.config.WxPayConfig;
import com.neuq.info.entity.Order;
import com.neuq.info.entity.User;
import com.neuq.info.service.OrderService;
import com.neuq.info.service.UserService;
import com.neuq.info.service.WxPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class wxPayController {

    public static Integer ORDER_WZF = 0;//未支付
    public static Integer ORDER_DJD = 1;//待接单
    public static Integer ORDER_YJD = 2;//已接单
    public static Integer ORDER_YWC = 3;//已完成
    public static Integer ORDER_YQX = 4;//已取消

    public static Integer PAY_WZF = 0; //未支付
    public static Integer PAY_YZF = 1;//已支付

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private WxPayService wxPayService;


    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @ApiOperation(value = "订单支付接口")
    @RequestMapping(value = "/preOrderPay", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String preOrderPay(@RequestParam(name = "orderId") String orderId,
                              @RequestParam(name = "totalFee") Integer totalFee,
                              HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.queryUserByUserId(userId);
        Order order = orderService.findOrderByOrderId(orderId);
        String content = null;
        Map map = new HashMap();
        ObjectMapper mapper = new ObjectMapper();

        boolean result = true;
        String info = "";

        String openId = user.getOpenId();
        //openid为空
        if(StringUtils.isBlank(openId)) {
            result = false;
            info = "获取到openId为空";
        } else if(null == order || order.getPayStatus() != 0 ||
                order.getOrderStatus() != OrderEnum.WzfOrderStatus.getValue()) {
            result = false;
            info = "订单出错，支付失败";
        } else {
            openId = openId.replace("\"", "").trim();
            String clientIP = CommonUtil.getClientIp(request);
            log.info("openId: " + openId + ", clientIP: " + clientIP);
            String randomNonceStr = RandomUtils.generateMixString(32);
            String prepayId = wxPayService.unifiedOrder(user, order, clientIP);
            log.info("prepayId: " + prepayId);

            if(StringUtils.isBlank(prepayId)) {
                result = false;
                info = "出错了，未获取到prepayId";
            } else {
                map.put("package", "prepay_id=" + prepayId);
                map.put("nonceStr", randomNonceStr);
                Long timeStamp= System.currentTimeMillis()/1000;
                map.put("timeStamp", timeStamp+"");
                String stringSignTemp = "appId="+ WxPayConfig.APP_ID
                        +"&nonceStr=" + randomNonceStr
                        + "&package=prepay_id=" + prepayId
                        + "&signType=MD5&timeStamp=" + timeStamp;
                String paySign=CommonUtil.sign(stringSignTemp, "&key=" + WxPayConfig.KEY_REFUND, "utf-8").toUpperCase();
                map.put("paySign", paySign);
            }
        }
        try {
            map.put("result", result);
            map.put("info", info);
            content = mapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    @ApiOperation(value = "支付成功接口")
    @RequestMapping(value = "/paySuccess", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public Boolean paySuccess(@RequestParam(name = "orderId") String orderId,
                                  HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Order order = orderService.findOrderByOrderId(orderId);
        if (null == order || order.getPayStatus() != PAY_WZF || order.getOrderStatus() != ORDER_WZF) {
            return false;
        }
        //设置订单
        order.setPayStatus(PAY_YZF);
        order.setOrderStatus(ORDER_DJD);
        orderService.editOrder(order);
        log.info("----支付成功-----");
        return true;
    }

}
