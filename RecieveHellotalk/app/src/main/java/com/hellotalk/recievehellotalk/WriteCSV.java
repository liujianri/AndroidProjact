package com.hellotalk.recievehellotalk;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by liu on 2/12/16.
 */
public class WriteCSV {
    private String resultFilePath;
    public  void WriteCSV(String time, String TotalFlow,  String appName){

        try {
            if (android.os.Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED)) {
                // 在4.0以下的低版本上/sdcard连接至/mnt/sdcard，而4.0以上版本则连接至/storage/sdcard0，所以有外接sdcard，/sdcard路径一定存在
                resultFilePath = "/sdcard" + File.separator + "traffic_monitor_"
                        +appName+ ".csv";
                System.out.println("BBB"+resultFilePath);
                // resultFilePath =
                // android.os.Environment.getExternalStorageDirectory() +
                // File.separator + "Emmagee_TestResult_" + mDateTime + ".csv";
            } else {
                resultFilePath = "/mnt/sdcard"
                        + File.separator + "traffic_monitor_" + appName
                        + ".csv";
                System.out.println("AAA"+resultFilePath);
            }
            File csv = new File(resultFilePath);
            BufferedWriter bw = new BufferedWriter(new FileWriter(csv, true)); // 附加
            // 添加新的数据行
            bw.write(time + "," + TotalFlow );
            bw.newLine();
            bw.close();

        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            e.printStackTrace();
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            e.printStackTrace();
        }
    }
}
