package com.lz233.onetext.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.lz233.onetext.MainActivity;
import com.lz233.onetext.R;
import com.zqc.opencc.android.lib.ChineseConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;
import static com.zqc.opencc.android.lib.ConversionType.S2HK;
import static com.zqc.opencc.android.lib.ConversionType.S2T;
import static com.zqc.opencc.android.lib.ConversionType.S2TWP;

public class OneTextUtils{
    private long currentTimeMillis = System.currentTimeMillis();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    @SuppressLint("CommitPrefEdits")
    public OneTextUtils(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("setting",MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public String[] readOneText(int OneTextCode) throws JSONException {
        int chinese_type = sharedPreferences.getInt("chinese_type",0);
        String language = Locale.getDefault().getLanguage();
        String country =Locale.getDefault().getCountry();
        String[] oneText = new String[4];
        JSONObject jsonObject = new JSONObject(readOneTextJSONArray().optString(OneTextCode));
        if(chinese_type == 0){
            if(language.equals("zh")&country.equals("HK")) {
                oneTextViaS2HK(oneText,jsonObject);
            }else if(language.equals("zh")&country.equals("MO")){
                oneTextViaS2T(oneText,jsonObject);
            }else if(language.equals("zh")&country.equals("TW")){
                oneTextViaS2TWP(oneText,jsonObject);
            }else {
                oneTextViaOriginal(oneText,jsonObject);
            }
        }else if (chinese_type == 1){
            oneTextViaOriginal(oneText,jsonObject);
        }else if(chinese_type == 2){
            oneTextViaS2T(oneText,jsonObject);
        }else if(chinese_type == 3){
            oneTextViaS2HK(oneText,jsonObject);
        }else if(chinese_type == 4){
            oneTextViaS2TWP(oneText,jsonObject);
        }
        JSONArray timeJsonArray = new JSONArray(jsonObject.optString("time"));
        final String timeOfRecord = timeJsonArray.optString(0);
        final String timeOfCreation = timeJsonArray.optString(1);
        if (!timeOfCreation.equals("")) {
            oneText[3] = context.getString(R.string.record_time)+"："+timeOfRecord+" "+context.getString(R.string.created_time)+"："+timeOfCreation;
        }else {
            oneText[3] = context.getString(R.string.record_time)+"："+timeOfRecord;
        }
        return oneText;
    }
    private String[] oneTextViaS2HK (String[] oneText,JSONObject jsonObject){
        oneText[0] = ChineseConverter.convert(jsonObject.optString("text"), S2HK, context);
        oneText[1] = ChineseConverter.convert(jsonObject.optString("by"),S2HK,context);
        oneText[2] = ChineseConverter.convert(jsonObject.optString("from"),S2HK,context);
        return oneText;
    }
    private String[] oneTextViaS2T (String[] oneText,JSONObject jsonObject){
        oneText[0] = ChineseConverter.convert(jsonObject.optString("text"), S2T, context);
        oneText[1] = ChineseConverter.convert(jsonObject.optString("by"),S2T,context);
        oneText[2] = ChineseConverter.convert(jsonObject.optString("from"),S2T,context);
        return oneText;
    }
    private String[] oneTextViaS2TWP (String[] oneText,JSONObject jsonObject){
        oneText[0] = ChineseConverter.convert(jsonObject.optString("text"), S2TWP, context);
        oneText[1] = ChineseConverter.convert(jsonObject.optString("by"),S2TWP,context);
        oneText[2] = ChineseConverter.convert(jsonObject.optString("from"),S2TWP,context);
        return oneText;
    }
    private String[] oneTextViaOriginal (String[] oneText,JSONObject jsonObject){
        oneText[0] = jsonObject.optString("text");
        oneText[1] = jsonObject.optString("by");
        oneText[2] = jsonObject.optString("from");
        return oneText;
    }
    public int getOneTextCode(Boolean forcedNewCode,Boolean requestFromWidget) throws JSONException {
        int oneTextCode = 0;
        Random random = new Random();
        if(((currentTimeMillis-sharedPreferences.getLong("widget_latest_refresh_time",0))>(sharedPreferences.getLong("widget_refresh_time",30)*60000))|forcedNewCode){
            if(requestFromWidget|(!sharedPreferences.getBoolean("widget_enabled",false))|forcedNewCode){
                oneTextCode = random.nextInt(readOneTextJSONArray().length());
                editor.putInt("onetext_code",oneTextCode);
                editor.putLong("widget_latest_refresh_time",currentTimeMillis);
                editor.apply();
            }
        }else {
            oneTextCode = sharedPreferences.getInt("onetext_code", 0);
        }
        //旧版逻辑，弃用
        /*if(((((currentTimeMillis-sharedPreferences.getLong("widget_latest_refresh_time",0))>(sharedPreferences.getLong("widget_refresh_time",30)*60000))&(!sharedPreferences.getBoolean("widget_enabled",false)))|(sharedPreferences.getInt("onetext_code",-1) == -1))|(forcedNewCode)){
            oneTextCode = random.nextInt(readOneTextJSONArray().length());
            editor.putInt("onetext_code",oneTextCode);
            editor.putLong("widget_latest_refresh_time",currentTimeMillis);
            editor.apply();
        }else {
            oneTextCode = sharedPreferences.getInt("onetext_code", 0);
        }*/
        return oneTextCode;
    }
    public Boolean ifFeedShouldUpdate(){
        if((currentTimeMillis-sharedPreferences.getLong("feed_latest_refresh_time",0))>(sharedPreferences.getLong("feed_refresh_time",1)*3600000)){
            editor.putLong("feed_latest_refresh_time",currentTimeMillis);
            editor.apply();
            return true;
        }else {
            return false;
        }
    }
    private JSONArray readOneTextJSONArray() throws JSONException {
        String feed_type = sharedPreferences.getString("feed_type","remote");
        JSONArray jsonArray = null;
        if(feed_type.equals("remote")) {
            jsonArray = new JSONArray(FileUtils.readTextFromFile(context.getFilesDir().getPath()+"/OneText/OneText-Library.json"));
        }
        if(feed_type.equals("local")) {
            jsonArray = new JSONArray(FileUtils.readTextFromFile(sharedPreferences.getString("feed_local_path",context.getFilesDir().getPath()+"/OneText/OneText-Library.json")));
        }
        return jsonArray;
    }
}
