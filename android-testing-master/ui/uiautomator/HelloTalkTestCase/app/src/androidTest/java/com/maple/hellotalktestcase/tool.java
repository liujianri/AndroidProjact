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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by liu on 16/3/17.
 */

public class tool {


    private static final int LAUNCH_TIMEOUT = 5000;

    public static void clickByID(UiDevice mDevice,String btnName,int time){
        slee(1000);
        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, btnName)),time).click();
    }

    public static void clickByText(UiDevice mDevice,String btnName,int time){
        slee(1000);
        mDevice.wait(Until.findObject(By.text(btnName)),time).click();
    }

    public static void CheckeExistsAndClickByID(UiDevice mDevice,String resID){
        UiObject uiObject = mDevice.findObject(new UiSelector().resourceId(resID));
        if (uiObject.exists()){
            try {
                sleep(1000);
                uiObject.click();
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void CheckeExistsAndClickByText(UiDevice mDevice,String text){
        UiObject uiObject = mDevice.findObject(new UiSelector().text(text));
        if (uiObject.exists()){
            try {
                sleep(1000);
                uiObject.click();
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void assertThatByText(UiDevice mDevice,String string,int time){
        UiObject2 UiObiect2 = mDevice.wait(Until.findObject(By.text(string)),time);
        assertThat(UiObiect2.getText(),is(equalTo(string)));
    }

    public static void slee(int i){
        try {
            sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getText(UiDevice mDevice, String s){
        String string = mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE,s)).getText();
        return string;
    }


    public static void setText(UiDevice mDevice,String reID,String text) {
        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE,reID)),2000).setText(text);
    }

}
