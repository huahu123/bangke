package com.neuq.info.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "User", description = "用户信息")
public class User {
    @ApiModelProperty(value = "自增id", required = true)
    private Long userId;

    @ApiModelProperty(value = "openId", required = true)
    private String openId;

    @ApiModelProperty(value = "自增id")
    private String avatarUrl;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "性别", required = true)
    private Byte gender;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "语言")
    private String language;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "国家")
    private String country;

    @ApiModelProperty(value = "公众号Id")
    private String unionId;

    @ApiModelProperty(value = "信用值")
    private Integer createValue;

    @ApiModelProperty(value = "钱包")
    private Float money;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "用户最后一次更新时间")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "用户创建时间")
    private Date createTime;

    @ApiModelProperty(value = "其他解析字段")
    private HashMap<String, String> watermark;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (openId != null ? !openId.equals(user.openId) : user.openId != null) return false;
        if (avatarUrl != null ? !avatarUrl.equals(user.avatarUrl) : user.avatarUrl != null) return false;
        if (nickName != null ? !nickName.equals(user.nickName) : user.nickName != null) return false;
        if (gender != null ? !gender.equals(user.gender) : user.gender != null) return false;
        if (city != null ? !city.equals(user.city) : user.city != null) return false;
        if (language != null ? !language.equals(user.language) : user.language != null) return false;
        if (province != null ? !province.equals(user.province) : user.province != null) return false;
        if (country != null ? !country.equals(user.country) : user.country != null) return false;
        return unionId != null ? unionId.equals(user.unionId) : user.unionId == null;
    }

    @Override
    public int hashCode() {
        int result = openId != null ? openId.hashCode() : 0;
        result = 31 * result + (avatarUrl != null ? avatarUrl.hashCode() : 0);
        result = 31 * result + (nickName != null ? nickName.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (province != null ? province.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (unionId != null ? unionId.hashCode() : 0);
        return result;
    }
}