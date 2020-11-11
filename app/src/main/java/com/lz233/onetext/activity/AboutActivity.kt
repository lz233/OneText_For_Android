package com.lz233.onetext.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.lz233.onetext.BuildConfig
import com.lz233.onetext.R
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        //曲 线 救 国
        fuckNav(findViewById(R.id.about_textview))
        //初始化
        if (BuildConfig.BUILD_TYPE == "GooglePlay") {
            coolapk_imageview.setVisibility(View.GONE)
            msappcenter_imageview.setVisibility(View.GONE)
        }
        green_android_imageview.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://green-android.org"))) }
        storage_isolation_imageview.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/RikkaApps/StorageRedirect-assets/blob/master/app_rule/apps/com.lz233.onetext.json"))) }
        coolapk_imageview.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://coolapk.com/apk/com.lz233.onetext"))) }
        msappcenter_imageview.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://install.appcenter.ms/users/lz233/apps/onetext/distribution_groups/onetext%20testgroup"))) }
        github_imageview.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/lz233/OneText_For_Android"))) }
        telegram_imageview.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/OneTextProject"))) }
        mstodo_imageview.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://to-do.microsoft.com/sharing?InvitationToken=WI9tpCxeg9ktR5mg-AI-qwPXapdT_kGucjpSBCP6fwLE9bN5Uz2vS61gY9X8RTaC0"))) }
        ver_textview.setText("${BuildConfig.VERSION_NAME} ${BuildConfig.VERSION_CODE}\n${BuildConfig.BUILD_TYPE}")
        val markwon = Markwon.create(applicationContext)
        markwon.setMarkdown(about_textview, getString(R.string.about_page_introduction))
    }
}