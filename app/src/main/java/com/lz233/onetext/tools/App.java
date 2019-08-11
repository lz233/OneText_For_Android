package com.lz233.onetext.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class App {
    //返回当前程序版本名
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            Log.e("VersionNameInfo", "Exception", e);
        }
        return versionName;
    }
    //返回当前程序版本号
    public static int getAppVersionCode(Context context) {
        int versioncode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),0);
            versioncode = pi.versionCode;
        }catch (Exception e) {
            Log.e("VersionCodeInfo", "Exception", e);
        }
        return versioncode;
    }
}
