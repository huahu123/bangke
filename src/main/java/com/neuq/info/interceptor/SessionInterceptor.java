package com.neuq.info.interceptor;

import com.neuq.info.dao.UserDao;
import com.neuq.info.entity.User;
import com.neuq.info.enums.ErrorStatus;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by lihang on 2017/4/19.
 */
@Log4j
public class SessionInterceptor implements HandlerInterceptor {
//    @Autowired
//    private RedisDao redisDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserDao userDao;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {

        String session = request.getHeader("session");
        log.info("请求的session为" + session);
        if (session != null && session != "") {
            String wxSessionObj = (String) redisTemplate.opsForValue().get(session);
//            Object wxSessionObj = redisDao.get(session);
            if (wxSessionObj == null) {
                log.info(session + "已过期");
                getResStr(ErrorStatus.user_identity_expired, response);
                return false;
            } else {
                String wxSessionStr = (String) wxSessionObj;
                String openId = wxSessionStr.split("#")[1];
                User user = userDao.queryUserByOpenId(openId);
                if (user != null) {
                    log.info("此次请求的用户id为" + user.getUserId() + ", openid为" + user.getOpenId());
                    request.setAttribute("userId", user.getUserId());
                    return true;
                } else {
                    getResStr(ErrorStatus.no_userinfo, response);
                    return false;
                }
            }
        } else {
            log.warn("请求数据中session参数为空");
            getResStr(ErrorStatus.user_identity_expired, response);
            return false;
        }
    }

    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    private void getResStr(ErrorStatus errorStatus, HttpServletResponse response) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out = response.getWriter();
            out.print("{\"code\":" + errorStatus.getCode() + ",\"message\":\"" + errorStatus.getMessage() + "\"}");
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
