package com.lz233.onetext.tools.utils

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import java.nio.charset.StandardCharsets
import java.util.*

fun getAS():String{
    //String UUID = "140457a3-af3f-407c-9e70-18b6548757b7";
    val UUID = UUID.randomUUID().toString()
    val calendar = Calendar.getInstance()
    val time = dateToStamp(calendar[Calendar.YEAR].toString() + "-" + (calendar[Calendar.MONTH] + 1) + "-" + calendar[Calendar.DATE] + " " + calendar[Calendar.HOUR_OF_DAY] + ":" + calendar[Calendar.MINUTE] + ":" + calendar[Calendar.SECOND]).substring(0, 10).toInt()
    //int time = (int) Calendar.getInstance(TimeZone.getTimeZone("UTC+8")).getTimeInMillis();
    val hexTime = "0x" + Integer.toHexString(time)
    val MD5Time = DigestUtils.md5Hex(time.toString())
    val a = "token://com.coolapk.market/c67ef5943784d09750dcfbb31020f0ab?$MD5Time$$UUID&com.coolapk.market"
    val MD5a = DigestUtils.md5Hex(Base64.encodeBase64(a.toByteArray(StandardCharsets.UTF_8)))
    return MD5a + UUID + hexTime
}