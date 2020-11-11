package com.lz233.onetext.tools.utils

import android.content.Context
import android.net.ConnectivityManager
import java.text.SimpleDateFormat

fun isUseWifi(context: Context): Boolean {
    val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
    return networkInfo?.isConnected ?: true
}

fun dp2px(context: Context, dipValue: Float): Int = (dipValue * context.resources.displayMetrics.density + 0.5f).toInt()

fun px2sp(context: Context, pxValue: Float): Int = (pxValue / context.resources.displayMetrics.scaledDensity + 0.5f).toInt()

fun dateToStamp(time: String): String {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = simpleDateFormat.parse(time)
    val ts = date?.time
    return ts.toString()
}