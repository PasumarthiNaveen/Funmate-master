package com.xitij.tiktuk.Utils;

import java.text.DecimalFormat;

public class PreciseCount {
    private static final double THOUSAND = 1000L;
    private static final double MILLION = 10000000L;
    private static final double BILLION = 1000000000L;

    public static String from(String count){
        int total = Integer.parseInt(count);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        return total<THOUSAND ? count :
                total<MILLION ? decimalFormat.format((total/THOUSAND)) + "K" :
                total<BILLION ? decimalFormat.format((total/MILLION)) + "M" : decimalFormat.format((total/BILLION)) + "B";
    }
}