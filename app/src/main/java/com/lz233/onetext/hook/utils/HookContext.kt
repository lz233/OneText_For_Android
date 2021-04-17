package com.lz233.onetext.hook.utils

import android.app.Activity
import android.content.Context

object HookContext {
    lateinit var context: Context
    lateinit var classLoader: ClassLoader
    lateinit var activity: Activity
}