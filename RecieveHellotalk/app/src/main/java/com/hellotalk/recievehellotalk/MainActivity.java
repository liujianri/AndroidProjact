package com.hellotalk.recievehellotalk;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static int startCount;
    public static int startTime;
    public static String appname;
    private ActivityManager am;
    private final static String HELLOTAK_PACKAGE = "com.hellotalk";
    private EditText editText ,editText2 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.           .killprocess");
        registerReceiver(receiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public int findPIDbyPackageName(String packagename) {
        int result = -1;

        if (am != null) {
            for (ActivityManager.RunningAppProcessInfo pi : am.getRunningAppProcesses()){
                if (pi.processName.equalsIgnoreCase(packagename)) {
                    result = pi.pid;
                }
                if (result != -1) break;
            }
        } else {
            result = -1;
        }

        return result;
    }

    public boolean isPackageRunning(String packagename) {
        return findPIDbyPackageName(packagename) != -1;
    }

    public boolean killPackageProcesses(String packagename) {
        boolean result = false;

        if (am != null) {
            am.killBackgroundProcesses(packagename);
            result = !isPackageRunning(packagename);
        } else {
            result = false;
        }

        return result;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("HellotalkReciever","kill process");
            killPackageProcesses(HELLOTAK_PACKAGE);
        }
    };


    public void startTest(View v) {
        startTime = Integer.parseInt(editText.getText().toString());
        appname = editText2.getText().toString();
        startCount = 0;
        Intent app = new Intent();
        app.setComponent(new ComponentName("com.hellotalk", "com.hellotalk.ui.LoginAnim"));
        app.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(app);
    }
}
