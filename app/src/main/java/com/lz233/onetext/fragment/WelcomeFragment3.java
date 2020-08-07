package com.lz233.onetext.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.lz233.onetext.R;

public class WelcomeFragment3 extends Fragment {
    private Activity activity;
    private ViewPager2 viewPager2;
    private AppCompatButton welcome_oauth_cancel_button;
    private AppCompatButton welcome_oauth_continue_button;

    public WelcomeFragment3(Activity activity, ViewPager2 viewPager2) {
        this.activity = activity;
        this.viewPager2 = viewPager2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_welcome3, container, false);
        //fb
        welcome_oauth_cancel_button = rootView.findViewById(R.id.welcome_oauth_cancel_button);
        welcome_oauth_continue_button = rootView.findViewById(R.id.welcome_oauth_continue_button);
        //懒得写

        //懒得写
        welcome_oauth_cancel_button.setOnClickListener(v -> viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1));
        welcome_oauth_continue_button.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/login/oauth/authorize/?client_id=a2cecb404f9d11e7abbe"))));
        return rootView;
    }

    public void onStart() {
        if(getContext().getSharedPreferences("setting", Context.MODE_PRIVATE).getBoolean("oauth_logined",false)){
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
        super.onStart();
    }
}
