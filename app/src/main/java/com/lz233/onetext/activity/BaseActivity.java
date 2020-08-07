package com.lz233.onetext.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lz233.onetext.R;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public abstract class BaseActivity extends AppCompatActivity {
    protected ViewGroup rootview;
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //Q导航栏沉浸
        rootview = findViewById(android.R.id.content);

        /*ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                v.setPadding(0,0,0,insets.getSystemWindowInsetBottom());
                return insets;
            }
        });*/
        //状态栏icon黑色
        int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (mode == Configuration.UI_MODE_NIGHT_NO) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
        rootview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        ViewCompat.setOnApplyWindowInsetsListener(rootview, (v, insets) -> {
            rootview.setPadding(insets.getSystemWindowInsetLeft(), 0, insets.getSystemWindowInsetRight(), 0);
            return insets;
        });
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            //rootView.setFitsSystemWindows(true);
            //rootView.setPadding(0,0,0,getNavigationBarHeight());
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        //rootView.setFitsSystemWindows(true);
        /*if ((Build.VERSION.SDK_INT<Build.VERSION_CODES.Q)|(getNavigationBarHeight()>dp2px(16))) {
            //rootView.setPadding(0,0,0,getNavigationBarHeight());
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
                //rootView.setFitsSystemWindows(true);
                rootView.setPadding(0,0,0,getNavigationBarHeight());
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }else {
                //rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                //rootView.setFitsSystemWindows(true);
            }
        }else {
            rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }*/
        //设置为miui主题
        //setMiuiTheme(BaseActivity.this,0,mode);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //全局自定义字体
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath(sharedPreferences.getString("font_path", null))
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    private int getNavigationBarHeight() {
        Resources resources = this.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        //Log.v("dbw", "Navi height:" + height);
        return height;
    }

    public void fuckNav(View last_layout) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) last_layout.getLayoutParams();
        lp.setMargins(getResources().getDimensionPixelSize(R.dimen.layout_margin), 0, getResources().getDimensionPixelSize(R.dimen.layout_margin), getNavigationBarHeight() + getResources().getDimensionPixelSize(R.dimen.layout_margin));
        last_layout.setLayoutParams(lp);
    }

    public void fuckNav(View last_layout, int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) last_layout.getLayoutParams();
        lp.setMargins(left, top, right, bottom);
        last_layout.setLayoutParams(lp);
    }
    /*
    public static int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }
    public boolean isNavigationBarShow(){
        DisplayMetrics metrics =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        Rect outRect1 = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
        int activityHeight = outRect1.height();
        Log.v("dbw", "hei1:"+height+"\nhei2:"+activityHeight);
        return activityHeight != height;
    }
    public static void setMiuiTheme(Activity act, int overrideTheme,int isnightmode) {
        int themeResId = 0;
        try {
            themeResId = act.getResources().getIdentifier("Theme.DayNight", "style", "miui");
        } catch (Throwable t) {}
        if (themeResId == 0) themeResId = act.getResources().getIdentifier((isnightmode == Configuration.UI_MODE_NIGHT_YES) ? "Theme.Dark" : "Theme.Light", "style", "miui");
        act.setTheme(themeResId);
        act.getTheme().applyStyle(overrideTheme, true);
    }*/
}
