package com.lz233.onetext.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.snackbar.Snackbar;
import com.lz233.onetext.R;

import io.noties.markwon.Markwon;

public class WelcomeFragment1 extends Fragment{
    private Activity activity;
    private ViewPager2 viewPager2;
    private TextView welcome_policy_detail_text;
    private AppCompatButton welcome_policy_disagree_button;
    private AppCompatButton welcome_policy_agree_button;

    public WelcomeFragment1(Activity activity, ViewPager2 viewPager2) {
        this.activity = activity;
        this.viewPager2 = viewPager2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_welcome1, container, false);
        //fb
        welcome_policy_detail_text = rootView.findViewById(R.id.welcome_policy_detail_text);
        welcome_policy_disagree_button = rootView.findViewById(R.id.welcome_policy_disagree_button);
        welcome_policy_agree_button = rootView.findViewById(R.id.welcome_policy_agree_button);
        //懒得写
        final Markwon markwon = Markwon.create(activity);
        markwon.setMarkdown(welcome_policy_detail_text, getString(R.string.welcome_privacy_policy_detail_text));
        //懒得写
        welcome_policy_disagree_button.setOnClickListener(v -> Snackbar.make(v, getString(R.string.welcome_privacy_policy_disagree_button), Snackbar.LENGTH_SHORT).show());
        welcome_policy_agree_button.setOnClickListener(v -> viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1));
        return rootView;
    }
}
