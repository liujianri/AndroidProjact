package com.maple.timerstart;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    protected int powerLevel = PowerManager.FULL_WAKE_LOCK;

    private Button btnStart,btnCl,btnGbCL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = (Button) findViewById(R.id.button);
        btnCl= (Button) findViewById(R.id.button2);
        btnGbCL= (Button) findViewById(R.id.button3);
        PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock powerlock = pm.newWakeLock(powerLevel, "camera");

        btnCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powerlock.acquire();
            }
        });

        btnGbCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powerlock.release();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyService.class);
                //启动FxService
                Log.e("ttt", "start");
                startService(intent);
            }
        });
    }
}
