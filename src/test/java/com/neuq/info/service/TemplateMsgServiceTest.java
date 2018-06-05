package com.neuq.info.service;

import com.neuq.info.common.utils.Base64;
import com.neuq.info.common.utils.RSAUtil;
import com.neuq.info.common.utils.SignUtils;
import com.neuq.info.common.utils.wxPayUtil.CommonUtil;
import com.neuq.info.common.utils.wxPayUtil.HttpUtil;
import com.neuq.info.common.utils.wxPayUtil.RandomUtils;
import com.neuq.info.config.WxPayConfig;
import com.neuq.info.entity.Order;
import com.neuq.info.entity.User;
import com.neuq.info.enums.TemplateMsgEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Lin Dexiang
 * @date 2018/5/23
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"classpath:spring/spring-service.xml",
        "classpath:spring/spring-dao.xml", "classpath:spring/spring-web.xml"})
public class TemplateMsgServiceTest {
    @Autowired
    private TemplateMsgService templateMsgService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Test
    public void sendMsgTest() {
        Long userId = 10l;
        String template_id = "0ipo-qhB69lHcgKqIumc51EV3fNKtyX-OjaT4ok9j2Q";
        //获取user
        User user = userService.queryUserByUserId(userId);
        Order order = orderService.findOrderByOrderId("1000000603659405");
        templateMsgService.sendMsg(order, user, TemplateMsgEnum.QXOrderStatus);
        templateMsgService.sendMsg(order, user, TemplateMsgEnum.JDOrderStatus);
        templateMsgService.sendMsg(order, user, TemplateMsgEnum.ZFCGOrderStatus);
        templateMsgService.sendMsg(order, user, TemplateMsgEnum.WCOrderStatus);
    }

    @Test
    public void test33() throws Exception{
        String encBankAcctNo = "62122620100xxxxxxxxx"; //加密的银行账号
        String encBankAcctName = "小郑"; //加密的银行账户名
        //注意 这里的  pksc8_public.pem  是上一步获取微信支付公钥后经openssl 转化成PKCS8格式的公钥
        String keyfile = WxPayConfig.PKSC8_PUBLIC_PATH; //读取PKCS8密钥文件
        PublicKey pub= RSAUtil.getPubKey(keyfile,"RSA");
        String rsa ="RSA/ECB/OAEPWITHSHA-1ANDMGF1PADDING";
        byte[] estr=RSAUtil.encrypt(encBankAcctNo.getBytes(),pub,2048, 11,rsa);   //对银行账号进行加密
        encBankAcctNo = Base64.encode(estr);//并转为base64格式

        System.out.println(encBankAcctNo);
    }


    @Test
    public void test1() throws Exception {
        //1~拼凑所需要传递的参数 map集合 ->查看API,传入参数哪些是必须的
        String encBankAcctNo = "6214852105738218"; //加密的银行账号
        String encBankAcctName = "林德相"; //加密的银行账户名
        String bank_code = "1001"; //银行卡的编号~
        String desc ="test";//转账描述
        String partner_trade_no = RandomUtils.generateString(32);//生成随机号，
        //这里大家没有该方法的，建议使用UUID。随便输出不超过32位的字符串即可
        String nonce_str1 =  RandomUtils.generateString(32);//同上
        String mch_id = WxPayConfig.MCH_ID;//获取商务号的id
        String amount = "10"; //付款金额，单位是分

        //2.0 对“收款方银行卡号”、“收款方用户名”进行“采用标准RSA算法”【付款到银行卡，这点最难】
        //定义自己公钥的路径
        String keyfile = WxPayConfig.PKSC8_PUBLIC_PATH; //读取PKCS8密钥文件
        //RSA工具类提供了，根据加载PKCS8密钥文件的方法
        PublicKey pub = RSAUtil.getPubKey(keyfile, "RSA");
        //rsa是微信付款到银行卡要求我们填充的字符串
        String rsa = "RSA/ECB/OAEPWITHSHA-1ANDMGF1PADDING";
        try {
            byte[] estr = RSAUtil.encrypt(encBankAcctNo.getBytes(), pub, 2048, 11, rsa);
        //对银行账号进行加密
            encBankAcctNo = Base64.encode(estr);//并转为base64格式
            estr = RSAUtil.encrypt(encBankAcctName.getBytes("UTF-8"), pub, 2048, 11, rsa);
            encBankAcctName = Base64.encode(estr); //对银行账户名加密并转为base64
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

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
        String sign = SignUtils.creatSign(parameters1);

        //4.0 把签名放到map集合中【因为签名也要传递过去，看API】
        parameters1.put("sign", sign);

        //5.0 将当前的map结合转化成xml格式 ~~ 在上述第三个难点推荐的文章有该方法
        String reuqestXml = CommonUtil.getRequestXml(parameters1);

        //6.0 发送请求到企业付款到银行的Api。发送请求是一个方法来的POST
        String wxUrl = "https://api.mch.weixin.qq.com/mmpaysptrans/pay_bank"; //获取退款的api接口
        try {
            //调用方法发送了 -- 在上述第三个难点推荐的文章有该方法
            String weixinPost = HttpUtil.httpClientCustomSSL(wxUrl, reuqestXml).toString();
            //7.0 解析返回的xml数据-- 在上述第三个难点推荐的文章有该方法
            Map<String, String> result = CommonUtil.parseXml(weixinPost);
            //8.0根据map中的result_code AND return_code来判断是否成功与失败~~写自己的逻辑

            if ("SUCCESS".equalsIgnoreCase(result.get("result_code"))
                    &&
                    "SUCCESS".equalsIgnoreCase(result.get("return_code"))) {
                //8表示退款成功
                //TODO写自己的逻辑
                //TODO 更改自己的申请单状态，生成记录等等
                System.out.println("123");
            } else {
                //9 表示退款失败
                //TODO 调用service的方法 ，存储失败提现的记录咯


            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
