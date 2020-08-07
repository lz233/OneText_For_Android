package com.lz233.onetext.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.lz233.onetext.R;

public class WelcomeFragment5 extends Fragment {
    private Activity activity;
    private ViewPager2 viewPager2;
    private AppCompatButton welcome_notification_button;

    public WelcomeFragment5(Activity activity, ViewPager2 viewPager2) {
        this.activity = activity;
        this.viewPager2 = viewPager2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_welcome5, container, false);
        //fb
        welcome_notification_button = rootView.findViewById(R.id.welcome_notification_button);
        //懒得写

        //懒得写
        welcome_notification_button.setOnClickListener(v -> {
            //viewPager2.setCurrentItem(4);
            SharedPreferences.Editor editor = activity.getSharedPreferences("setting", Context.MODE_PRIVATE).edit();
            editor.putBoolean("first_run",false);
            editor.apply();
            activity.finish();
        });
        return rootView;
    }

}
