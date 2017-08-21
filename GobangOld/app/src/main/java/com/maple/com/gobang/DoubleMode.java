package com.maple.com.gobang;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * Created by 80087647 on 2016-04-15.
 */
public class DoubleMode extends Activity{
    private GobangView gobangView;
    private Switch mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.double_mode);
        mSwitch = (Switch) findViewById(R.id.switch1);

        mSwitch.setChecked(true);
        gobangView = (GobangView) findViewById(R.id.gobangView);
        findViewById(R.id.startAgain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gobangView.startAgain();
                gobangView.clickSound();
            }
        });
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                gobangView.setSoundst(isChecked);
            }
        });
        findViewById(R.id.btnRetract).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gobangView.retract();
                gobangView.clickSound();
            }
        });
    }

}
