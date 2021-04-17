package com.lz233.onetext.hook.utils

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.lz233.onetext.hook.TAG
import de.robv.android.xposed.XposedBridge
import android.util.Log as ALog

object LogUtil {

    private val handler by lazy { Handler(Looper.getMainLooper()) }

    @JvmOverloads
    fun toast(msg: String, force: Boolean = true) {
        if (!force) return
        handler.post {
            Toast.makeText(HookContext.context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    @JvmStatic
    private fun doLog(f: (String, String) -> Int, obj: Any?, toXposed: Boolean = false, toToast: Boolean = true) {
        val str = if (obj is Throwable) ALog.getStackTraceString(obj) else obj.toString()
        if (str.length > maxLength) {
            val chunkCount: Int = str.length / maxLength
            for (i in 0..chunkCount) {
                val max: Int = 4000 * (i + 1)
                if (max >= str.length) {
                    doLog(f, str.substring(maxLength * i))
                } else {
                    doLog(f, str.substring(maxLength * i, max))
                }
            }
        } else {
            f(TAG, str)
            if (toToast) toast(str, false)
            if (toXposed) XposedBridge.log("$TAG : $str")
        }
    }

    @JvmStatic
    fun _d(obj: Any?) {
        doLog(ALog::d, obj, toXposed = false, toToast = false)
    }

    @JvmStatic
    fun d(obj: Any?) {
        doLog(ALog::d, obj)
    }

    @JvmStatic
    fun i(obj: Any?) {
        doLog(ALog::i, obj)
    }

    @JvmStatic
    fun e(obj: Any?) {
        doLog(ALog::e, obj, true)
    }

    @JvmStatic
    fun v(obj: Any?) {
        doLog(ALog::v, obj)
    }

    @JvmStatic
    fun w(obj: Any?) {
        doLog(ALog::w, obj)
    }

    private const val maxLength = 4000
}