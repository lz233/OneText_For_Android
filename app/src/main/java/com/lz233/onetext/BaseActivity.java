package com.lz233.onetext;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("setting",MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        //状态栏icon黑色
        int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if(mode == Configuration.UI_MODE_NIGHT_NO) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        //设置为miui主题
        //setMiuiTheme(BaseActivity.this,0,mode);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //全局自定义字体
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath(sharedPreferences.getString("font_path",null))
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    public static void setMiuiTheme(Activity act, int overrideTheme,int isnightmode) {
        int themeResId = 0;
        try {
            themeResId = act.getResources().getIdentifier("Theme.DayNight", "style", "miui");
        } catch (Throwable t) {}
        if (themeResId == 0) themeResId = act.getResources().getIdentifier((isnightmode == Configuration.UI_MODE_NIGHT_YES) ? "Theme.Dark" : "Theme.Light", "style", "miui");
        act.setTheme(themeResId);
        act.getTheme().applyStyle(overrideTheme, true);
    }
}
