package com.neuq.info.enums;

/**
 * @author Lin Dexiang
 * @date 2018/5/13
 */
public enum PayEnum {
    UnPayStatus("未支付", 0),
    AlreadyPayStatsu("已支付", 1);

    private String name;
    private Integer value;

    PayEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }

    public static PayEnum getPayEnum(Integer value) {
        if (value == null)
            return null;
        if (value == 0)
            return UnPayStatus;
        else if (value == 1)
            return AlreadyPayStatsu;
        else
            return null;
    }
}
