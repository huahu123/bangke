package com.neuq.info.enums;

/**
 * @author Lin Dexiang
 * @date 2018/5/9
 */
public enum TemplateMsgEnum {


    QXOrderStatus("取消订单", 0),
    JDOrderStatus("接单通知", 1),
    ZFCGOrderStatus("订单支付成功通知", 2),
    WCOrderStatus("订单完成通知", 3);


    private String name;
    private Integer value;

    TemplateMsgEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }
}
