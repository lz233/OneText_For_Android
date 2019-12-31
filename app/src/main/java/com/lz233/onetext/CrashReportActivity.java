package com.lz233.onetext;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.microsoft.appcenter.crashes.Crashes;
import com.microsoft.appcenter.crashes.model.ErrorReport;
import com.microsoft.appcenter.utils.async.AppCenterConsumer;
import com.microsoft.appcenter.utils.async.AppCenterFuture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import io.noties.markwon.Markwon;

public class CrashReportActivity extends BaseActivity {
    private TextView crash_report_textView;
    final String[] report = new String[1];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //fb
        crash_report_textView = findViewById(R.id.crash_report_textView);
        //初始化
        AppCenterFuture<ErrorReport> future = Crashes.getLastSessionCrashReport();
        future.thenAccept(new AppCenterConsumer<ErrorReport>() {
            @Override
            public void accept(ErrorReport errorReport) {
                try {
                //Toast.makeText(MainActivity.this,errorReport.getStackTrace(),Toast.LENGTH_SHORT).show();
                    report[0] = getResources().getText(R.string.report_page_tip)+"\n\nAppNamespace: `"+errorReport.getDevice().getAppNamespace()+"`\n\nAppVersion: `"+errorReport.getDevice().getAppVersion()+"`\n\nAppBuild: `"+errorReport.getDevice().getAppBuild()+"`\n\nOemName: `"+errorReport.getDevice().getOemName()+"`\n\nModel: `"+errorReport.getDevice().getModel()+"`\n\nOsName: `"+errorReport.getDevice().getOsName()+"`\n\nOsVersion: `"+errorReport.getDevice().getOsVersion()+"`\n\nOsApiLevel: `"+errorReport.getDevice().getOsApiLevel()+"`\n\nOsBuild: `"+errorReport.getDevice().getOsBuild()+"`\n\nSdkVersion: `"+errorReport.getDevice().getSdkVersion()+"`\n\nLocale: `"+errorReport.getDevice().getLocale()+"`\n\nScreenSize: `"+errorReport.getDevice().getScreenSize()+"`\n\nThreadName: `"+errorReport.getThreadName()+"`\n\nStackTrace:\n```bash\n"+errorReport.getStackTrace()+"\n```";
                    final Markwon markwon = Markwon.create(getApplicationContext());
                    markwon.setMarkdown(crash_report_textView, report[0]);
                }catch (Exception e){
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_copy) {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText("Label", report[0]));
            Snackbar.make(getWindow().getDecorView(), R.string.succeed, Snackbar.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
