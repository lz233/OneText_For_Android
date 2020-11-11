package com.lz233.onetext.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.lz233.onetext.R
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.content_privacy_policy.*

class PrivacyPolicyActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        //
        //曲 线 救 国
        fuckNav(privacy_policy_textView)
        val markwon = Markwon.create(applicationContext)
        markwon.setMarkdown(privacy_policy_textView, "${getString(R.string.privacy_policy)}\n\n${getString(R.string.terms_and_conditions)}")
    }
}