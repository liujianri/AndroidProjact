package com.maple.com.gobang;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * Created by 80087647 on 2016-04-28.
 */
public class CreateGame extends Activity{
    private CreateGameView createGameView;
    private Switch mSwitch;
    private AlertDialog alertDialog ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creat_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mSwitch = (Switch) findViewById(R.id.switch1);
        alertDialog = new AlertDialog.Builder(CreateGame.this).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        mSwitch.setChecked(true);

        createGameView = (CreateGameView) findViewById(R.id.gobangView);
        findViewById(R.id.startAgain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGameView.startAgain();
                createGameView.clickSound();
            }
        });
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                createGameView.setSoundst(isChecked);
            }
        });
        findViewById(R.id.btnRetract).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGameView.retract();
                createGameView.sendMessageA("retract");
                createGameView.clickSound();
            }
        });

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            alertDialog.setMessage(getString(R.string.quit_the_game));
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    createGameView.sendMessageA("end");
                    finish();
                }
            });
            alertDialog.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        createGameView.shutdownServer();
        super.onDestroy();
    }
}
