package com.lz233.onetext.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.lz233.onetext.R;

public class WelcomeFragment3 extends Fragment {
    private ViewPager2 viewPager2;
    private AppCompatButton welcome_widget_button;
    public WelcomeFragment3(ViewPager2 viewPager2){
        this.viewPager2 = viewPager2;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_welcome3, container, false);
        //fb
        welcome_widget_button = rootView.findViewById(R.id.welcome_widget_button);
        //懒得写

        //懒得写
        welcome_widget_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager2.setCurrentItem(3);
            }
        });
        return rootView;
    }

}
