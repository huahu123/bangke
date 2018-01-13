package com.neuq.info.web;

import com.neuq.info.dto.ResultModel;
import com.neuq.info.entity.Order;
import com.neuq.info.entity.User;
import com.neuq.info.enums.ResultStatus;
import com.neuq.info.service.OrderService;
import com.neuq.info.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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

    @RequestMapping(value = "/info", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ApiOperation(notes = "获取个人信息", httpMethod = "GET", value = "获取个人信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "session", value = "登陆后返回的3rd_session", required = true, paramType = "header", dataType = "string")
    })
    @ResponseBody
    public ResultModel getUnReadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.queryUserByUserId(userId);
        if (user == null) {
            return new ResultModel(ResultStatus.USER_NOT_FOUND);
        }
        return new ResultModel(ResultStatus.SUCCESS, user);
    }


    @ApiOperation(notes = "获取个人全部订单", httpMethod = "GET", value = "获取个人全部订单")
    @RequestMapping(value = "/myOrder", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "postId", value = "postId", required = true, paramType = "path", dataType = "long"),
            @ApiImplicitParam(name = "session", value = "登陆后返回的3rd_session", required = true, paramType = "header", dataType = "string")
    })
    @ResponseBody
    public ResultModel myOrder(@RequestParam("orderStatus") Integer orderStatus,
                            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        //找到客户或者帮客的id为userid的订单，插入保证这两个id不相同
        HashMap condition = new HashMap();
        condition.put("customerId", userId);
        condition.put("providerId", userId);
        condition.put("orderStatus", 0);
        List<Order> orders = orderService.listOrderForUser(condition);
        if (orders.size() == 0) {
            return new ResultModel(ResultStatus.NO_MORE_DATA);
        }
        return new ResultModel(ResultStatus.SUCCESS, orders);
    }



}
