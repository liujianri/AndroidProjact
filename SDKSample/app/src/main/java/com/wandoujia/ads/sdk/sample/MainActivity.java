package com.wandoujia.ads.sdk.sample;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wandoujia.ads.sdk.Ads;
import com.wandoujia.ads.sdk.models.AdConfig;
import com.wandoujia.ads.sdk.models.AdInitResponse;


public class MainActivity extends Activity {
  private static final String APP_ID = "100020115";
  private static final String SECRET_KEY = "1fa2aef2b4ec33b43c267c64d0166755";
  private static final String BANNER = "30d7fd51a4c3ede0c01c8b1c7c84fea7";
  private static final String INTERSTITIAL = "2d1ccfaaab9a09d4be8eec7d86ccca77";
  private static final String APP_WALL = "66caff24c98802b40dbb014bbf39f0be";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    new AsyncTask<Void, Void, Boolean>() {
      @Override
      protected Boolean doInBackground(Void... params) {
        try {
          Ads.init(MainActivity.this, APP_ID, SECRET_KEY);
          return true;
        } catch (Exception e) {
          Log.e("ads-sample", "error", e);
          return false;
        }
      }

      @Override
      protected void onPostExecute(Boolean success) {
        final ViewGroup container = (ViewGroup) findViewById(R.id.banner_container);

        if (success) {
          /**
           * pre load
           */
          Ads.preLoad(BANNER, Ads.AdFormat.banner);
          Ads.preLoad(INTERSTITIAL, Ads.AdFormat.interstitial);
          Ads.preLoad(APP_WALL, Ads.AdFormat.appwall);

          /**
           * add ad views
           */
          View bannerView = Ads.createBannerView(MainActivity.this, BANNER);
          container.addView(bannerView, new ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.WRAP_CONTENT
          ));

          Button btI = new Button(MainActivity.this);
          btI.setText("interstitial");
          btI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Ads.showInterstitial(MainActivity.this, INTERSTITIAL);
            }
          });
          container.addView(btI);

          Button btW = new Button(MainActivity.this);
          btW.setText("app wall");
          btW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Ads.showAppWall(MainActivity.this, APP_WALL);
            }
          });
          container.addView(btW);
        } else {
          TextView errorMsg = new TextView(MainActivity.this);
          errorMsg.setText("init failed");
          container.addView(errorMsg);
        }
      }
    }.execute();
  }
}
