package com.lz233.onetext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.lz233.onetext.tools.FeedUtils;

import org.json.JSONException;

import pub.devrel.easypermissions.EasyPermissions;

public class SetFeedActivity extends BaseActivity {
    private int currentFeedCode;
    private boolean isAddFeed;
    private FeedUtils feedUtils;
    private String[] feedInf;
    private EditText setfeed_name_edittext;
    private EditText setfeed_type_edittext;
    private TextInputLayout setfeed_url_textinputlayout;
    private EditText setfeed_url_edittext;
    private TextInputLayout setfeed_path_textinputlayout;
    private EditText setfeed_path_edittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_feed);
        //fb
        setfeed_name_edittext = findViewById(R.id.setfeed_name_edittext);
        setfeed_type_edittext = findViewById(R.id.setfeed_type_edittext);
        setfeed_url_textinputlayout = findViewById(R.id.setfeed_url_textinputlayout);
        setfeed_url_edittext = findViewById(R.id.setfeed_url_edittext);
        setfeed_path_textinputlayout = findViewById(R.id.setfeed_path_textinputlayout);
        setfeed_path_edittext = findViewById(R.id.setfeed_path_edittext);
        //初始化
        Intent intent = getIntent();
        currentFeedCode = intent.getIntExtra("feed_int", -1);
        //Toast.makeText(SetFeedActivity.this,String.valueOf(currentFeedCode), Toast.LENGTH_LONG).show();
        feedUtils = new FeedUtils(this);
        if (currentFeedCode == -1) {
            isAddFeed = true;
            //Toast.makeText(SetFeedActivity.this,String.valueOf(isAddFeed), Toast.LENGTH_LONG).show();
            setfeed_path_textinputlayout.setVisibility(View.GONE);
            setfeed_url_textinputlayout.setVisibility(View.GONE);
        } else {
            feedInf = feedUtils.getFeedInf(currentFeedCode);
            setfeed_name_edittext.setText(feedInf[0]);
            String feedType = feedInf[1];
            setfeed_type_edittext.setText(feedType);
            setfeed_url_edittext.setText(feedInf[2]);
            setfeed_path_edittext.setText(feedInf[3]);
            if (feedType.equals("remote")) {
                setfeed_path_textinputlayout.setVisibility(View.GONE);
            } else if (feedType.equals("local")) {
                setfeed_url_textinputlayout.setVisibility(View.GONE);
            }
        }
        //监听器
        setfeed_type_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String currentString = setfeed_type_edittext.getText().toString();
                if (currentString.equals("remote")) {
                    setfeed_url_textinputlayout.setVisibility(View.VISIBLE);
                    setfeed_path_textinputlayout.setVisibility(View.GONE);
                } else if (currentString.equals("local")) {
                    setfeed_url_textinputlayout.setVisibility(View.GONE);
                    setfeed_path_textinputlayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        setfeed_path_edittext.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (EasyPermissions.hasPermissions(SetFeedActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, 3);
                } else {
                    Snackbar.make(v, getString(R.string.request_permissions_text), Snackbar.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            try {
                if (isAddFeed) {
                    feedUtils.addFeed(setfeed_name_edittext.getText().toString(), setfeed_type_edittext.getText().toString(), setfeed_url_edittext.getText().toString(), setfeed_path_edittext.getText().toString());
                } else {
                    feedUtils.alterFeed(currentFeedCode, setfeed_name_edittext.getText().toString(), setfeed_type_edittext.getText().toString(), setfeed_url_edittext.getText().toString(), setfeed_path_edittext.getText().toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                Uri uri = data.getData();
                String file_path = getPath(this, uri);
                setfeed_path_edittext.setText(file_path);
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
}
