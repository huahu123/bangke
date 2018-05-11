package com.neuq.info.common.utils;

import java.util.Random;
import java.util.UUID;

public class OrderUtil {

    /*UUID生成16位唯一订单号*/
    public static String getOrderIdByUUId() {
        int machineId = 1;//最大支持1-9个集群机器部署
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if(hashCodeV < 0) {//有可能是负数
            hashCodeV = - hashCodeV;
        }
        // 0 代表前面补充0
        // 4 代表长度为4
        // d 代表参数为正数型
        return machineId + String.format("%015d", hashCodeV);
    }

    public static String generatePayCode() {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(getOrderIdByUUId());
    }
}
