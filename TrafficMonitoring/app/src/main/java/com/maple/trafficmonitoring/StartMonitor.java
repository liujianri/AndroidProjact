package com.maple.trafficmonitoring;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by liu on 1/12/16.
 */
public class StartMonitor extends Activity{
    private Drawable icon ;
    private int uid = 0,i;
    private String version;
    private ImageView imageView;
    private TextView tv ,tv2, tv3,tv5,tv4,tv6,tvTime,testTime,tv7;
    private Button btnStart;
    private int Hcont=0,interval=5,Qcont=0,cont = 0;
    private SeekBar timeBar ,TestTimeBar;
    private long r;
    private long rx;
    private long tx;
    private long Tms;
    private long Htraf=0;
    private long Qtraf=0;
    private boolean first = false,testEnd=true,isTime=true,SaveCSV = false;
    private Context c;
    private String packageName;
    private String AppName;
    private String rt;
    private WriteCSV write= new WriteCSV();
    private String    mDateTime;
    private CheckBox chkSaveCsv;

    final Handler handler = new Handler();
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {

            i = Integer.parseInt(String.valueOf(tvTime.getText()))*1000;
            int isB = isBackground(c);
            rx = TrafficStats.getUidRxBytes(uid);
            tx = TrafficStats.getUidTxBytes(uid);
            rt = String.valueOf(Qtraf + Htraf);
            tv5.setText(getString(R.string.str)+AppName+getString(R.string.UseTotal) + bytes2kb(Qtraf + Htraf));
            if (isB==1){
                long ms = Hcont * i;
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                String hms = formatter.format(ms);
                Htraf = rx + tx - r - Qtraf;
                tv6.setText(getString(R.string.BackgroundOperation) + hms + getString(R.string.useTraffic) +bytes2kb(Htraf) );
                Hcont = Hcont + 1;
                System.out.println(rx + tx + "   " + hms);
                writeData("后台",hms,rt,String.valueOf(Qtraf),String.valueOf(Htraf));
            }else if (isB == 2) {
                long ms =Qcont * i;
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                String hms = formatter.format(ms);
                Qtraf = rx + tx - r - Htraf;
                tv7.setText(getString(R.string.foreground) + hms + getString(R.string.useTraffic) +bytes2kb(Qtraf) );
                Qcont = Qcont + 1;
                System.out.println(rx + tx+ "   " + hms);
                writeData("前台",hms,rt,String.valueOf(Qtraf),String.valueOf(Htraf));
            }
            Tms = cont*i;
            cont=cont+1;
            handler.postDelayed(this, i);// 50是延时时长
            if (isB ==3){
                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                startActivity(LaunchIntent);
            }

            if (!testTime.getText().equals(getString(R.string.timeless))){
                int lg = Integer.parseInt(String.valueOf(testTime.getText()));
                System.out.println(lg*60*60*1000+"  "+Tms);
                if (lg*60*60*1000 <= Tms){
                    stop();
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_monitor);
        ints();
        Intent intent = getIntent();
        packageName = intent.getStringExtra("packageName");
        AppName = intent.getStringExtra("APPName");
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(packageName,PackageManager.GET_ACTIVITIES);
            uid = ai.uid;
            icon = ai.loadIcon(pm);
            PackageInfo packageInfo = pm.getPackageInfo(packageName,0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        imageView.setImageDrawable(icon);
        tv.setText(getString(R.string.ApplicationName)+AppName);
        tv2.setText(getString(R.string.packageName)+packageName);
        tv3.setText(getString(R.string.Version)+version);
        try {
            c = createPackageContext(packageName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            Log.e("000000", String.valueOf(c));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnStart.getText().equals(getString(R.string.Start))) {
                    tv4.setText(R.string.testing);
                    btnStart.setText(R.string.Stop);
                    timeBar.setEnabled(false);
                    TestTimeBar.setEnabled(false);
                    chkSaveCsv.setClickable(false);
                    Toast.makeText(StartMonitor.this, R.string.startTest, Toast.LENGTH_SHORT).show();
                    if(first){
                        Hcont=1;
                        Qcont=1;
                        cont = 1;
                    }
                    first=true;
                    testEnd=false;
                    Htraf=0;
                    Qtraf=0;
                    tv6.setText(getString(R.string.foreground) + "00:00:00" + getString(R.string.useTraffic) +bytes2kb(Htraf) );
                    tv7.setText(getString(R.string.foreground) + "00:00:00" + getString(R.string.useTraffic) +bytes2kb(Qtraf) );
                    r = TrafficStats.getUidRxBytes(uid) + TrafficStats.getUidTxBytes(uid);
                    handler.postDelayed(runnable, i);// 打开定时器，执行操作
                }else {
                    stop();
                }
            }
        });

    }

    private void stop (){
        handler.removeCallbacks(runnable);
        tv4.setText(R.string.EndTest);
        timeBar.setEnabled(true);
        TestTimeBar.setEnabled(true);
        chkSaveCsv.setClickable(true);
        testEnd=true;
        isTime=true;
        btnStart.setText(getString(R.string.Start));
    }


    private void  ints (){
        imageView = (ImageView) findViewById(R.id.imageView);
        tv = (TextView) findViewById(R.id.textView);
        tv2 = (TextView) findViewById(R.id.textView2);
        tv3 = (TextView) findViewById(R.id.textView3);
        tv5 = (TextView) findViewById(R.id.textView5);
        tv4 = (TextView) findViewById(R.id.textView4);
        tv6 = (TextView) findViewById(R.id.textView6);
        tv7 = (TextView) findViewById(R.id.textView7);
        chkSaveCsv = (CheckBox) findViewById(R.id.SaveCSV);
        chkSaveCsv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveCSV=isChecked;
            }
        });
        btnStart = (Button) findViewById(R.id.btnStart);
        testEnd=true;

        testTime = (TextView) findViewById(R.id.TestTime);
        testTime.setText(getString(R.string.timeless));
        TestTimeBar = (SeekBar) findViewById(R.id.TestTimeline);
        TestTimeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress==0){
                    testTime.setText(getString(R.string.timeless));
                }else {
                    testTime.setText(Integer.toString(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tvTime = (TextView) findViewById(R.id.time);
        tvTime .setText(String.valueOf(interval));
        timeBar = (SeekBar) findViewById(R.id.timeline);
        timeBar.setProgress(interval);
        timeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                tvTime.setText(Integer.toString(arg1 + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // when tracking stoped, update preferences
            }
        });

    }

    public static String bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        if (returnValue > 1)
            return (returnValue + "MB");
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        return (returnValue + "KB");
    }

    public int isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.getPackageName(), "处于后台"
                            + appProcess.processName);
                    return 1;
                } else {
                    Log.i(context.getPackageName(), "处于前台"
                            + appProcess.processName);
                    return 2;
                }
            }
        }
        return 3;
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

    private void writeData(String QorH,String time,String TotalFlow,String Q ,String H){
        if (SaveCSV) {
            if (isTime) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                mDateTime = formatter.format(curDate);
                isTime = false;
            }
            write.WriteCSV(QorH, time, TotalFlow, Q, H, mDateTime, AppName, version,getBaseContext());
        }
    }

}
