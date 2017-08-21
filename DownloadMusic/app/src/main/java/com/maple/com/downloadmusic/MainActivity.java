package com.maple.com.downloadmusic;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnStart;
    private ArrayList<String> arrayList = new ArrayList<>();
    private String FROMPATH;
    private Thread thread;
    private Handler handler;
    private final static String TOPATH = "/mnt/sdcard/DownloadMusic/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart= (Button) findViewById(R.id.btnStart);

         handler =new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what==1){
                    Toast.makeText(MainActivity.this, "下载成功,文件下载在：sdcard/DownloadMusic", Toast.LENGTH_LONG).show();
                }
            }
        };

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FROMPATH = "/mnt/sdcard/qqmusic/oltmp/";
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        copy(FROMPATH, TOPATH);
                    }
                });
                thread.start();

            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FROMPATH = "/mnt/sdcard/kugou/down_c/default/";
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        copy(FROMPATH, TOPATH);
                    }
                });
                thread.start();
            }
        });
    }

    private int copy(String fromFile, String toFile)
    {

        //要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        //如同判断SD卡是否存在或者文件是否存在
        //如果不存在则 return出去
        if(!root.exists())
        {
            return -1;
        }
        //如果存在则获取当前目录下的全部文件 填充数组
        currentFiles = root.listFiles();
        //目标目录
        File targetDir = new File(toFile);
        //创建目录
        if(!targetDir.exists())
        {
            targetDir.mkdirs();
        }
        //遍历要复制该目录下的全部文件
        for(int i= 0;i<currentFiles.length;i++)
        {
            try {
                String name = i+"maple.mp3";
                File file = new File(targetDir+"/"+name);
                InputStream fosfrom = new FileInputStream(currentFiles[i].getPath());
                FileUtils.copyInputStreamToFile(fosfrom, file);
                scanFile(MainActivity.this, "/mnt/sdcard/");
                arrayList.add(name);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        rename();
        handler.sendEmptyMessage(1);
        return 0;
    }

    private void rename(){

        ContentResolver mResolver = getContentResolver();
        Cursor cursor = mResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        int i = 0;
        int cursorCount = cursor.getCount();
        if (cursorCount >0 )
        {
            cursor.moveToFirst();
            while (i < cursorCount)
            {
                String display_name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                Log.e("display_name", display_name);
                if(arrayList.contains(display_name) )
                {
                    File from =new File(TOPATH,display_name) ;
                    File to=new File(TOPATH,tilte+".mp3") ;
                    from.renameTo(to) ;
                    Log.e("name", tilte + ".mp3");
                }
                Log.e("name", "..............................");
                i++;
                cursor.moveToNext();
            }
            cursor.close();
        }
    }
    public void scanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        Log.e("boradcast", filePath);
        context.sendBroadcast(scanIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (thread!=null){
            thread.interrupt();
            thread=null;
        }
    }
}
