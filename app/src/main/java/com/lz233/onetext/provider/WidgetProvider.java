package com.lz233.onetext.provider;

import android.app.Notification;
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
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.lz233.onetext.R;
import com.lz233.onetext.activity.MainActivity;
import com.lz233.onetext.tools.utils.CoreUtil;
import com.lz233.onetext.tools.utils.GetUtil;
import com.lz233.onetext.tools.utils.GoogleTranslateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.content.Context.NOTIFICATION_SERVICE;

public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager manger = AppWidgetManager.getInstance(context);
        ComponentName thisName = new ComponentName(context, WidgetProvider.class);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        run(context, views,manger);
        manger.updateAppWidget(thisName, views);
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
            Intent openIntent = new Intent(context, MainActivity.class);
            openIntent.setPackage(context.getPackageName());
            PendingIntent openPendingIntent = PendingIntent.getActivity(context, 0, openIntent, 0);
            views.setOnClickPendingIntent(R.id.onetext_widget_layout, openPendingIntent);
            run(context, views,appWidgetManager);
            Toast.makeText(context,"update",Toast.LENGTH_SHORT).show();
            //views.setOnClickPendingIntent(R.id.onetext_widget_layout,getPendingIntent(context,R.id.onetext_widget_layout));
            // 更新小部件
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private PendingIntent getPendingIntent(Context context, int resID) {
        Intent intent = new Intent();
        intent.setClass(context, WidgetProvider.class);//如果没有这一句，表示匿名的。加上表示是显式的。在单个按钮的时候是没啥区别的，但是多个的时候就有问题了
        intent.setAction("btn.text.com");
        //设置data域的时候，把控件id一起设置进去，
        // 因为在绑定的时候，是将同一个id绑定在一起的，所以哪个控件点击，发送的intent中data中的id就是哪个控件的id
        intent.setData(Uri.parse("id:" + resID));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        return pendingIntent;
    }

    public void run(final Context context, final RemoteViews views, final AppWidgetManager appWidgetManager) {
        try {
            final SharedPreferences sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
            if (sharedPreferences.getBoolean("widget_dark", false)) {
                views.setTextColor(R.id.onetext_widget_text_textview_shadow, context.getColor(R.color.colorText1Widget));
                views.setTextColor(R.id.onetext_widget_center_text_textview_shadow, context.getColor(R.color.colorText1Widget));
                views.setTextColor(R.id.onetext_widget_by_textview_shadow, context.getColor(R.color.colorText2Widget));
                views.setTextColor(R.id.onetext_widget_text_textview, context.getColor(R.color.colorText1Widget));
                views.setTextColor(R.id.onetext_widget_center_text_textview, context.getColor(R.color.colorText1Widget));
                views.setTextColor(R.id.onetext_widget_by_textview, context.getColor(R.color.colorText2Widget));
            } else {
                views.setTextColor(R.id.onetext_widget_text_textview_shadow, context.getColor(R.color.colorWhiteWidget));
                views.setTextColor(R.id.onetext_widget_center_text_textview_shadow, context.getColor(R.color.colorWhiteWidget));
                views.setTextColor(R.id.onetext_widget_by_textview_shadow, context.getColor(R.color.colorWhiteWidget));
                views.setTextColor(R.id.onetext_widget_text_textview, context.getColor(R.color.colorWhiteWidget));
                views.setTextColor(R.id.onetext_widget_center_text_textview, context.getColor(R.color.colorWhiteWidget));
                views.setTextColor(R.id.onetext_widget_by_textview, context.getColor(R.color.colorWhiteWidget));
                //LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //View viewParent= inflater.inflate((XmlPullParser) views, null);
                //TextView view =viewParent.findViewById(R.id.onetext_widget_center_text_textview);
                //view.setTextColor(Color.RED);
            }
                /*OneTextUtil oneTextUtil = new OneTextUtil(context);
                String[] oneText = oneTextUtil.readOneText(oneTextUtil.getOneTextCode(false, true));
                String originalText = oneText[0];
                String text = originalText.replace("\n", " ");
                String by = oneText[1];*/
            final CoreUtil coreUtil = new CoreUtil(context);
            final HashMap[] hashMap = new HashMap[1];
            final HashMap feedMap = coreUtil.getFeedInformation(sharedPreferences.getInt("feed_code", 0));
            if(feedMap.get("feed_type").equals("internet")){
                if(sharedPreferences.getString("api_string","").equals("")|coreUtil.ifOneTextShouldUpdate(true)){
                    new GetUtil().sendGet((String) feedMap.get("api_url"), new GetUtil.GetCallback() {
                        @Override
                        public void onGetDone(String result) {
                            try {
                                hashMap[0] = coreUtil.convertOneText(new JSONObject(result),feedMap);
                                showOneText(context,sharedPreferences,views, hashMap[0]);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("api_string",result);
                            editor.apply();
                            ComponentName thisName = new ComponentName(context, WidgetProvider.class);
                            appWidgetManager.updateAppWidget(thisName, views);
                        }
                    });
                }else {
                    hashMap[0] = coreUtil.convertOneText(new JSONObject(sharedPreferences.getString("api_string","")),feedMap);
                    showOneText(context,sharedPreferences,views,hashMap[0]);
                }
            }else {
                hashMap[0] = coreUtil.getOneText(false, true);
                showOneText(context,sharedPreferences,views, hashMap[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void showOneText(Context context,SharedPreferences sharedPreferences,RemoteViews views,HashMap hashMap){
        String originalText = (String) hashMap.get("text");
        if (originalText == null) {
            originalText = "";
        }
        String text = originalText.replace("\n", " ");
        String by = (String) hashMap.get("by");
        if (by == null) {
            by = "";
        }
        if (sharedPreferences.getBoolean("widget_center", true)) {
            if (sharedPreferences.getBoolean("widget_shadow", true)) {
                views.setTextViewText(R.id.onetext_widget_center_text_textview_shadow, text);
                views.setViewVisibility(R.id.onetext_widget_text_textview_shadow, View.GONE);
                views.setViewVisibility(R.id.onetext_widget_center_text_textview_shadow, View.VISIBLE);
                views.setViewVisibility(R.id.onetext_widget_by_textview_shadow, View.GONE);
                views.setViewVisibility(R.id.onetext_widget_text_textview, View.GONE);
                views.setViewVisibility(R.id.onetext_widget_center_text_textview, View.GONE);
                views.setViewVisibility(R.id.onetext_widget_by_textview, View.GONE);
            } else {
                views.setTextViewText(R.id.onetext_widget_center_text_textview, text);
                views.setViewVisibility(R.id.onetext_widget_text_textview_shadow, View.GONE);
                views.setViewVisibility(R.id.onetext_widget_center_text_textview_shadow, View.GONE);
                views.setViewVisibility(R.id.onetext_widget_by_textview_shadow, View.GONE);
                views.setViewVisibility(R.id.onetext_widget_text_textview, View.GONE);
                views.setViewVisibility(R.id.onetext_widget_center_text_textview, View.VISIBLE);
                views.setViewVisibility(R.id.onetext_widget_by_textview, View.GONE);
            }
        } else {
            if (sharedPreferences.getBoolean("widget_shadow", true)) {
                views.setTextViewText(R.id.onetext_widget_text_textview_shadow, text);
                views.setViewVisibility(R.id.onetext_widget_text_textview_shadow, View.VISIBLE);
                views.setViewVisibility(R.id.onetext_widget_center_text_textview_shadow, View.GONE);
                views.setViewVisibility(R.id.onetext_widget_text_textview, View.GONE);
                views.setViewVisibility(R.id.onetext_widget_center_text_textview, View.GONE);
                if (!by.equals("")) {
                    views.setTextViewText(R.id.onetext_widget_by_textview_shadow, "—— " + by);
                    views.setViewVisibility(R.id.onetext_widget_by_textview_shadow, View.VISIBLE);
                    views.setViewVisibility(R.id.onetext_widget_by_textview, View.GONE);
                } else {
                    views.setViewVisibility(R.id.onetext_widget_by_textview_shadow, View.GONE);
                    views.setViewVisibility(R.id.onetext_widget_by_textview, View.GONE);
                }
            } else {
                views.setTextViewText(R.id.onetext_widget_text_textview, text);
                views.setViewVisibility(R.id.onetext_widget_text_textview_shadow, View.GONE);
                views.setViewVisibility(R.id.onetext_widget_center_text_textview_shadow, View.GONE);
                views.setViewVisibility(R.id.onetext_widget_text_textview, View.VISIBLE);
                views.setViewVisibility(R.id.onetext_widget_center_text_textview, View.GONE);
                if (!by.equals("")) {
                    views.setTextViewText(R.id.onetext_widget_by_textview, "—— " + by);
                    views.setViewVisibility(R.id.onetext_widget_by_textview_shadow, View.GONE);
                    views.setViewVisibility(R.id.onetext_widget_by_textview, View.VISIBLE);
                } else {
                    views.setViewVisibility(R.id.onetext_widget_by_textview_shadow, View.GONE);
                    views.setViewVisibility(R.id.onetext_widget_by_textview, View.GONE);
                }
            }
        }
        if (sharedPreferences.getBoolean("widget_notification_enabled", false)) {
            RemoteViews notificationViewsLarge = new RemoteViews(context.getPackageName(), R.layout.notification_layout_large);
            notificationViewsLarge.setTextViewText(R.id.onetext_notification_large_text_textview, originalText);
            if (!by.equals("")) {
                notificationViewsLarge.setViewVisibility(R.id.onetext_notification_large_by_textview, View.VISIBLE);
                notificationViewsLarge.setTextViewText(R.id.onetext_notification_large_by_textview, "—— " + by);
            } else {
                notificationViewsLarge.setViewVisibility(R.id.onetext_notification_large_by_textview, View.GONE);
            }
            Intent openIntent = new Intent(context, MainActivity.class);
            openIntent.setPackage(context.getPackageName());
            PendingIntent openPendingIntent = PendingIntent.getActivity(context, 0, openIntent, 0);
            notificationViewsLarge.setOnClickPendingIntent(R.id.onetext_notification_large_layout, openPendingIntent);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setContentIntent(openPendingIntent)
                    //.setCustomContentView(notificationViewsSmall)
                    .setCustomBigContentView(notificationViewsLarge)
                    //.setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setColor(context.getColor(R.color.colorText2))
                    .setContentTitle("“" + text + "”")
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
        SharedPreferences.Editor editor = context.getSharedPreferences("setting", Context.MODE_PRIVATE).edit();
        editor.putBoolean("widget_enabled", true);
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
        SharedPreferences.Editor editor = context.getSharedPreferences("setting", Context.MODE_PRIVATE).edit();
        editor.putBoolean("widget_enabled", false);
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