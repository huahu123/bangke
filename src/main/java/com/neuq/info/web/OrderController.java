package com.neuq.info.web;
import com.neuq.info.entity.User;
import com.neuq.info.enums.OrderEnum;
import com.neuq.info.common.utils.DateTimeUtil;
import com.neuq.info.common.utils.NeiborUtil;
import com.neuq.info.common.utils.OrderUtil;
import com.neuq.info.dto.ResultResponse;
import com.neuq.info.entity.Order;
import com.neuq.info.enums.TemplateMsgEnum;
import com.neuq.info.service.OrderService;
import com.neuq.info.service.TemplateMsgService;
import com.neuq.info.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * created by lindexiang
 * on 下午9:39
 */

@Log4j
@Controller
@RequestMapping("/Order")
@Api(value = "订单相关的API")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private TemplateMsgService templateMsgService;

    @Autowired
    private UserService userService;

    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @ApiOperation(value = "根据orderid获取order")
    @RequestMapping(value = "/OrderInfo", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultResponse orderInfo(@RequestParam(value = "orderId") String orderId, HttpServletRequest request) {
        try {
            Order order = orderService.findOrderByOrderId(orderId);
            if (order == null)
                return new ResultResponse(-1, false, "订单不存在");
            return new ResultResponse(0, true, order);
        } catch (Exception e) {
            log.error(String.format("error: %s", ExceptionUtils.getStackTrace(e)));
            return new ResultResponse(-1, false, e.getMessage());
        }
    }

    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @ApiOperation(value = "帮客查询附近订单")
    @RequestMapping(value = "/Provider/ListOrder", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultResponse listOrder(@RequestParam BigDecimal longitude,
                                 @RequestParam BigDecimal latitude,
                                 @RequestParam(defaultValue = "0") Integer orderStatus,
                                 @RequestParam Double dis, HttpServletRequest request ) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            List<BigDecimal> pos = NeiborUtil.getNeiborPoi(longitude, latitude, dis);
            Order query = Order.builder()
                    .orderStatus(orderStatus)
                    .minlng(pos.get(0))
                    .maxlng(pos.get(1))
                    .minlat(pos.get(2))
                    .maxlat(pos.get(3))
                    .build();

            List<Order> orders = orderService.queryAll(query);
            if (orders == null || orders.size() == 0) {
                return new ResultResponse(0, true, new ArrayList());
            }

            return new ResultResponse(0, true, filterOrder(orders, 1));
        } catch (Exception e) {
            log.error(String.format("error: %s", ExceptionUtils.getStackTrace(e)));
            return new ResultResponse(-1, false, e.getMessage());
        }
    }

    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @ApiOperation(value = "客户查询附近的订单")
    @RequestMapping(value = "/customer/ListOrder", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultResponse customerListOrder(@RequestParam BigDecimal longitude,
                                    @RequestParam BigDecimal latitude,
                                    @RequestParam(defaultValue = "0") Integer orderStatus,
                                    @RequestParam Double dis, HttpServletRequest request ) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            List<BigDecimal> pos = NeiborUtil.getNeiborPoi(longitude, latitude, dis);
            Order query = Order.builder()
                    .orderStatus(orderStatus)
                    .minlng(pos.get(0))
                    .maxlng(pos.get(1))
                    .minlat(pos.get(2))
                    .maxlat(pos.get(3))
                    .build();

            List<Order> orders = orderService.queryAll(query);

            if (orders == null || orders.size() == 0) {
                return new ResultResponse(0, true, new ArrayList());
            }

            return new ResultResponse(0, true, filterOrder(orders, 0));
        } catch (Exception e) {
            log.error(String.format("error: %s", ExceptionUtils.getStackTrace(e)));
            return new ResultResponse(-1, false, e.getMessage());
        }
    }

    //0 表示客户的单子  1 表示帮客的单子
    public List<Order> filterOrder(List<Order> orders, Integer flag) {
        List<Order> ret = new LinkedList<>();
        if (flag == 0) {
            orders.forEach(it -> {
                if (it.getCustomerId() == 0)
                    ret.add(it);
            });
        } else if (flag == 1) {
            orders.forEach(it -> {
                if (it.getProviderId() == 0)
                    ret.add(it);
            });
        }
        return ret;

    }

    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @ApiOperation(value = "帮客接单")
    @RequestMapping(value = "/Provider/EnterOrder", method = RequestMethod.GET,produces =  {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultResponse submitOrder(@RequestParam String orderId, HttpServletRequest request) {

        try {
            Long userId = (Long) request.getAttribute("userId");
            User user = userService.queryUserByUserId(userId);
            Order order = orderService.findOrderByOrderId(orderId);
            //订单不存在
            if (order == null)
                return new ResultResponse(-1, false, "订单不存在");
            //订单不是等待接单的状态
            if (!Objects.equals(order.getOrderStatus(), OrderEnum.DjdOrderStatus.getValue()))
                return new ResultResponse(-1, false, "订单不是待接单状态");
            //订单已经加入帮客了
            if (order.getProviderId() != 0)
                return  new ResultResponse(-1, false, "订单已被接单");
            //不能加入自己创建的订单
            if (Objects.equals(order.getCustomerId(), userId))
                return new ResultResponse(-1, false, "不能接自己创建的订单");

            order.setProviderId(userId);
            order.setOrderStatus(OrderEnum.YjdOrderStatus.getValue());
            orderService.editOrder(order);
            //发送接单通知
            templateMsgService.sendMsg(order, user, TemplateMsgEnum.JDOrderStatus);
            return new ResultResponse(0, true, order);
        } catch (Exception e) {
            log.error(String.format("error: %s", ExceptionUtils.getStackTrace(e)));
            return new ResultResponse(-1, false, e.getMessage());
        }
    }

    @ApiImplicitParam(name = "session", value = "session", required = true, paramType = "header", dataType = "string")
    @ApiOperation(value = "客户买帮客的单子")
    @RequestMapping(value = "/Customer/EnterOrder", method = RequestMethod.GET,produces =  {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultResponse customerBuyOrder(@RequestParam String orderId, HttpServletRequest request) {

        try {
            Long userId = (Long) request.getAttribute("userId");
            Order order = orderService.findOrderByOrderId(orderId);

            //订单不存在
            if (order == null)
                return new ResultResponse(-1, false, "订单不存在");

            //订单不是未支付的状态
            if (!Objects.equals(order.getOrderStatus(), OrderEnum.WzfOrderStatus.getValue()))
                return new ResultResponse(-1, false, "订单不是待接单状态");

            //订单已经被购买
            if (order.getCustomerId() != 0)
                return  new ResultResponse(-1, false, "订单已被购买");
            //不能加入自己创建的订单
            if (Objects.equals(order.getProviderId(), userId))
                return new ResultResponse(-1, false, "不能接自己创建的订单");

            order.setCustomerId(userId);
            order.setOrderStatus(OrderEnum.DjdOrderStatus.getValue());

            orderService.editOrder(order);
            return new ResultResponse(0, true, order);
        } catch (Exception e) {
            log.error(String.format("error: %s", ExceptionUtils.getStackTrace(e)));
            return new ResultResponse(-1, false, e.getMessage());
        }
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
                                      @RequestParam(required = true, defaultValue = "1") Integer queueType,
                                      @RequestParam(required = true) String contactName,
                                      @RequestParam(required = true) String phoneNum,
                                      @RequestParam(required = true, defaultValue = "0") Byte gender,
                                      @RequestParam(required = true) String comment,
                                      @RequestParam(required = true, defaultValue = "0") Double fee,
                                      @RequestParam(required = true, defaultValue = "0") Double extraFee,
                                      @RequestParam(required = true, defaultValue = "") String images,
                                      HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Date st, et;
            st = DateTimeUtil.parseDateTime(startTime,
                    DateTimeUtil.DEFAULT_DATE_TIME_HHmm_FORMAT_PATTERN).toDate();
            et = DateTimeUtil.parseDateTime(arriveTime,
                    DateTimeUtil.DEFAULT_DATE_TIME_HHmm_FORMAT_PATTERN).toDate();
            if (et.before(st))
                return new ResultResponse(-1, false, "到达时间不能早于开始时间");
            if (st.before(DateTime.now().toDate()) || et.before(DateTime.now().toDate()))
                return new ResultResponse(-1, false, "开始时间或者到达时间均不能早于当前时间");

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
            return new ResultResponse(0, true, order);
        } catch (ParseException e) {
            return new ResultResponse(-1, false, "日期格式错误");
        } catch (Exception e) {
            return new ResultResponse(-1, false, "下单失败");
        }
    }

    //帮客发布代排队订单
    @ApiOperation(value = "帮客发布多余的票")
    @ApiImplicitParam(name = "session", value = "session", paramType = "query", dataType = "string")
    @RequestMapping(value = "/provider/CreateOrder", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ResultResponse createOrder(@RequestParam(required = true) String restaurantName,
                                      @RequestParam(required = true) String restaurantLocation,
                                      @RequestParam(required = true) BigDecimal longitude,
                                      @RequestParam(required = true) BigDecimal latitude,
                                      @RequestParam(required = true) Integer restaurantPeople,
                                      @RequestParam(required = true) String contactName,
                                      @RequestParam(required = true) String phoneNum,
                                      @RequestParam(required = true) String comment,
                                      @RequestParam(required = true, defaultValue = "0") Double fee,
                                      @RequestParam(required = true, defaultValue = "") String picUrls,
                                      HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Date st = new Date(); //设置成当前时间
            Date et = getDataShift(st, 1); //结束时间比开始时间多一天

            if (StringUtils.isBlank(picUrls))
                return new ResultResponse(-1, false, "图片地址不能为空");
            //判断picUrls

            try {
                JSONArray jsonArray = JSONArray.fromObject(picUrls);
            } catch (Exception e) {
                return new ResultResponse(-1, false, "图片地址解析失败");
            }

            Order order = new Order();
            order.setRestaurantName(restaurantName);
            order.setRestaurantLocation(restaurantLocation);
            order.setLongitude(longitude);
            order.setLatitude(latitude);
            order.setRestaurantPeople(restaurantPeople);
            order.setQueueType(1);
            order.setContactName(contactName);
            order.setPhoneNum(phoneNum);
            order.setGender((byte) 1);
            order.setComment(comment);
            order.setFee(fee);
            order.setExtraFee((double) 0);
            order.setCustomerId((long) 0);
            order.setProviderId(userId);   //设置为帮客的id
            order.setOrderId(OrderUtil.getOrderIdByUUId());
            order.setStartTime(st);
            order.setArriveTime(et);
            order.setPayCode(OrderUtil.generatePayCode());
            order.setPicUrls(picUrls);

            orderService.createOrder(order);
            return new ResultResponse(0, true, order);
        } catch (Exception e) {
            return new ResultResponse(-1, false, "下单失败");
        }
    }

    private Date getDataShift(Date curDate, int shiftDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(curDate);

        calendar.add(Calendar.DAY_OF_MONTH, shiftDate);
        return calendar.getTime();
    }


}
