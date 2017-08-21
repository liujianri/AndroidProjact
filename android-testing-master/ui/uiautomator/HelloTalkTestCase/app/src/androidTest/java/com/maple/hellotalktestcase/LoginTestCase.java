package com.maple.hellotalktestcase;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import java.util.List;

import static com.maple.hellotalktestcase.StartTest.BASIC_SAMPLE_PACKAGE;
import static java.lang.Thread.sleep;

/**
 * Created by liu on 16/3/17.
 */

public class LoginTestCase {

    public  void Login(UiDevice mDevice){
        // Context of the app under test.
        tool.CheckeExistsAndClickByID(mDevice,"com.hellotalk:id/sign_in");

        mDevice.wait(Until.findObject(By.text("Email")),3000).setText("test10040@ttt.com");
        List<UiObject2> lisCollect = mDevice.findObjects(By.res("com.hellotalk:id/inputtext"));
        lisCollect.get(1).setText("test1234");
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE,"btn_login")).click();
        try {
            sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UiObject got_it = mDevice.findObject(new UiSelector().text("Got it"));
        if (got_it.exists()){
            try {
                got_it.click();
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
        }
        mDevice.wait(Until.findObject(By.text("Search")),1000).click();
        int h= UiDevice.getInstance().getDisplayHeight();
        int w=UiDevice.getInstance().getDisplayWidth();
        mDevice.swipe(w/2,(h/2)+100,w/2,(h/2)-100,50);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mDevice.wait(Until.findObject(By.text("Profile")),10000).click();
    }

}
