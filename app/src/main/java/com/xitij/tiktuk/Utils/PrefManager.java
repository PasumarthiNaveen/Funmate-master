package com.xitij.tiktuk.Utils;

import android.content.Context;
import android.preference.PreferenceManager;

public class PrefManager {

    public static void setUserNameEditable(Context context, boolean isEditable) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("isEditable",isEditable).apply();
    }

    public static boolean isUserNameEditable(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isEditable",true);
    }


    public static void setPaidContest(Context context, boolean isPaid) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("isPaid",isPaid).apply();
    }

    public static boolean isPaidContest(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isPaid",true);
    }

    public static void setPayAmount(Context context,String payAmount) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("pay_amount",payAmount).apply();
    }

    public static String getPayAmount(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("pay_amount","0");
    }

    public static void setPayPoint(Context context,String payAmount) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("pay_point",payAmount).apply();
    }

    public static String getPayPoint(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("pay_point","0");
    }

    public static void setFullName(Context context, String fullName) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("full_name",fullName).apply();
    }

    public static String getFullName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("full_name","Unknown");
    }


    public static void setReferralCode(Context context, String referralCode) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("referralCode",referralCode).apply();
    }

    public static String getReferralCode(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("referralCode","null");
    }


    public static void setTempVUrl(Context context, String vurl) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("vurl",vurl).apply();
    }

    public static String getTempVUrl(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("vurl","none");
    }

    public static void setTempTUrl(Context context, String turl) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("turl",turl).apply();
    }

    public static String getTempTUrl(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("turl","none");
    }



}
