package com.neuq.info.web;

import com.neuq.info.dao.RedisDao;
import com.neuq.info.entity.User;
import com.neuq.info.enums.ErrorStatus;
import com.neuq.info.service.UserService;
import com.neuq.info.service.WxService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by lihang on 2017/4/13.
 */
@Controller
@RequestMapping("/wx")
@Api(value = "用户登陆态相关API")
public class WxController {
    @Autowired
    private WxService wxService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisDao redisDao;

    /**
     * 根据客户端传过来的code从微信服务器获取appid和session_key，然后生成3rdkey返回给客户端，后续请求客户端传3rdkey来维护客户端登录态
     *
     * @param wxCode 小程序登录时获取的code
     * @return
     */
    @RequestMapping(value = "/getSession", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(notes = "根据code获取appid和session_key生成3rdkey", httpMethod = "GET", value = "根据code获取appid和session_key生成3rdkey")

    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "小程序登录时获取的code", paramType = "query", dataType = "string")
        }) //这个是swagger的注解
    @ResponseBody
    public Map<String, Object> createSssion(@RequestParam(required = true, value = "code") String wxCode) {
        Map<String, Object> wxSessionMap = wxService.getWxSession(wxCode);

        if (null == wxSessionMap) {
            return rtnParam(ErrorStatus.communication_failure, null);
        }
        //获取异常
        if (wxSessionMap.containsKey("errcode")) {
            return rtnParam(ErrorStatus.failed_get_WeChat_session_key, null);
        }
        String wxOpenId = (String) wxSessionMap.get("openid");
        System.out.println("openid= " + wxOpenId);
        String wxSessionKey = (String) wxSessionMap.get("session_key");
        System.out.println("session_key= " + wxSessionKey);
        Long expires = Long.valueOf(String.valueOf(wxSessionMap.get("expires_in")));
        String thirdSession = wxService.create3rdSession(wxOpenId, wxSessionKey, expires); //thirdSession为key，存储在redis中
        System.out.println("thirdSession= " + thirdSession);
        Map<String, String> map = new HashMap<String, String>();
        map.put("sessionId", thirdSession);
        return rtnParam(ErrorStatus.exist, map);
    }

    /**
     * 验证用户信息完整性
     *
     * @param rawData   微信用户基本信息
     * @param signature 数据签名
     * @param sessionId 会话ID
     * @return
     */
    @RequestMapping(value = "/checkUserInfo", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(notes = "此API暂时不用", httpMethod = "GET", value = "此API暂时不用")

    @ApiImplicitParams({
            @ApiImplicitParam(name = "rawData", value = "小程序登录时获取的code", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "signature", value = "小程序登录时获取的code", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "sessionId", value = "小程序登录时获取的code", paramType = "query", dataType = "string")
    })
    @ResponseBody
    public Map<String, Object> checkUserInfo(@RequestParam(required = true, value = "rawData") String rawData,
                                             @RequestParam(required = true, value = "signature") String signature,
                                             @RequestParam(required = true, defaultValue = "sessionId") String sessionId) {
        Object wxSessionObj = redisDao.get(sessionId);
        if (null == wxSessionObj) {
            return rtnParam(ErrorStatus.user_identity_expired, null);
        }
        String wxSessionStr = (String) wxSessionObj;
        String sessionKey = wxSessionStr.split("#")[0];
        StringBuffer sb = new StringBuffer(rawData);
        sb.append(sessionKey);

        byte[] encryData = DigestUtils.sha1(sb.toString());
        byte[] signatureData = signature.getBytes();
        Boolean checkStatus = Arrays.equals(encryData, signatureData);
        return rtnParam(ErrorStatus.exist, new HashMap().put("checkPass", checkStatus));
    }

    /**
     * 获取用户openId和unionId数据(如果没绑定微信开放平台，解密数据中不包含unionId)
     *
     * @param encryptedData 加密数据
     * @param iv            加密算法的初始向量
     * @return
     */
    @RequestMapping(value = "/decodeUserInfo", method = RequestMethod.POST, produces = "application/json")
    @ApiOperation(notes = "解析用户数据", httpMethod = "GET", value = "解析用户数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "encryptedData", value = "用户加密数据", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "iv", value = "加密算法的初始向量", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "session", value = "登陆后返回的3rd_session", required = true, paramType = "header", dataType = "string")
    })
    @ResponseBody
    public Map<String, Object> decodeUserInfo(@RequestParam(required = true, value = "encryptedData") String encryptedData,
                                              @RequestParam(required = true, defaultValue = "iv") String iv, HttpServletRequest request) {
       String sessionKey = (String) request.getAttribute("sessionKey");
        String openId = (String) request.getAttribute("openId");

        User user = userService.queryUserByOpenId(openId);
        User user1 = userService.decodeUserInfo(encryptedData, iv, sessionKey);
        if (user1 == null) {
            return rtnParam(ErrorStatus.user_sensitive_data_decryption_failed, null);
        }
//        System.out.println(user);
//        System.out.println(user1);
//        System.out.println(user.equals(user1));
        if(user==null){
            if(userService.insertUser(user1)==0){
                return rtnParam(ErrorStatus.insertuserinfo_wrong, null);
            }
        }else {
            if(!user.equals(user1)){
                if(userService.updateUser(user1)==0){
                    return rtnParam(ErrorStatus.updateuserinfo_wrong, null);
                }
            }
        }
        return rtnParam(ErrorStatus.SUCCESS, null);
    }

    protected Map<String, Object> rtnParam(ErrorStatus errorStatus, Object data) {
        //正常的业务逻辑
        Map<String, Object> map;
        if (errorStatus.getCode() == 0) {
            map = new HashMap<String, Object>();
            map.put("code", errorStatus.getCode());
            map.put("message", errorStatus.getMessage());
            map.put("content", (data == null) ? new Object() : data);
            return map;
        } else {
            map = new HashMap<String, Object>();
            map.put("code", errorStatus.getCode());
            map.put("message", errorStatus.getMessage());
            return map;
        }
    }
}
