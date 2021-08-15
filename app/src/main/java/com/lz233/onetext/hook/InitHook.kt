package com.lz233.onetext.hook

import android.app.Activity
import android.app.Application
import android.content.Context
import android.widget.TextView
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.findMethodByCondition
import com.github.kyuubiran.ezxhelper.utils.findObjectByCondition
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import com.lz233.onetext.BuildConfig
import com.lz233.onetext.hook.utils.HookContext
import com.lz233.onetext.hook.utils.LogUtil
import com.lz233.onetext.hook.utils.ktx.callMethod
import com.lz233.onetext.hook.utils.ktx.findClass
import com.lz233.onetext.hook.utils.ktx.hookAfterAllMethods
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class InitHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.netease.cloudmusic") {
            EzXHelperInit.initHandleLoadPackage(lpparam)
            XposedHelpers.findAndHookMethod(Application::class.java, "attach", Context::class.java, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                    HookContext.context = param.args[0] as Context
                    //获取 classloader
                    HookContext.classLoader = HookContext.context.classLoader
                    EzXHelperInit.setEzClassLoader(HookContext.classLoader)
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
        findMethodByCondition("com.netease.cloudmusic.activity.LyricShareActivity") {
            (it.parameterTypes.size == 1) && (it.parameterTypes[0].name == "com.netease.cloudmusic.meta.CommonLyricLine")
        }.hookBefore {
            val thisObject = it.thisObject as Activity
            val textView = thisObject.findObjectByCondition {
                (it is TextView) && (it.text == "歌词图片")
            } as TextView
            textView.setOnLongClickListener {
                val lyricShareActivity = "com.netease.cloudmusic.activity.LyricShareActivity".findClass()
                val musicInfo = findMethodByCondition(lyricShareActivity) {
                    (it.parameterTypes.size == 1) && (it.returnType.name == "com.netease.cloudmusic.meta.MusicInfo")
                }.invoke(null, thisObject)
                val list = findMethodByCondition(lyricShareActivity) {
                    (it.parameterTypes.size == 1) && (it.returnType.name == "com.netease.cloudmusic.ui.PagerListView")
                }.invoke(null, thisObject)?.callMethod("getRealAdapter")?.callMethod("getList")!! as List<Any>
                thisObject.startActivity(thisObject.packageManager.getLaunchIntentForPackage(BuildConfig.APPLICATION_ID)?.apply {
                    putExtra("text", StringBuilder().apply {
                        for (commonLyricLine in list.listIterator()) {
                            if (commonLyricLine.callMethod("isShare") as Boolean) {
                                append("${commonLyricLine.callMethod("getContent")}\n")
                            }
                        }
                        deleteAt(lastIndex)
                    }.toString())
                    putExtra("by", musicInfo.callMethod("getSingerName", true) as String)
                    putExtra("from", musicInfo.callMethod("getMusicName") as String)
                    putExtra("uri", "https://y.music.163.com/m/song?id=${musicInfo.callMethod("getId") as Long}")
                })
                true
            }
        }
        /*"com.netease.cloudmusic.activity.LyricShareActivity"
                .hookBeforeMethod("S3", "com.netease.cloudmusic.meta.CommonLyricLine") {
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
                }*/
    }
}