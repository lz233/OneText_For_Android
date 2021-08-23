package com.lz233.onetext.activity

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lz233.onetext.R

abstract class BaseActivity : AppCompatActivity() {
    protected lateinit var rootView: ViewGroup
    protected lateinit var sharedPreferences: SharedPreferences
    protected lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        rootView = findViewById(android.R.id.content)
        //状态栏icon黑色
        val mode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (mode == Configuration.UI_MODE_NIGHT_NO) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v: View?, insets: WindowInsetsCompat ->
            rootView.setPadding(insets.systemWindowInsetLeft, 0, insets.systemWindowInsetRight, 0)
            insets
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
    }

    fun getNavigationBarHeight(): Int {
        val resources = this.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        //Log.v("dbw", "Navi height:" + height);
        return resources.getDimensionPixelSize(resourceId)
    }

    fun fuckNav(last_layout: View) {
        val lp = last_layout.layoutParams as LinearLayout.LayoutParams
        lp.setMargins(resources.getDimensionPixelSize(R.dimen.layout_margin), 0, resources.getDimensionPixelSize(R.dimen.layout_margin), getNavigationBarHeight() + resources.getDimensionPixelSize(R.dimen.layout_margin))
        last_layout.layoutParams = lp
    }

    fun fuckNav(last_layout: View, left: Int, top: Int, right: Int, bottom: Int) {
        val lp = last_layout.layoutParams as RelativeLayout.LayoutParams
        lp.setMargins(left, top, right, bottom)
        last_layout.layoutParams = lp
    }
}