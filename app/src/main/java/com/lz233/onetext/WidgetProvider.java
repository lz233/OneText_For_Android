package com.lz233.onetext;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import com.lz233.onetext.tools.FileUtils;
import com.zqc.opencc.android.lib.ChineseConverter;

import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Random;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.zqc.opencc.android.lib.ConversionType.S2HK;
import static com.zqc.opencc.android.lib.ConversionType.S2T;
import static com.zqc.opencc.android.lib.ConversionType.S2TWP;

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
            /*Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            intent.setClass(context, MainActivity.class);*/

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            Intent openIntent = new Intent(context,MainActivity.class);
            openIntent.setPackage(context.getPackageName());
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
                views.setTextColor(R.id.onetext_widget_text_textview, context.getResources().getColor(R.color.colorWhiteWidget));
                views.setTextColor(R.id.onetext_widget_by_textview,context.getResources().getColor(R.color.colorWhiteWidget));
            }else {
                views.setTextColor(R.id.onetext_widget_text_textview,context.getResources().getColor(R.color.colorText1Widget));
                views.setTextColor(R.id.onetext_widget_by_textview,context.getResources().getColor(R.color.colorText2Widget));
            }
            if ((FileUtils.isFile(context.getFilesDir().getPath()+"/OneText/OneText-Library.json"))|(FileUtils.isFile(sharedPreferences.getString("feed_local_path",null)))){
                JSONArray jsonArray = null;
                Long currentTimeMillis = System.currentTimeMillis();
                Random random = new Random();
                String feed_type = sharedPreferences.getString("feed_type","remote");
                if((currentTimeMillis-sharedPreferences.getLong("widget_latest_refresh_time",0))>(sharedPreferences.getLong("widget_refresh_time",30)*60000)){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(feed_type.equals("remote")){
                        jsonArray = new JSONArray(FileUtils.readTextFromFile(context.getFilesDir().getPath()+"/OneText/OneText-Library.json"));
                    }
                    if(feed_type.equals("local")) {
                        jsonArray = new JSONArray(FileUtils.readTextFromFile(sharedPreferences.getString("feed_local_path",context.getFilesDir().getPath()+"/OneText/OneText-Library.json")));
                    }
                    onetext_code = random.nextInt(jsonArray.length());
                    editor.putInt("onetext_code",onetext_code);
                    editor.putLong("widget_latest_refresh_time",currentTimeMillis);
                    editor.commit();
                }else {
                    if(feed_type.equals("remote")){
                        jsonArray = new JSONArray(FileUtils.readTextFromFile(context.getFilesDir().getPath()+"/OneText/OneText-Library.json"));
                    }
                    if(feed_type.equals("local")) {
                        jsonArray = new JSONArray(FileUtils.readTextFromFile(sharedPreferences.getString("feed_local_path",context.getFilesDir().getPath()+"/OneText/OneText-Library.json")));
                    }
                    onetext_code = sharedPreferences.getInt("onetext_code",random.nextInt(jsonArray.length()));
                }
                String language = Locale.getDefault().getLanguage();
                String country =Locale.getDefault().getCountry();
                JSONObject jsonObject = new JSONObject(jsonArray.optString(onetext_code));
                String originalText;
                String text;
                String by;
                if(language.equals("zh")&country.equals("CN")) {
                    originalText = jsonObject.optString("text");
                    text = originalText.replace("\n"," ");
                    by = jsonObject.optString("by");
                }else if(language.equals("zh")&country.equals("HK")) {
                    originalText = ChineseConverter.convert(jsonObject.optString("text"), S2HK, context);
                    text = originalText.replace("\n"," ");
                    by = ChineseConverter.convert(jsonObject.optString("by"),S2HK,context);
                }else if(language.equals("zh")&country.equals("MO")){
                    originalText = ChineseConverter.convert(jsonObject.optString("text"), S2T, context);
                    text = originalText.replace("\n"," ");
                    by = ChineseConverter.convert(jsonObject.optString("by"),S2T,context);
                }else if(language.equals("zh")&country.equals("TW")){
                    originalText = ChineseConverter.convert(jsonObject.optString("text"), S2TWP, context);
                    text = originalText.replace("\n"," ");
                    by = ChineseConverter.convert(jsonObject.optString("by"),S2TWP,context);
                }else {
                    originalText = jsonObject.optString("text");
                    text = originalText.replace("\n"," ");
                    by = jsonObject.optString("by");
                }
                views.setTextViewText(R.id.onetext_widget_text_textview,text);
                if(!by.equals("")) {
                    views.setViewVisibility(R.id.onetext_widget_by_textview,View.VISIBLE);
                    views.setTextViewText(R.id.onetext_widget_by_textview,"—— "+by);
                }else {
                    views.setViewVisibility(R.id.onetext_widget_by_textview,View.GONE);
                }
                if(sharedPreferences.getBoolean("widget_notification_enabled",false)) {
                    RemoteViews notificationViewsLarge = new RemoteViews(context.getPackageName(), R.layout.notification_layout_large);
                    notificationViewsLarge.setTextViewText(R.id.onetext_notification_large_text_textview,originalText);
                    if(!by.equals("")) {
                        notificationViewsLarge.setViewVisibility(R.id.onetext_notification_large_by_textview,View.VISIBLE);
                        notificationViewsLarge.setTextViewText(R.id.onetext_notification_large_by_textview,"—— "+by);
                    }else {
                        notificationViewsLarge.setViewVisibility(R.id.onetext_notification_large_by_textview,View.GONE);
                    }
                    Intent openIntent = new Intent(context,MainActivity.class);
                    openIntent.setPackage(context.getPackageName());
                    PendingIntent openPendingIntent = PendingIntent.getActivity(context,0,openIntent,0);
                    notificationViewsLarge.setOnClickPendingIntent(R.id.onetext_notification_large_layout,openPendingIntent);
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                            .setContentIntent(openPendingIntent)
                            //.setCustomContentView(notificationViewsSmall)
                            .setCustomBigContentView(notificationViewsLarge)
                            //.setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                            .setSmallIcon(R.drawable.ic_notification)
                            .setColor(context.getResources().getColor(R.color.colorText2))
                            .setContentTitle("“"+text+"”")
                            .setContentText(context.getText(R.string.widget_notification_tip_text))
                            .setWhen(System.currentTimeMillis())
                            .setSound(null)
                            .setVibrate(new long[]{0});
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        builder.setChannelId("widget_onetext");
                    }
                    Notification notification = builder.build();
                    notificationManager.notify(1, notification);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
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