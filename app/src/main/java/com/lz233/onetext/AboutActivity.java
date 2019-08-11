package com.lz233.onetext;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.lz233.onetext.tools.App;

public class AboutActivity extends BaseActivity {
    private TextView ver_textview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        //fb
        ver_textview = findViewById(R.id.ver_textview);
        ver_textview.setText(App.getAppVersionName(AboutActivity.this)+" ("+String.valueOf(App.getAppVersionCode(AboutActivity.this)+")"));
    }
}
