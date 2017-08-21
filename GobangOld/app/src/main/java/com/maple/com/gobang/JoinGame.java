package com.maple.com.gobang;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * Created by 80087647 on 2016-05-03.
 */
public class JoinGame extends Activity{
    private JoinGameView joinGameView;
    private Switch mSwitch;
    private String string;
    private AlertDialog alertDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent =getIntent();
        string = intent.getStringExtra("device");
        joinGameView = (JoinGameView) findViewById(R.id.gobangView);
        joinGameView.setAddress(string);

        alertDialog = new AlertDialog.Builder(JoinGame.this).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);

        mSwitch = (Switch) findViewById(R.id.switch1);
        mSwitch.setChecked(true);
        findViewById(R.id.startAgain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGameView.startAgain();
                joinGameView.clickSound();
            }
        });
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                joinGameView.setSoundst(isChecked);
            }
        });
        findViewById(R.id.btnRetract).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGameView.retract();
                joinGameView.sendMessageA("retract");
                joinGameView.clickSound();
            }
        });
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            alertDialog.setMessage(JoinGame.this.getString(R.string.quit_the_game));
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    joinGameView.sendMessageA("end");
                    finish();
                }
            });
            alertDialog.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
