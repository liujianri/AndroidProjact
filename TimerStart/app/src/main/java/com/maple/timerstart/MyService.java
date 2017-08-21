package com.maple.timerstart;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Random;

public class MyService extends Service {
    private  boolean Runing=false;
    private int time=5000;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Runing =true;
        new Thread(){
            @Override
            public void run() {
                super.run();
                while(Runing)
                {
                    try {
                        System.out.println("aaaa   "+time);
                        sleep(time);
                        time=testRandom2();
                        System.out.println("tttt   "+time);
                        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.maple.com.gobang");
                        startActivity(LaunchIntent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    private  int testRandom2(){
        int max=2000000;
        int min=600000;
        Random random=new Random();
        return random.nextInt(max)%(max-min+1) + min;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runing =false;
    }
}
