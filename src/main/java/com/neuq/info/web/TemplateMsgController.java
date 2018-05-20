package com.neuq.info.web;

import com.neuq.info.dao.RedisDao;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Lin Dexiang
 * @date 2018/5/21
 */

@Controller
@RequestMapping("/templateMsg")
@Api(value = "")
public class TemplateMsgController {

    @Autowired
    RedisDao redisDao;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @ApiOperation(value = "收集formId")
    @RequestMapping(value = "/getFormId", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public Boolean getFormId(@RequestParam(name = "form_id") String formId,
                             HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        log.info(userId.toString());
        log.info(formId);
        return true;
    }
}
