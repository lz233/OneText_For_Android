package com.lz233.onetext.tools;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class FeedUtils {
    private long currentTimeMillis = System.currentTimeMillis();
    private Context context;
    private JSONArray jsonArray;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String[] feedInf = new String[4];

    public FeedUtils(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("setting", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        try {
            jsonArray = new JSONArray(FileUtils.readTextFromFile(context.getFilesDir().getPath() + "/Feed/Feed.json"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String[] getFeedInf(int feedCode) {
        JSONObject jsonObject = jsonArray.optJSONObject(feedCode);
        feedInf[0] = jsonObject.optString("feed_name");
        feedInf[1] = jsonObject.optString("feed_type");
        feedInf[2] = jsonObject.optString("feed_url");
        feedInf[3] = jsonObject.optString("feed_path");
        return feedInf;
    }

    public int getFeedCode() {
        return sharedPreferences.getInt("feed_code", 0);
    }

    public void addFeed(String feedName, String feedType, String feedURL, String feedPath) throws JSONException {
        JSONArray mJsonArray = new JSONArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject mJsonObject = jsonArray.optJSONObject(i);
            mJsonArray.put(mJsonObject);
        }
        if (feedURL == null) feedURL = "";
        if (feedPath == null) feedPath = "";
        JSONObject mJsonObject = new JSONObject().put("feed_name", feedName).put("feed_type", feedType).put("feed_url", feedURL).put("feed_path", feedPath);
        mJsonArray.put(mJsonObject);
        jsonArray = mJsonArray;
        FileUtils.deleteFile(context.getFilesDir().getPath() + "/Feed/Feed.json");
        FileUtils.writeTextToFile(mJsonArray.toString(), context.getFilesDir().getPath() + "/Feed/", "Feed.json");
    }

    public void deleteFeed(int position) {
        int feedInt = sharedPreferences.getInt("feed_code", 0);
        if (feedInt > position) {
            editor.putInt("feed_code", sharedPreferences.getInt("feed_code", 0) - 1);
        } else if (feedInt == position) {
            if (getFeedInf(feedInt)[1].equals("remote")) {
                FileUtils.deleteFile(context.getFilesDir().getPath() + "/OneText/OneText-Library.json");
            }
            editor.remove("feed_code");
            editor.remove("feed_latest_refresh_time");
            editor.remove("widget_latest_refresh_time");
            editor.remove("onetext_code");
        }
        editor.apply();
        JSONArray mJsonArray = new JSONArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            if (!(position == i)) {
                JSONObject mJsonObject = jsonArray.optJSONObject(i);
                mJsonArray.put(mJsonObject);
            }
        }
        jsonArray = mJsonArray;
        FileUtils.deleteFile(context.getFilesDir().getPath() + "/Feed/Feed.json");
        FileUtils.writeTextToFile(mJsonArray.toString(), context.getFilesDir().getPath() + "/Feed/", "Feed.json");
    }

    public void alterFeed(int position, String feedName, String feedType, String feedURL, String feedPath) throws JSONException {
        JSONArray mJsonArray = new JSONArray();
        int feedInt = sharedPreferences.getInt("feed_code", 0);
        if (feedURL == null) feedURL = "";
        if (feedPath == null) feedPath = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject mJsonObject;
            if (position == i) {
                mJsonObject = new JSONObject().put("feed_name", feedName).put("feed_type", feedType).put("feed_url", feedURL).put("feed_path", feedPath);
            } else {
                mJsonObject = jsonArray.optJSONObject(i);
            }
            mJsonArray.put(mJsonObject);
        }
        jsonArray = mJsonArray;
        if (getFeedInf(feedInt)[1].equals("remote")) {
            FileUtils.deleteFile(context.getFilesDir().getPath() + "/OneText/OneText-Library.json");
        }
        FileUtils.deleteFile(context.getFilesDir().getPath() + "/Feed/Feed.json");
        FileUtils.writeTextToFile(mJsonArray.toString(), context.getFilesDir().getPath() + "/Feed/", "Feed.json");
        if (feedInt == position) {
            editor.remove("feed_latest_refresh_time");
            editor.remove("widget_latest_refresh_time");
            editor.remove("onetext_code");
            editor.apply();
        }
    }
}
