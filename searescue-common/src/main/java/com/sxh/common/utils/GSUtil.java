package com.sxh.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author sxh
 * @date 2022/04/04 19:55
 **/

public class GSUtil {
    private static final double EARTH_RADIUS = 6378.137;
    private static double rad(double d){
        return d * Math.PI / 180.0;
    }
    public static double getmeter(double long1, double lat1, double long2, double lat2) {
        double a, b, d, sa2, sb2;
        lat1 = rad(lat1);
        lat2 = rad(lat2);
        a = lat1 - lat2;
        b = rad(long1 - long2);

        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2   * EARTH_RADIUS
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2));
        d= d * 1000;
        BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.UP);
        return bg.doubleValue();

    }

}
