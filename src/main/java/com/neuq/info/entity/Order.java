package com.neuq.info.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "Order", description = "订单信息")
public class Order{

    @ApiModelProperty(value = "自增id", required = true)
    private Long autoId;

    @ApiModelProperty(value = "订单id", required = true)
    private String orderId;

    @ApiModelProperty(value = "餐厅名字", required = true)
    private String restaurantName;

    @ApiModelProperty(value = "餐厅位置", required = true)
    private String restaurantLocation;

    @ApiModelProperty(value = "位置经度", required = true)
    private BigDecimal longitude;

    @ApiModelProperty(value = "位置纬度", required = true)
    private BigDecimal latitude;

    @ApiModelProperty(value = "就餐人数", required = true)
    private Integer restaurantPeople;

    @ApiModelProperty(value = "排队类型", required = true)
    private Integer queueType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "开始时间", required = true)
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "到达时间",required = true)
    private Date arriveTime;

    @ApiModelProperty(value = "联系人名字", required = true)
    private String contactName;

    @ApiModelProperty(value = "电话号码", required = true)
    private String phoneNum;

    @ApiModelProperty(value = "性别", required = true)
    private Byte gender;

    @ApiModelProperty(value = "备注", required = true)
    private String comment;

    @ApiModelProperty(value = "费用", required = true)
    private Double fee;

    @ApiModelProperty(value = "额外费用", required = true)
    private Double extraFee;

    @ApiModelProperty(value = "用户ID", required = true)
    private Long customerId;

    @ApiModelProperty(value = "帮客ID")
    private Long providerId;

    @ApiModelProperty(value = "订单状态", required = true)
    private Integer orderStatus;

    @ApiModelProperty(value = "支付状态", required = true)
    private Integer payStatus;

    @ApiModelProperty(value = "支付状态", required = true)
    private String payCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "订单最后一次更新时间")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "订单创建时间")
    private Date createTime;

    @ApiModelProperty(value = "查询的起始纬度，范围查询使用")
    private BigDecimal minlng;

    @ApiModelProperty(value = "查询的终止纬度，范围查询使用")
    private BigDecimal maxlng;

    @ApiModelProperty(value = "查询的终止精度，范围查询使用")
    private BigDecimal minlat;

    @ApiModelProperty(value = "查询的终止精度，范围查询使用")
    private BigDecimal maxlat;


}