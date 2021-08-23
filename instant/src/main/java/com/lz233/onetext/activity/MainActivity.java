package com.lz233.onetext.activity;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.documentfile.provider.DocumentFile;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.lz233.onetext.BuildConfig;
import com.lz233.onetext.R;
import com.lz233.onetext.tools.utils.AppUtilKt;
import com.lz233.onetext.tools.utils.CoolapkAuthUtilKt;
import com.lz233.onetext.tools.utils.CoreUtil;
import com.lz233.onetext.tools.utils.DownloadUtil;
import com.lz233.onetext.tools.utils.FileUtil;
import com.lz233.onetext.tools.utils.GetUtil;
import com.lz233.onetext.tools.utils.QRCodeUtil;
import com.lz233.onetext.tools.utils.SaveBitmapUtil;
import com.lz233.onetext.view.AdjustImageView;
import com.lz233.onetext.view.NiceImageView;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.noties.markwon.Markwon;
import io.noties.markwon.SoftBreakAddsNewLinePlugin;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private boolean showLyric = true;
    private CoreUtil coreUtil;

    private ImageView sponsor_imageview;
    private NiceImageView avatar_imageview;
    private SwipeRefreshLayout onetext_swiperefreshlayout;
    private LinearLayout main_linearlayout;
    private ProgressBar progressBar;
    private LinearLayout pic_layout;
    private CardView uri_layout;
    private TextView onetext_quote1_textview;
    private TextView onetext_text_textview;
    private TextView onetext_quote2_textview;
    private TextView onetext_by_textview;
    private TextView onetext_time_textview;
    private TextView onetext_from_textview;
    private ImageView seal_imageview;
    private LinearLayout qr_linearLayout;
    private ImageView qr_imageview;
    private CardView push_layout;
    private AdjustImageView push_imageview;
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //曲 线 救 国
        fuckNav(findViewById(R.id.last_layout));
        //fb
        coreUtil = new CoreUtil(MainActivity.this);
        sponsor_imageview = findViewById(R.id.sponsor_imageview);
        avatar_imageview = findViewById(R.id.avatar_imageview);
        onetext_swiperefreshlayout = findViewById(R.id.onetext_swiperefreshlayout);
        main_linearlayout = findViewById(R.id.main_linearlayout);
        pic_layout = findViewById(R.id.pic_layout);
        uri_layout = findViewById(R.id.uri_layout);
        progressBar = findViewById(R.id.progressBar);
        onetext_quote1_textview = findViewById(R.id.onetext_quote1_textview);
        onetext_text_textview = findViewById(R.id.onetext_text_textview);
        onetext_quote2_textview = findViewById(R.id.onetext_quote2_textview);
        onetext_by_textview = findViewById(R.id.onetext_by_textview);
        onetext_time_textview = findViewById(R.id.onetext_time_textview);
        onetext_from_textview = findViewById(R.id.onetext_from_textview);
        seal_imageview = findViewById(R.id.seal_imageview);
        qr_linearLayout = findViewById(R.id.qr_linearLayout);
        qr_imageview = findViewById(R.id.qr_imageview);
        push_layout = findViewById(R.id.push_layout);
        push_imageview = findViewById(R.id.push_imageview);
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
        //剪贴板
        final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        //装载feed
        coreUtil.initFeedFile();
        //daynight
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
        //检查更新
        //checkUpdate();
        main_linearlayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        onetext_quote1_textview.setTextSize(sharedPreferences.getInt("onetext_text_size", AppUtilKt.px2sp(this, getResources().getDimensionPixelSize(R.dimen.onetext_size))));
        onetext_text_textview.setTextSize(sharedPreferences.getInt("onetext_text_size", AppUtilKt.px2sp(this, getResources().getDimensionPixelSize(R.dimen.onetext_size))));
        onetext_quote2_textview.setTextSize(sharedPreferences.getInt("onetext_text_size", AppUtilKt.px2sp(this, getResources().getDimensionPixelSize(R.dimen.onetext_size))));
        onetext_by_textview.setTextSize(sharedPreferences.getInt("onetext_by_size", AppUtilKt.px2sp(this, getResources().getDimensionPixelSize(R.dimen.text_size))));
        onetext_time_textview.setTextSize(sharedPreferences.getInt("onetext_time_size", AppUtilKt.px2sp(this, getResources().getDimensionPixelSize(R.dimen.small_text_size))));
        onetext_from_textview.setTextSize(sharedPreferences.getInt("onetext_from_size", AppUtilKt.px2sp(this, getResources().getDimensionPixelSize(R.dimen.small_text_size))));
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
        onetext_text_size_seekbar.setProgress(sharedPreferences.getInt("onetext_text_size", AppUtilKt.px2sp(this, getResources().getDimensionPixelSize(R.dimen.onetext_size))));
        onetext_by_size_seekbar.setProgress(sharedPreferences.getInt("onetext_by_size", AppUtilKt.px2sp(this, getResources().getDimensionPixelSize(R.dimen.text_size))));
        onetext_time_size_seekbar.setProgress(sharedPreferences.getInt("onetext_time_size", AppUtilKt.px2sp(this, getResources().getDimensionPixelSize(R.dimen.small_text_size))));
        onetext_from_size_seekbar.setProgress(sharedPreferences.getInt("onetext_from_size", AppUtilKt.px2sp(this, getResources().getDimensionPixelSize(R.dimen.small_text_size))));
        //监听器
        avatar_imageview.setOnClickListener(v -> {
            startActivity(new Intent().setClass(MainActivity.this, AboutActivity.class));
        });
        onetext_swiperefreshlayout.setOnRefreshListener(() -> {
            initRun(true);
            onetext_swiperefreshlayout.setRefreshing(false);
        });
        onetext_text_textview.setOnClickListener(v -> {
            clipboardManager.setPrimaryClip(ClipData.newPlainText("onetext", onetext_text_textview.getText()));
            Snackbar.make(v, getString(R.string.copied), Snackbar.LENGTH_SHORT).show();
        });
        onetext_by_textview.setOnClickListener(v -> {
            clipboardManager.setPrimaryClip(ClipData.newPlainText("by", onetext_by_textview.getText()));
            Snackbar.make(v, getString(R.string.copied), Snackbar.LENGTH_SHORT).show();
        });
        onetext_time_textview.setOnClickListener(v -> {
            clipboardManager.setPrimaryClip(ClipData.newPlainText("time", onetext_time_textview.getText()));
            Snackbar.make(v, getString(R.string.copied), Snackbar.LENGTH_SHORT).show();
        });
        onetext_from_textview.setOnClickListener(v -> {
            clipboardManager.setPrimaryClip(ClipData.newPlainText("from", onetext_from_textview.getText()));
            Snackbar.make(v, getString(R.string.copied), Snackbar.LENGTH_SHORT).show();
        });
        push_imageview.setOnLongClickListener(view -> {
            push_layout.setVisibility(View.GONE);
            editor.putBoolean("hide_push_once_time", true);
            editor.apply();
            Snackbar.make(view, R.string.push_hided_once, Snackbar.LENGTH_LONG).setAction(R.string.undo, view1 -> {
                push_layout.setVisibility(View.VISIBLE);
                editor.putBoolean("hide_push_once_time", false);
                editor.apply();
            }).show();
            return true;
        });
        save_button.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                shotOneTextViaMediaStore();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    Uri uri = Uri.parse(sharedPreferences.getString("pic_uri_tree", "content://com.android.externalstorage.documents/tree/primary%3APictures"));
                    int takeFlags = getIntent().getFlags();
                    takeFlags &= (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    getContentResolver().takePersistableUriPermission(uri, takeFlags);
                    DocumentFile root = DocumentFile.fromTreeUri(MainActivity.this, uri);
                    shotOneTextViaSAF(root);
                } catch (Exception e) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse("content://com.android.externalstorage.documents/document/primary:Pictures"));
                    startActivityForResult(intent, 233);
                }
            } else if (EasyPermissions.hasPermissions(MainActivity.this, permissions)) {
                shotOneTextViaMediaStore();
            } else {
                Snackbar.make(view, getString(R.string.request_permissions_text), Snackbar.LENGTH_SHORT).show();
            }
        });
        refresh_button.setOnClickListener(view -> {
            initRun(true);
        });
        seal_button.setOnClickListener(view -> {
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
        onetext_text_size_button.setOnClickListener(view -> {
            int text_size = AppUtilKt.px2sp(MainActivity.this, getResources().getDimensionPixelSize(R.dimen.onetext_size));
            onetext_quote1_textview.setTextSize(text_size);
            onetext_text_textview.setTextSize(text_size);
            onetext_quote2_textview.setTextSize(text_size);
            onetext_text_size_seekbar.setProgress(text_size);
            editor.putInt("onetext_text_size", text_size);
            editor.apply();
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
        onetext_by_size_button.setOnClickListener(view -> {
            int text_size = AppUtilKt.px2sp(MainActivity.this, getResources().getDimensionPixelSize(R.dimen.text_size));
            onetext_by_textview.setTextSize(text_size);
            onetext_by_size_seekbar.setProgress(text_size);
            editor.putInt("onetext_by_size", text_size);
            editor.apply();
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
        onetext_time_size_button.setOnClickListener(view -> {
            int text_size = AppUtilKt.px2sp(MainActivity.this, getResources().getDimensionPixelSize(R.dimen.small_text_size));
            onetext_time_textview.setTextSize(text_size);
            onetext_time_size_seekbar.setProgress(text_size);
            editor.putInt("onetext_time_size", text_size);
            editor.apply();
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
        onetext_from_size_button.setOnClickListener(view -> {
            int text_size = AppUtilKt.px2sp(MainActivity.this, getResources().getDimensionPixelSize(R.dimen.small_text_size));
            onetext_from_textview.setTextSize(text_size);
            onetext_from_size_seekbar.setProgress(text_size);
            editor.putInt("onetext_from_size", text_size);
            editor.apply();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sharedPreferences.getBoolean("oauth_show", true)) {
            if (sharedPreferences.getBoolean("oauth_logined", false)) {
                if (FileUtil.isFile(getFilesDir().getPath() + "/Oauth/Avatar.png")) {
                    Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) avatar_imageview.getLayoutParams();
                    layoutParams.width = AppUtilKt.dp2px(this, 30);
                    layoutParams.height = AppUtilKt.dp2px(this, 30);
                    avatar_imageview.setLayoutParams(layoutParams);
                    avatar_imageview.setImageURI(Uri.fromFile(new File(getFilesDir().getPath() + "/Oauth/Avatar.png")));
                }
            }
        } else {
            Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) avatar_imageview.getLayoutParams();
            layoutParams.width = AppUtilKt.dp2px(this, 24);
            layoutParams.height = AppUtilKt.dp2px(this, 24);
            avatar_imageview.setLayoutParams(layoutParams);
            avatar_imageview.setImageDrawable(getDrawable(R.drawable.ic_settings));
        }
        initRun(false);
        getPush();
        final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, permissions)) {
            request_permissions_layout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 233) {
                Uri uriTree = resultData.getData();
                if (uriTree != null) {
                    int takeFlags = getIntent().getFlags();
                    takeFlags &= (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    getContentResolver().takePersistableUriPermission(uriTree, takeFlags);
                    editor.putString("pic_uri_tree", uriTree.toString());
                    editor.apply();
                    // 创建所选目录的DocumentFile，可以使用它进行文件操作
                    DocumentFile root = DocumentFile.fromTreeUri(this, uriTree);
                    // 比如使用它创建文件夹
                    //DocumentFile[] rootList = root.listFiles();
                    shotOneTextViaSAF(root);
                }
            }
        }

    }

    private void initRun(final Boolean forcedRefresh) {
        progressBar.setVisibility(View.VISIBLE);
        final HashMap feedMap = coreUtil.getFeedInformation(sharedPreferences.getInt("feed_code", 0));
        //载入赞助信息
        String sponsorUrl = (String) feedMap.get("sponsor_url");
        if (sponsorUrl.equals("")) {
            sponsor_imageview.setOnClickListener(null);
            sponsor_imageview.setVisibility(View.GONE);
        } else {
            sponsor_imageview.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sponsorUrl))));
            sponsor_imageview.setVisibility(View.VISIBLE);
        }
        if ((getIntent().getStringExtra("text") != null) & showLyric) {
            HashMap hashMap = new HashMap();
            hashMap.put("text", getIntent().getStringExtra("text"));
            hashMap.put("by", getIntent().getStringExtra("by"));
            hashMap.put("from", getIntent().getStringExtra("from"));
            hashMap.put("time", "");
            hashMap.put("uri", getIntent().getStringExtra("uri"));
            showOneText(hashMap);
            showLyric = false;
            progressBar.post(() -> progressBar.setVisibility(View.GONE));
        } else if (feedMap.get("feed_type").equals("internet")) {
            if (sharedPreferences.getString("api_string", "").equals("") | coreUtil.ifOneTextShouldUpdate(false) | forcedRefresh) {
                new GetUtil().sendGet((String) feedMap.get("api_url"), result -> {
                    onetext_text_textview.post(() -> {
                        try {
                            showOneText(coreUtil.convertOneText(new JSONObject(result), feedMap));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                    coreUtil.refreshLatestRefreshTime();
                    progressBar.setVisibility(View.GONE);
                    editor.putString("api_string", result);
                    editor.apply();
                });
                coreUtil.refreshLatestRefreshTime();
            } else {
                onetext_text_textview.post(() -> {
                    try {
                        showOneText(coreUtil.convertOneText(new JSONObject(sharedPreferences.getString("api_string", "")), feedMap));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
                progressBar.setVisibility(View.GONE);
            }
        } else {
            coreUtil.initOneText(new CoreUtil.OnOneTextInitListener() {
                @Override
                public void onSuccess(File file) {
                    onetext_text_textview.post(() -> {
                        try {
                            showOneText(coreUtil.getOneText(forcedRefresh, false));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                    coreUtil.runOneTextUpdate(new CoreUtil.OnOneTextUpdateListener() {
                        @Override
                        public void noUpdateRequired() {
                            progressBar.post(() -> progressBar.setVisibility(View.GONE));
                        }

                        @Override
                        public void onSuccess(File file) {
                            progressBar.post(() -> progressBar.setVisibility(View.GONE));
                        }

                        @Override
                        public void onDownloading(int progress) {

                        }

                        @Override
                        public void onFailed(Exception e) {
                            progressBar.post(() -> progressBar.setVisibility(View.GONE));
                            Snackbar.make(rootView, getString(R.string.onetext_refresh_faild_text), Snackbar.LENGTH_LONG).setAction(R.string.onetext_refresh_faild_button, v -> initRun(false)).show();
                        }
                    });
                }

                @Override
                public void onDownloading(int progress) {

                }

                @Override
                public void onFailed(@Nullable Exception e) {
                    Snackbar.make(rootView, getString(R.string.onetext_init_faild_text), Snackbar.LENGTH_LONG).setAction(R.string.onetext_init_faild_button, v -> initRun(false)).show();
                }
            });
        }
    }

    private void showOneText(HashMap hashMap) {
        //final Markwon markwon = Markwon.builder(this).usePlugin(SoftBreakAddsNewLinePlugin.create()).usePlugin(StrikethroughPlugin.create()).usePlugin(TaskListPlugin.create(this)).build();
        final Markwon markwon = Markwon.builder(this).usePlugin(SoftBreakAddsNewLinePlugin.create()).usePlugin(TaskListPlugin.create(this)).usePlugin(StrikethroughPlugin.create()).build();
        //Spanned markdown = markwon.toMarkdown("**Hello there!**");
        //final Markwon markwon = Markwon.create(getApplicationContext());
        String text = ((String) hashMap.get("text"));
        final String by = (String) hashMap.get("by");
        final String time = (String) hashMap.get("time");
        final String from = (String) hashMap.get("from");
        final String uri = (String) hashMap.get("uri");
        if (text.equals("")) {
            onetext_quote1_textview.setVisibility(View.GONE);
            onetext_text_textview.setVisibility(View.GONE);
            onetext_quote2_textview.setVisibility(View.GONE);
        } else {
            onetext_quote1_textview.setVisibility(View.VISIBLE);
            onetext_text_textview.setVisibility(View.VISIBLE);
            onetext_quote2_textview.setVisibility(View.VISIBLE);
            //onetext_text_textview.setText((String) hashMap.get("text"));
            onetext_quote1_textview.setText((String) hashMap.get("quote1"));
            onetext_quote2_textview.setText((String) hashMap.get("quote2"));
            text = text.replace(CoreUtil.textReplaceHolderString, CoreUtil.textReplaceString);
            markwon.setMarkdown(onetext_text_textview, text);
        }
        if (by.equals("")) {
            onetext_by_textview.setVisibility(View.GONE);
        } else {
            onetext_by_textview.setVisibility(View.VISIBLE);
            //onetext_by_textview.setText((String) hashMap.get("by"));
            markwon.setMarkdown(onetext_by_textview, "—— " + hashMap.get("by"));
        }
        if (time.equals("")) {
            onetext_time_textview.setVisibility(View.GONE);
        } else {
            onetext_time_textview.setVisibility(View.VISIBLE);
            //onetext_time_textview.setText((String) hashMap.get("time"));
            markwon.setMarkdown(onetext_time_textview, (String) hashMap.get("time"));
        }
        if (from.equals("")) {
            onetext_from_textview.setVisibility(View.GONE);
        } else {
            onetext_from_textview.setVisibility(View.VISIBLE);
            //onetext_from_textview.setText((String) hashMap.get("from"));
            markwon.setMarkdown(onetext_from_textview, (String) hashMap.get("from"));
        }
        if (uri.equals("")) {
            uri_layout.setOnClickListener(null);
            qr_linearLayout.setVisibility(View.GONE);
        } else {
            uri_layout.setOnClickListener(view -> Snackbar.make(view, R.string.onetext_uri_open_text, Snackbar.LENGTH_SHORT).setAction(R.string.onetext_uri_open_button, view1 -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)))).show());
            if (getString(R.string.interface_mode).equals("day")) {
                qr_linearLayout.setVisibility(View.VISIBLE);
                qr_imageview.setImageBitmap(QRCodeUtil.createQRCodeBitmap(uri, 85, 85));
            } else {
                qr_linearLayout.setVisibility(View.GONE);
            }
        }
        //更新小部件
        Intent intent = new Intent("com.lz233.onetext.widget");
        intent.setPackage(getPackageName());
        MainActivity.this.sendBroadcast(intent);
    }

    private void shotOneTextViaSAF(DocumentFile root) {
        //HashMap hashMap = new HashMap();
        DocumentFile oneTextDir;
        try {
            if (root.findFile("OneText").isDirectory()) {
                oneTextDir = root.findFile("OneText");
            } else {
                oneTextDir = root.createDirectory("OneText");
            }
        } catch (Exception e) {
            oneTextDir = root.createDirectory("OneText");
        }
        String pic_name = "OneText " + System.currentTimeMillis() + ".jpg";
        final DocumentFile pic = oneTextDir.createFile("image/jpeg", pic_name);
        Bitmap bitmap = SaveBitmapUtil.getCacheBitmapFromView(pic_layout);
        OutputStream stream = null;
        try {
            stream = getContentResolver().openOutputStream(pic.getUri());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            if (stream != null) {
                stream.close();
            }
            Snackbar.make(rootView, getString(R.string.save_succeed) + " " + pic_name, Snackbar.LENGTH_SHORT).setAction(R.string.share_text, view -> {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, pic.getUri());
                intent.setType("image/*");
                startActivity(Intent.createChooser(intent, getString(R.string.share_text)));
            }).show();
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(rootView, getString(R.string.save_fail), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void shotOneTextViaMediaStore() {
        try {
            final String pic_file_path;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                pic_file_path = Environment.DIRECTORY_PICTURES + File.separator + "OneText" + File.separator;
            } else {
                pic_file_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "OneText" + File.separator;
            }
            final String pic_file_name = "OneText " + System.currentTimeMillis() + ".jpg";
            Bitmap bitmap = SaveBitmapUtil.getCacheBitmapFromView(pic_layout);
            ContentResolver resolver = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, pic_file_name);
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, pic_file_path);
            } else {
                File path = new File(pic_file_path);
                //noinspection ResultOfMethodCallIgnored
                path.mkdirs();
                values.put(MediaStore.MediaColumns.DATA, path + File.separator + pic_file_name);
            }
            final Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (imageUri != null) {
                OutputStream stream = resolver.openOutputStream(imageUri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                if (stream != null) {
                    stream.close();
                }
            }
            Snackbar.make(rootView, getString(R.string.save_succeed) + " " + pic_file_name, Snackbar.LENGTH_SHORT).setAction(R.string.share_text, view -> {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                intent.setType("image/*");
                startActivity(Intent.createChooser(intent, getString(R.string.share_text)));
            }).show();
        } catch (Exception e) {
            Snackbar.make(rootView, getString(R.string.save_fail), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void getPush() {
        if (sharedPreferences.getBoolean("disable_push", false)) {
            push_layout.setVisibility(View.GONE);
        } else {
            new Thread(() -> {
                try {
                    final boolean isTest = false;
                    final String deviceLanguage = Locale.getDefault().getLanguage();
                    final String deviceCountry = Locale.getDefault().getCountry();
                    final String deviceCode = deviceLanguage + "_" + deviceCountry;
                    final JSONObject spJsonObject = new JSONObject(sharedPreferences.getString("push_information", "{\"id\":0}"));
                    final String spUri = spJsonObject.optString("uri");
                    if (AppUtilKt.isUseWifi(MainActivity.this) & ((System.currentTimeMillis() - sharedPreferences.getLong("push_latest_refresh_time", 0)) > (isTest ? 0 : 86400000))) {
                        new GetUtil().sendGet(isTest ? "https://gitee.com/lz233-sakura/OneTextRes/raw/master/push-test.json" : "https://gitee.com/lz233-sakura/OneTextRes/raw/master/push.json", result -> {
                            try {
                                final JSONObject resultJsonObject = new JSONObject(result);
                                final String uri = resultJsonObject.getString("uri");
                                if (isTest | resultJsonObject.getInt("id") > spJsonObject.getInt("id") | (!FileUtil.isFile(getCacheDir().getPath() + "/Push/" + deviceCode))) {
                                    editor.putBoolean("hide_push_once_time", false);
                                    if (!isTest) editor.putString("push_information", result);
                                    editor.apply();
                                    String bannerUri = "";
                                    if (resultJsonObject.optString(deviceCode).equals("")) {
                                        bannerUri = resultJsonObject.optString("00_00", "http://127.0.0.1");
                                    } else {
                                        bannerUri = resultJsonObject.optString(deviceCode);
                                    }
                                    final String finalBannerUri = bannerUri;
                                    new Thread(() -> DownloadUtil.get().download(finalBannerUri, getCacheDir().getPath() + "/Push/", deviceCode, new DownloadUtil.OnDownloadListener() {
                                        @Override
                                        public void onDownloadSuccess(File file) {
                                            editor.putLong("push_latest_refresh_time", System.currentTimeMillis());
                                            push_imageview.post(() -> {
                                                push_imageview.setImageBitmap(BitmapFactory.decodeFile(getCacheDir().getPath() + "/Push/" + deviceCode));
                                                push_imageview.setOnClickListener(view -> {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
                                                    if (resultJsonObject.optBoolean("cancelable")) {
                                                        push_layout.setVisibility(View.GONE);
                                                        editor.putBoolean("hide_push_once_time", true);
                                                        editor.apply();
                                                    }
                                                });
                                                push_layout.setVisibility(View.VISIBLE);
                                            });
                                        }

                                        @Override
                                        public void onDownloading(int progress) {

                                        }

                                        @Override
                                        public void onDownloadFailed(Exception e) {

                                        }
                                    })).start();
                                } else {
                                    editor.putLong("push_latest_refresh_time", System.currentTimeMillis());
                                    if (!sharedPreferences.getBoolean("hide_push_once_time", false)) {
                                        push_imageview.post(() -> {
                                            push_imageview.setImageBitmap(BitmapFactory.decodeFile(getCacheDir().getPath() + "/Push/" + deviceCode));
                                            push_imageview.setOnClickListener(view -> {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(spUri)));
                                                if (spJsonObject.optBoolean("cancelable")) {
                                                    push_layout.setVisibility(View.GONE);
                                                    editor.putBoolean("hide_push_once_time", true);
                                                    editor.apply();
                                                }
                                            });
                                            push_layout.setVisibility(View.VISIBLE);
                                        });
                                    }
                                }
                                editor.apply();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } else {
                        if (FileUtil.isFile(getCacheDir().getPath() + "/Push/" + deviceCode)) {
                            if (!sharedPreferences.getBoolean("hide_push_once_time", false)) {
                                push_imageview.post(() -> {
                                    push_imageview.setImageBitmap(BitmapFactory.decodeFile(getCacheDir().getPath() + "/Push/" + deviceCode));
                                    push_imageview.setOnClickListener(view -> {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(spUri)));
                                        if (spJsonObject.optBoolean("cancelable")) {
                                            push_layout.setVisibility(View.GONE);
                                            editor.putBoolean("hide_push_once_time", true);
                                            editor.apply();
                                        }
                                    });
                                    push_layout.setVisibility(View.VISIBLE);
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void checkUpdate() {
        boolean isTest = false;
        if (AppUtilKt.isUseWifi(MainActivity.this) & ((System.currentTimeMillis() - sharedPreferences.getLong("update_latest_refresh_time", 0)) > (isTest ? 0 : 86400000))) {
            if (isTest ? true : BuildConfig.BUILD_TYPE.equals("coolapk")) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://api.coolapk.com/v6/apk/detail?id=com.lz233.onetext&installed=1")
                        .post(new FormBody.Builder().build())
                        .addHeader("X-App-Id", "com.coolapk.market")
                        .addHeader("X-App-Version", "9.0.2")
                        .addHeader("X-App-Code", "1902151")
                        .addHeader("X-Sdk-Int", "25")
                        .addHeader("X-App-Token", CoolapkAuthUtilKt.getAS())
                        .addHeader("X-Sdk-Locale", "zh-CN")
                        .addHeader("X-Requested-With", "XMLHttpRequest")
                        .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36")
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        try {
                            int appVersion = new JSONObject(response.body().string()).optJSONObject("data").optInt("apkversioncode");
                            if (appVersion > BuildConfig.VERSION_CODE) {
                                Snackbar.make(rootView, R.string.check_new_version_text, Snackbar.LENGTH_SHORT).setAction(R.string.check_new_version_button, view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://coolapk.com/apk/com.lz233.onetext")))).show();
                            }
                            editor.putLong("update_latest_refresh_time", System.currentTimeMillis());
                            editor.apply();
                        } catch (JSONException | NullPointerException e) {
                            // 修复酷安 API 返回数据不符合预期时取不存在的字段值引发的空指针异常
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    public void requestPermissions(final String[] permissions) {
        //申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            request_permissions_layout.setVisibility(View.GONE);
        } else {
            if (EasyPermissions.hasPermissions(this, permissions)) {
                //已经打开权限
                //Toast.makeText(this, "已经申请相关权限", Toast.LENGTH_SHORT).show();
                request_permissions_layout.setVisibility(View.GONE);
            } else {
                //没有打开相关权限、申请权限
                request_permissions_layout.setVisibility(View.VISIBLE);
                request_permissions_button.setOnClickListener(v -> EasyPermissions.requestPermissions(MainActivity.this, getString(R.string.request_permissions_storage_detail_text).replace("%s", Environment.getExternalStorageDirectory() + "/Pictures/OneText/"), 1, permissions));
            }
        }
    }

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
            startActivity(new Intent().setClass(MainActivity.this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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
