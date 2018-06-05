package com.neuq.info.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuq.info.common.utils.wxPayUtil.HttpUtil;
import com.neuq.info.config.TemplateMsgConfig;
import com.neuq.info.config.WxPayConfig;
import com.neuq.info.entity.Order;
import com.neuq.info.entity.Template;
import com.neuq.info.entity.TemplateParam;
import com.neuq.info.entity.User;
import com.neuq.info.enums.TemplateMsgEnum;
import lombok.extern.log4j.Log4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lin Dexiang
 * @date 2018/5/23
 */
@Log4j
@Service
public class TemplateMsgService {

    @Autowired
    private WxService wxService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;


    /**
     * 0 取消订单
     * 1 接单通知
     * 2 订单支付成功通知
     * 3 订单完成通知
     * */
    public void sendMsg(Order order, User user, TemplateMsgEnum templateMsgEnum) {

        String open_id = user.getOpenId();
        //formId
        String formId = getFormId(user.getUserId());
        if (StringUtils.isEmpty(formId)) {
            log.error("给openId=" + open_id +"发送模版消息时获取不到有效的formId,无法发送");
            return;
        }

        String access_Token = wxService.getAccessToken();
        if (StringUtils.isEmpty(access_Token)) {
            log.error("给openId=" + open_id + "发送模版消息时获取access_Token为空");
            return;
        }

        Template t = null;
        if (templateMsgEnum.getValue() == 0) {
            //取消订单
            t = generatorCancelOrderMsg(order, formId, open_id);

        } else if (templateMsgEnum.getValue() == 1){
            t = generatorReceiveOrderMsg(order, formId, open_id);
        } else if (templateMsgEnum.getValue() == 2) {
            t = generatorSuccessPayOrderMsg(order, formId, open_id);
        } else if (templateMsgEnum.getValue() == 3) {
            t = generatorFinishOrderMsg(order, formId, open_id);
        }
        if ( null == t) {
            log.info("Template为null，发送消息失败");
            return;
        }

        String jsonString = JSONObject.fromObject(t).toString();
        String url = WxPayConfig.SEND_TEMPLAYE_MESSAGE_URL + "?access_token=" + access_Token;
        log.info("发送模版消息的url=" + url);
        try {
            StringBuffer buffer = HttpUtil.httpsRequest(url, "POST", jsonString);
            Map map = new ObjectMapper().readValue(buffer.toString(), Map.class);
            if (map.get("errcode").equals(0)) {
                log.info("给openId=" + open_id + "发送模版消息成功");
            } else {
                log.info("给openId=" + open_id + "发送模版消息失败,错误原因是" + map.get("errmsg"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFormId(Long userId) {
        String formId = (String) redisTemplate.opsForList().leftPop(String.valueOf(userId));
        return formId;
    }

    private Template generatorCancelOrderMsg(Order order, String formId, String open_id){

        Map<String, TemplateParam> m = new HashMap<String,TemplateParam>();
        TemplateParam keyword1 = new TemplateParam();
        keyword1.setValue(order.getCreateTime().toString());
        m.put("keyword1", keyword1);

        TemplateParam keyword2 = new TemplateParam();
        String price = String.valueOf(order.getAllFee());
        keyword2.setValue(price + "元");
        m.put("keyword2", keyword2);

        TemplateParam keyword3 = new TemplateParam();
        keyword3.setValue(order.getOrderId());
        m.put("keyword3", keyword3);

        TemplateParam keyword4 = new TemplateParam();
        keyword4.setValue("订单已成功申请" + price + "元退款，请关注您的账户变动");
        m.put("keyword4", keyword4);

        TemplateParam keyword5 = new TemplateParam();
        keyword5.setValue("订单没有在指定时间内完成，自动取消");
        m.put("keyword5", keyword5);

        Template t = Template.builder()
                .page("")
                .touser(open_id)
                .topColor("#000000")
                .template_id(TemplateMsgConfig.CANCLE_ORDER_MSG_ID)
                .form_id(formId)
                .data(m)
                .build();
        return t;
    }

    //接单通知
    private Template generatorReceiveOrderMsg(Order order, String formId, String open_id) {

        //订单号
        Map<String, TemplateParam> m = new HashMap<String,TemplateParam>();
        TemplateParam keyword1 = new TemplateParam();
        keyword1.setValue(order.getOrderId());
        m.put("keyword1", keyword1);

        //接单时间
        TemplateParam keyword2 = new TemplateParam();
        keyword2.setValue(new Date().toString());
        m.put("keyword2", keyword2);

        //接单人
        User user = userService.queryUserByUserId(order.getProviderId());
        TemplateParam keyword3 = new TemplateParam();
        keyword3.setValue("");//TODO
        m.put("keyword3", keyword3);

        //联系电话
        TemplateParam keyword4 = new TemplateParam();
        keyword4.setValue("无");
        m.put("keyword4", keyword4);

        Template t = Template.builder()
                .page("")
                .touser(open_id)
                .topColor("#000000")
                .template_id(TemplateMsgConfig.RECEIVE_ORDER_MSG_ID)
                .form_id(formId)
                .data(m)
                .build();
        return t;
    }

    //订单支付成功通知
    private Template generatorSuccessPayOrderMsg(Order order, String formId, String open_id) {
        //订单号
        Map<String, TemplateParam> m = new HashMap<String,TemplateParam>();
        TemplateParam keyword1 = new TemplateParam();
        keyword1.setValue(order.getOrderId());
        m.put("keyword1", keyword1);

        //下单时间
        TemplateParam keyword2 = new TemplateParam();
        keyword2.setValue(order.getCreateTime().toString());
        m.put("keyword2", keyword2);

        //订单金额
        String price = String.valueOf(order.getAllFee());
        TemplateParam keyword3 = new TemplateParam();
        keyword3.setValue(price);
        m.put("keyword3", keyword3);

        Template t = Template.builder()
                .page("")
                .touser(open_id)
                .topColor("#000000")
                .template_id(TemplateMsgConfig.SUCCESS_PAY_ORDER_MSG_ID)
                .form_id(formId)
                .data(m)
                .build();
        return t;
    }


    //订单完成通知
    private Template generatorFinishOrderMsg(Order order, String formId, String open_id) {
        //订单号
        Map<String, TemplateParam> m = new HashMap<String,TemplateParam>();
        TemplateParam keyword1 = new TemplateParam();
        keyword1.setValue(order.getOrderId());
        m.put("keyword1", keyword1);

        //商品名称
        TemplateParam keyword2 = new TemplateParam();
        keyword2.setValue(order.getRestaurantName());
        m.put("keyword2", keyword2);

        //订单金额
        String price = String.valueOf(order.getAllFee());
        TemplateParam keyword3 = new TemplateParam();
        keyword3.setValue(price);
        m.put("keyword3", keyword3);

        //完成时间
        TemplateParam keyword4 = new TemplateParam();
        keyword3.setValue(new Date().toString());
        m.put("keyword4", keyword4);
        Template t = Template.builder()
                .page("")
                .touser(open_id)
                .topColor("#000000")
                .template_id(TemplateMsgConfig.SUCCESS_FINISH_ORDER_MSG_ID)
                .form_id(formId)
                .data(m)
                .build();
        return t;
    }



}
