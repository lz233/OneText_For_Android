package com.lz233.onetext.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.snackbar.Snackbar;
import com.lz233.onetext.R;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class WelcomeFragment2 extends Fragment implements EasyPermissions.PermissionCallbacks {
    private Activity activity;
    private ViewPager2 viewPager2;
    private TextView welcome_permissions_detail_text;
    private AppCompatButton welcome_permissions_disagree_button;
    private AppCompatButton welcome_permissions_agree_button;

    public WelcomeFragment2(Activity activity, ViewPager2 viewPager2) {
        this.activity = activity;
        this.viewPager2 = viewPager2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_welcome2, container, false);
        //fb
        welcome_permissions_detail_text = rootView.findViewById(R.id.welcome_permissions_detail_text);
        welcome_permissions_disagree_button = rootView.findViewById(R.id.welcome_permissions_disagree_button);
        welcome_permissions_agree_button = rootView.findViewById(R.id.welcome_permissions_agree_button);
        //懒得写
        final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        welcome_permissions_detail_text.setText(getString(R.string.request_permissions_storage_detail_text).replace("%s", Environment.getExternalStorageDirectory() + "/Pictures/OneText/"));
        //懒得写
        welcome_permissions_disagree_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!EasyPermissions.hasPermissions(activity, permissions)) {
                    Snackbar.make(v, getString(R.string.request_permissions_next_time_text), Snackbar.LENGTH_SHORT).show();
                }
                viewPager2.setCurrentItem(2);
            }
        });
        welcome_permissions_agree_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(activity, permissions)) {
                    viewPager2.setCurrentItem(2);
                }else {
                    Intent intent = new Intent("com.lz233.onetext.requestpermission");
                    intent.setPackage(getContext().getPackageName());
                    getContext().sendBroadcast(intent);
                }
            }
        });
        return rootView;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //把申请权限的回调交由EasyPermissions处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, activity);
    }
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        viewPager2.setCurrentItem(2);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}
