package com.maple.uiautomatordemo;

import android.app.UiAutomation;
import android.support.test.espresso.core.deps.guava.base.Strings;
import android.support.test.uiautomator.UiAutomatorTestCase;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

/**
 * Created by liu on 11/3/17.
 */

public class UITest extends UiAutomatorTestCase {
    public void testDemo() throws UiObjectNotFoundException {
        getUiDevice().pressHome();
        UiObject Calculator = new UiObject(new UiSelector().text("HelloTalk"));

        Calculator.clickAndWaitForNewWindow();
        UiObject seven = new UiObject(new UiSelector().text("Moments"));
        seven.click();
        UiObject plus = new UiObject(new UiSelector().resourceId("com.hellotalk:id/action_edit"));
        plus.click();
        UiObject one = new UiObject(new UiSelector().resourceId("com.hellotalk:id/btn_emoji"));
        one.click();
        UiObject ones = new UiObject(new UiSelector().resourceId("com.hellotalk:id/item_image"));
        ones.click();

        UiObject result = new UiObject(new UiSelector().text("Post"));
        String s = result.getText().toString();
        result.clickAndWaitForNewWindow(1000);
        UiObject RE = new UiObject(new UiSelector().text("Default"));
        RE.click();

        getUiDevice().pressBack();
    }
    public void testDeo() throws UiObjectNotFoundException {
        getUiDevice().pressBack();
        UiObject RE = new UiObject(new UiSelector().text("Default"));
        RE.click();
    }
}

