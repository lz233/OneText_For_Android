package com.lz233.onetext.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.lz233.onetext.R;
import com.lz233.onetext.tools.feed.Feed;
import com.lz233.onetext.tools.feed.FeedAdapter;
import com.lz233.onetext.tools.utils.DownloadUtil;
import com.lz233.onetext.tools.utils.FileUtil;
import com.lz233.onetext.view.NiceImageView;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class SettingActivity extends BaseActivity {
    private Boolean isSettingUpdated = false;
    private Receiver receiver;
    private View view;
    private LinearLayout oauth_linearlayout;
    private NiceImageView oauth_avatar_imageview;
    private TextView oauth_name_textview;
    private TextView oauth_bio_textview;
    private String font_path_yiqi;
    private String font_path_canger;
    private TextView current_font_textview;
    private LinearLayout font_yiqi_layout;
    private LinearLayout font_canger_layout;
    private TextView font_yiqi_textview;
    private TextView font_canger_textview;
    private TextView font_system_textview;
    private TextView font_custom_textview;
    private AppCompatSpinner interface_daynight_spinner;
    private SwitchMaterial oauth_hide_switch;
    private AppCompatSpinner chinese_type_spinner;
    private ImageView feed_reset_imageview;
    private ImageView feed_add_imageview;
    private RecyclerView feed_recyclerview;
    private FeedAdapter feedAdapter;
    private List<Feed> feedList = new ArrayList<>();
    private SwitchMaterial feed_auto_update_switch;
    private IndicatorSeekBar feed_refresh_seekbar;
    private ImageView feed_refresh_imageview;
    private TextView widget_enable_textview;
    private SwitchMaterial widget_dark_switch;
    private SwitchMaterial widget_shadow_switch;
    private SwitchMaterial widget_center_switch;
    private SwitchMaterial widget_notification_switch;
    private IndicatorSeekBar widget_refresh_seekbar;
    private ImageView widget_refresh_imageview;
    private TextView about_page_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //曲 线 救 国
        fuckNav(findViewById(R.id.last_layout));
        //by
        view = getWindow().getDecorView();
        font_path_yiqi = getFilesDir().getPath() + "/Fonts/yiqi.ttf";
        font_path_canger = getFilesDir().getPath() + "/Fonts/canger.ttf";
        oauth_linearlayout = findViewById(R.id.oauth_linearlayout);
        oauth_avatar_imageview = findViewById(R.id.oauth_avatar_imageview);
        oauth_name_textview = findViewById(R.id.oauth_name_textview);
        oauth_bio_textview = findViewById(R.id.oauth_bio_textview);
        current_font_textview = findViewById(R.id.current_font_textview);
        font_yiqi_layout = findViewById(R.id.font_yiqi_layout);
        font_canger_layout = findViewById(R.id.font_canger_layout);
        font_yiqi_textview = findViewById(R.id.font_yiqi_textview);
        font_canger_textview = findViewById(R.id.font_canger_textview);
        font_system_textview = findViewById(R.id.font_system_textview);
        font_custom_textview = findViewById(R.id.font_custom_textview);
        interface_daynight_spinner = findViewById(R.id.interface_daynight_spinner);
        oauth_hide_switch = findViewById(R.id.oauth_hide_switch);
        chinese_type_spinner = findViewById(R.id.chinese_type_spinner);
        feed_reset_imageview = findViewById(R.id.feed_reset_imageview);
        feed_add_imageview = findViewById(R.id.feed_add_imageview);
        feed_recyclerview = findViewById(R.id.feed_recyclerview);
        feed_auto_update_switch = findViewById(R.id.feed_auto_update_switch);
        feed_refresh_seekbar = findViewById(R.id.feed_refresh_seekbar);
        feed_refresh_imageview = findViewById(R.id.feed_refresh_imageview);
        widget_enable_textview = findViewById(R.id.widget_enable_textview);
        widget_dark_switch = findViewById(R.id.widget_dark_switch);
        widget_shadow_switch = findViewById(R.id.widget_shadow_switch);
        widget_center_switch = findViewById(R.id.widget_center_switch);
        widget_notification_switch = findViewById(R.id.widget_notification_switch);
        widget_refresh_seekbar = findViewById(R.id.widget_refresh_seekbar);
        widget_refresh_imageview = findViewById(R.id.widget_refresh_imageview);
        about_page_textview = findViewById(R.id.about_page_textview);
        //初始化
        updateFontStatus();
        //接收广播
        receiver = new Receiver();
        registerReceiver(receiver, new IntentFilter("com.lz233.onetext.updatefeedlist"));
        registerReceiver(receiver, new IntentFilter("com.lz233.onetext.issettingupdated"));
        Intent intent = new Intent(this, Service.class);
        startService(intent);
        switch (sharedPreferences.getInt("interface_daynight", 0)) {
            case 0:
                interface_daynight_spinner.setSelection(0);
                break;
            case 1:
                interface_daynight_spinner.setSelection(1);
                break;
            case 2:
                interface_daynight_spinner.setSelection(2);
                break;
        }
        if (sharedPreferences.getBoolean("oauth_hide", false)) {
            oauth_linearlayout.setVisibility(View.GONE);
        }
        oauth_hide_switch.setChecked(sharedPreferences.getBoolean("oauth_hide", false));
        switch (sharedPreferences.getInt("chinese_type", 0)) {
            case 0:
                chinese_type_spinner.setSelection(0);
                break;
            case 1:
                chinese_type_spinner.setSelection(1);
                break;
            case 2:
                chinese_type_spinner.setSelection(2);
                break;
            case 3:
                chinese_type_spinner.setSelection(3);
            case 4:
                chinese_type_spinner.setSelection(4);

        }
        feed_auto_update_switch.setChecked(sharedPreferences.getBoolean("feed_auto_update", true));
        feed_refresh_seekbar.setIndicatorTextFormat(getString(R.string.feed_refresh_text) + " ${PROGRESS} " + getString(R.string.hour));
        feed_refresh_seekbar.setProgress(sharedPreferences.getLong("feed_refresh_time", 1));
        widget_refresh_seekbar.setIndicatorTextFormat(getString(R.string.widget_refresh_text) + " ${PROGRESS} " + getString(R.string.minute));
        widget_refresh_seekbar.setProgress(sharedPreferences.getLong("widget_refresh_time", 30));
        widget_dark_switch.setChecked(sharedPreferences.getBoolean("widget_dark", false));
        widget_shadow_switch.setChecked(sharedPreferences.getBoolean("widget_shadow", true));
        widget_center_switch.setChecked(sharedPreferences.getBoolean("widget_center", true));
        widget_notification_switch.setChecked(sharedPreferences.getBoolean("widget_notification_enabled", false));
        // 监听器
        font_yiqi_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FileUtil.isFile(font_path_yiqi)) {
                    final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel("download_file", getString(R.string.font_notification_text), NotificationManager.IMPORTANCE_DEFAULT);
                        channel.setSound(null, null);
                        channel.enableLights(false);
                        channel.enableVibration(false);
                        channel.setVibrationPattern(new long[]{0});
                        notificationManager.createNotificationChannel(channel);
                    }
                    //新建下载任务
                    Snackbar.make(view, getString(R.string.font_start_download), Snackbar.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            DownloadUtil.get().download("https://onetext.xyz/1.ttf", getFilesDir().getPath() + "/Fonts/", "yiqi.ttf", new DownloadUtil.OnDownloadListener() {
                                @Override
                                public void onDownloadSuccess(File file) {
                                    editor.putString("font_path", font_path_yiqi);
                                    editor.apply();
                                    updateFontStatus();
                                    notificationManager.cancel(2);
                                }

                                @Override
                                public void onDownloading(int progress) {
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(SettingActivity.this)
                                            .setSmallIcon(R.drawable.ic_notification)
                                            .setColor(getColor(R.color.colorText2))
                                            .setContentTitle(getString(R.string.font_notification_title) + progress + "%")
                                            .setContentText(getString(R.string.font_yiqi))
                                            .setWhen(System.currentTimeMillis())
                                            .setSound(null)
                                            .setVibrate(new long[]{0});
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        builder.setChannelId("download_file");
                                    }
                                    Notification notification = builder.build();
                                    notificationManager.notify(2, notification);
                                }

                                @Override
                                public void onDownloadFailed(Exception e) {
                                    e.printStackTrace();
                                    FileUtil.deleteFile(font_path_yiqi);
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(SettingActivity.this)
                                            .setSmallIcon(R.drawable.ic_notification)
                                            .setColor(getColor(R.color.colorText2))
                                            .setContentTitle(getString(R.string.font_notification_failed))
                                            .setContentText(getString(R.string.font_yiqi))
                                            .setWhen(System.currentTimeMillis())
                                            .setSound(null)
                                            .setVibrate(new long[]{0});
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        builder.setChannelId("download_file");
                                    }
                                    Notification notification = builder.build();
                                    notificationManager.notify(2, notification);
                                }
                            });
                        }
                    }).start();
                } else {
                    Snackbar.make(view, getString(R.string.setting_succeed_text), Snackbar.LENGTH_SHORT).show();
                    editor.putString("font_path", font_path_yiqi);
                    editor.apply();
                    updateFontStatus();
                }
            }
        });
        font_canger_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FileUtil.isFile(font_path_canger)) {
                    final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel("download_file", getString(R.string.font_notification_text), NotificationManager.IMPORTANCE_DEFAULT);
                        channel.setSound(null, null);
                        channel.enableLights(false);
                        channel.enableVibration(false);
                        channel.setVibrationPattern(new long[]{0});
                        notificationManager.createNotificationChannel(channel);
                    }
                    //新建下载任务
                    Snackbar.make(view, getString(R.string.font_start_download), Snackbar.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            DownloadUtil.get().download("https://onetext.xyz/2.ttf", getFilesDir().getPath() + "/Fonts/", "canger.ttf", new DownloadUtil.OnDownloadListener() {
                                @Override
                                public void onDownloadSuccess(File file) {
                                    editor.putString("font_path", font_path_canger);
                                    editor.apply();
                                    updateFontStatus();
                                    notificationManager.cancel(3);
                                }

                                @Override
                                public void onDownloading(int progress) {
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(SettingActivity.this)
                                            .setSmallIcon(R.drawable.ic_notification)
                                            .setColor(getColor(R.color.colorText2))
                                            .setContentTitle(getString(R.string.font_notification_title) + progress + "%")
                                            .setContentText(getString(R.string.font_canger))
                                            .setWhen(System.currentTimeMillis())
                                            .setSound(null)
                                            .setVibrate(new long[]{0});
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        builder.setChannelId("download_file");
                                    }
                                    Notification notification = builder.build();
                                    notificationManager.notify(3, notification);
                                }

                                @Override
                                public void onDownloadFailed(Exception e) {
                                    e.printStackTrace();
                                    FileUtil.deleteFile(font_path_canger);
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(SettingActivity.this)
                                            .setSmallIcon(R.drawable.ic_notification)
                                            .setColor(getColor(R.color.colorText2))
                                            .setContentTitle(getString(R.string.font_notification_failed))
                                            .setContentText(getString(R.string.font_canger))
                                            .setWhen(System.currentTimeMillis())
                                            .setSound(null)
                                            .setVibrate(new long[]{0});
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        builder.setChannelId("download_file");
                                    }
                                    Notification notification = builder.build();
                                    notificationManager.notify(3, notification);
                                }
                            });
                        }
                    }).start();
                } else {
                    Snackbar.make(view, getString(R.string.setting_succeed_text), Snackbar.LENGTH_SHORT).show();
                    editor.putString("font_path", font_path_canger);
                    editor.apply();
                    updateFontStatus();
                }
            }
        });
        font_system_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.remove("font_path");
                editor.apply();
                Snackbar.make(view, getString(R.string.setting_succeed_text), Snackbar.LENGTH_SHORT).show();
                updateFontStatus();
            }
        });
        font_custom_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (EasyPermissions.hasPermissions(SettingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, 1);
                //} else {
                //    Snackbar.make(view, getString(R.string.request_permissions_text), Snackbar.LENGTH_SHORT).show();
                //}
            }
        });
        /*SpinnerAdapter adapter = SpinnerAdapter.createFromResource(SettingActivity.this,R.array.daynight,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        interface_daynight_spinner.setBackgroundResource(R.drawable.bgstyle_md2);
        interface_daynight_spinner.setAdapter(adapter);*/
        interface_daynight_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        break;
                    case 1:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                    case 2:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                }
                editor.putInt("interface_daynight", i);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        oauth_hide_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    oauth_linearlayout.setVisibility(View.GONE);
                    editor.putBoolean("oauth_hide", true);
                } else {
                    oauth_linearlayout.setVisibility(View.VISIBLE);
                    editor.putBoolean("oauth_hide", false);
                }
                editor.apply();
            }
        });
        chinese_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editor.putInt("chinese_type", i);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        feed_reset_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.remove("feed_code");
                editor.remove("feed_latest_refresh_time");
                editor.remove("widget_latest_refresh_time");
                editor.remove("onetext_code");
                editor.commit();
                FileUtil.deleteFile(getFilesDir().getPath() + "/OneText/OneText-Library.json");
                FileUtil.copyAssets(SettingActivity.this, "Feed", getFilesDir().getPath() + "/Feed");
                initFeed();
            }
        });
        feed_add_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, SetFeedActivity.class));
            }
        });
        feed_auto_update_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("feed_auto_update", true);
                } else {
                    editor.putBoolean("feed_auto_update", false);
                }
            }
        });
        feed_refresh_seekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                editor.putLong("feed_refresh_time", seekBar.getProgress());
                editor.apply();
            }
        });
        feed_refresh_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feed_refresh_seekbar.setProgress(1);
                editor.remove("feed_refresh_time");
                editor.apply();
                Snackbar.make(view, getString(R.string.succeed), Snackbar.LENGTH_SHORT).show();
            }
        });
        widget_dark_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("widget_dark", true);
                } else {
                    editor.putBoolean("widget_dark", false);
                }
                editor.commit();
                Intent intent = new Intent("com.lz233.onetext.widget");
                intent.setPackage(getPackageName());
                SettingActivity.this.sendBroadcast(intent);
            }
        });
        widget_shadow_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("widget_shadow", true);
                } else {
                    editor.putBoolean("widget_shadow", false);
                }
                editor.commit();
                Intent intent = new Intent("com.lz233.onetext.widget");
                intent.setPackage(getPackageName());
                SettingActivity.this.sendBroadcast(intent);
            }
        });
        widget_center_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("widget_center", true);
                } else {
                    editor.putBoolean("widget_center", false);
                }
                editor.commit();
                Intent intent = new Intent("com.lz233.onetext.widget");
                intent.setPackage(getPackageName());
                SettingActivity.this.sendBroadcast(intent);
            }
        });
        widget_notification_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("widget_onetext", getString(R.string.widget_notification_text), NotificationManager.IMPORTANCE_DEFAULT);
                    channel.setSound(null, null);
                    channel.enableLights(false);
                    channel.enableVibration(false);
                    channel.setVibrationPattern(new long[]{0});
                    notificationManager.createNotificationChannel(channel);
                }
                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        final NotificationChannel channel = notificationManager.getNotificationChannel("widget_onetext");
                        if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                            widget_notification_switch.setChecked(false);
                            Snackbar.make(rootview, getString(R.string.widget_notification_permissions_text), Snackbar.LENGTH_SHORT).setAction(getString(R.string.request_permissions_agree_button), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
                                    startActivity(intent);
                                }
                            }).show();
                        }
                    }
                    editor.putBoolean("widget_notification_enabled", true);
                    editor.commit();
                    Snackbar.make(rootview, getString(R.string.widget_notification_next_effective_text), Snackbar.LENGTH_SHORT).show();
                } else {
                    editor.putBoolean("widget_notification_enabled", false);
                    editor.commit();
                }
            }
        });
        widget_refresh_seekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                editor.putLong("widget_refresh_time", seekBar.getProgress());
                editor.apply();
            }
        });
        widget_refresh_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                widget_refresh_seekbar.setProgress(30);
                editor.remove("widget_refresh_time");
                editor.apply();
                Snackbar.make(view, getString(R.string.succeed), Snackbar.LENGTH_SHORT).show();
            }
        });
        about_page_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent().setClass(SettingActivity.this, AboutActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sharedPreferences.getBoolean("oauth_logined", false)) {
            try {
                final JSONObject jsonObject = new JSONObject(sharedPreferences.getString("oauth_account_information", ""));
                if (FileUtil.isFile(getFilesDir().getPath() + "/Oauth/Avatar.png")) {
                    oauth_avatar_imageview.setImageURI(Uri.fromFile(new File(getFilesDir().getPath() + "/Oauth/Avatar.png")));
                }
                oauth_name_textview.setText(jsonObject.optString("name"));
                oauth_bio_textview.setText(jsonObject.optString("bio"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        initFeed();
        if (sharedPreferences.getBoolean("widget_enabled", false)) {
            widget_enable_textview.setText(R.string.widget_enable_text);
        } else {
            widget_enable_textview.setText(R.string.widget_disenable_text);
        }
        oauth_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sharedPreferences.getBoolean("oauth_logined", false)) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/login/oauth/authorize/?client_id=a2cecb404f9d11e7abbe")));
                } else {
                    Snackbar.make(v, R.string.oauth_logout_text, Snackbar.LENGTH_LONG).setAction(R.string.oauth_logout_button, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editor.remove("oauth_access_token");
                            editor.remove("oauth_account_information");
                            editor.remove("oauth_logined");
                            editor.apply();
                            FileUtil.deleteFile(getFilesDir().getPath() + "/Oauth/Avatar.png");
                            finish();
                            startActivity(getIntent());
                        }
                    }).show();
                }
            }
        });
    }

    private void updateFontStatus() {
        current_font_textview.post(new Runnable() {
            @Override
            public void run() {
                if (sharedPreferences.getString("font_path", "").equals("")) {
                    current_font_textview.setVisibility(View.GONE);
                } else {
                    current_font_textview.setVisibility(View.VISIBLE);
                    File file = new File(sharedPreferences.getString("font_path", ""));
                    current_font_textview.setText(getString(R.string.current_font_text) + file.getName());
                }
            }
        });
    }

    public void initFeed() {
        feedList.clear();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        feed_recyclerview.setLayoutManager(linearLayoutManager);
        feedAdapter = new FeedAdapter(feedList);
        feed_recyclerview.setAdapter(feedAdapter);
        int selectedInt = sharedPreferences.getInt("feed_code", 0);
        try {
            JSONArray jsonArray = new JSONArray(FileUtil.readTextFromFile(getFilesDir().getPath() + "/Feed/Feed.json"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = new JSONObject(jsonArray.optString(i));
                String feedType = jsonObject.optString("feed_type");
                int feedTypeImageInt = 0;
                Boolean ifSelected;
                if (feedType.equals("remote")) {
                    feedTypeImageInt = R.drawable.ic_cloud;
                } else if (feedType.equals("local")) {
                    feedTypeImageInt = R.drawable.ic_file;
                } else if (feedType.equals("internet")) {
                    feedTypeImageInt = R.drawable.ic_world;
                }
                if (selectedInt == i) {
                    ifSelected = true;
                } else {
                    ifSelected = false;
                }
                Feed feed = new Feed(this, feedTypeImageInt, jsonObject.optString("feed_name"), ifSelected);
                feedList.add(feed);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) & isSettingUpdated) { //监控/拦截/屏蔽返回键
            System.exit(0);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();
                try {
                    String file_path = getPath(this, uri);
                    Snackbar.make(view, getString(R.string.setting_succeed_text), Snackbar.LENGTH_SHORT).show();
                    editor.putString("font_path", file_path);
                    editor.apply();
                    //finishActivity(requestCode);
                    updateFontStatus();
                    //Toast.makeText(this, "文件路径："+uri.getPath().toString(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.lz233.onetext.updatefeedlist")) {
                initFeed();
            }
            if (intent.getAction().equals("com.lz233.onetext.issettingupdated")) {
                isSettingUpdated = true;
            }
        }
    }
}
