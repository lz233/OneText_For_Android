package com.lz233.onetext.tools.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.lz233.onetext.BuildConfig;
import com.lz233.onetext.R;
import com.zqc.opencc.android.lib.ChineseConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import static com.zqc.opencc.android.lib.ConversionType.S2HK;
import static com.zqc.opencc.android.lib.ConversionType.S2T;
import static com.zqc.opencc.android.lib.ConversionType.S2TWP;

public class CoreUtil {
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private long currentTimeMillis;
    private String oneTextArray;

    public CoreUtil(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        currentTimeMillis = System.currentTimeMillis();
    }

    private boolean ifHaveOneTextFiles() {
        return FileUtil.isFile(context.getFilesDir().getPath() + "/OneText/OneText-Library.json");
    }

    private String getOneTextArray() {
        HashMap feedMap = getFeedInformation(sharedPreferences.getInt("feed_code", 0));
        if (feedMap.get("feed_type").equals("remote")) {
            if (ifHaveOneTextFiles()) {
                oneTextArray = FileUtil.readTextFromFile(context.getFilesDir().getPath() + "/OneText/OneText-Library.json");
            } else {
                return "[{\"text\":\"File does not exist.\"}]";
            }
        } else if (feedMap.get("feed_type").equals("local")) {
            oneTextArray = FileUtil.readTextFromFile((String) feedMap.get("feed_path"));
        }
        return oneTextArray;
    }

    private int getOneTextCode(boolean forceRefresh, boolean ifFromWidget) {
        if (forceRefresh) {
            return generateNewOneTextCode();
        } else {
            if (ifOneTextShouldUpdate(ifFromWidget)) {
                return generateNewOneTextCode();
            } else {
                return sharedPreferences.getInt("onetext_code", 0);
            }
        }
    }

