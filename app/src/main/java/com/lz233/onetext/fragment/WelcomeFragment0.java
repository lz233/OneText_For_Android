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

import com.kd.easybarrage.Barrage;
import com.kd.easybarrage.BarrageView;
import com.lz233.onetext.R;

public class WelcomeFragment0 extends Fragment {
    private ViewPager2 viewPager2;
    private LinearLayout welcome1_linearlayout;
    private ImageView welcome1_icon_imageview;
    private AppCompatButton welcome_start_button;
    private String[] danmu = {
            "即使这些回忆使我感到悲伤，我也必须前进，相信未来。",
            "自古以来，天空上就是另一个世界。",
            "我还没找到。我还不知道。",
            "忘记是什么时候看到的了，你绑马尾辫的样子简直合适到犯规！",
            "宠辱不惊，看庭前花开花落。得失俱忘，观天上云卷云舒",
            "室雅何须大，花香不在多",
            "寄一份心情给久违的青春。",
            "寒夜客来茶当酒，竹炉汤沸火初红。",
            "杜鹃再拜忧天泪，精卫无穷填海心",
            "山一程，水一程，身向榆关那畔行，夜深千帐灯。",
            "山有木兮木有枝，心悦君兮君不知。",
            "巡礼者们朝着心中描绘的圣地，继续前进着。",
            "巧笑倩兮，美目盼兮，素以为绚兮。",
            "想和你重新认识一次 从你叫什么名字说起。",
            "慕君之心，至死方休。",
            "我听到了他的心跳声，温暖得不真实。也许，这只是一个梦吧。",
            "时间冲淡的，是存在感，而不是存在。",
    };
    private BarrageView barrageview;

    public WelcomeFragment0(ViewPager2 viewPager2){
        this.viewPager2 = viewPager2;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        barrageview.destroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_welcome0, container, false);
        //fb
        welcome1_linearlayout = rootView.findViewById(R.id.welcome1_linearlayout);
        welcome1_icon_imageview = rootView.findViewById(R.id.welcome1_icon_imageview);
        welcome_start_button = rootView.findViewById(R.id.welcome_start_button);

        barrageview = rootView.findViewById(R.id.barrageview);
        for(int i=0;i<danmu.length;i++){
            barrageview.addBarrage(new Barrage(danmu[i],R.color.colorText4));
        }

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

    @Override
    public void onResume() {
        super.onResume();
        barrageview.destroy();
        for (int i = 0; i < barrageview.getChildCount(); i++) {
            View v = barrageview.getChildAt(i);
            v.animate().x(-1000).setDuration(0).start();
        }
        for(int i=0;i<danmu.length;i++){
            barrageview.addBarrage(new Barrage(danmu[i],R.color.colorText4));
        }
        barrageview.animate().alpha(1).setDuration(1).start();
    }

    @Override
    public void onPause() {
        super.onPause();
        barrageview.animate().alpha(0).setDuration(0).start();
    }
}
