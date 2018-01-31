package com.neuq.info.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.neuq.info.common.utils.wxPayUtil.*;
import com.neuq.info.config.WxPayConfig;
import com.neuq.info.entity.PayInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @AUTHOR lindexiang
 * @DATE 上午11:52
 */

@Controller
@RequestMapping("/wxPay")
@Api(value = "微信支付相关api")
public class wxPayController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @ApiOperation(value = "预下单")
    @RequestMapping(value = "/prePay", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String prePay(@RequestParam(name = "code")String code, HttpServletRequest request) {
        String content = null;
        Map map = new HashMap();
        ObjectMapper mapper = new ObjectMapper();

        boolean result = true;
        String info = "";

        log.info("\n======================================================");
        log.info("code: " + code);

        String openId = getOpenId(code);
        if(StringUtils.isBlank(openId)) {
            result = false;
            info = "获取到openId为空";
        } else {
            openId = openId.replace("\"", "").trim();

            String clientIP = CommonUtil.getClientIp(request);

            log.info("openId: " + openId + ", clientIP: " + clientIP);

            String randomNonceStr = RandomUtils.generateMixString(32);
            String prepayId = unifiedOrder(openId, clientIP, randomNonceStr);

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
                String paySign=CommonUtil.sign(stringSignTemp, "&key=" + WxPayConfig.APP_KEY, "utf-8").toUpperCase();
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
    public void paySuccess(HttpServletRequest request) {
        log.info("----支付成功-----");
    }



    private String getOpenId(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + WxPayConfig.APP_ID +
                "&secret=" + WxPayConfig.APP_SECRET + "&js_code=" + code + "&grant_type=authorization_code";

        HttpUtil httpUtil = new HttpUtil();
        try {

            HttpResult httpResult = httpUtil.doGet(url, null, null);

            if(httpResult.getStatusCode() == 200) {

                JsonParser jsonParser = new JsonParser();
                JsonObject obj = (JsonObject) jsonParser.parse(httpResult.getBody());

                log.error("getOpenId: " + obj.toString());

                if(obj.get("errcode") != null) {
                    log.error("getOpenId returns errcode: " + obj.get("errcode"));
                    return "";
                } else {
                    return obj.get("openid").toString();
                }
                //return httpResult.getBody();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 调用统一下单接口
     * @param openId
     */
    private String unifiedOrder(String openId, String clientIP, String randomNonceStr) {

        try {

            String url = WxPayConfig.URL_UNIFIED_ORDER;

            PayInfo payInfo = createPayInfo(openId, clientIP, randomNonceStr);
            String md5 = getSign(payInfo);
            payInfo.setSign(md5);

            log.error("md5 value: " + md5);

            String xml = CommonUtil.payInfoToXML(payInfo);
            xml = xml.replace("__", "_").replace("<![CDATA[1]]>", "1");
            //xml = xml.replace("__", "_").replace("<![CDATA[", "").replace("]]>", "");
            log.error(xml);

            StringBuffer buffer = HttpUtil.httpsRequest(url, "POST", xml);
            log.error("unifiedOrder request return body: \n" + buffer.toString());
            Map<String, String> result = CommonUtil.parseXml(buffer.toString());


            String return_code = result.get("return_code");
            if(StringUtils.isNotBlank(return_code) && return_code.equals("SUCCESS")) {

                String return_msg = result.get("return_msg");
                if(StringUtils.isNotBlank(return_msg) && !return_msg.equals("OK")) {
                    //log.error("统一下单错误！");
                    return "";
                }

                String prepay_Id = result.get("prepay_id");
                return prepay_Id;

            } else {
                return "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private PayInfo createPayInfo(String openId, String clientIP, String randomNonceStr) {

        Date date = new Date();
        String timeStart = TimeUtils.getFormatTime(date, WxPayConfig.TIME_FORMAT);
        String timeExpire = TimeUtils.getFormatTime(TimeUtils.addDay(date, WxPayConfig.TIME_EXPIRE), WxPayConfig.TIME_FORMAT);

        String randomOrderId = CommonUtil.getRandomOrderId();

        PayInfo payInfo = new PayInfo();
        payInfo.setAppid(WxPayConfig.APP_ID);
        payInfo.setMch_id(WxPayConfig.MCH_ID);
        payInfo.setDevice_info("WEB");
        payInfo.setNonce_str(randomNonceStr);
        payInfo.setSign_type("MD5");  //默认即为MD5
        payInfo.setBody("同城帮客-代排队");
        payInfo.setAttach("杭州");  //附加数据
        payInfo.setOut_trade_no(randomOrderId);
        payInfo.setTotal_fee(1); // 设置支付金额 分
        payInfo.setSpbill_create_ip(clientIP); //支付的ip
        payInfo.setTime_start(timeStart); //支付开始时间
        payInfo.setTime_expire(timeExpire);//支付结束时间
        payInfo.setNotify_url(WxPayConfig.URL_NOTIFY);
        payInfo.setTrade_type("JSAPI");
        payInfo.setLimit_pay("no_credit");
        payInfo.setOpenid(openId);

        return payInfo;
    }

    private String getSign(PayInfo payInfo) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("appid=" + payInfo.getAppid())
                .append("&attach=" + payInfo.getAttach())
                .append("&body=" + payInfo.getBody())
                .append("&device_info=" + payInfo.getDevice_info())
                .append("&limit_pay=" + payInfo.getLimit_pay())
                .append("&mch_id=" + payInfo.getMch_id())
                .append("&nonce_str=" + payInfo.getNonce_str())
                .append("&notify_url=" + payInfo.getNotify_url())
                .append("&openid=" + payInfo.getOpenid())
                .append("&out_trade_no=" + payInfo.getOut_trade_no())
                .append("&sign_type=" + payInfo.getSign_type())
                .append("&spbill_create_ip=" + payInfo.getSpbill_create_ip())
                .append("&time_expire=" + payInfo.getTime_expire())
                .append("&time_start=" + payInfo.getTime_start())
                .append("&total_fee=" + payInfo.getTotal_fee())
                .append("&trade_type=" + payInfo.getTrade_type())
                .append("&key=" + WxPayConfig.APP_KEY);

        log.error("排序后的拼接参数：" + sb.toString());
        System.out.println();

        return CommonUtil.getMD5(sb.toString().trim()).toUpperCase();
    }


}