    private int generateNewOneTextCode() {
        Random random = new Random();
        int code = 0;
        try {
            code = random.nextInt(new JSONArray(oneTextArray).length());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        refreshLatestRefreshTime();
        editor.putInt("onetext_code", code);
        editor.apply();
        return code;
    }

    public boolean ifOneTextShouldUpdate(boolean ifFromWidget) {
        if (ifFromWidget | (!sharedPreferences.getBoolean("widget_enabled", false))) {
            return (ifFromWidget) & ((currentTimeMillis - sharedPreferences.getLong("onetext_latest_refresh_time", 0)) >= (sharedPreferences.getLong("onetext_refresh_time", 30) * 60000));
        }
        return false;
    }

    public void refreshLatestRefreshTime() {
        editor.putLong("onetext_latest_refresh_time", currentTimeMillis);
        editor.apply();
    }

    public void initOneText(final OnOneTextInitListener onOneTextInitListener) {
        final HashMap hashMap = getFeedInformation(sharedPreferences.getInt("feed_code", 0));
        if (hashMap.get("feed_type").equals("remote")) {
            if (ifHaveOneTextFiles()) {
                onOneTextInitListener.onSuccess(new File(context.getFilesDir().getPath() + "/OneText/OneText-Library.json"));
            } else {
                new Thread(() -> DownloadUtil.get().download((String) hashMap.get("feed_url"), context.getFilesDir().getPath() + "/OneText/", "OneText-Library.json", new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        onOneTextInitListener.onSuccess(file);
                    }

                    @Override
                    public void onDownloading(int progress) {
                        onOneTextInitListener.onDownloading(progress);
                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                        onOneTextInitListener.onFailed(e);
                    }
                })).start();
            }
        } else if (hashMap.get("feed_type").equals("local")) {
            if (FileUtil.isFile((String) hashMap.get("feed_path"))) {
                onOneTextInitListener.onSuccess(new File((String) hashMap.get("feed_path")));
            } else {
                onOneTextInitListener.onFailed(null);
            }
        }

    }

    private JSONObject getOneTextObject(boolean forceRefresh, boolean ifFromWidget, HashMap feedMap) throws JSONException {
        /*JSONObject jsonObject = null;
        if (feedMap.get("feed_type").equals("internet")) {
            final String[] mResult = new String[]{null};
            new GetUtil().sendGet((String) feedMap.get("api_url"), new GetUtil.GetCallback() {
                @Override
                public void onGetDone(String result) {
                    mResult[0] = result;
                }
            });
            while (mResult==null){};
            return jsonObject;
        } else {
            jsonObject = new JSONArray(getOneTextArray()).optJSONObject(getOneTextCode(forceRefresh, ifFromWidget));
        }*/
        return new JSONArray(getOneTextArray()).optJSONObject(getOneTextCode(forceRefresh, ifFromWidget));
    }

    public HashMap getOneText(boolean forceRefresh, boolean ifFromWidget) throws JSONException {
        HashMap feedMap = getFeedInformation(sharedPreferences.getInt("feed_code", 0));
        JSONObject jsonObject = getOneTextObject(forceRefresh, ifFromWidget, feedMap);
        return convertOneText(jsonObject, feedMap);
    }

    public HashMap convertOneText(JSONObject jsonObject, HashMap feedMap) {
        HashMap hashMap = new HashMap();
        int chinese_type = sharedPreferences.getInt("chinese_type", 0);
        String language = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();
        if (chinese_type == 0) {
            if (language.equals("zh") & country.equals("HK")) {
                oneTextViaS2HK(feedMap, jsonObject, hashMap);
            } else if (language.equals("zh") & country.equals("MO")) {
                oneTextViaS2T(feedMap, jsonObject, hashMap);
            } else if (language.equals("zh") & country.equals("TW")) {
                oneTextViaS2TWP(feedMap, jsonObject, hashMap);
            } else {
                oneTextViaOriginal(feedMap, jsonObject, hashMap);
            }
        } else if (chinese_type == 1) {
            oneTextViaOriginal(feedMap, jsonObject, hashMap);
        } else if (chinese_type == 2) {
            oneTextViaS2T(feedMap, jsonObject, hashMap);
        } else if (chinese_type == 3) {
            oneTextViaS2HK(feedMap, jsonObject, hashMap);
        } else if (chinese_type == 4) {
            oneTextViaS2TWP(feedMap, jsonObject, hashMap);
        }
        hashMap.put("uri",jsonObject.optString((String) feedMap.get("uri_key")));
        hashMap.put("time","");
        try {
            JSONArray timeJsonArray = new JSONArray(jsonObject.optString((String) feedMap.get("time_key")));
            final String timeOfRecord = timeJsonArray.optString(0);
            final String timeOfCreation = timeJsonArray.optString(1);
            if (!timeOfCreation.equals("")) {
                hashMap.put("time", context.getString(R.string.record_time) + "：" + timeOfRecord + " " + context.getString(R.string.created_time) + "：" + timeOfCreation);
            } else {
                hashMap.put("time", context.getString(R.string.record_time) + "：" + timeOfRecord);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    private HashMap oneTextViaS2HK(HashMap feedMap, JSONObject jsonObject, HashMap hashMap) {
        hashMap.put("text", ChineseConverter.convert(jsonObject.optString((String) feedMap.get("text_key")), S2HK, context));
        hashMap.put("by", ChineseConverter.convert(jsonObject.optString((String) feedMap.get("by_key")), S2HK, context));
        hashMap.put("from", ChineseConverter.convert(jsonObject.optString((String) feedMap.get("from_key")), S2HK, context));
        return hashMap;
    }

    private HashMap oneTextViaS2T(HashMap feedMap, JSONObject jsonObject, HashMap hashMap) {
        hashMap.put("text", ChineseConverter.convert(jsonObject.optString((String) feedMap.get("text_key")), S2T, context));
        hashMap.put("by", ChineseConverter.convert(jsonObject.optString((String) feedMap.get("by_key")), S2T, context));
        hashMap.put("from", ChineseConverter.convert(jsonObject.optString((String) feedMap.get("from_key")), S2T, context));
        return hashMap;
    }

    private HashMap oneTextViaS2TWP(HashMap feedMap, JSONObject jsonObject, HashMap hashMap) {
        hashMap.put("text", ChineseConverter.convert(jsonObject.optString((String) feedMap.get("text_key")), S2TWP, context));
        hashMap.put("by", ChineseConverter.convert(jsonObject.optString((String) feedMap.get("by_key")), S2TWP, context));
        hashMap.put("from", ChineseConverter.convert(jsonObject.optString((String) feedMap.get("from_key")), S2TWP, context));
        return hashMap;
    }

    private HashMap oneTextViaOriginal(HashMap feedMap, JSONObject jsonObject, HashMap hashMap) {
        hashMap.put("text", jsonObject.optString((String) feedMap.get("text_key")));
        hashMap.put("by", jsonObject.optString((String) feedMap.get("by_key")));
        hashMap.put("from", jsonObject.optString((String) feedMap.get("from_key")));
        return hashMap;
    }

    public boolean initFeedFile() {
        boolean ifsucceed = true;
        if (!FileUtil.isDirectory(context.getFilesDir().getPath() + "/Feed/")) {
            File file = new File(context.getFilesDir().getPath() + "/Feed/");
            ifsucceed = file.mkdirs();
        }
        if ((!FileUtil.isFile(context.getFilesDir().getPath() + "/Feed/Feed.json")) | ((sharedPreferences.getInt("version_code", 20200407) - 20200919) < 0)) {
            ifsucceed = FileUtil.copyAssets(context, "Feed", context.getFilesDir().getPath() + "/Feed");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("version_code", BuildConfig.VERSION_CODE);
            editor.remove("feed_code");
            editor.remove("feed_latest_refresh_time");
            editor.remove("widget_latest_refresh_time");
            editor.remove("onetext_code");
            editor.apply();
        }
        return ifsucceed;
    }

    private boolean ifHaveFeedFiles() {
        return FileUtil.isFile(context.getFilesDir().getPath() + "/Feed/Feed.json");
    }

    private String getFeedArray() {
        if (ifHaveFeedFiles()) {
            return FileUtil.readTextFromFile(context.getFilesDir().getPath() + "/Feed/Feed.json");
        } else {
            return "[{\"feed_name\":\"File does not exist.\"}]";
        }
    }

    public HashMap getFeedInformation(int position) {
        HashMap hashMap = new HashMap();
        try {
            JSONArray jsonArray = new JSONArray(getFeedArray());
            JSONObject jsonObject = jsonArray.optJSONObject(position);
            hashMap.put("feed_name", jsonObject.optString("feed_name"));
            hashMap.put("feed_type", jsonObject.optString("feed_type"));
            hashMap.put("feed_url", jsonObject.optString("feed_url"));
            hashMap.put("feed_path", jsonObject.optString("feed_path"));
            hashMap.put("text_key", jsonObject.optString("text_key"));
            hashMap.put("by_key", jsonObject.optString("by_key"));
            hashMap.put("uri_key",jsonObject.optString("uri_key"));
            hashMap.put("time_key", jsonObject.optString("time_key"));
            hashMap.put("from_key", jsonObject.optString("from_key"));
            hashMap.put("api_method", jsonObject.optString("api_method"));
            hashMap.put("api_url", jsonObject.optString("api_url"));
            hashMap.put("object_key", jsonObject.optString("object_key"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    public void addFeed(String feedName, String feedType, String feedURL, String feedPath) throws JSONException {
        JSONArray jsonArray = new JSONArray(getFeedArray());
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
        FileUtil.deleteFile(context.getFilesDir().getPath() + "/Feed/Feed.json");
        FileUtil.writeTextToFile(mJsonArray.toString(), context.getFilesDir().getPath() + "/Feed/", "Feed.json");
    }

    public void deleteFeed(int position) throws JSONException {
        int feedInt = sharedPreferences.getInt("feed_code", 0);
        if (feedInt > position) {
            editor.putInt("feed_code", sharedPreferences.getInt("feed_code", 0) - 1);
        } else if (feedInt == position) {
            if (getFeedInformation(position).get("feed_type").equals("remote")) {
                FileUtil.deleteFile(context.getFilesDir().getPath() + "/OneText/OneText-Library.json");
            }
            editor.remove("feed_code");
            editor.remove("feed_latest_refresh_time");
            editor.remove("widget_latest_refresh_time");
            editor.remove("onetext_code");
        }
        editor.apply();
        JSONArray jsonArray = new JSONArray(getFeedArray());
        JSONArray mJsonArray = new JSONArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            if (!(position == i)) {
                JSONObject mJsonObject = jsonArray.optJSONObject(i);
                mJsonArray.put(mJsonObject);
            }
        }
        jsonArray = mJsonArray;
        FileUtil.deleteFile(context.getFilesDir().getPath() + "/Feed/Feed.json");
        FileUtil.writeTextToFile(mJsonArray.toString(), context.getFilesDir().getPath() + "/Feed/", "Feed.json");
    }

    public void runOneTextUpdate(final OnOneTextUpdateListener onOneTextUpdateListener) {
        final HashMap hashMap = getFeedInformation(sharedPreferences.getInt("feed_code", 0));
        if (hashMap.get("feed_type").equals("feed_type")) {
            if (sharedPreferences.getBoolean("feed_auto_update", true)) {
                if ((currentTimeMillis - sharedPreferences.getLong("feed_latest_refresh_time", 0)) > (sharedPreferences.getLong("feed_refresh_time", 1) * 3600000)) {
                    new Thread(() -> DownloadUtil.get().download((String) hashMap.get("feed_url"), context.getFilesDir().getPath() + "/OneText/", "OneText-Library.json", new DownloadUtil.OnDownloadListener() {
                        @Override
                        public void onDownloadSuccess(File file) {
                            editor.putLong("feed_latest_refresh_time", currentTimeMillis);
                            editor.apply();
                            onOneTextUpdateListener.onSuccess(file);
                        }

                        @Override
                        public void onDownloading(int progress) {
                            onOneTextUpdateListener.onDownloading(progress);
                        }

                        @Override
                        public void onDownloadFailed(Exception e) {
                            onOneTextUpdateListener.onFailed(e);
                        }
                    })).start();
                } else {
                    onOneTextUpdateListener.noUpdateRequired();
                }
            } else {
                onOneTextUpdateListener.noUpdateRequired();
            }
        } else {
            onOneTextUpdateListener.noUpdateRequired();
        }

    }

    public interface OnOneTextUpdateListener {
        void noUpdateRequired();

        void onSuccess(File file);

        void onDownloading(int progress);

        void onFailed(Exception e);
    }

    public interface OnOneTextInitListener {
        void onSuccess(File file);

        void onDownloading(int progress);

        void onFailed(@Nullable Exception e);
    }
}
