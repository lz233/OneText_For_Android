package com.lz233.onetext.activity;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.lz233.onetext.R;
import com.lz233.onetext.fragment.WelcomeFragment0;
import com.lz233.onetext.fragment.WelcomeFragment1;
import com.lz233.onetext.fragment.WelcomeFragment2;
import com.lz233.onetext.fragment.WelcomeFragment3;
import com.lz233.onetext.fragment.WelcomeFragment4;
import com.lz233.onetext.fragment.WelcomeFragment5;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class WelcomeActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{
    private Receiver receiver;
    private static final int NUM_PAGES = 6;
    private FragmentStateAdapter pagerAdapter;
    private ViewPager2 viewpager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        viewpager2 = findViewById(R.id.viewpager2);
        //懒
        receiver = new Receiver();
        registerReceiver(receiver, new IntentFilter("com.lz233.onetext.requestpermission"));
        Intent intent = new Intent(this, Service.class);
        startService(intent);
        //barrageview.addBarrage(new Barrage("233", R.color.colorText4));
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewpager2.setAdapter(pagerAdapter);
        viewpager2.setUserInputEnabled(false);
        //监听器

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //返回屏蔽 （被我屏蔽了）其实就是不再屏蔽qaq
    @Override
    public void onBackPressed() {
        int check = viewpager2.getCurrentItem();
        if (check > 0)
            check --;
        viewpager2.setCurrentItem(check);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //把申请权限的回调交由EasyPermissions处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        viewpager2.setCurrentItem(viewpager2.getCurrentItem()+1);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
    class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.lz233.onetext.requestpermission")) {
                final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                EasyPermissions.requestPermissions(WelcomeActivity.this, getString(R.string.request_permissions_storage_detail_text).replace("%s", Environment.getExternalStorageDirectory() + "/Pictures/OneText/"), 1, permissions);
            }
        }
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
                    return new WelcomeFragment0(viewpager2);
                case 1:
                    return new WelcomeFragment1(WelcomeActivity.this,viewpager2);
                case 2:
                    return new WelcomeFragment2(WelcomeActivity.this,viewpager2);
                case 3:
                    return new WelcomeFragment3(WelcomeActivity.this,viewpager2);
                case 4:
                    return new WelcomeFragment4(viewpager2);
                case 5:
                    return new WelcomeFragment5(WelcomeActivity.this,viewpager2);
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}
