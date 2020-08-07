package com.lz233.onetext.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.lz233.onetext.R;

public class WelcomeFragment4 extends Fragment {
    private ViewPager2 viewPager2;
    private AppCompatButton welcome_widget_button;
    public WelcomeFragment4(ViewPager2 viewPager2){
        this.viewPager2 = viewPager2;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_welcome4, container, false);
        //fb
        welcome_widget_button = rootView.findViewById(R.id.welcome_widget_button);
        //懒得写

        //懒得写
        welcome_widget_button.setOnClickListener(v -> viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1));
        return rootView;
    }

}
