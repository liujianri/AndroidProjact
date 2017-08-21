package com.hellotalk.recievehellotalk;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by android-dev on 14/12/16.
 */

public class HellotalkReciever extends BroadcastReceiver {

    private Handler handler;
    private WriteCSV write = new WriteCSV();
    private boolean isTime= true;
    private String    mDateTime;

    @Override
    public void onReceive(final Context context, Intent intent) {

        long time = intent.getLongExtra("time", 0);
        Log.d("HellotalkReciever", "start app cost time:" + time);
        Intent intent1 = new Intent("com.hellotalk.kill.myself");
        wirt(String.valueOf(time));
        context.sendBroadcast(intent1);
        if (handler == null) {
            handler = new Handler();
        }

        if (++MainActivity.startCount < MainActivity.startTime) {
            Log.d("HellotalkReciever", "restart count:" + MainActivity.startCount);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent app = new Intent();
                    app.setComponent(new ComponentName("com.hellotalk", "com.hellotalk.ui.LoginAnim"));
                    app.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(app);
                }
            }, 10000);
        }

    }


    private void wirt(String TotalFlow){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        Date curDate = new Date(System.currentTimeMillis());
        String hms = formatter.format(curDate);
        write.WriteCSV(hms,TotalFlow,MainActivity.appname);
    }
}
