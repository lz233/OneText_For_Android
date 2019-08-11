package com.lz233.onetext;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.lz233.onetext.tools.App;

import io.noties.markwon.Markwon;

public class AboutActivity extends BaseActivity {
    private TextView ver_textview;
    private TextView about_textview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        //fb
        ver_textview = findViewById(R.id.ver_textview);
        about_textview = findViewById(R.id.about_textview);
        final Markwon markwon = Markwon.create(getApplicationContext());
        ver_textview.setText(App.getAppVersionName(AboutActivity.this)+" ("+String.valueOf(App.getAppVersionCode(AboutActivity.this)+")"));
        markwon.setMarkdown(about_textview, getString(R.string.about_page_introduction));

    }
}
