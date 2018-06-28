package com.neuq.info.enums;

/**
 * @author Lin Dexiang
 * @date 2018/5/9
 */
public enum WithdrawDespositEnum {


    Success("转账成功", 1),
    Fail("转账失败", 2);


    private String name;
    private Integer value;

    WithdrawDespositEnum(String name, Integer value) {
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
