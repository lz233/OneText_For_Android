package com.lz233.onetext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Instrumentation;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.lz233.onetext.tools.FileUtils;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import pub.devrel.easypermissions.EasyPermissions;

public class SettingActivity extends BaseActivity {
    private Boolean isSettingUpdated = false;
    private View view;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private long taskid_yiqi;
    private DownloadManager downloadManager_yiqi;
    private long taskid_canger;
    private DownloadManager downloadManager_canger;
    private String font_path_yiqi;
    private String font_path_canger;
    private TextView current_font_textview;
    private LinearLayout font_yiqi_layout;
    private LinearLayout font_canger_layout;
    private TextView font_yiqi_textview;
    private TextView font_canger_textview;
    private TextView font_system_textview;
    private TextView font_custom_textview;
    private SwitchMaterial interface_md2_switch;
    private AppCompatSpinner interface_daynight_spinner;
    private RadioGroup feed_type_radiogroup;
    private RadioButton feed_type_remote_radiobutton;
    private RadioButton feed_type_local_radiobutton;
    private LinearLayout feed_remote_layout;
    private LinearLayout feed_local_layout;
    private EditText feed_edittext;
    private ImageView feed_imageview;
    private IndicatorSeekBar feed_refresh_seekbar;
    private ImageView feed_refresh_imageview;
    private TextView feed_local_path_textview;
    private TextView feed_local_choose_textview;
    private TextView widget_enable_textview;
    private SwitchMaterial widget_dark_switch;
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
        //by
        view = getWindow().getDecorView();
        sharedPreferences = getSharedPreferences("setting",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        font_path_yiqi = Environment.getExternalStorageDirectory()+"/Android/data/com.lz233.onetext/files/Download/Fonts/yiqi.ttf";
        font_path_canger = Environment.getExternalStorageDirectory()+"/Android/data/com.lz233.onetext/files/Download/Fonts/canger.ttf";
        current_font_textview = findViewById(R.id.current_font_textview);
        font_yiqi_layout = findViewById(R.id.font_yiqi_layout);
        font_canger_layout = findViewById(R.id.font_canger_layout);
        font_yiqi_textview = findViewById(R.id.font_yiqi_textview);
        font_canger_textview = findViewById(R.id.font_canger_textview);
        font_system_textview = findViewById(R.id.font_system_textview);
        font_custom_textview = findViewById(R.id.font_custom_textview);
        interface_md2_switch = findViewById(R.id.interface_md2_switch);
        interface_daynight_spinner = findViewById(R.id.interface_daynight_spinner);
        feed_type_radiogroup = findViewById(R.id.feed_type_radiogroup);
        feed_type_remote_radiobutton = findViewById(R.id.feed_type_remote_radiobutton);
        feed_type_local_radiobutton = findViewById(R.id.feed_type_local_radiobutton);
        feed_remote_layout = findViewById(R.id.feed_remote_layout);
        feed_local_layout = findViewById(R.id.feed_local_layout);
        feed_edittext = findViewById(R.id.feed_edittext);
        feed_imageview = findViewById(R.id.feed_imageview);
        feed_refresh_seekbar = findViewById(R.id.feed_refresh_seekbar);
        feed_refresh_imageview = findViewById(R.id.feed_refresh_imageview);
        feed_local_path_textview = findViewById(R.id.feed_local_path_textview);
        feed_local_choose_textview = findViewById(R.id.feed_local_choose_textview);
        widget_enable_textview = findViewById(R.id.widget_enable_textview);
        widget_dark_switch = findViewById(R.id.widget_dark_switch);
        widget_notification_switch = findViewById(R.id.widget_notification_switch);
        widget_refresh_seekbar = findViewById(R.id.widget_refresh_seekbar);
        widget_refresh_imageview = findViewById(R.id.widget_refresh_imageview);
        about_page_textview = findViewById(R.id.about_page_textview);
        //初始化
        updateFontStatus();
        switch (sharedPreferences.getInt("interface_daynight",0)){
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
        if(sharedPreferences.getString("interface_style","md2").equals("md2")) {
            interface_md2_switch.setChecked(true);
        }
        if(sharedPreferences.getString("feed_type","remote").equals("remote")) {
            feed_type_remote_radiobutton.setChecked(true);
            feed_remote_layout.setVisibility(View.VISIBLE);
            feed_local_layout.setVisibility(View.GONE);
        }
        if (sharedPreferences.getString("feed_type","remote").equals("local")) {
            feed_type_local_radiobutton.setChecked(true);
            feed_local_layout.setVisibility(View.VISIBLE);
            feed_remote_layout.setVisibility(View.GONE);
        }
        feed_edittext.setText(sharedPreferences.getString("feed_URL","https://github.com/lz233/OneText-Library/raw/master/OneText-Library.json"));
        feed_local_path_textview.setText(sharedPreferences.getString("feed_local_path",""));
        feed_refresh_seekbar.setIndicatorTextFormat(getString(R.string.feed_refresh_text)+" ${PROGRESS} "+getString(R.string.hour));
        feed_refresh_seekbar.setProgress(sharedPreferences.getLong("feed_refresh_time",1));
        widget_refresh_seekbar.setIndicatorTextFormat(getString(R.string.widget_refresh_text)+" ${PROGRESS} "+getString(R.string.minute));
        widget_refresh_seekbar.setProgress(sharedPreferences.getLong("feed_refresh_time",30));
        widget_dark_switch.setChecked(sharedPreferences.getBoolean("widget_dark",false));
        widget_notification_switch.setChecked(sharedPreferences.getBoolean("widget_notification_enabled",false));
        // 监听器
        font_yiqi_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(EasyPermissions.hasPermissions(SettingActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //if(!FileUtils.isFile(Environment.getExternalStorageDirectory()+"/Android/data/com.lz233.onetext/files/Download/qiyi.ttf")){
                    if(!FileUtils.isFile(font_path_yiqi)){
                        //新建下载任务
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("https://font.salt-fish.club/1.ttf"));
                        request.setDestinationInExternalFilesDir(SettingActivity.this, Environment.DIRECTORY_DOWNLOADS,"Fonts/yiqi.ttf");
                        Context context = getApplicationContext();
                        downloadManager_yiqi= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                        taskid_yiqi = downloadManager_yiqi.enqueue(request);
                        context.registerReceiver(receiver_yiqi, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                        font_yiqi_textview.setText(R.string.downloading_text);
                    }else {
                        Snackbar.make(view, getString(R.string.setting_succeed_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        editor.putString("font_path",font_path_yiqi);
                        editor.apply();
                        updateFontStatus();
                    }
                }else {
                    Snackbar.make(view, getString(R.string.request_permissions_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }
        });
        font_canger_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(EasyPermissions.hasPermissions(SettingActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //if(!FileUtils.isFile(Environment.getExternalStorageDirectory()+"/Android/data/com.lz233.onetext/files/Download/qiyi.ttf")){
                    if(!FileUtils.isFile(font_path_canger)){
                        //新建下载任务
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("https://font.salt-fish.club/2.ttf"));
                        request.setDestinationInExternalFilesDir(SettingActivity.this, Environment.DIRECTORY_DOWNLOADS,"Fonts/canger.ttf");
                        Context context = getApplicationContext();
                        downloadManager_canger= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                        taskid_canger = downloadManager_canger.enqueue(request);
                        context.registerReceiver(receiver_canger, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                        font_canger_textview.setText(R.string.downloading_text);
                    }else {
                        Snackbar.make(view, getString(R.string.setting_succeed_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        editor.putString("font_path",font_path_canger);
                        editor.apply();
                        updateFontStatus();
                    }
                }else {
                    Snackbar.make(view, getString(R.string.request_permissions_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }
        });
        font_system_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.remove("font_path");
                editor.apply();
                Snackbar.make(view, getString(R.string.setting_succeed_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                updateFontStatus();
            }
        });
        font_custom_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(EasyPermissions.hasPermissions(SettingActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent,1);
                }else {
                    Snackbar.make(view, getString(R.string.request_permissions_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }
        });
        interface_md2_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    editor.putString("interface_style","md2");
                    editor.apply();
                    isSettingUpdated = true;
                    Snackbar.make(view, getString(R.string.setting_succeed_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }else {
                    editor.putString("interface_style","default");
                    editor.apply();
                    isSettingUpdated = true;
                    Snackbar.make(view, getString(R.string.setting_succeed_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }
        });
        final int[] OnItemSelectedInt = {0};
        interface_daynight_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    OnItemSelectedInt[0] = OnItemSelectedInt[0] + 1;
                    if(OnItemSelectedInt[0]>1){
                        isSettingUpdated = true;
                        Snackbar.make(view, getString(R.string.setting_succeed_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                    switch (i) {
                        case 0:
                            editor.putInt("interface_daynight",0);
                            break;
                        case 1:
                            editor.putInt("interface_daynight",1);
                            break;
                        case 2:
                            editor.putInt("interface_daynight",2);
                            break;
                    }
                }else {
                    switch (i) {
                        case 0:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                            editor.putInt("interface_daynight",0);
                            break;
                        case 1:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            editor.putInt("interface_daynight",1);
                            break;
                        case 2:
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            editor.putInt("interface_daynight",2);
                            break;
                    }
                }
                editor.apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        feed_type_radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.feed_type_remote_radiobutton:
                        editor.putString("feed_type","remote");
                        feed_remote_layout.setVisibility(View.VISIBLE);
                        feed_local_layout.setVisibility(View.GONE);
                        break;
                    case R.id.feed_type_local_radiobutton:
                        editor.putString("feed_type","local");
                        feed_local_layout.setVisibility(View.VISIBLE);
                        feed_remote_layout.setVisibility(View.GONE);
                        break;
                }
                editor.remove("feed_latest_refresh_time");
                editor.remove("widget_latest_refresh_time");
                editor.remove("onetext_code");
                editor.apply();
                isSettingUpdated = true;
                Snackbar.make(view, getString(R.string.setting_succeed_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });
        feed_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sharedPreferences.getString("feed_URL","https://github.com/lz233/OneText-Library/raw/master/OneText-Library.json").equals(feed_edittext.getText().toString())){
                    if(feed_edittext.getText().toString().equals("")) {
                        editor.remove("feed_URL");
                    }else {
                        editor.putString("feed_URL",feed_edittext.getText().toString());
                    }
                    editor.remove("feed_latest_refresh_time");
                    editor.remove("widget_latest_refresh_time");
                    editor.remove("onetext_code");
                    editor.apply();
                    isSettingUpdated = true;
                    FileUtils.deleteFile(getFilesDir().getPath()+"/OneText/OneText-Library.json");
                    Snackbar.make(view, getString(R.string.setting_succeed_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }else {
                    Snackbar.make(view, getString(R.string.feed_url_no_changed_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
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
                editor.putLong("feed_refresh_time",seekBar.getProgress());
                editor.apply();
            }
        });
        feed_refresh_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feed_refresh_seekbar.setProgress(1);
                editor.remove("feed_refresh_time");
                editor.apply();
                Snackbar.make(view, getString(R.string.succeed), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });
        feed_local_choose_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(EasyPermissions.hasPermissions(SettingActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent,2);
                }else {
                    Snackbar.make(view, getString(R.string.request_permissions_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }
        });
        widget_dark_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if(sharedPreferences.getBoolean("widget_enabled",false)) {
                        editor.putBoolean("widget_dark",true);
                        editor.commit();
                        Intent intent = new Intent("com.lz233.onetext.widget");
                        intent.setPackage(getPackageName());
                        SettingActivity.this.sendBroadcast(intent);
                    }else {
                        widget_dark_switch.setChecked(false);
                        Snackbar.make(view, getString(R.string.widget_disenable_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                }else {
                    if(sharedPreferences.getBoolean("widget_enabled",false)){
                        editor.putBoolean("widget_dark",false);
                        editor.commit();
                        Intent intent = new Intent("com.lz233.onetext.widget");
                        intent.setPackage(getPackageName());
                        SettingActivity.this.sendBroadcast(intent);
                    }else {
                        widget_dark_switch.setChecked(true);
                        Snackbar.make(view, getString(R.string.widget_disenable_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                }
            }
        });
        widget_notification_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("widget_onetext", getString(R.string.widget_notification_text), NotificationManager.IMPORTANCE_DEFAULT);
                    channel.setSound(null,null);
                    channel.enableLights(false);
                    channel.enableVibration(false);
                    channel.setVibrationPattern(new long[]{0});
                    notificationManager.createNotificationChannel(channel);
                }
                if(b){
                    if(sharedPreferences.getBoolean("widget_enabled",false)) {
                        editor.putBoolean("widget_notification_enabled",true);
                        editor.commit();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            final NotificationChannel channel = notificationManager.getNotificationChannel("widget_onetext");
                            if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                                Snackbar.make(view, getString(R.string.widget_notification_permissions_text), Snackbar.LENGTH_SHORT).setAction(getString(R.string.request_permissions_button), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                                        intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
                                        startActivity(intent);
                                    }
                                }).show();
                            }else {
                                Snackbar.make(view, getString(R.string.widget_notification_next_effective_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                            }
                        }else {
                            Snackbar.make(view, getString(R.string.widget_notification_next_effective_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        }
                    }else {
                        widget_notification_switch.setChecked(false);
                        Snackbar.make(view, getString(R.string.widget_disenable_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                }else {
                    editor.putBoolean("widget_notification_enabled",false);
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
                editor.putLong("widget_refresh_time",seekBar.getProgress());
                editor.apply();
            }
        });
        widget_refresh_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                widget_refresh_seekbar.setProgress(30);
                editor.remove("widget_refresh_time");
                editor.apply();
                Snackbar.make(view, getString(R.string.succeed), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
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
        // The activity is about to become visible.
        if(sharedPreferences.getBoolean("widget_enabled",false)) {
            widget_enable_textview.setText(R.string.widget_enable_text);
        }else {
            widget_enable_textview.setText(R.string.widget_disenable_text);
        }
    }
    private void updateFontStatus() {
        current_font_textview.post(new Runnable() {
            @Override
            public void run() {
                if(sharedPreferences.getString("font_path","").equals("")){
                    current_font_textview.setVisibility(View.GONE);
                }else {
                    current_font_textview.setVisibility(View.VISIBLE);
                    current_font_textview.setText(getString(R.string.current_font_text)+" "+sharedPreferences.getString("font_path",""));
                }
            }
        });
    }
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if((keyCode == KeyEvent.KEYCODE_BACK)&isSettingUpdated) { //监控/拦截/屏蔽返回键
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
                    Snackbar.make(view, getString(R.string.setting_succeed_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    editor.putString("font_path",file_path);
                    editor.apply();
                    //finishActivity(requestCode);
                    updateFontStatus();
                    //Toast.makeText(this, "文件路径："+uri.getPath().toString(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (requestCode == 2) {
                Uri uri = data.getData();
                try {
                    String file_path = getPath(this, uri);
                    editor.putString("feed_local_path",file_path);
                    editor.remove("feed_latest_refresh_time");
                    editor.remove("onetext_code");
                    editor.apply();
                    isSettingUpdated = true;
                    Snackbar.make(view, getString(R.string.setting_succeed_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    feed_local_path_textview.setText(file_path);
                    //finishActivity(requestCode);
                    //Toast.makeText(this, "文件路径："+uri.getPath().toString(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //广播接受者，接收下载状态
    private BroadcastReceiver receiver_yiqi = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(taskid_yiqi);//筛选下载任务，传入任务ID，可变参数
            Cursor c = downloadManager_yiqi.query(query);
            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch (status) {
                    case DownloadManager.STATUS_PAUSED:
                        //MLog.i(">>>下载暂停");
                    case DownloadManager.STATUS_PENDING:
                        //MLog.i(">>>下载延迟");
                    case DownloadManager.STATUS_RUNNING:
                        //MLog.i(">>>正在下载");
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        //MLog.i(">>>下载完成");
                        //下载完成安装APK
                        //downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + versionName;
                        //installAPK(new File(downloadPath));
                        //Toast.makeText(SettingActivity.this,"ok",Toast.LENGTH_SHORT).show();
                        //FileUtils.copyFile(Environment.getExternalStorageDirectory()+"/Android/data/com.lz233.onetext/files/Download/qiyi.ttf",font_path_yiqi,true,true);
                        font_yiqi_textview.post(new Runnable() {
                            @Override
                            public void run() {
                                font_yiqi_textview.setText(R.string.font_yiqi);
                            }
                        });
                        Snackbar.make(view, getString(R.string.setting_succeed_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        editor.putString("font_path",font_path_yiqi);
                        editor.apply();
                        updateFontStatus();
                        break;
                    case DownloadManager.STATUS_FAILED:
                        //MLog.i(">>>下载失败");
                        font_yiqi_textview.setText(R.string.download_failed_text);
                        break;
                }
            }
        }
    };
    //广播接受者，接收下载状态
    private BroadcastReceiver receiver_canger = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(taskid_canger);//筛选下载任务，传入任务ID，可变参数
            Cursor c = downloadManager_canger.query(query);
            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch (status) {
                    case DownloadManager.STATUS_PAUSED:
                        //MLog.i(">>>下载暂停");
                    case DownloadManager.STATUS_PENDING:
                        //MLog.i(">>>下载延迟");
                    case DownloadManager.STATUS_RUNNING:
                        //MLog.i(">>>正在下载");
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        //MLog.i(">>>下载完成");
                        //下载完成安装APK
                        //downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + versionName;
                        //installAPK(new File(downloadPath));
                        //Toast.makeText(SettingActivity.this,"ok",Toast.LENGTH_SHORT).show();
                        //FileUtils.copyFile(Environment.getExternalStorageDirectory()+"/Android/data/com.lz233.onetext/files/Download/qiyi.ttf",font_path_yiqi,true,true);
                        font_canger_textview.post(new Runnable() {
                            @Override
                            public void run() {
                                font_canger_textview.setText(R.string.font_canger);
                            }
                        });
                        Snackbar.make(view, getString(R.string.setting_succeed_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        editor.putString("font_path",font_path_yiqi);
                        editor.apply();
                        updateFontStatus();
                        break;
                    case DownloadManager.STATUS_FAILED:
                        //MLog.i(">>>下载失败");
                        font_canger_textview.setText(R.string.download_failed_text);
                        break;
                }
            }
        }
    };
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
}
