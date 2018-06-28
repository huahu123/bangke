package com.neuq.info.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lin Dexiang
 * @date 2018/6/2
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "WithdrawDeposit", description = "提现")
public class WithdrawDeposit {

    @ApiModelProperty(value = "自增ID", required = true)
    private Long autoId;
    @ApiModelProperty(value = "银行卡号", required = true)
    private String cardId;
    @ApiModelProperty(value = "收款人名字", required = true)
    private String name;
    @ApiModelProperty(value = "userId", required = true)
    private Long userId;
    @ApiModelProperty(value = "银行编号", required = true)
    private String bankId;
    @ApiModelProperty(value = "提现金额", required = true)
    private Double money;
    @ApiModelProperty(value = "提现状态", required = true)
    private Integer status;
    @ApiModelProperty(value = "备注", required = false)
    private String comment;

}
