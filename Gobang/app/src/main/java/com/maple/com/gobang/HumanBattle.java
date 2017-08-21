package com.maple.com.gobang;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;


import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.ads.interstitial.AbstractInterstitialADListener;
import com.qq.e.ads.interstitial.InterstitialAD;



/**
 * Created by 80087647 on 2016-04-20.
 */
public class HumanBattle extends Activity {
    private HumanBattleView humanBattleView;
    private Switch mSwitch;
    private boolean show = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.human_battle);
        mSwitch = (Switch) findViewById(R.id.switch1);
        mSwitch.setChecked(true);
        humanBattleView = (HumanBattleView) findViewById(R.id.humanBattleView);
        findViewById(R.id.startAgain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                humanBattleView.startAgain();
                humanBattleView.clickSound();


            }
        });
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                humanBattleView.setSoundst(isChecked);

            }
        });
        findViewById(R.id.btnRetract).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                humanBattleView.retract();
                humanBattleView.clickSound();
            }
        });
        gtAd();
    }
    BannerView bv;
    private void gtAd (){
        RelativeLayout bad = (RelativeLayout) findViewById(R.id.ada);
        bv = new BannerView(HumanBattle.this, ADSize.BANNER, data.APPID, data.BannerPosID);
        bv.setRefresh(30);
        bv.setADListener(new AbstractBannerADListener() {

            @Override
            public void onNoAD(int arg0) {
                Log.i("AD_DEMO", "BannerNoAD，eCode=" + arg0);
            }

            @Override
            public void onADReceiv() {
                Log.i("AD_DEMO", "ONBannerReceive");
            }
        });
        bad.addView(bv);
        bv.loadAD();
        show = true;
    }
    InterstitialAD iad;
    private InterstitialAD getIAD() {
        if (iad == null) {
            iad = new InterstitialAD(HumanBattle.this, data.APPID, data.InterteristalPosID);
        }
        return iad;
    }

    private void showAD() {
        getIAD().setADListener(new AbstractInterstitialADListener() {

            @Override
            public void onNoAD(int arg0) {
                Log.i("AD_DEMO", "LoadInterstitialAd Fail:" + arg0);
            }

            @Override
            public void onADReceive() {
                Log.i("AD_DEMO", "onADReceive");
                iad.show();
            }
        });
        iad.loadAD();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if (show) {
                showAD();
                show = false;
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

