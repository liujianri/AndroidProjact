package com.maple.changename;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private Button btnStart,btnMov;
    private final static String pic = "/mnt/sdcard/pic/";
    private final static String mov = "/mnt/sdcard/mov/";
    private  String houzhui ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = (Button) findViewById(R.id.button);
        btnMov = (Button) findViewById(R.id.button2);


        btnMov .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rename( mov,".mp4");
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rename( pic,".jpg");
            }
        });
    }

    private int rename(String toFile,String t){

        File[] currentFiles;
        File root = new File(toFile);
        //如同判断SD卡是否存在或者文件是否存在
        //如果不存在则 return出去
        if(!root.exists())
        {
            root.mkdirs();
            Toast.makeText(MainActivity.this, "-1", Toast.LENGTH_SHORT).show();
            return -1;
        }
        //如果存在则获取当前目录下的全部文件 填充数组
        currentFiles = root.listFiles();

        File targetDir = new File(pic);
        //创建目录
        if(!targetDir.exists())
        {
            targetDir.mkdirs();
        }

        for (int i = 0; i <currentFiles.length ; i++) {
            String Oldname = getFileNameNoEx(currentFiles[i].getName());
            Log.e("Oldname",Oldname);


            if (getExtensionName(currentFiles[i].getName()).equals("txt")){
                houzhui = t;
            }else {
                houzhui = ".txt";
            }
            //File oleFile = new File(currentFiles[i] ); //要重命名的文件或文件夹
            File newFile = new File(targetDir+"/"+Oldname+houzhui );  //重命名为zhidian1
            //oleFile.renameTo(newFile);  //执行重命名
            currentFiles[i].renameTo(newFile);

        }

        return 0;

    }

    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

}
