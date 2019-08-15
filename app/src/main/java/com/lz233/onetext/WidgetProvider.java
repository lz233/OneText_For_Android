package com.lz233.onetext;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.lz233.onetext.tools.FileUtils;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import android.view.View;
import java.util.Random;

public class WidgetProvider extends AppWidgetProvider {
    private int onetext_code;
    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager manger = AppWidgetManager.getInstance(context);
        ComponentName thisName = new ComponentName(context,WidgetProvider.class);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        run(context,views);
        manger.updateAppWidget(thisName,views);
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        //Log.i("lz", "onUpdate");
        for (int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];
            //Log.i("lz", "onUpdate appWidgetId=" + appWidgetId);
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            intent.setClass(context, MainActivity.class);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            Intent openIntent = new Intent(context,MainActivity.class);
            PendingIntent openPendingIntent = PendingIntent.getActivity(context,0,openIntent,0);
            views.setOnClickPendingIntent(R.id.onetext_widget_layout,openPendingIntent);
            run(context,views);
            //views.setOnClickPendingIntent(R.id.onetext_widget_layout,getPendingIntent(context,R.id.onetext_widget_layout));
            // 更新小部件
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
    private PendingIntent getPendingIntent(Context context, int resID){
        Intent intent = new Intent();
        intent.setClass(context, WidgetProvider.class);//如果没有这一句，表示匿名的。加上表示是显式的。在单个按钮的时候是没啥区别的，但是多个的时候就有问题了
        intent.setAction("btn.text.com");
        //设置data域的时候，把控件id一起设置进去，
        // 因为在绑定的时候，是将同一个id绑定在一起的，所以哪个控件点击，发送的intent中data中的id就是哪个控件的id
        intent.setData(Uri.parse("id:" + resID));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,intent,0);
        return pendingIntent;
    }
    public void run(final Context context, final RemoteViews views) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("setting",Context.MODE_PRIVATE);
            if(sharedPreferences.getBoolean("widget_dark",false)){
                views.setTextColor(R.id.onetext_widget_text_textview, context.getResources().getColor(R.color.colorWhite));
                views.setTextColor(R.id.onetext_widget_by_textview,context.getResources().getColor(R.color.colorWhite));
            }else {
                views.setTextColor(R.id.onetext_widget_text_textview,context.getResources().getColor(R.color.colorText1));
                views.setTextColor(R.id.onetext_widget_by_textview,context.getResources().getColor(R.color.colorText2));
            }
            if (FileUtils.isFile(context.getFilesDir().getPath()+"/OneText/OneText-Library.json")){
                JSONArray jsonArray;
                Long currentTimeMillis = System.currentTimeMillis();
                Random random = new Random();
                if((currentTimeMillis-sharedPreferences.getLong("widget_latest_refresh_time",0))>(sharedPreferences.getLong("widget_refresh_time",30)*60000)){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    jsonArray = new JSONArray(FileUtils.readTextFromFile(context.getFilesDir().getPath()+"/OneText/OneText-Library.json"));
                    onetext_code = random.nextInt(jsonArray.length());
                    editor.putInt("onetext_code",onetext_code);
                    editor.putLong("widget_latest_refresh_time",currentTimeMillis);
                    editor.commit();
                }else {
                    jsonArray = new JSONArray(FileUtils.readTextFromFile(context.getFilesDir().getPath()+"/OneText/OneText-Library.json"));
                    onetext_code = sharedPreferences.getInt("onetext_code",random.nextInt(jsonArray.length()));
                }
                JSONObject jsonObject = new JSONObject(jsonArray.optString(onetext_code));
                final String text = jsonObject.optString("text").replace("\n"," ");
                final String by = jsonObject.optString("by");
                views.setTextViewText(R.id.onetext_widget_text_textview,text);
                if(!by.equals("")) {
                    views.setViewVisibility(R.id.onetext_widget_by_textview,View.VISIBLE);
                    views.setTextViewText(R.id.onetext_widget_by_textview,"—— "+by);
                }else {
                    views.setViewVisibility(R.id.onetext_widget_by_textview,View.GONE);
                }
            }
        }catch (Exception e) {

        }

    }
        /**
         * 当 Widget 被删除时调用该方法。
         *
         * @param context
         * @param appWidgetIds
         */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        //Toast.makeText(context, "onDeleted", Toast.LENGTH_SHORT).show();
    }

    /**
     * 当 Widget 第一次被添加时调用，例如用户添加了两个你的 Widget，那么只有在添加第一个 Widget 时该方法会被调用。
     * 所以该方法比较适合执行你所有 Widgets 只需进行一次的操作
     *
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("setting",Context.MODE_PRIVATE).edit();
        editor.putBoolean("widget_enabled",true);
        editor.apply();
        super.onEnabled(context);
    }

    /**
     * 与 onEnabled 恰好相反，当你的最后一个 Widget 被删除时调用该方法，所以这里用来清理之前在 onEnabled() 中进行的操作。
     *
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("setting",Context.MODE_PRIVATE).edit();
        editor.putBoolean("widget_enabled",false);
        editor.apply();
        super.onDisabled(context);
    }

    /**
     * 当 Widget 第一次被添加或者大小发生变化时调用该方法，可以在此控制 Widget 元素的显示和隐藏。
     *
     * @param context
     * @param appWidgetManager
     * @param appWidgetId
     * @param newOptions
     */
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }
}