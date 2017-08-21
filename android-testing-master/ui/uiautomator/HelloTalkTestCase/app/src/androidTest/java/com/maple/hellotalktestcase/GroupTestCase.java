package com.maple.hellotalktestcase;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.util.Log;

import java.util.List;

import static com.maple.hellotalktestcase.StartTest.BASIC_SAMPLE_PACKAGE;


/**
 * Created by liu on 16/3/17.
 */

public class GroupTestCase {

    private int TWO_TIMEOUT=2000;
    private int Five_TIMEOUT=5000;
    private int ONE_TIMEOUT = 1000;
    private int count = 0;

    public void bulidGroup(UiDevice mDevice) {
        tool.slee(Five_TIMEOUT);
        UiObject Talks = mDevice.findObject(new UiSelector().text("Talks"));
        if (Talks.exists()){
            try {
                Talks.click();
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
            tool.clickByID(mDevice,"icon",TWO_TIMEOUT);
            tool.clickByText(mDevice,"Group Chat",TWO_TIMEOUT);
            tool.slee(TWO_TIMEOUT);
            List<UiObject2> name = mDevice.findObjects(By.res(BASIC_SAMPLE_PACKAGE,"name"));
            name.get(0).click();
            tool.slee(500);
            name.get(3).click();
            tool.slee(500);
            tool.clickByText(mDevice,"Partner",ONE_TIMEOUT);
            tool.slee(ONE_TIMEOUT);
            List<UiObject2> select_btn = mDevice.findObjects(By.res(BASIC_SAMPLE_PACKAGE,"select_btn"));
            if (select_btn.size()!=0){
                select_btn.get(0).click();
                tool.slee(500);
                select_btn.get(1).click();
                tool.slee(500);
                select_btn.get(2).click();
                tool.slee(500);
                select_btn.get(4).click();
            }
            tool.clickByID(mDevice,"action_ok",ONE_TIMEOUT);
            tool.slee(Five_TIMEOUT);
            UiObject Group = mDevice.findObject(new UiSelector().text("Group Chat"));
            if (Group.exists()){
                tool.assertThatByText(mDevice,"Group Chat",Five_TIMEOUT);
            }else {
                mDevice.pressBack();
                tool.assertThatByText(mDevice,"Group Chat",Five_TIMEOUT);
            }

        }else {
            mDevice.pressBack();
            mDevice.pressBack();
            mDevice.pressBack();
            if (count<2){
                count++;
                bulidGroup(mDevice);
            }
        }
    }
    public void GruopSettingTestCase(UiDevice mDevice){
        tool.clickByID(mDevice,"chat_setting",TWO_TIMEOUT);
        tool.slee(TWO_TIMEOUT);
        int h=UiDevice.getInstance().getDisplayHeight();
        int w=UiDevice.getInstance().getDisplayWidth();
        //加人
        UiObject2 View_grid = mDevice.findObject(By.clazz("android.widget.GridView"));
        tool.slee(TWO_TIMEOUT);
        List<UiObject2>  View_gridChild = View_grid.getChildren();
        View_gridChild.get(View_gridChild.size()-2).click();
        tool.slee(TWO_TIMEOUT);
        mDevice.drag(w/2,(h/2)+300,w/2,(h/2)-300,100);
        tool.slee(TWO_TIMEOUT);
        List<UiObject2> name = mDevice.findObjects(By.res("com.hellotalk:id/name"));
        name.get(4).click();
        tool.slee(ONE_TIMEOUT);
        name.get(3).click();
        tool.slee(ONE_TIMEOUT);
        name.get(2).click();
        tool.slee(ONE_TIMEOUT);
        tool.clickByID(mDevice,"action_ok",TWO_TIMEOUT);
        tool.slee(Five_TIMEOUT);

        //踢人
        tool.clickByID(mDevice,"chat_setting",TWO_TIMEOUT);
        tool.slee(ONE_TIMEOUT);
        UiObject2 View_grid2 = mDevice.findObject(By.clazz("android.widget.GridView"));
        List<UiObject2>   View_gridChild2 = View_grid2.getChildren();
        View_gridChild2.get(View_gridChild2.size()-1).click();
        tool.slee(TWO_TIMEOUT);
        List<UiObject2> delete_action = mDevice.findObjects(By.res("com.hellotalk:id/delete_action"));
        delete_action.get(1).click();

        //修改群名字
        tool.slee(TWO_TIMEOUT);
        tool.clickByID(mDevice,"room_name_layout",TWO_TIMEOUT);
        tool.setText(mDevice,"name","Automated test group");
        tool.clickByID(mDevice,"action_ok",ONE_TIMEOUT);
        tool.slee(Five_TIMEOUT);
        tool.assertThatByText(mDevice,"Automated test group",TWO_TIMEOUT);

        //二维码界面
        tool.clickByID(mDevice,"group_qrcode",TWO_TIMEOUT);
        tool.slee(ONE_TIMEOUT);
        tool.assertThatByText(mDevice,"Automated test group",TWO_TIMEOUT);
        mDevice.findObject(By.clazz("android.widget.ImageView")).click();

        tool.clickByText(mDevice,"Save as image",ONE_TIMEOUT);
        tool.slee(ONE_TIMEOUT);
        mDevice.findObject(By.clazz("android.widget.ImageView")).click();
        tool.slee(1000);
        tool.clickByText(mDevice,"Scan QR Code",ONE_TIMEOUT);
        tool.slee(Five_TIMEOUT);
        mDevice.pressBack();
        tool.slee(ONE_TIMEOUT);
        mDevice.findObject(By.clazz("android.widget.ImageView")).click();
        tool.clickByText(mDevice,"Share",ONE_TIMEOUT);
        tool.slee(Five_TIMEOUT);
        tool.clickByText(mDevice,"hellotalk",ONE_TIMEOUT);
        tool.slee(Five_TIMEOUT);
        tool.clickByText(mDevice,"Partner",ONE_TIMEOUT);
        tool.slee(ONE_TIMEOUT);
        tool.clickByID(mDevice,"contactitem_avatar_iv",ONE_TIMEOUT);
        tool.slee(ONE_TIMEOUT);

        tool.slee(ONE_TIMEOUT);
        tool.CheckeExistsAndClickByText(mDevice,"No thanks");

        tool.clickByID(mDevice,"imgMsg",ONE_TIMEOUT);
        tool.slee(ONE_TIMEOUT);
        mDevice.findObject(By.clazz("android.widget.ImageView")).click();

        tool.clickByText(mDevice,"Save",ONE_TIMEOUT);

        tool.slee(Five_TIMEOUT);
        mDevice.pressBack();
        mDevice.pressBack();

        tool.clickByText(mDevice,"Automated test group",ONE_TIMEOUT);
        tool.slee(ONE_TIMEOUT);
        tool.clickByID(mDevice,"chat_setting",TWO_TIMEOUT);

        //修改群公告

        tool.clickByText(mDevice,"Group Notice",ONE_TIMEOUT);
        tool.slee(TWO_TIMEOUT);
        tool.setText(mDevice,"et_content", "This is automated testing");
        tool.slee(ONE_TIMEOUT);
        tool.clickByID(mDevice,"action_ok",ONE_TIMEOUT);
        tool.clickByText(mDevice,"Post",TWO_TIMEOUT);
        tool.slee(Five_TIMEOUT);
        tool.assertThatByText(mDevice,"This is automated testing",TWO_TIMEOUT);

        //群管理
            //群聊确认，群管理员
        tool.clickByID(mDevice,"manage_options_layout",TWO_TIMEOUT);
        tool.clickByID(mDevice,"switch_btn",TWO_TIMEOUT);
        tool.clickByText(mDevice,"Set the Administrator",ONE_TIMEOUT);
        tool.slee(TWO_TIMEOUT);
        List<UiObject2> select_btn = mDevice.findObjects(By.res(BASIC_SAMPLE_PACKAGE,"select_btn"));
        if (select_btn.size()>=2){
            tool.slee(ONE_TIMEOUT);
            select_btn.get(0).click();
            tool.slee(ONE_TIMEOUT);
            select_btn.get(1).click();

            tool.clickByID(mDevice,"action_ok",ONE_TIMEOUT);
            tool.slee(ONE_TIMEOUT);
            tool.clickByText(mDevice,"Set the Administrator",ONE_TIMEOUT);

            tool.clickByID(mDevice,"delete",ONE_TIMEOUT);
            tool.clickByID(mDevice,"action_ok",TWO_TIMEOUT);
        }else {
            mDevice.pressBack();
        }

        // 群主转让
        tool.slee(TWO_TIMEOUT);
        tool.clickByText(mDevice,"Ownership Transfer",ONE_TIMEOUT);
        tool.clickByID(mDevice,"contactitem_avatar_iv",ONE_TIMEOUT);
        tool.clickByText(mDevice,"OK", TWO_TIMEOUT);
        tool.slee(Five_TIMEOUT);

        UiObject2 profile_scrollview = mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE,"profile_scrollview"));
        profile_scrollview.scroll(Direction.DOWN,0.3f,100);

