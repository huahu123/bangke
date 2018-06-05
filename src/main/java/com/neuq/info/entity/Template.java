package com.neuq.info.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author Lin Dexiang
 * @date 2018/5/23
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "Template", description = "消息模版")
public class Template {
    @ApiModelProperty(value = "接受方的openID")
    private String touser;
    @ApiModelProperty(value = "模版ID")
    private String template_id;
    @ApiModelProperty(value = "模板消息详情链接")
    private String page;
    @ApiModelProperty(value = "消息顶部的颜色")
    private String topColor;
    @ApiModelProperty(value = "表单Id")
    private String form_id;
    @ApiModelProperty(value = "参数列表")
    private Map<String, TemplateParam> data;

}
