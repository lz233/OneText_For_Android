package com.lz233.onetext.hook

import android.app.Activity
import android.app.Application
import android.content.Context
import android.widget.TextView
import com.lz233.onetext.BuildConfig
import com.lz233.onetext.hook.utils.HookContext
import com.lz233.onetext.hook.utils.LogUtil
import com.lz233.onetext.hook.utils.ktx.*
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class InitHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.netease.cloudmusic") {
            XposedHelpers.findAndHookMethod(Application::class.java, "attach", Context::class.java, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                    HookContext.context = param.args[0] as Context
                    //获取 classloader
                    HookContext.classLoader = HookContext.context.classLoader
                    init()
                }
            })
        }
    }

    private fun init() {
        LogUtil.d(HookContext.context.packageName)
        //获取 activity
        XposedHelpers.findClass("android.app.Instrumentation", HookContext.classLoader)
                .hookAfterAllMethods("newActivity") { activityParam ->
                    HookContext.activity = activityParam.result as Activity
                    LogUtil.d("Current activity: ${HookContext.activity.javaClass}")
                }
        "com.netease.cloudmusic.activity.LyricShareActivity"
                .hookBeforeMethod("U3", "com.netease.cloudmusic.meta.CommonLyricLine") {
                    val thisObject = it.thisObject as Activity
                    (thisObject.getObjectField("W") as TextView).setOnLongClickListener {
                        val lyricShareActivity = "com.netease.cloudmusic.activity.LyricShareActivity".findClass()
                        val musicInfo = lyricShareActivity.callStaticMethod("I3", thisObject)!!
                        val list = lyricShareActivity.callStaticMethod("H3", thisObject)
                                ?.callMethod("getRealAdapter")
                                ?.callMethod("getList")!! as List<Any>
                        thisObject.startActivity(thisObject.packageManager.getLaunchIntentForPackage(BuildConfig.APPLICATION_ID)?.apply {
                            putExtra("text", StringBuilder().apply {
                                for (commonLyricLine in list.listIterator()) {
                                    if (commonLyricLine.callMethod("isShare") as Boolean) {
                                        append("${commonLyricLine.callMethod("getContent")}\n")
                                    }
                                }
                                deleteAt(length - 1)
                            }.toString())
                            putExtra("by", musicInfo.callMethod("getSingerName", true) as String)
                            putExtra("from", musicInfo.callMethod("getMusicName") as String)
                            putExtra("uri", "https://y.music.163.com/m/song?id=${musicInfo.callMethod("getId") as Long}")
                        })
                        true
                    }
                }
    }
}