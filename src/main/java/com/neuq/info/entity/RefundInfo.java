package com.neuq.info.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lin Dexiang
 * @date 2018/5/12
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "RefundInfo", description = "退款信息")
public class RefundInfo {

    private String appid;
    private String mch_id;
    private String nonce_str;
    private String sign;
    private String out_trade_no;
    private String out_refund_no;
    private int refund_fee;
    private String notify_url;
    private String transaction_id;
    private int total_fee;
}
