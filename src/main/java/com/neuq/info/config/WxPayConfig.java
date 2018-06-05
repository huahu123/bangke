package com.neuq.info.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @AUTHOR lindexiang
 * @DATE 下午9:55
 */
public class WxPayConfig {
    public static final String APP_ID = "wx48000b86228fe774";

    public static final String APP_SECRET = "56660a838c0a74f96a8c1de3b84a295e";

    public static final String MCH_ID = "1493984102";  //商户号

    public static final String URL_UNIFIED_ORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    public static final String URL_NOTIFY =  "https://daluotech.natapp4.cc/wxPay/paySuccess";

    public static final String URL_REFUND = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    public static final String TIME_FORMAT = "yyyyMMddHHmmss";

    public static final int TIME_EXPIRE = 2;  //单位是day

    public static final String KEY_PATH = "/Users/lindexiang/softWare/apiclient_cert.p12";

    public static final String KEY_REFUND = "192006250b4c09247ec02edce69f6a2d";

    public static final String URL_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token";

    public static final String ACCESS_TOKEN_REDIS_KEY = "accesstokenkey123";

    public static final String SEND_TEMPLAYE_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send";

    public static final String GET_PUBLIC_KEY_URL = "https://fraud.mch.weixin.qq.com/risk/getpublickey";

    public static final String PKSC8_PUBLIC_PATH = "/Users/lindexiang/softWare/pksc8_public.pem";

    public static final String WITHDRAW_DESPOSIT_URL = "https://api.mch.weixin.qq.com/mmpaysptrans/pay_bank";

    public static final Map<String, String> bank = new HashMap(){
        {
            put("工商银行", "1002");
            put("农业银行", "1005");
            put("中国银行", "1026");
            put("建设银行", "1003");
            put("招商银行", "1001");
            put("邮储银行", "1066");
            put("交通银行", "1020");
            put("浦发银行", "1004");
            put("民生银行", "1006");
            put("兴业银行", "1009");
            put("平安银行", "1010");
            put("中信银行", "1021");
            put("华夏银行", "1025");
            put("广发银行", "1027");
            put("光大银行", "1022");
            put("北京银行", "1032");
            put("宁波银行", "1056");
        }
    };

}
