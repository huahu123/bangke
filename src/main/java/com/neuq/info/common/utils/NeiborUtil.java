package com.neuq.info.common.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @AUTHOR lindexiang
 * @DATE 下午8:23
 */
public class NeiborUtil {
    public static List<Double> getNeiborPoi(BigDecimal longitude, BigDecimal latitude, double dis) {
        double latitudeDouble = latitude.doubleValue();
        double longitudeDouble = longitude.doubleValue();
        //先计算查询点的经纬度范围
        double r = 6371;//地球半径千米
        double dlng =  2*Math.asin(Math.sin(dis/(2*r))/Math.cos(latitudeDouble*Math.PI/180));
        dlng = dlng*180/Math.PI;//角度转为弧度
        double dlat = dis/r;
        dlat = dlat*180/Math.PI;
        double minlat =latitudeDouble-dlat;
        double maxlat = latitudeDouble+dlat;
        double minlng = latitudeDouble -dlng;
        double maxlng = latitudeDouble + dlng;
        return new ArrayList<>(Arrays.asList(minlng, maxlng, minlat, maxlat));
    }

}