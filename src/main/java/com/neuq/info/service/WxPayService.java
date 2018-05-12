package com.neuq.info.service;

import com.neuq.info.common.utils.wxPayUtil.CommonUtil;
import com.neuq.info.common.utils.wxPayUtil.HttpUtil;
import com.neuq.info.common.utils.wxPayUtil.TimeUtils;
import com.neuq.info.config.WxPayConfig;
import com.neuq.info.entity.PayInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @author Lin Dexiang
 * @date 2018/5/11
 */

@Service
public class WxPayService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());




    /**
     * 调用统一下单接口
     */
//    private boolean unifiedOrder1(User user, Order order, String clientIP) {
//
//        String openId = user.getOpenId().replace("\"", "").trim();
//        String url = WxPayConfig.URL_UNIFIED_ORDER;
//        Integer totalFee = (int) ((order.getFee() + order.getExtraFee()) * 100);
//
//        try {
//
//            PayInfo payInfo = createPayInfo1(openId, clientIP, totalFee);
//            String md5 = getSign(payInfo);
//            payInfo.setSign(md5);
//
//            log.error("md5 value: " + md5);
//
//            String xml = CommonUtil.payInfoToXML(payInfo);
//            xml = xml.replace("__", "_").replace("<![CDATA[1]]>", "1");
//            log.error(xml);
//
//            StringBuffer buffer = HttpUtil.httpsRequest(url, "POST", xml);
//            log.error("unifiedOrder request return body: \n" + buffer.toString());
//            Map<String, String> result = CommonUtil.parseXml(buffer.toString());
//
//
//            String return_code = result.get("return_code");
//            if(StringUtils.isNotBlank(return_code) && return_code.equals("SUCCESS")) {
//
//                String return_msg = result.get("return_msg");
//                if(StringUtils.isNotBlank(return_msg) && !return_msg.equals("OK")) {
//                    //log.error("统一下单错误！");
//                    return "";
//                }
//
//                String prepay_Id = result.get("prepay_id");
//                return prepay_Id;
//
//            } else {
//                return "";
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return "";
//    }




    /**
     * 调用统一下单接口
     * @param openId
     */
    public String unifiedOrder(String openId, String clientIP, String randomNonceStr, Integer totalFee) {

        try {

            String url = WxPayConfig.URL_UNIFIED_ORDER;

            PayInfo payInfo = createPayInfo(openId, clientIP, randomNonceStr, totalFee);
            String md5 = getSign(payInfo);
            payInfo.setSign(md5);

            log.error("md5 value: " + md5);

            String xml = CommonUtil.payInfoToXML(payInfo);
            xml = xml.replace("__", "_").replace("<![CDATA[1]]>", "1");
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

    private PayInfo createPayInfo(String openId, String clientIP, String randomNonceStr, Integer totalFee) {

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
        payInfo.setBody("tcbk");
        payInfo.setAttach("hz");  //附加数据
        payInfo.setOut_trade_no(randomOrderId);
        payInfo.setTotal_fee(totalFee); // 设置支付金额 分
        payInfo.setSpbill_create_ip(clientIP); //支付的ip
        payInfo.setTime_start(timeStart); //支付开始时间
        payInfo.setTime_expire(timeExpire);//支付结束时间
        payInfo.setNotify_url(WxPayConfig.URL_NOTIFY);
        payInfo.setTrade_type("JSAPI");
        payInfo.setLimit_pay("no_credit");
        payInfo.setOpenid(openId);

        return payInfo;
    }

    public String getSign(PayInfo payInfo) throws Exception {
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


//    private String getOpenId(String code) {
//        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + WxPayConfig.APP_ID +
//                "&secret=" + WxPayConfig.APP_SECRET + "&js_code=" + code + "&grant_type=authorization_code";
//
//        HttpUtil httpUtil = new HttpUtil();
//        try {
//
//            HttpResult httpResult = httpUtil.doGet(url, null, null);
//
//            if(httpResult.getStatusCode() == 200) {
//
//                JsonParser jsonParser = new JsonParser();
//                JsonObject obj = (JsonObject) jsonParser.parse(httpResult.getBody());
//
//                log.error("getOpenId: " + obj.toString());
//
//                if(obj.get("errcode") != null) {
//                    log.error("getOpenId returns errcode: " + obj.get("errcode"));
//                    return "";
//                } else {
//                    return obj.get("openid").toString();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

}
