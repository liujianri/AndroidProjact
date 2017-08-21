package com.maple.hellotalktestcase;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;

import android.support.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;




/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class StartTest {

    public static final String BASIC_SAMPLE_PACKAGE = "com.hellotalk";

    private static final int LAUNCH_TIMEOUT = 5000;


    private UiDevice mDevice;

    @Before
    public void startApp(){

        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        mDevice.pressHome();
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
        context.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }
    //@Test
    public void loginTest() {
        LoginTestCase loginTestCase = new LoginTestCase();
        loginTestCase.Login(mDevice);
    }
    @Test
    public void GroupTest(){
        GroupTestCase group = new GroupTestCase();
        group.bulidGroup(mDevice);
        group.sendMessage(mDevice);
        group.GruopSettingTestCase(mDevice);
    }
    //@Test
    public void Chattest(){
        ChatTestCase chatTestCase = new ChatTestCase();
        chatTestCase.sendMessage(mDevice);
    }


}
