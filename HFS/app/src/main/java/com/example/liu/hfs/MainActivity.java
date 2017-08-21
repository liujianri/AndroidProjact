package com.example.liu.hfs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private NavView navView;
    private TextView textView;
    private PointView PointView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );//设置全屏
        setContentView(R.layout.activity_main);
        navView = (NavView) findViewById(R.id.nav);
        PointView = (com.example.liu.hfs.PointView) findViewById(R.id.pointview);
        textView = (TextView) findViewById(R.id.textView);
        navView.setOnNavAndSpeedListener(new NavView.OnNavAndSpeedListener() {
            @Override
            public void onNavAndSpeed(float nav, float speed) {
                textView.setText(String.valueOf(nav)+"g"+String.valueOf(speed));
                PointView.setX(nav);
                PointView.setY(speed);
                Log.e("------",nav+"--"+speed+"---");
            }

        });
    }


}
