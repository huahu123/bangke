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
@ApiModel(value = "WidthdrawDeposit", description = "提现")
public class WidthdrawDeposit {

    @ApiModelProperty(value = "加密的银行账号", required = true)
    private String encBankAcctNo;
    @ApiModelProperty(value = "加密的银行账户名", required = true)
    private String encBankAcctName;
    @ApiModelProperty(value = "银行卡的编号", required = true)
    private String bank_code;
    @ApiModelProperty(value = "转账描述", required = true)
    private String desc;
    @ApiModelProperty(value = "生成随机号", required = true)
    private String partner_trade_no;
    @ApiModelProperty(value = "生成随机号", required = true)
    private String nonce_str1;
    @ApiModelProperty(value = "获取商务号的id", required = true)
    private int mch_id;
    @ApiModelProperty(value = "付款金额，单位是分", required = true)
    private String amount;
    @ApiModelProperty(value = "转账状态")
    private Integer status;

}
