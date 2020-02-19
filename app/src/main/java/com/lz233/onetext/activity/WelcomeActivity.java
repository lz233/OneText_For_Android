package com.lz233.onetext.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.kd.easybarrage.Barrage;
import com.kd.easybarrage.BarrageView;
import com.lz233.onetext.R;
import com.lz233.onetext.fragment.WelcomeFragment1;
import com.lz233.onetext.fragment.WelcomeFragment2;
import com.lz233.onetext.fragment.WelcomeFragment3;
import com.lz233.onetext.fragment.WelcomeFragment4;

public class WelcomeActivity extends BaseActivity {
    private static final int NUM_PAGES = 5;
    private String[] danmu = {"即使这些回忆使我感到悲伤，我也必须前进，相信未来。",
            "即使是在我感到了我的孤单，即将失去所有的希望，这份回忆，也使我更加坚强。",
            "梦如同黎明的泡沫一样渐渐消失。",
            "每个人都在自己的生命中频繁地抛弃着自己的过去。",
            "这世上，有所谓的普通人类吗……",
            "任何人身上都有一定的不健全。完美的人是不存在的。",
            "这即是说人类为拙作——绝对正确的选择是做不到的——",
            "所以……肯定……无论是怎样的人类……最后都……会变成这样——？",
            "当你想要放弃的时候，想想是什么让你当初坚持走到了这里。",
            "我想告诉你，这个世界是为了你幸福才存在的。",
            "人死后会成为什么?夜空中的一座孤岛。",
            "影响大众想象力的，并不是事实本身，而是它扩散和传播的方式。",
            "神啊，求求你。已经足够了。已经没事了。我们都会熬过去的。",
            "自古以来，天空上就是另一个世界。",
            "在东京的天空上，我们决定性的改变了世界的模样。",
            "我还没找到。我还不知道。"};
    private FragmentStateAdapter pagerAdapter;
    private BarrageView barrageview;
    private ViewPager2 viewpager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //fb
        barrageview = findViewById(R.id.barrageview);
        viewpager2 = findViewById(R.id.viewpager2);
        for(int i=0;i<danmu.length;i++){
            barrageview.addBarrage(new Barrage(danmu[i],R.color.colorText4));
        }
        //barrageview.addBarrage(new Barrage("233", R.color.colorText4));
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewpager2.setAdapter(pagerAdapter);
        viewpager2.setUserInputEnabled(false);
        //监听器

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        barrageview.destroy();
    }

    //返回屏蔽
    @Override
    public void onBackPressed() {
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position){
                default:
                    return new WelcomeFragment1(viewpager2);
                case 1:
                    return new WelcomeFragment2(WelcomeActivity.this,viewpager2);
                case 2:
                    return new WelcomeFragment3(viewpager2);
                case 3:
                    return new WelcomeFragment4(WelcomeActivity.this,viewpager2);

            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}
