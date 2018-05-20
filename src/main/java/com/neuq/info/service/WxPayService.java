package com.neuq.info.service;

import com.neuq.info.common.utils.wxPayUtil.CommonUtil;
import com.neuq.info.common.utils.wxPayUtil.HttpUtil;
import com.neuq.info.common.utils.wxPayUtil.RandomUtils;
import com.neuq.info.common.utils.wxPayUtil.TimeUtils;
import com.neuq.info.config.WxPayConfig;
import com.neuq.info.entity.Order;
import com.neuq.info.entity.PayInfo;
import com.neuq.info.entity.RefundInfo;
import com.neuq.info.entity.User;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.security.KeyStore;
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
    public String unifiedOrder(User user, Order order, String clientIP) {

        String openId = user.getOpenId().replace("\"", "").trim();
        String url = WxPayConfig.URL_UNIFIED_ORDER;
        Integer totalFee = (int) ((order.getFee() + order.getExtraFee()) * 100);

        try {
            PayInfo payInfo = createPayInfo(openId, clientIP, totalFee, order.getOrderId());
            String md5 = getSign(payInfo);
            payInfo.setSign(md5);
            log.info("md5 value: " + md5);
            String xml = CommonUtil.payInfoToXML(payInfo);
            xml = xml.replace("__", "_").replace("<![CDATA[1]]>", "1");
            log.info(xml);

            StringBuffer buffer = HttpUtil.httpsRequest(url, "POST", xml);
            log.info("unifiedOrder request return body: \n" + buffer.toString());
            Map<String, String> result = CommonUtil.parseXml(buffer.toString());
            String return_code = result.get("return_code");
            if(StringUtils.isNotBlank(return_code) && return_code.equals("SUCCESS")) {

                String return_msg = result.get("return_msg");
                if(StringUtils.isNotBlank(return_msg) && !return_msg.equals("OK")) {
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


    private PayInfo createPayInfo(String openId, String clientIP, Integer totalFee, String outTradeNo) {

        Date date = new Date();
        String timeStart = TimeUtils.getFormatTime(date, WxPayConfig.TIME_FORMAT);
        String timeExpire = TimeUtils.getFormatTime(TimeUtils.addDay(date, WxPayConfig.TIME_EXPIRE), WxPayConfig.TIME_FORMAT);
        String randomNonceStr = RandomUtils.generateMixString(32);

        //重新totalFee为1分钱 TODO
        totalFee = 1;

        PayInfo payInfo = new PayInfo();
        payInfo.setAppid(WxPayConfig.APP_ID);
        payInfo.setMch_id(WxPayConfig.MCH_ID);
        payInfo.setDevice_info("WEB");
        payInfo.setNonce_str(randomNonceStr);
        payInfo.setSign_type("MD5");  //默认即为MD5
        payInfo.setBody("tcbk");
        payInfo.setAttach("hz");  //附加数据
        payInfo.setOut_trade_no(outTradeNo);
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

    public String refund(Order order){
        String nonceStr = RandomUtils.generateMixString(32);
        String outTradeNo = order.getOrderId();
        String outRefundNo = outTradeNo;
        Integer totalFee = (int) ((order.getFee() + order.getExtraFee()) * 100);
        //重写Fee TODO
        totalFee = 1;
        RefundInfo refundInfo = RefundInfo.builder()
                .appid(WxPayConfig.APP_ID)
                .mch_id(WxPayConfig.MCH_ID)
                .nonce_str(nonceStr)
                .out_refund_no(order.getOrderId())
                .out_trade_no(order.getOrderId())
                .refund_fee(totalFee)
                .total_fee(totalFee)
                .build();

        try {
            String md5 = getRefundPayInfoSign(refundInfo);
            refundInfo.setSign(md5);
            String xml = CommonUtil.refundInfoToXML(refundInfo);
            xml = xml.replace("__", "_").replace("<![CDATA[1]]>", "1");
            log.info(xml);
            String refundUrl = WxPayConfig.URL_REFUND;
            StringBuffer buffer = refundPost(refundUrl, xml); //发送退款请求
            log.info("refund request return body: \n" + buffer.toString());
            Map<String, String> result = CommonUtil.parseXml(buffer.toString());
            String return_code = result.get("return_code");

            if(StringUtils.isNotBlank(return_code) && return_code.equals("SUCCESS")) {

                String return_msg = result.get("return_msg");
                if(StringUtils.isNotBlank(return_msg) && !return_msg.equals("OK")) {
                    return "";
                }
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "success";

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
                .append("&key=" + WxPayConfig.KEY_REFUND);

        log.error("排序后的拼接参数：" + sb.toString());
        System.out.println();
        return CommonUtil.getMD5(sb.toString().trim()).toUpperCase();
    }

    private String getRefundPayInfoSign(RefundInfo refundInfo) throws Exception {
        StringBuffer sb = new StringBuffer();

        sb.append("appid=" + refundInfo.getAppid())
                .append("&mch_id=" + refundInfo.getMch_id())
                .append("&nonce_str=" + refundInfo.getNonce_str())
                .append("&out_refund_no=" + refundInfo.getOut_refund_no())
                .append("&out_trade_no=" + refundInfo.getOut_trade_no())
                .append("&refund_fee=" + refundInfo.getRefund_fee())
                .append("&total_fee=" + refundInfo.getTotal_fee())
                .append("&key=" + WxPayConfig.KEY_REFUND);
        log.info(sb.toString());
        return CommonUtil.getMD5(sb.toString().trim()).toUpperCase();
    }

    public static StringBuffer refundPost(String url, String xmlParam) {
        StringBuffer sb = new StringBuffer();
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream instream = new FileInputStream(new File(WxPayConfig.KEY_PATH));
            try {
                keyStore.load(instream, WxPayConfig.MCH_ID.toCharArray());
            } finally {
                instream.close();
            }

            // 证书
            SSLContext sslcontext = SSLContexts.custom()
                    .loadKeyMaterial(keyStore, WxPayConfig.MCH_ID.toCharArray())
                    .build();
            // 只允许TLSv1协议
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[]{"TLSv1"},
                    null,
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            //创建基于证书的httpClient,后面要用到
            CloseableHttpClient client = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build();

            HttpPost httpPost = new HttpPost(url);//退款接口
            StringEntity reqEntity = new StringEntity(xmlParam);
            // 设置类型
            reqEntity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(reqEntity);
            CloseableHttpResponse response = client.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                System.out.println(response.getStatusLine());
                if (entity != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
                    String text = "";
                    while ((text = bufferedReader.readLine()) != null) {
                        sb.append(text);
                    }
                }
                EntityUtils.consume(entity);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }

}
