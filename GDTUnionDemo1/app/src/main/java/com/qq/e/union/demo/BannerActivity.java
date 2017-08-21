package com.qq.e.union.demo;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;


public class BannerActivity extends Activity implements OnClickListener {

  ViewGroup bannerContainer;
  BannerView bv;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_banner);
    bannerContainer = (ViewGroup) this.findViewById(R.id.bannerContainer);
    bannerContainer.setOnClickListener(this);
    this.findViewById(R.id.btn).setOnClickListener(this);
    this.findViewById(R.id.refreshBanner).setOnClickListener(this);
    this.findViewById(R.id.closeBanner).setOnClickListener(this);
    this.initBanner();
    this.bv.loadAD();
  }

  private void initBanner() {
    this.bv = new BannerView(this, ADSize.BANNER, Constants.APPID, Constants.BannerPosID);
    // 注意：如果开发者的banner不是始终展示在屏幕中的话，请关闭自动刷新，否则将导致曝光率过低。
    // 并且应该自行处理：当banner广告区域出现在屏幕后，再手动loadAD。
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
    bannerContainer.addView(bv);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.refreshBanner:
        doRefreshBanner();
        break;
      case R.id.closeBanner:
        doCloseBanner();
        break;
      case R.id.btn:
        bannerContainer.performClick();
        break;
      case R.id.bannerContainer:
        Toast.makeText(BannerActivity.this, "HHHHH", Toast.LENGTH_SHORT).show();
        clickView(bv, 544f, 304f);
        break;
      default:
        break;
    }
  }



  private void doRefreshBanner() {
    if (bv == null) {
      initBanner();
    }
    bv.loadAD();
  }

  private void doCloseBanner() {
    bannerContainer.removeAllViews();
    bv.destroy();
    bv = null;
  }


  public void clickView(View view,float x,float y)
  {
    long downTime = SystemClock.uptimeMillis();
    System.out.println(x);
    System.out.println(y);
    final MotionEvent downEvent = MotionEvent.obtain(
            downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
    downTime+=10;
    final MotionEvent upEvent = MotionEvent.obtain(
            downTime, downTime, MotionEvent.ACTION_UP, x, y, 0);
    view.onTouchEvent(downEvent);
    view.onTouchEvent(upEvent);
    downEvent.recycle();
    upEvent.recycle();
  }

}