        //Notificatins switch
        tool.slee(ONE_TIMEOUT);
        tool.clickByID(mDevice,"new_message_alerts",ONE_TIMEOUT);
        tool.slee(Five_TIMEOUT);
        tool.clickByID(mDevice,"new_message_alerts",ONE_TIMEOUT);
        tool.slee(Five_TIMEOUT);

        //dont receive free call switch
        tool.clickByID(mDevice,"voip_allow",ONE_TIMEOUT);
        tool.slee(Five_TIMEOUT);
        tool.clickByID(mDevice,"voip_allow",ONE_TIMEOUT);
        tool.slee(Five_TIMEOUT);

        //put on top of talk lis switch
        tool.clickByID(mDevice,"chat_top",ONE_TIMEOUT);
        tool.slee(Five_TIMEOUT);
        tool.clickByID(mDevice,"chat_top",ONE_TIMEOUT);
        tool.slee(Five_TIMEOUT);

        profile_scrollview.scroll(Direction.DOWN,0.8f,200);
        // chanage My alias
        tool.slee(ONE_TIMEOUT);
        tool.clickByID(mDevice,"my_nickname_in_group_layout",ONE_TIMEOUT);

        tool.setText(mDevice,"name","I am a test robot");
        tool.slee(TWO_TIMEOUT);
        tool.clickByID(mDevice,"action_ok",ONE_TIMEOUT);
        tool.slee(Five_TIMEOUT);
        UiObject alias = mDevice.findObject(new UiSelector().text("My Alias"));
        if (!alias.exists()){
            tool.slee(10000);
        }
        tool.assertThatByText(mDevice,"I'm a test robot",TWO_TIMEOUT);

