package com.maple.com.gobang;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.mobads.AppActivity;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button btnDoubleMode;
    private Button btnBluetoothMode;
    private Button btnHumanBattle;
    private Button btnAbout;
   // private AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDoubleMode = (Button) findViewById(R.id.btnDoubleMode);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        btnBluetoothMode = (Button) findViewById(R.id.btnBluetoothMode);
        btnHumanBattle = (Button) findViewById(R.id.btnHumanBattle);
        btnAbout = (Button) findViewById(R.id.btnAbout);

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


    }

    @Override
    protected void onResume() {
//        RelativeLayout yourOriginnalLayout = (RelativeLayout) findViewById(R.id.ad);
//        // setContentView(yourOriginnalLayout);
//        // 代码设置AppSid，此函数必须在AdView实例化前调用
//        // AdView.setAppSid("debug");
//
//        // 设置'广告着陆页'动作栏的颜色主题
//        // 目前开放了七大主题：黑色、蓝色、咖啡色、绿色、藏青色、红色、白色(默认) 主题
//        AppActivity.setActionBarColorTheme(AppActivity.ActionBarColorTheme.ACTION_BAR_WHITE_THEME);
////        另外，也可设置动作栏中单个元素的颜色, 颜色参数为四段制，0xFF(透明度, 一般填FF)DE(红)DA(绿)DB(蓝)
////        AppActivity.getActionBarColorTheme().set[Background|Title|Progress|Close]Color(0xFFDEDADB);
//
//        // 创建广告View
//        String adPlaceId = "2015351"; //  重要：请填上您的广告位ID，代码位错误会导致无法请求到广告
//        adView = new AdView(this, adPlaceId);
//        // 设置监听器
//        adView.setListener(new AdViewListener() {
//            public void onAdSwitch() {
//                Log.w("", "onAdSwitch");
//            }
//
//            public void onAdShow(JSONObject info) {
//                // 广告已经渲染出来
//                Log.w("", "onAdShow " + info.toString());
//            }
//
//            public void onAdReady(AdView adView) {
//                // 资源已经缓存完毕，还没有渲染出来
//                Log.w("", "onAdReady " + adView);
//            }
//
//            public void onAdFailed(String reason) {
//
//                Log.w("", "onAdFailed " + reason);
//            }
//
//            public void onAdClick(JSONObject info) {
//                // Log.w("", "onAdClick " + info.toString());
//
//            }
//
//            @Override
//            public void onAdClose(JSONObject arg0) {
//                Log.w("", "onAdClose");
//            }
//        });
//        // 将adView添加到父控件中(注：该父控件不一定为您的根控件，只要该控件能通过addView能添加广告视图即可)
//        RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT);
//        rllp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        yourOriginnalLayout.addView(adView, rllp);
//
//

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        //adView.destroy();
        super.onDestroy();
    }

}
