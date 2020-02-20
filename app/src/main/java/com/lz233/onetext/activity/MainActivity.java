package com.lz233.onetext.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.lz233.onetext.R;
import com.lz233.onetext.tools.utils.AppUtil;
import com.lz233.onetext.tools.utils.CoreUtil;
import com.lz233.onetext.tools.utils.FileUtil;
import com.lz233.onetext.tools.utils.SaveBitmapUtil;
import com.lz233.onetext.view.NiceImageView;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.microsoft.appcenter.crashes.model.ErrorReport;
import com.microsoft.appcenter.utils.async.AppCenterConsumer;
import com.microsoft.appcenter.utils.async.AppCenterFuture;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.json.JSONException;

import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static com.lz233.onetext.tools.utils.AppUtil.px2sp;

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private CoreUtil coreUtil;
    private NiceImageView avatar_imageview;
    private SwipeRefreshLayout onetext_swiperefreshlayout;
    private ProgressBar progressBar;
    private LinearLayout pic_layout;
    private TextView onetext_quote1_textview;
    private TextView onetext_text_textview;
    private TextView onetext_quote2_textview;
    private TextView onetext_by_textview;
    private TextView onetext_from_textview;
    private TextView onetext_time_textview;
    private ImageView seal_imageview;
    private LinearLayout request_permissions_layout;
    private AppCompatButton request_permissions_button;
    private AppCompatButton save_button;
    private AppCompatButton refresh_button;
    private AppCompatButton seal_button;
    private IndicatorSeekBar onetext_text_size_seekbar;
    private AppCompatButton onetext_text_size_button;
    private IndicatorSeekBar onetext_by_size_seekbar;
    private AppCompatButton onetext_by_size_button;
    private IndicatorSeekBar onetext_time_size_seekbar;
    private AppCompatButton onetext_time_size_button;
    private IndicatorSeekBar onetext_from_size_seekbar;
    private AppCompatButton onetext_from_size_button;

    /*static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*if(sharedPreferences.getString("interface_style","md2").equals("default")){
            setContentView(R.layout.activity_main);
        }else if(sharedPreferences.getString("interface_style","md2").equals("md2")){
            setContentView(R.layout.activity_main_2);
        }*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //曲 线 救 国
        fuckNav(findViewById(R.id.last_layout));
        //fb
        coreUtil = new CoreUtil(MainActivity.this);
        avatar_imageview = findViewById(R.id.avatar_imageview);
        onetext_swiperefreshlayout = findViewById(R.id.onetext_swiperefreshlayout);
        pic_layout = findViewById(R.id.pic_layout);
        progressBar = findViewById(R.id.progressBar);
        onetext_quote1_textview = findViewById(R.id.onetext_quote1_textview);
        onetext_text_textview = findViewById(R.id.onetext_text_textview);
        onetext_quote2_textview = findViewById(R.id.onetext_quote2_textview);
        onetext_by_textview = findViewById(R.id.onetext_by_textview);
        onetext_from_textview = findViewById(R.id.onetext_from_textview);
        onetext_time_textview = findViewById(R.id.onetext_time_textview);
        seal_imageview = findViewById(R.id.seal_imageview);
        request_permissions_layout = findViewById(R.id.request_permissions_layout);
        request_permissions_button = findViewById(R.id.request_permissions_button);
        save_button = findViewById(R.id.save_button);
        refresh_button = findViewById(R.id.refresh_button);
        seal_button = findViewById(R.id.seal_button);
        onetext_text_size_seekbar = findViewById(R.id.onetext_text_size_seekbar);
        onetext_text_size_button = findViewById(R.id.onetext_text_size_button);
        onetext_by_size_seekbar = findViewById(R.id.onetext_by_size_seekbar);
        onetext_by_size_button = findViewById(R.id.onetext_by_size_button);
        onetext_time_size_seekbar = findViewById(R.id.onetext_time_size_seekbar);
        onetext_time_size_button = findViewById(R.id.onetext_time_size_button);
        onetext_from_size_seekbar = findViewById(R.id.onetext_from_size_seekbar);
        onetext_from_size_button = findViewById(R.id.onetext_from_size_button);
        //申请权限
        final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions);
        //初始化
        AppCenter.start(getApplication(), "2bd0575c-79d2-45d9-97f3-95e6a81e34e0", Analytics.class, Crashes.class);
        AppCenterFuture<Boolean> hasCrashedInLastSession = Crashes.hasCrashedInLastSession();
        hasCrashedInLastSession.thenAccept(new AppCenterConsumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {
                if (aBoolean) {
                    AppCenterFuture<ErrorReport> getLastSessionCrashReport = Crashes.getLastSessionCrashReport();
                    getLastSessionCrashReport.thenAccept(new AppCenterConsumer<ErrorReport>() {
                        @Override
                        public void accept(ErrorReport errorReport) {
                            ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content);
                            Snackbar.make(rootView, R.string.report_text, Snackbar.LENGTH_LONG).setAction(R.string.report_button, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent().setClass(MainActivity.this, CrashReportActivity.class));
                                }
                            }).show();
                        }
                    });
                }
            }
        });
        //Analytics.trackEvent("My custom event");
        //Crashes.generateTestCrash();
        //装载feed
        coreUtil.initFeedFile();
        //welcome
        if (sharedPreferences.getBoolean("first_run", true)) {
            startActivity(new Intent().setClass(MainActivity.this, WelcomeActivity.class));
        }
        switch (sharedPreferences.getInt("interface_daynight", 0)) {
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
        onetext_quote1_textview.setTextSize(sharedPreferences.getInt("onetext_text_size", px2sp(this, getResources().getDimensionPixelSize(R.dimen.onetext_size))));
        onetext_text_textview.setTextSize(sharedPreferences.getInt("onetext_text_size", px2sp(this, getResources().getDimensionPixelSize(R.dimen.onetext_size))));
        onetext_quote2_textview.setTextSize(sharedPreferences.getInt("onetext_text_size", px2sp(this, getResources().getDimensionPixelSize(R.dimen.onetext_size))));
        onetext_by_textview.setTextSize(sharedPreferences.getInt("onetext_by_size", px2sp(this, getResources().getDimensionPixelSize(R.dimen.text_size))));
        onetext_time_textview.setTextSize(sharedPreferences.getInt("onetext_time_size", px2sp(this, getResources().getDimensionPixelSize(R.dimen.small_text_size))));
        onetext_from_textview.setTextSize(sharedPreferences.getInt("onetext_from_size", px2sp(this, getResources().getDimensionPixelSize(R.dimen.small_text_size))));
        if (sharedPreferences.getBoolean("seal_enabled", false)) {
            seal_button.setText(R.string.seal_button_ison);
            seal_imageview.setVisibility(View.VISIBLE);
        } else {
            seal_button.setText(R.string.seal_button_isoff);
            seal_imageview.setVisibility(View.GONE);
        }
        onetext_text_size_seekbar.setIndicatorTextFormat(getString(R.string.onetext_text_size_text) + " ${PROGRESS} SP");
        onetext_by_size_seekbar.setIndicatorTextFormat(getString(R.string.onetext_by_size_text) + " ${PROGRESS} SP");
        onetext_time_size_seekbar.setIndicatorTextFormat(getString(R.string.onetext_time_size_text) + " ${PROGRESS} SP");
        onetext_from_size_seekbar.setIndicatorTextFormat(getString(R.string.onetext_from_size_text) + " ${PROGRESS} SP");
        onetext_text_size_seekbar.setProgress(sharedPreferences.getInt("onetext_text_size", px2sp(this, getResources().getDimensionPixelSize(R.dimen.onetext_size))));
        onetext_by_size_seekbar.setProgress(sharedPreferences.getInt("onetext_by_size", px2sp(this, getResources().getDimensionPixelSize(R.dimen.text_size))));
        onetext_time_size_seekbar.setProgress(sharedPreferences.getInt("onetext_time_size", px2sp(this, getResources().getDimensionPixelSize(R.dimen.small_text_size))));
        onetext_from_size_seekbar.setProgress(sharedPreferences.getInt("onetext_from_size", px2sp(this, getResources().getDimensionPixelSize(R.dimen.small_text_size))));
        //监听器
        avatar_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(MainActivity.this, SettingActivity.class));
            }
        });
        onetext_swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initRun(true);
                onetext_swiperefreshlayout.setRefreshing(false);
            }
        });
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EasyPermissions.hasPermissions(MainActivity.this, permissions)) {
                    try {
                        final String pic_file_path;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            pic_file_path = Environment.DIRECTORY_PICTURES + File.separator;
                        } else {
                            pic_file_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator;
                        }
                        final String pic_file_name = "OneText " + System.currentTimeMillis() + ".jpg";
                        Bitmap bitmap = SaveBitmapUtil.getCacheBitmapFromView(pic_layout);
                        ContentResolver resolver = getContentResolver();
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.MediaColumns.DISPLAY_NAME, pic_file_name);
                        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
                        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            values.put(MediaStore.MediaColumns.RELATIVE_PATH, pic_file_path + "OneText");
                        } else {
                            File path = new File(pic_file_path + "OneText");
                            //noinspection ResultOfMethodCallIgnored
                            path.mkdirs();
                            values.put(MediaStore.MediaColumns.DATA, path + File.separator + pic_file_name);
                        }
                        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        if (imageUri != null) {
                            OutputStream stream = resolver.openOutputStream(imageUri);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            if (stream != null) {
                                stream.close();
                            }
                        }
                        Snackbar.make(view, getString(R.string.save_succeed) + " " + pic_file_name, Snackbar.LENGTH_SHORT).setAction(R.string.share_text, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_SEND);
                                intent.putExtra(Intent.EXTRA_STREAM, FileUtil.getUriFromFile(new File(pic_file_path + pic_file_name), MainActivity.this));
                                intent.setType("image/*");
                                startActivity(Intent.createChooser(intent, getString(R.string.share_text)));
                            }
                        }).show();
                    } catch (Exception e) {
                        Snackbar.make(view, getString(R.string.save_fail), Snackbar.LENGTH_SHORT).show();
                    }

                    /*
                    if (!FileUtil.isDirectory(pic_file_path)) {
                        File file = new File(pic_file_path);
                        file.mkdirs();
                    }
                    Boolean isSuccess = SaveBitmapUtil.saveBitmapToSdCard(MainActivity.this, SaveBitmapUtil.getCacheBitmapFromView(pic_layout), pic_file_path + pic_file_name);
                    if (isSuccess) {
                        Snackbar.make(view, getString(R.string.save_succeed) + " " + pic_file_name, Snackbar.LENGTH_SHORT).setAction(R.string.share_text, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_SEND);
                                intent.putExtra(Intent.EXTRA_STREAM, FileUtil.getUriFromFile(new File(pic_file_path + pic_file_name), MainActivity.this));
                                intent.setType("image/*");
                                startActivity(Intent.createChooser(intent, getString(R.string.share_text)));
                            }
                        }).show();
                    } else {
                        Snackbar.make(view, getString(R.string.save_fail), Snackbar.LENGTH_SHORT).show();
                    }*/

                } else {
                    Snackbar.make(view, getString(R.string.request_permissions_text), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        save_button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (EasyPermissions.hasPermissions(MainActivity.this, permissions)) {
                    if (FileUtil.deleteDir(new File(Environment.getExternalStorageDirectory() + "/Pictures/OneText/"))) {
                        Snackbar.make(v, getString(R.string.succeed), Snackbar.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initRun(true);
                Analytics.trackEvent("Refreshed");
            }
        });
        seal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.getBoolean("seal_enabled", false)) {
                    seal_imageview.setVisibility(View.GONE);
                    editor.putBoolean("seal_enabled", false);
                    editor.apply();
                    seal_button.setText(R.string.seal_button_isoff);
                } else {
                    seal_imageview.setVisibility(View.VISIBLE);
                    editor.putBoolean("seal_enabled", true);
                    editor.apply();
                    seal_button.setText(R.string.seal_button_ison);
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
                editor.putInt("onetext_text_size", seekBar.getProgress());
                editor.apply();
            }
        });
        onetext_text_size_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int text_size = px2sp(MainActivity.this, getResources().getDimensionPixelSize(R.dimen.onetext_size));
                onetext_quote1_textview.setTextSize(text_size);
                onetext_text_textview.setTextSize(text_size);
                onetext_quote2_textview.setTextSize(text_size);
                onetext_text_size_seekbar.setProgress(text_size);
                editor.putInt("onetext_text_size", text_size);
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
                editor.putInt("onetext_by_size", seekBar.getProgress());
                editor.apply();
            }
        });
        onetext_by_size_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int text_size = px2sp(MainActivity.this, getResources().getDimensionPixelSize(R.dimen.text_size));
                onetext_by_textview.setTextSize(text_size);
                onetext_by_size_seekbar.setProgress(text_size);
                editor.putInt("onetext_by_size", text_size);
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
                editor.putInt("onetext_time_size", seekBar.getProgress());
                editor.apply();
            }
        });
        onetext_time_size_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int text_size = px2sp(MainActivity.this, getResources().getDimensionPixelSize(R.dimen.small_text_size));
                onetext_time_textview.setTextSize(text_size);
                onetext_time_size_seekbar.setProgress(text_size);
                editor.putInt("onetext_time_size", text_size);
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
                editor.putInt("onetext_from_size", seekBar.getProgress());
                editor.apply();
            }
        });
        onetext_from_size_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int text_size = px2sp(MainActivity.this, getResources().getDimensionPixelSize(R.dimen.small_text_size));
                onetext_from_textview.setTextSize(text_size);
                onetext_from_size_seekbar.setProgress(text_size);
                editor.putInt("onetext_from_size", text_size);
                editor.apply();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
        if (sharedPreferences.getBoolean("oauth_hide", false)) {
            Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) avatar_imageview.getLayoutParams();
            layoutParams.width = AppUtil.dp2px(this, 24);
            layoutParams.height = AppUtil.dp2px(this, 24);
            avatar_imageview.setLayoutParams(layoutParams);
            avatar_imageview.setImageDrawable(getDrawable(R.drawable.ic_settings));
        } else {
            if (sharedPreferences.getBoolean("oauth_logined", false)) {
                if (FileUtil.isFile(getFilesDir().getPath() + "/Oauth/Avatar.png")) {
                    Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) avatar_imageview.getLayoutParams();
                    layoutParams.width = AppUtil.dp2px(this, 30);
                    layoutParams.height = AppUtil.dp2px(this, 30);
                    avatar_imageview.setLayoutParams(layoutParams);
                    avatar_imageview.setImageURI(Uri.fromFile(new File(getFilesDir().getPath() + "/Oauth/Avatar.png")));
                }
            }
        }
        initRun(false);
        final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, permissions)) {
            request_permissions_layout.setVisibility(View.GONE);
        }
        //Toast.makeText(MainActivity.this,"test",Toast.LENGTH_SHORT).show();
    }

    private void initRun(final Boolean forcedRefresh) {
        progressBar.setVisibility(View.VISIBLE);
        coreUtil.initOneText(new CoreUtil.OnOneTextInitListener() {
            @Override
            public void onSuccess(File file) {
                try {
                    final HashMap hashMap = coreUtil.getOneText(forcedRefresh, false);
                    final String text = (String) hashMap.get("text");
                    final String by = (String) hashMap.get("by");
                    final String time = (String) hashMap.get("time");
                    final String from = (String) hashMap.get("from");
                    onetext_text_textview.post(new Runnable() {
                        @Override
                        public void run() {
                            if (text == null) {
                                onetext_quote1_textview.setVisibility(View.GONE);
                                onetext_text_textview.setVisibility(View.GONE);
                                onetext_quote2_textview.setVisibility(View.GONE);
                            } else {
                                onetext_quote1_textview.setVisibility(View.VISIBLE);
                                onetext_text_textview.setVisibility(View.VISIBLE);
                                onetext_quote2_textview.setVisibility(View.VISIBLE);
                                onetext_text_textview.setText((String) hashMap.get("text"));
                            }
                            if (by == null) {
                                onetext_by_textview.setVisibility(View.GONE);
                            } else {
                                onetext_by_textview.setVisibility(View.VISIBLE);
                                onetext_by_textview.setText((String) hashMap.get("by"));
                            }
                            if (time == null) {
                                onetext_time_textview.setVisibility(View.GONE);
                            } else {
                                onetext_time_textview.setVisibility(View.VISIBLE);
                                onetext_time_textview.setText((String) hashMap.get("time"));
                            }
                            if (from == null) {
                                onetext_from_textview.setVisibility(View.GONE);
                            } else {
                                onetext_from_textview.setVisibility(View.VISIBLE);
                                onetext_from_textview.setText((String) hashMap.get("from"));
                            }
                        }
                    });
                    //更新小部件
                    Intent intent = new Intent("com.lz233.onetext.widget");
                    intent.setPackage(getPackageName());
                    MainActivity.this.sendBroadcast(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                coreUtil.runOneTextUpdate(new CoreUtil.OnOneTextUpdateListener() {
                    @Override
                    public void noUpdateRequired() {
                        progressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(File file) {
                        progressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onDownloading(int progress) {

                    }

                    @Override
                    public void onFailed(Exception e) {
                        progressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                        Snackbar.make(rootview, getString(R.string.onetext_refresh_faild_text), Snackbar.LENGTH_LONG).setAction(R.string.onetext_refresh_faild_button, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initRun(false);
                            }
                        }).show();
                    }
                });
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onFailed(@Nullable Exception e) {
                Snackbar.make(rootview, getString(R.string.onetext_init_faild_text), Snackbar.LENGTH_LONG).setAction(R.string.onetext_init_faild_button, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initRun(false);
                    }
                }).show();
            }
        });
    }
    /*
    public void initRun(final Boolean forcedRefresh) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final OneTextUtil oneTextUtil = new OneTextUtil(MainActivity.this);
                    if (!FileUtil.isFile(getFilesDir().getPath() + "/OneText/OneText-Library.json")) {
                        progressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        });
                        oneTextUtil.updateRemoteFeed(true, new OneTextUtil.OnUpdateFeedListener() {
                            @Override
                            public void noNeedRequired(boolean noNeed) {
                                try {
                                    showOneText(oneTextUtil, forcedRefresh);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void updateCompleted(boolean complete) {
                                try {
                                    showOneText(oneTextUtil, forcedRefresh);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void updateFailed(Exception e) {
                                FileUtil.deleteFile(getFilesDir().getPath() + "/OneText/OneText-Library.json");
                                Snackbar.make(rootview, getString(R.string.onetext_refresh_faild_text), Snackbar.LENGTH_LONG).setAction(R.string.refresh_onetext_button, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        initRun(forcedRefresh);
                                    }
                                }).show();
                                //System.exit(0);
                            }
                        });
                    } else {
                        showOneText(oneTextUtil, forcedRefresh);
                    }
                    runOneTextUpdate(oneTextUtil);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showOneText(OneTextUtil oneTextUtil, Boolean forcedRefresh) throws JSONException {
        onetext_code = oneTextUtil.getOneTextCode(forcedRefresh, false);
        String[] oneText = oneTextUtil.readOneText(onetext_code);
        final String final_text = oneText[0];
        final String final_by = oneText[1];
        final String final_from = oneText[2];
        final String time = oneText[3];
        onetext_text_textview.post(new Runnable() {
            @Override
            public void run() {
                //progressBar.setVisibility(View.GONE);
                onetext_text_textview.setText(final_text);
                if (!final_by.equals("")) {
                    onetext_by_textview.setText("—— " + final_by);
                    onetext_by_textview.setVisibility(View.VISIBLE);
                } else {
                    onetext_by_textview.setVisibility(View.GONE);
                }
                if (!final_from.equals("")) {
                    onetext_from_textview.setText(final_from);
                    onetext_from_textview.setVisibility(View.VISIBLE);
                } else {
                    onetext_from_textview.setVisibility(View.GONE);
                }
                onetext_time_textview.setText(time);
            }
        });
        //更新小部件
        Intent intent = new Intent("com.lz233.onetext.widget");
        intent.setPackage(getPackageName());
        MainActivity.this.sendBroadcast(intent);
    }

    private void runOneTextUpdate(final OneTextUtil oneTextUtil) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                progressBar.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
                oneTextUtil.updateRemoteFeed(false, new OneTextUtil.OnUpdateFeedListener() {
                    @Override
                    public void noNeedRequired(boolean noNeed) {
                        progressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void updateCompleted(boolean complete) {
                        progressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void updateFailed(Exception e) {
                        FileUtil.deleteFile(getFilesDir().getPath() + "/OneText/OneText-Library.json");
                        progressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                        Snackbar.make(rootview, getString(R.string.onetext_refresh_faild_text), Snackbar.LENGTH_LONG).setAction(R.string.refresh_onetext_button, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initRun(false);
                            }
                        }).show();
                    }
                });
            }
        }).start();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void requestPermissions(final String[] permissions) {
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
                    EasyPermissions.requestPermissions(MainActivity.this, getString(R.string.request_permissions_storage_detail_text), 1, permissions);
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
