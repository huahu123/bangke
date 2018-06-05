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
 * @date 2018/5/23
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "TemplateParam", description = "模版参数")
public class TemplateParam {
    @ApiModelProperty(value = "值")
    private String value;
    @ApiModelProperty(value = "颜色")
    private String color = "#000000";
}
