package com.maple.com.gobang;

import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import android.widget.Toast;

import com.qq.e.ads.appwall.APPWall;


public class MainActivity extends AppCompatActivity {

    private Button btnDoubleMode;
    private Button btnBluetoothMode;
    private Button btnHumanBattle;
    private Button btnAbout;
    private Button btnAd;
    private boolean showd=false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDoubleMode = (Button) findViewById(R.id.btnDoubleMode);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        btnBluetoothMode = (Button) findViewById(R.id.btnBluetoothMode);
        btnHumanBattle = (Button) findViewById(R.id.btnHumanBattle);
        btnAbout = (Button) findViewById(R.id.btnAbout);
//        btnAd = (Button) findViewById(R.id.btnAd);


        btnHumanBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HumanBattle.class);
                startActivity(intent);
            }
        });

        btnDoubleMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DoubleMode.class);
                startActivity(intent);
            }
        });
        btnBluetoothMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,JoinGameList.class);
                startActivity(intent);
            }
        });
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,About.class);
                startActivity(intent);
            }
        });
////        btnAd.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                APPWall wall = new APPWall(MainActivity.this, data.APPID, data.APPWallPosID);
////                wall.setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
////                wall.doShowAppWall();
////
//          }
//        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
