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

/**
 * Created by liu on 16/3/17.
 */

public class ChatTestCase {
    private static final int LAUNCH_TIMEOUT = 5000;
    private int count = 0;
    public void sendMessage(UiDevice mDevice){

        tool.slee(3000);
        UiObject Profile = mDevice.findObject(new UiSelector().text("Profile"));
        if (!Profile.exists()){
            mDevice.pressBack();
            mDevice.pressBack();
            mDevice.pressBack();
            if (count<2){
                count++;
                sendMessage(mDevice);
            }
        }
        try {
            Profile.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        tool.slee(1000);
        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE,"following_layout")),LAUNCH_TIMEOUT).click();
        mDevice.wait(Until.findObjects(By.res("com.hellotalk:id/head")),LAUNCH_TIMEOUT).get(1).click();
        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE,"profile_btn_msg")),2000).click();
        tool.slee(2000);
        mDevice.wait(Until.findObject(By.text("Type a message...")),2000).setText("nihao");
        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE,"btn_voice")).click();
        tool.slee(1000);
        mDevice.waitForIdle();
        tool.CheckeExistsAndClickByText(mDevice,"No thanks");
        tool.assertThatByText(mDevice,"nihao",2000);
        mDevice.pressBack();

        //启动送礼物
        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE,"chatted_msg")),2000).click();
        mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE,"btn_add")),3000).click();
        mDevice.wait(Until.findObject(By.text("Send Gift")),2000).click();
        tool.assertThatByText(mDevice,"Membership",2000);
        mDevice.pressBack();

        //发送贺卡
        mDevice.wait(Until.findObject(By.text("Card")),2000).click();
        tool.clickByText(mDevice,"Send",3000);
        tool.assertThatByText(mDevice,"Happy Birthday",2000);

        // 发送名片
        tool.slee(2000);
        UiObject intrduce = mDevice.findObject(new UiSelector().text("Introduce"));
        if (intrduce.exists()){
            try {
                intrduce.click();
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            tool.clickByID(mDevice,"btn_add",2000);
            tool.clickByText(mDevice,"Introduce",2000);
        }
        tool.slee(LAUNCH_TIMEOUT);
        List<UiObject2> chatted_msg = mDevice.findObjects(By.res("com.hellotalk:id/user_name"));
        String ss = chatted_msg.get(1).getText();
        chatted_msg.get(1).click();
        tool.clickByText(mDevice,"OK",2000);
        tool.assertThatByText(mDevice,ss,2000);

        // 发送表情
        tool.clickByID(mDevice,"btn_emoji",1000);
        tool.clickByID(mDevice,"item_image",2000);

        UiObject2 image_emoji= mDevice.findObject(By.clazz("android.widget.AdapterView"));
        tool.slee(1000);
        image_emoji.getChildren().get(1).click();
        tool.slee(1000);
        tool.clickByID(mDevice,"item_image",2000);
        tool.slee(1000);
        image_emoji.getChildren().get(2).click();
        tool.slee(1000);
        tool.clickByID(mDevice,"item_image",2000);
        tool.slee(1000);
        tool.clickByID(mDevice,"btn_voice",2000);

        //发一张图片
        tool.clickByID(mDevice,"btn_photo",1000);
        tool.CheckeExistsAndClickByText(mDevice,"OK");
        tool.clickByID(mDevice,"media_photo_image",3000);
        tool.clickByID(mDevice,"checkImageView",3000);
        tool.clickByText(mDevice,"Send",1000);

        //发多张图片
        tool.clickByID(mDevice,"btn_photo",5000);
        tool.slee(LAUNCH_TIMEOUT);

        List<UiObject2> photo_check= mDevice.findObjects(By.clazz("android.widget.ImageView"));
        photo_check.get(1).click();
        tool.slee(500);
        photo_check.get(2).click();
        tool.slee(500);
        photo_check.get(4).click();
        tool.slee(500);
        photo_check.get(6).click();
        tool.slee(500);
        photo_check.get(3).click();
        tool.slee(500);
        tool.clickByText(mDevice,"Send",3000);

        // 拍照
        tool.clickByID(mDevice,"btn_capture",500);
        tool.slee(LAUNCH_TIMEOUT);
        mDevice.findObject(By.res("com.android.camera:id/v6_shutter_button_internal")).click();
        mDevice.wait(Until.findObject(By.res("com.android.camera:id/v6_btn_done")),6000).click();
        tool.clickByID(mDevice,"action_ok",LAUNCH_TIMEOUT);

        tool.clickByID(mDevice,"btn_capture",3000);
        tool.slee(2000);
        mDevice.findObject(By.res("com.android.camera:id/v6_shutter_button_internal")).click();
        mDevice.wait(Until.findObject(By.res("com.android.camera:id/v6_btn_cancel")),6000).click();
        mDevice.wait(Until.findObject(By.res("com.android.camera:id/v6_btn_cancel")),6000).click();

        //发送涂鸦
        tool.clickByID(mDevice,"btn_doogle",3000);
        tool.slee(1000);
        int h=UiDevice.getInstance().getDisplayHeight();
        int w=UiDevice.getInstance().getDisplayWidth();
        mDevice.swipe(w/2,(h/2)+200,w/2,(h/2)-200,100);
        tool.clickByID(mDevice,"action_send",3000);

        //发语音
        tool.clickByID(mDevice,"btn_voice",5000);
        UiObject btn_record = mDevice.findObject(new UiSelector().resourceId("btn_record"));
        if(btn_record.exists()){
            try {
                btn_record.click();
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            tool.clickByID(mDevice,"btn_voice",LAUNCH_TIMEOUT);
            tool.clickByID(mDevice,"btn_record",2000);
        }
        tool.slee(10000);
        tool.clickByID(mDevice,"btn_record",2000);

        //使用翻译控件
        tool.slee(1000);
        mDevice.wait(Until.findObject(By.text("Type a message...")),2000).setText("you");
        tool.clickByID(mDevice,"btn_translate",LAUNCH_TIMEOUT);
        tool.slee(5000);
        tool.clickByID(mDevice,"btn_send_result",1000);

    }


}
