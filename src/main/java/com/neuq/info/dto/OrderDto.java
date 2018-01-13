package com.neuq.info.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @AUTHOR lindexiang
 * @DATE 下午11:53
 */

public class OrderDto {

    public Integer autoId;
    public String orderId;
    public String restaurantName;
    public String restaurantLocation;
    public BigDecimal longitude;
    public BigDecimal latitude;
    public Integer restaurantPeople;
    public Integer queueType;
    public Date startTime;
    public Date arriveTime;
    public String contactName;
    public String phoneNum;
    public Byte gender;
    public String comment;
    public Double fee;
    public Double extraFee;
    public Long customerId;
    public Long providerId;
}
