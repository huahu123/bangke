package com.neuq.info.service;

import com.neuq.info.common.utils.Base64;
import com.neuq.info.common.utils.RSAUtil;
import com.neuq.info.common.utils.SignUtils;
import com.neuq.info.common.utils.wxPayUtil.CommonUtil;
import com.neuq.info.common.utils.wxPayUtil.HttpUtil;
import com.neuq.info.common.utils.wxPayUtil.RandomUtils;
import com.neuq.info.common.utils.wxPayUtil.TimeUtils;
import com.neuq.info.config.WxPayConfig;
import com.neuq.info.entity.*;
import com.neuq.info.enums.WithdrawDespositEnum;
import com.neuq.info.exception.SignException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import java.util.*;

/**
 * @author Lin Dexiang
 * @date 2018/5/11
 */
@Service
public class WxPayService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WithdrawDespositService withdrawDespositService;

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

    /**
     * 银行卡提现
     * */
    public void withDrawDesposit(WithdrawDeposit withdrawDeposit) throws Exception {
        //1~拼凑所需要传递的参数 map集合 ->查看API,传入参数哪些是必须的
        String encBankAcctNo = withdrawDeposit.getCardId(); //加密的银行账号
        String encBankAcctName = withdrawDeposit.getName(); //加密的银行账户名
        String bank_code = withdrawDeposit.getBankId(); //银行卡的编号~
        String desc ="TCBK";//转账描述
        String partner_trade_no = RandomUtils.generateString(32);//生成随机号，
        //这里大家没有该方法的，建议使用UUID。随便输出不超过32位的字符串即可
        String nonce_str1 =  RandomUtils.generateString(32);//同上
        String mch_id = WxPayConfig.MCH_ID;//获取商务号的id
        String amount = String.valueOf(withdrawDeposit.getMoney()/100); //付款金额，单位是分

        //2.0 对“收款方银行卡号”、“收款方用户名”进行“采用标准RSA算法”【付款到银行卡，这点最难】定义自己公钥的路径
        String keyfile = WxPayConfig.PKSC8_PUBLIC_PATH; //读取PKCS8密钥文件
        //RSA工具类提供了，根据加载PKCS8密钥文件的方法
        PublicKey pub = RSAUtil.getPubKey(keyfile, "RSA");
        //rsa是微信付款到银行卡要求我们填充的字符串
        String rsa = "RSA/ECB/OAEPWITHSHA-1ANDMGF1PADDING";

        byte[] estr = new byte[0];
        try {
            estr = RSAUtil.encrypt(encBankAcctNo.getBytes(), pub, 2048, 11, rsa);
        } catch (Exception e) {
            throw new SignException(String.format("userId: %s, 银行卡提现时签名失败", withdrawDeposit.getUserId()), e);
        }
        //对银行账号进行加密
        encBankAcctNo = Base64.encode(estr);//并转为base64格式
        try {
            estr = RSAUtil.encrypt(encBankAcctName.getBytes("UTF-8"), pub, 2048, 11, rsa);
        } catch (Exception e) {
            throw new SignException(String.format("userId: %s, 银行卡提现时对银行账户加密失败", withdrawDeposit.getUserId()), e);
        }
        encBankAcctName = Base64.encode(estr); //对银行账户名加密并转为base64

        //3.0   根据要传递的参数生成自己的签名
        SortedMap<String, String> parameters1 = new TreeMap<String, String>();
        parameters1.put("mch_id", mch_id);
        parameters1.put("partner_trade_no", partner_trade_no);
        parameters1.put("nonce_str", nonce_str1);
        parameters1.put("enc_bank_no", encBankAcctNo);
        parameters1.put("enc_true_name", encBankAcctName);
        parameters1.put("bank_code", bank_code);
        parameters1.put("amount", amount);
        parameters1.put("desc", desc);
        //直接调用签名方法，在上述第三个难点中可以找到哦
        String sign = null;
        sign = SignUtils.creatSign(parameters1);


        //4.0 把签名放到map集合中【因为签名也要传递过去，看API】
        parameters1.put("sign", sign);

        //5.0 将当前的map结合转化成xml格式 ~~ 在上述第三个难点推荐的文章有该方法
        String reuqestXml = CommonUtil.getRequestXml(parameters1);

        //6.0 发送请求到企业付款到银行的Api。发送请求是一个方法来的POST
        String wxUrl = WxPayConfig.WITHDRAW_DESPOSIT_URL; //获取退款的api接口
        Map<String, String> result = new HashMap<>();
        //调用方法发送了 -- 在上述第三个难点推荐的文章有该方法
        String weixinPost = HttpUtil.httpClientCustomSSL(wxUrl, reuqestXml).toString();
        //7.0 解析返回的xml数据-- 在上述第三个难点推荐的文章有该方法
        result = CommonUtil.parseXml(weixinPost);
        //8.0根据map中的result_code AND return_code来判断是否成功与失败~~写自己的逻辑

        if ("SUCCESS".equalsIgnoreCase(result.get("result_code"))
                &&
                "SUCCESS".equalsIgnoreCase(result.get("return_code"))) {

            withdrawDeposit.setComment("转账成功");
            withdrawDeposit.setStatus(WithdrawDespositEnum.Success.getValue());
        } else {
            //9 表示退款失败
            withdrawDeposit.setComment(result.get("return_msg"));
            withdrawDeposit.setStatus(WithdrawDespositEnum.Fail.getValue());
        }
        withdrawDespositService.insertWithdrawDesposit(withdrawDeposit);
    }


    private PayInfo createPayInfo(String openId, String clientIP, Integer totalFee, String outTradeNo) {

        Date date = new Date();
        String timeStart = TimeUtils.getFormatTime(date, WxPayConfig.TIME_FORMAT);
        String timeExpire = TimeUtils.getFormatTime(TimeUtils.addDay(date, WxPayConfig.TIME_EXPIRE), WxPayConfig.TIME_FORMAT);
        String randomNonceStr = RandomUtils.generateMixString(32);

        //重新totalFee为1分钱 TODO
        totalFee = 500;

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
            StringBuffer buffer = HttpUtil.httpClientCustomSSL(refundUrl, xml); //发送退款请求
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







}
