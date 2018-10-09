package com.neuq.info.enums;

/**
 * @author Lin Dexiang
 * @date 2018/5/9
 */
public enum OrderEnum {

    WzfOrderStatus("未支付", 0),
    DjdOrderStatus("待接单", 1),
    YjdOrderStatus("已接单", 2),
    YwcOrderStatus("已完成", 3),
    YqxOrderStatus("已取消", 4);

    //帮客发布订单的各种状态



    private String name;
    private Integer value;

    OrderEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }

    public static OrderEnum getOrderEnum(Integer value) {
        if (value == null)
            return null;
        switch (value) {
            case 0:
                return WzfOrderStatus;
            case 1:
                return DjdOrderStatus;
            case 2:
                return YjdOrderStatus;
            case 3:
                return YwcOrderStatus;
            case 4:
                return YqxOrderStatus;
            default:
                return null;
        }
    }
}