        //display member alias switch
        tool.clickByID(mDevice,"display_group_member_nickname",ONE_TIMEOUT);

        tool.clickByID(mDevice,"display_group_member_nickname",ONE_TIMEOUT);


        // chat files
        tool.clickByID(mDevice,"chatfile_options_layout",ONE_TIMEOUT);
        tool.slee(ONE_TIMEOUT);
        tool.clickByID(mDevice,"media_photo_image1",ONE_TIMEOUT);
        tool.slee(TWO_TIMEOUT);
        mDevice.pressBack();

        tool.clickByID(mDevice,"action_ok",TWO_TIMEOUT);

        tool.clickByID(mDevice,"checkImage1",ONE_TIMEOUT);

        tool.clickByID(mDevice,"checkImage2",ONE_TIMEOUT);

        tool.clickByID(mDevice,"album_btn_download",ONE_TIMEOUT);
        tool.slee(TWO_TIMEOUT);
        tool.clickByID(mDevice,"album_btn_delete",ONE_TIMEOUT);

        tool.clickByText(mDevice,"OK",ONE_TIMEOUT);
        tool.slee(ONE_TIMEOUT);
        tool.clickByID(mDevice,"action_ok",TWO_TIMEOUT);

        tool.clickByID(mDevice,"action_ok",TWO_TIMEOUT);

        tool.clickByID(mDevice,"checkImage1",ONE_TIMEOUT);

        tool.clickByID(mDevice,"checkImage2",ONE_TIMEOUT);

        tool.clickByID(mDevice,"album_btn_send",ONE_TIMEOUT);
        tool.slee(TWO_TIMEOUT);
        tool.clickByText(mDevice,"自动化测试群",TWO_TIMEOUT);

        tool.clickByText(mDevice,"OK",ONE_TIMEOUT);
        tool.slee(ONE_TIMEOUT);
        tool.clickByID(mDevice,"chat_setting",TWO_TIMEOUT);
        tool.slee(ONE_TIMEOUT);

        //群聊记录
        profile_scrollview.scroll(Direction.DOWN,0.99f,200);
        tool.slee(ONE_TIMEOUT);
        tool.clickByID(mDevice,"search_history_options_layout",ONE_TIMEOUT);

        tool.clickByID(mDevice,"voice_type",ONE_TIMEOUT);

        tool.clickByID(mDevice,"search_edit_clear",ONE_TIMEOUT);


        tool.clickByID(mDevice,"abc_type",ONE_TIMEOUT);

        tool.clickByID(mDevice,"search_edit_clear",ONE_TIMEOUT);


