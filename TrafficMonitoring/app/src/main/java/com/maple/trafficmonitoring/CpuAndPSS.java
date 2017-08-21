package com.maple.trafficmonitoring;

import android.app.Activity;

import android.app.ActivityManager;
import android.app.ApplicationErrorReport;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;


import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;






/**
 * Created by liu on 5/12/16.
 */
public class CpuAndPSS extends Activity {

    private ImageView im;
    private TextView txAppName,txPackageName,txVersion;
    private String packageName,AppName,version;
    private Button btnStartTest;
    private Drawable icon ;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
    private SeekBar CpuTimeline,CpuTestTimeline;
    private TextView CpuTestTime,CpuTime;
    private CheckBox CpuSaveCSV;
    private int interva=5;
    private boolean CSaveCSV = false,testEnd=true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cpu_and_pss);
        intll();
        btnStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnStartTest.getText().equals(getString(R.string.Start))){
                    Intent intent = new Intent();
                    intent.setClass(CpuAndPSS.this, mService.class);
                    intent.putExtra("packageName", packageName);
                    intent.putExtra("time",CpuTime.getText());
                    intent.putExtra("testTime",CpuTestTime.getText());
                    intent.putExtra("saveCSV",CSaveCSV);
                    intent.putExtra("AppName",AppName);
                    intent.putExtra("version",version);
                    //启动FxService
                    Log.e("tt", packageName);
                    testEnd=false;
                    btnStartTest.setText(getString(R.string.Stop));
                    startService(intent);
                }else {
                    testEnd=true;
                    btnStartTest.setText(getString(R.string.Start));
                }

            }

        });

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (testEnd){
                return super.onKeyDown(keyCode, event);
            }else {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private void intll(){
        im = (ImageView) findViewById(R.id.im);
        txAppName = (TextView) findViewById(R.id.txAppName);
        txPackageName = (TextView) findViewById(R.id.txPackageName);
        txVersion = (TextView) findViewById(R.id.txVersion);
        btnStartTest = (Button) findViewById(R.id.btnStartTest);
        Intent intent = getIntent();
        packageName = intent.getStringExtra("packageName");
        AppName = intent.getStringExtra("APPName");

        PackageManager pm = getPackageManager();

        try {
            ApplicationInfo ai = pm.getApplicationInfo(packageName,PackageManager.GET_ACTIVITIES);
            icon = ai.loadIcon(pm);
            PackageInfo packageInfo = pm.getPackageInfo(packageName,0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        im.setImageDrawable(icon);
        txAppName.setText(getString(R.string.ApplicationName)+AppName);
        txPackageName.setText(getString(R.string.packageName)+packageName);
        txVersion.setText(getString(R.string.Version)+version);
        CpuTestTime = (TextView) findViewById(R.id.CpuTestTime);
        CpuTime = (TextView) findViewById(R.id.CpuTime);
        CpuTimeline = (SeekBar) findViewById(R.id.CpuTimeline);
        CpuTestTimeline = (SeekBar) findViewById(R.id.CpuTestTimeline);
        CpuSaveCSV = (CheckBox) findViewById(R.id.CpuSaveCSV);

        CpuTime.setText(String.valueOf(interva));
        CpuTimeline.setProgress(interva);
        CpuTimeline.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                CpuTime.setText(Integer.toString(progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        CpuTestTime.setText(getString(R.string.timeless));
        CpuTestTimeline.setProgress(0);
        CpuTestTimeline.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress==0){
                    CpuTestTime.setText(getString(R.string.timeless));
                }else {
                    CpuTestTime.setText(Integer.toString(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        CpuSaveCSV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CSaveCSV=isChecked;
            }
        });
    }

}
