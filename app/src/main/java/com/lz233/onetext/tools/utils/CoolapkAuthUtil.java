package com.lz233.onetext.tools.utils;

import android.util.Log;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;

public class CoolapkAuthUtil {
    public static String getAS() {
        String result = "";
        try {
            //String UUID = "140457a3-af3f-407c-9e70-18b6548757b7";
            String UUID = java.util.UUID.randomUUID().toString();
            Calendar calendar = Calendar.getInstance();
            int time = Integer.parseInt((AppUtil.dateToStamp(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND)).substring(0, 10)));
            //int time = (int) Calendar.getInstance(TimeZone.getTimeZone("UTC+8")).getTimeInMillis();
            String hexTime = "0x" + Integer.toHexString(time);
            String MD5Time = DigestUtils.md5Hex(String.valueOf(time));
            String a = "token://com.coolapk.market/c67ef5943784d09750dcfbb31020f0ab?" + MD5Time + "$" + UUID + "&com.coolapk.market";
            String MD5a = DigestUtils.md5Hex(Base64.encodeBase64(a.getBytes(StandardCharsets.UTF_8)));
            result = MD5a + UUID + hexTime;
            Log.i("233", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
