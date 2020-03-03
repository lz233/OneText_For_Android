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

public class WelcomeFragment0 extends Fragment {
    private ViewPager2 viewPager2;
    private LinearLayout welcome1_linearlayout;
    private ImageView welcome1_icon_imageview;
    private AppCompatButton welcome_start_button;
    public WelcomeFragment0(ViewPager2 viewPager2){
        this.viewPager2 = viewPager2;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_welcome0, container, false);
        //fb
        welcome1_linearlayout = rootView.findViewById(R.id.welcome1_linearlayout);
        welcome1_icon_imageview = rootView.findViewById(R.id.welcome1_icon_imageview);
        welcome_start_button = rootView.findViewById(R.id.welcome_start_button);
        //懒得写
        Animation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(3000);
        welcome1_icon_imageview.setAnimation(alphaAnimation);
        welcome_start_button.setAnimation(alphaAnimation);
        //懒得写
        welcome_start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1);
            }
        });
        return rootView;
    }

}
