package com.neuq.info.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private Long userId;
    private String openId;
    private String avatarUrl;
    private String nickName;
    private Byte gender;
    private String city;
    private String language;
    private String province;
    private String country;
    private String unionId;
    private Integer createValue;
    private Float money;
    private Date createTime;
    private Date updateTime;
    private HashMap<String, String> watermark;
    private static final long serialVersionUID = 1L;

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