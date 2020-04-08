package com.lz233.onetext.activity;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import com.lz233.onetext.R;

import io.noties.markwon.Markwon;

public class PrivacyPolicyActivity extends BaseActivity {
    private TextView privacy_policy_textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //
        privacy_policy_textView = findViewById(R.id.privacy_policy_textView);
        //
        //曲 线 救 国
        fuckNav(privacy_policy_textView);
        final Markwon markwon = Markwon.create(getApplicationContext());
        markwon.setMarkdown(privacy_policy_textView, getString(R.string.privacy_policy)+"\n\n"+getString(R.string.terms_and_conditions));
    }

}
