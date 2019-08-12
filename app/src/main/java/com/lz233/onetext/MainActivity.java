package com.lz233.onetext;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lz233.onetext.tools.FileUtils;
import com.lz233.onetext.tools.SaveBitmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import pub.devrel.easypermissions.EasyPermissions;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{
    private int onetext_code;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ProgressBar progressBar;
    private LinearLayout pic_layout;
    private TextView onetext_quote1_textview;
    private TextView onetext_text_textview;
    private TextView onetext_quote2_textview;
    private TextView onetext_by_textview;
    private TextView onetext_from_textview;
    private TextView onetext_time_textview;
    private LinearLayout request_permissions_layout;
    private AppCompatButton request_permissions_button;
    private IndicatorSeekBar onetext_text_size_seekbar;
    private AppCompatButton onetext_text_size_button;
    private IndicatorSeekBar onetext_by_size_seekbar;
    private AppCompatButton onetext_by_size_button;
    private IndicatorSeekBar onetext_time_size_seekbar;
    private AppCompatButton onetext_time_size_button;
    private IndicatorSeekBar onetext_from_size_seekbar;
    private AppCompatButton onetext_from_size_button;
    private AppCompatButton save_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //fb
        pic_layout = findViewById(R.id.pic_layout);
        progressBar = findViewById(R.id.progressBar);
        onetext_quote1_textview = findViewById(R.id.onetext_quote1_textview);
        onetext_text_textview = findViewById(R.id.onetext_text_textview);
        onetext_quote2_textview = findViewById(R.id.onetext_quote2_textview);
        onetext_by_textview = findViewById(R.id.onetext_by_textview);
        onetext_from_textview = findViewById(R.id.onetext_from_textview);
        onetext_time_textview = findViewById(R.id.onetext_time_textview);
        request_permissions_layout = findViewById(R.id.request_permissions_layout);
        request_permissions_button = findViewById(R.id.request_permissions_button);
        onetext_text_size_seekbar = findViewById(R.id.onetext_text_size_seekbar);
        onetext_text_size_button = findViewById(R.id.onetext_text_size_button);
        onetext_by_size_seekbar = findViewById(R.id.onetext_by_size_seekbar);
        onetext_by_size_button = findViewById(R.id.onetext_by_size_button);
        onetext_time_size_seekbar = findViewById(R.id.onetext_time_size_seekbar);
        onetext_time_size_button = findViewById(R.id.onetext_time_size_button);
        onetext_from_size_seekbar = findViewById(R.id.onetext_from_size_seekbar);
        onetext_from_size_button = findViewById(R.id.onetext_from_size_button);
        save_button = findViewById(R.id.save_button);
        //申请权限
        final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions);
        //初始化
        AppCenter.start(getApplication(), "2bd0575c-79d2-45d9-97f3-95e6a81e34e0", Analytics.class, Crashes.class);
        //Analytics.trackEvent("My custom event");
        //Crashes.generateTestCrash();
        sharedPreferences = getSharedPreferences("setting",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        run();
        onetext_quote1_textview.setTextSize(sharedPreferences.getInt("onetext_text_size",px2sp(this,getResources().getDimensionPixelSize(R.dimen.onetext_size))));
        onetext_text_textview.setTextSize(sharedPreferences.getInt("onetext_text_size",px2sp(this,getResources().getDimensionPixelSize(R.dimen.onetext_size))));
        onetext_quote2_textview.setTextSize(sharedPreferences.getInt("onetext_text_size",px2sp(this,getResources().getDimensionPixelSize(R.dimen.onetext_size))));
        onetext_by_textview.setTextSize(sharedPreferences.getInt("onetext_by_size",px2sp(this,getResources().getDimensionPixelSize(R.dimen.text_size))));
        onetext_time_textview.setTextSize(sharedPreferences.getInt("onetext_time_size",px2sp(this,getResources().getDimensionPixelSize(R.dimen.small_text_size))));
        onetext_from_textview.setTextSize(sharedPreferences.getInt("onetext_from_size",px2sp(this,getResources().getDimensionPixelSize(R.dimen.small_text_size))));
        onetext_text_size_seekbar.setIndicatorTextFormat(getString(R.string.onetext_text_size_text)+" ${PROGRESS} SP");
        onetext_by_size_seekbar.setIndicatorTextFormat(getString(R.string.onetext_by_size_text)+" ${PROGRESS} SP");
        onetext_time_size_seekbar.setIndicatorTextFormat(getString(R.string.onetext_time_size_text)+" ${PROGRESS} SP");
        onetext_from_size_seekbar.setIndicatorTextFormat(getString(R.string.onetext_from_size_text)+" ${PROGRESS} SP");
        onetext_text_size_seekbar.setProgress(sharedPreferences.getInt("onetext_text_size",px2sp(this,getResources().getDimensionPixelSize(R.dimen.onetext_size))));
        onetext_by_size_seekbar.setProgress(sharedPreferences.getInt("onetext_by_size",px2sp(this,getResources().getDimensionPixelSize(R.dimen.text_size))));
        onetext_time_size_seekbar.setProgress(sharedPreferences.getInt("onetext_time_size",px2sp(this,getResources().getDimensionPixelSize(R.dimen.small_text_size))));
        onetext_from_size_seekbar.setProgress(sharedPreferences.getInt("onetext_from_size",px2sp(this,getResources().getDimensionPixelSize(R.dimen.small_text_size))));
        //监听器
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(EasyPermissions.hasPermissions(MainActivity.this, permissions)){
                    String pic_file_name = "OneText "+ UUID.randomUUID().toString()+".jpg";
                    String pic_file_path = Environment.getExternalStorageDirectory()+"/Pictures/OneText/";
                    if(!FileUtils.isDirectory(pic_file_path)) {
                        File file = new File(pic_file_path);
                        file.mkdirs();
                    }
                    Boolean isSuccess = SaveBitmap.saveBitmapToSdCard(MainActivity.this,SaveBitmap.getCacheBitmapFromView(pic_layout), pic_file_path+pic_file_name);
                    if(isSuccess) {
                        Snackbar.make(view, getString(R.string.save_succeed)+" "+pic_file_name, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }else {
                        Snackbar.make(view, getString(R.string.save_fail), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                }else {
                    Snackbar.make(view, getString(R.string.request_permissions_text), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }
        });
        onetext_text_size_seekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                onetext_quote1_textview.setTextSize(seekParams.progress);
                onetext_text_textview.setTextSize(seekParams.progress);
                onetext_quote2_textview.setTextSize(seekParams.progress);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                editor.putInt("onetext_text_size",seekBar.getProgress());
                editor.apply();
            }
        });
        onetext_text_size_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int text_size = px2sp(MainActivity.this,getResources().getDimensionPixelSize(R.dimen.onetext_size));
                onetext_quote1_textview.setTextSize(text_size);
                onetext_text_textview.setTextSize(text_size);
                onetext_quote2_textview.setTextSize(text_size);
                onetext_text_size_seekbar.setProgress(text_size);
                editor.putInt("onetext_text_size",text_size);
                editor.apply();
            }
        });
        onetext_by_size_seekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                onetext_by_textview.setTextSize(seekParams.progress);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                editor.putInt("onetext_by_size",seekBar.getProgress());
                editor.apply();
            }
        });
        onetext_by_size_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int text_size = px2sp(MainActivity.this,getResources().getDimensionPixelSize(R.dimen.text_size));
                onetext_by_textview.setTextSize(text_size);
                onetext_by_size_seekbar.setProgress(text_size);
                editor.putInt("onetext_by_size",text_size);
                editor.apply();
            }
        });
        onetext_time_size_seekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                onetext_time_textview.setTextSize(seekParams.progress);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                editor.putInt("onetext_time_size",seekBar.getProgress());
                editor.apply();
            }
        });
        onetext_time_size_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int text_size = px2sp(MainActivity.this,getResources().getDimensionPixelSize(R.dimen.small_text_size));
                onetext_time_textview.setTextSize(text_size);
                onetext_time_size_seekbar.setProgress(text_size);
                editor.putInt("onetext_time_size",text_size);
                editor.apply();
            }
        });
        onetext_from_size_seekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                onetext_from_textview.setTextSize(seekParams.progress);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                editor.putInt("onetext_from_size",seekBar.getProgress());
                editor.apply();
            }
        });
        onetext_from_size_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int text_size = px2sp(MainActivity.this,getResources().getDimensionPixelSize(R.dimen.small_text_size));
                onetext_from_textview.setTextSize(text_size);
                onetext_from_size_seekbar.setProgress(text_size);
                editor.putInt("onetext_from_size",text_size);
                editor.apply();
            }
        });
    }
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
        //run();
        //Toast.makeText(MainActivity.this,"test",Toast.LENGTH_SHORT).show();
    }
    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Random random = new Random();
                    Long currentTimeMillis = System.currentTimeMillis();
                    JSONArray jsonArray;
                    Boolean shouldUpdate = false;
                    if((currentTimeMillis-sharedPreferences.getLong("onetext_latest_refresh_time",0))>(sharedPreferences.getLong("feed_refresh_time",30)*60000)){
                        if(!FileUtils.isFile(getFilesDir().getPath()+"/OneText/OneText-Library.json")){
                            progressBar.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.VISIBLE);
                                }
                            });
                            FileUtils.downLoadFileFromURL(sharedPreferences.getString("feed_URL","https://github.com/lz233/OneText-Library/raw/master/OneText-Library.json"),getFilesDir().getPath()+"/OneText/","OneText-Library.json",true);
                        } else{
                            shouldUpdate = true;
                        }
                        jsonArray = new JSONArray(FileUtils.readTextFromFile(getFilesDir().getPath()+"/OneText/OneText-Library.json"));
                        onetext_code = random.nextInt(jsonArray.length());
                        editor.putInt("onetext_code",onetext_code);
                        editor.putLong("onetext_latest_refresh_time",currentTimeMillis);
                        editor.commit();
                    }else {
                        jsonArray = new JSONArray(FileUtils.readTextFromFile(getFilesDir().getPath()+"/OneText/OneText-Library.json"));
                        onetext_code = sharedPreferences.getInt("onetext_code",random.nextInt(jsonArray.length()));
                    }
                    if(sharedPreferences.getBoolean("widget_request_download",false)) {
                        shouldUpdate = true;
                    }
                    JSONObject jsonObject = new JSONObject(jsonArray.optString(onetext_code));
                    final String text = jsonObject.optString("text");
                    final String by = jsonObject.optString("by");
                    final String from = jsonObject.optString("from");
                    JSONArray timeJsonArray = new JSONArray(jsonObject.optString("time"));
                    final String timeOfRecord = timeJsonArray.optString(0);
                    final String timeOfCreation = timeJsonArray.optString(1);
                    final String time;
                    if (!timeOfCreation.equals("")) {
                        time = getString(R.string.record_time)+"："+timeOfRecord+" "+getString(R.string.created_time)+"："+timeOfCreation;
                    }else {
                        time = getString(R.string.record_time)+"："+timeOfRecord;
                    }
                    onetext_text_textview.post(new Runnable() {
                        @Override
                        public void run() {
                            //progressBar.setVisibility(View.GONE);
                            onetext_text_textview.setText(text);
                            if(!by.equals("")) {
                                onetext_by_textview.setText("—— "+by);
                            }else {
                                onetext_by_textview.setVisibility(View.GONE);
                            }
                            if(!from.equals("")) {
                                onetext_from_textview.setText(from);
                            }else {
                                onetext_from_textview.setVisibility(View.GONE);
                            }
                            onetext_time_textview.setText(time);
                        }
                    });
                    //更新小部件
                    Intent intent = new Intent("com.lz233.onetext.widget");
                    intent.setPackage(getPackageName());
                    MainActivity.this.sendBroadcast(intent);
                    if(shouldUpdate) {
                        runUpdate();
                    }else {
                        progressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void runUpdate () {
        new Thread(new Runnable() {
            @Override
            public void run() {
                progressBar.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
                FileUtils.downLoadFileFromURL(sharedPreferences.getString("feed_URL","https://github.com/lz233/OneText-Library/raw/master/OneText-Library.json"),getFilesDir().getPath()+"/OneText/","OneText-Library.json",true);
                editor.remove("widget_request_download");
                editor.apply();
                progressBar.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
        startActivity(new Intent().setClass(MainActivity.this, SettingActivity.class));
        return true;
        }

        return super.onOptionsItemSelected(item);
        }
    public void requestPermissions(final String[] permissions){
        //申请权限
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //已经打开权限
            //Toast.makeText(this, "已经申请相关权限", Toast.LENGTH_SHORT).show();
            request_permissions_layout.setVisibility(View.GONE);
        } else {
            //没有打开相关权限、申请权限
            request_permissions_layout.setVisibility(View.VISIBLE);
            request_permissions_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EasyPermissions.requestPermissions(MainActivity.this,getString(R.string.request_permissions_text), 1, permissions);
                }
            });
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //把申请权限的回调交由EasyPermissions处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //下面两个方法是实现EasyPermissions的EasyPermissions.PermissionCallbacks接口
    //分别返回授权成功和失败的权限
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //Log.i(TAG, "获取成功的权限" + perms);
        request_permissions_layout.setVisibility(View.GONE);
    }

    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //Log.i(TAG, "获取失败的权限" + perms);
    }
}
