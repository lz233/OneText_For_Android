package com.lz233.onetext.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.lz233.onetext.R;
import com.lz233.onetext.tools.utils.FileUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EditFeedActivity extends BaseActivity {
    private EditText edit_feed_edittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_feed);
        //
        Toolbar toolbar = findViewById(R.id.toolbar);
        edit_feed_edittext = findViewById(R.id.edit_feed_edittext);
        //
        setSupportActionBar(toolbar);
        String feedString = FileUtil.readTextFromFile(getFilesDir().getPath() + "/Feed/Feed.json");
        JSONArray originalJsonArray = null;
        try {
            originalJsonArray = new JSONArray(feedString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fuckNav(edit_feed_edittext);
        edit_feed_edittext.setText(stringToJSON(originalJsonArray.toString().replace("\\/","/"))+"\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        //
        toolbar.setNavigationOnClickListener(view -> finish());
        JSONArray finalOriginalJsonArray = originalJsonArray;
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_save:
                    try {
                        String jsonString = edit_feed_edittext.getText().toString();
                        JSONArray jsonArray = new JSONArray(jsonString);
                        if (jsonArray.length()==0){
                            Snackbar.make(rootview, getString(R.string.edit_feed_empty), Snackbar.LENGTH_SHORT).show();
                        }else {
                            String currentFeedName = finalOriginalJsonArray.getJSONObject(sharedPreferences.getInt("feed_code",0)).getString("feed_name");
                            boolean nameChanged = false;
                            for (int i =0;i<jsonArray.length();i++){
                                if (jsonArray.getJSONObject(i).getString("feed_name").equals(currentFeedName)){
                                    nameChanged=true;
                                    editor.putInt("feed_code", i);
                                    break;
                                }
                            }
                            if (!nameChanged){
                                editor.putInt("feed_code", 0);
                                editor.remove("onetext_code");
                                FileUtil.deleteFile(getFilesDir().getPath() + "/OneText/OneText-Library.json");
                            }
                            editor.remove("feed_latest_refresh_time");
                            editor.remove("widget_latest_refresh_time");
                            editor.apply();
                            FileUtil.deleteFile(getFilesDir().getPath() + "/Feed/Feed.json");
                            FileUtil.writeTextToFile(jsonArray.toString(),getFilesDir().getPath() + "/Feed/","Feed.json");
                            Intent intent = new Intent("com.lz233.onetext.updatefeedlist");
                            intent.setPackage(getPackageName());
                            sendBroadcast(intent);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Snackbar.make(rootview, getString(R.string.edit_feed_illegal), Snackbar.LENGTH_SHORT).show();
                    }
                    break;
            }
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_feed, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public static String stringToJSON(String strJson) {
        // 计数tab的个数
        int tabNum = 0;
        StringBuffer jsonFormat = new StringBuffer();
        int length = strJson.length();

        char last = 0;
        boolean isInString = false;
        for (int i = 0; i < length; i++) {
            char c = strJson.charAt(i);
            if (c == '{') {
                tabNum++;
                jsonFormat.append(c + "\n");
                jsonFormat.append(getSpaceOrTab(tabNum));
            } else if (c == '}') {
                tabNum--;
                jsonFormat.append("\n");
                jsonFormat.append(getSpaceOrTab(tabNum));
                jsonFormat.append(c);
            } else if (c == ',') {
                jsonFormat.append(c + "\n");
                jsonFormat.append(getSpaceOrTab(tabNum));
            } else if (c == ':') {
                if (isInString){
                    jsonFormat.append(c);
                }else {
                    jsonFormat.append(c + " ");
                }
            } else if (c == '[') {
                tabNum++;
                char next = strJson.charAt(i + 1);
                if ((next == ']')|isInString) {
                    jsonFormat.append(c);
                } else {
                    jsonFormat.append(c + "\n");
                    jsonFormat.append(getSpaceOrTab(tabNum));
                }
            } else if (c == ']') {
                tabNum--;
                if ((last == '[')|isInString) {
                    jsonFormat.append(c);
                } else {
                    jsonFormat.append("\n" + getSpaceOrTab(tabNum) + c);
                }
            }else if (c=='"') {
                isInString= !isInString;
                jsonFormat.append(c);
            } else {
                jsonFormat.append(c);
            }
            last = c;
        }
        return jsonFormat.toString();
    }

    private static String getSpaceOrTab(int tabNum) {
        StringBuffer sbTab = new StringBuffer();
        for (int i = 0; i < tabNum; i++) {
            sbTab.append('\t');
        }
        return sbTab.toString();
    }
}