package com.maple.com.gobang;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.mobads.AppActivity;

import org.json.JSONObject;

/**
 * Created by 80087647 on 2016-04-20.
 */
public class HumanBattle extends Activity{
    private HumanBattleView humanBattleView;
    private Switch mSwitch;

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

    }




}
