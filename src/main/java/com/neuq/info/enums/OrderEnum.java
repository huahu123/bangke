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
}
