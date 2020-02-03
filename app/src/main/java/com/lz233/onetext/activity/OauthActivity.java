package com.lz233.onetext.activity;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.lz233.onetext.R;
import com.lz233.onetext.tools.utils.DownloadUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OauthActivity extends BaseActivity {
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);
        final Uri data = getIntent().getData();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String code = data.getQueryParameter("code");
                    RequestBody requestBodyPost = new FormBody.Builder()
                            .add("client_id", "a2cecb404f9d11e7abbe")
                            .add("client_secret", "214b362bc1ee17f9915870e4e412d701e2874117")
                            .add("code", code)
                            .build();
                    Request requestPost = new Request.Builder()
                            .url("https://github.com/login/oauth/access_token")
                            .post(requestBodyPost)
                            .build();
                    client.newCall(requestPost).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            onFailed();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String body = response.body().string();
                            String accessToken = body.substring(body.indexOf("=") + 1, body.indexOf("&"));
                            editor.putString("oauth_access_token", accessToken);
                            editor.apply();
                            Request requestGet = new Request.Builder().get().url("https://api.github.com/user?access_token=" + accessToken).build();
                            client.newCall(requestGet).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    onFailed();
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    final String accountInformation = response.body().string();
                                    try {
                                        JSONObject jsonObject = new JSONObject(accountInformation);
                                        DownloadUtil.get().download(jsonObject.optString("avatar_url"), getFilesDir().getPath() + "/Oauth/", "Avatar.png", new DownloadUtil.OnDownloadListener() {
                                            @Override
                                            public void onDownloadSuccess(File file) {
                                                editor.putString("oauth_account_information",accountInformation);
                                                editor.putBoolean("oauth_logined",true);
                                                editor.apply();
                                                finish();
                                            }

                                            @Override
                                            public void onDownloading(int progress) {

                                            }

                                            @Override
                                            public void onDownloadFailed(Exception e) {
                                            }
                                        });
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    onFailed();
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    private void onFailed(){
        Snackbar.make(rootview, getString(R.string.oauth_failed_text), Snackbar.LENGTH_SHORT).show();
    }
}