        tool.clickByID(mDevice,"translation_type",ONE_TIMEOUT);
        tool.clickByID(mDevice,"search_edit_clear",ONE_TIMEOUT);

        tool.clickByID(mDevice,"transliteration_type",ONE_TIMEOUT);
        tool.clickByID(mDevice,"search_edit_clear",ONE_TIMEOUT);

        tool.clickByID(mDevice,"search_member_icon",ONE_TIMEOUT);
        tool.clickByID(mDevice,"contactitem_avatar_iv",ONE_TIMEOUT);
        mDevice.pressBack();

        tool.clickByID(mDevice,"search_date_icon",ONE_TIMEOUT);
        mDevice.pressBack();
        mDevice.pressBack();

        //View Chat History
        tool.clickByID(mDevice,"view_history",ONE_TIMEOUT);
        tool.clickByID(mDevice,"7 Days",ONE_TIMEOUT);
        mDevice.pressBack();
        tool.clickByID(mDevice,"30 Days",ONE_TIMEOUT);
        mDevice.pressBack();
        tool.clickByID(mDevice,"View All",ONE_TIMEOUT);
        mDevice.pressBack();
        mDevice.pressBack();


        //Translation Target Language
        tool.clickByID(mDevice,"language_options",ONE_TIMEOUT);
        tool.clickByID(mDevice,"rec_to_layout",ONE_TIMEOUT);
        tool.slee(ONE_TIMEOUT);
        UiObject2 listView_switch = mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE,"listView_switch"));
        listView_switch.scroll(Direction.DOWN,0.7f,100);
        tool.clickByID(mDevice,"language_english_name",ONE_TIMEOUT);
        tool.slee(ONE_TIMEOUT);
        tool.clickByID(mDevice,"sent_to_layout",ONE_TIMEOUT);
        tool.slee(ONE_TIMEOUT);
        UiObject2 listView_switc = mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE,"listView_switch"));
        listView_switc.scroll(Direction.DOWN,0.7f,100);
        tool.clickByID(mDevice,"sent_to_layout",ONE_TIMEOUT);
        tool.slee(ONE_TIMEOUT);
        mDevice.pressBack();

        //Delete conversation
        tool.clickByID(mDevice,"clear_history",ONE_TIMEOUT);
        tool.clickByID(mDevice,"OK",ONE_TIMEOUT);

        //Leave group & Delete Chat
        tool.clickByID(mDevice,"leave_group__delete_chat",ONE_TIMEOUT);
        tool.clickByID(mDevice,"OK",ONE_TIMEOUT);
        tool.slee(TWO_TIMEOUT);
        mDevice.pressBack();
    }

    public void sendMessage(UiDevice mDevice){

        tool.slee(TWO_TIMEOUT);
        for (int i = 0; i <20 ; i++) {
            mDevice.wait(Until.findObject(By.text("Type a message...")),TWO_TIMEOUT).setText("test message ...."+i);
            tool.slee(ONE_TIMEOUT);
            mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE,"btn_voice")).click();
            tool.CheckeExistsAndClickByText(mDevice,"No thanks");
            tool.slee(ONE_TIMEOUT);
        }

        //发语音
        tool.clickByID(mDevice,"btn_voice",Five_TIMEOUT);
        UiObject btn_record = mDevice.findObject(new UiSelector().resourceId("btn_record"));
        if(btn_record.exists()){
            try {
                btn_record.click();
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            tool.clickByID(mDevice,"btn_voice",Five_TIMEOUT);
            tool.clickByID(mDevice,"btn_voice",Five_TIMEOUT);
            tool.slee(TWO_TIMEOUT);
            tool.clickByID(mDevice,"btn_record",TWO_TIMEOUT);
        }
        tool.slee(10000);
        tool.clickByID(mDevice,"btn_record",TWO_TIMEOUT);
        tool.slee(ONE_TIMEOUT);

        //发多张图片
        tool.clickByID(mDevice,"btn_photo",ONE_TIMEOUT);
        tool.CheckeExistsAndClickByText(mDevice,"OK");
        tool.clickByID(mDevice,"media_photo_image",3000);
        tool.clickByID(mDevice,"checkImageView",3000);
        tool.clickByText(mDevice,"Send",ONE_TIMEOUT);

        //发多张图片
        tool.clickByID(mDevice,"btn_photo",Five_TIMEOUT);
        tool.slee(Five_TIMEOUT);

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

    }
}
