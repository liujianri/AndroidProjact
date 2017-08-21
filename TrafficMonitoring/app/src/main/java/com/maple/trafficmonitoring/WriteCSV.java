package com.maple.trafficmonitoring;

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
    boolean aBoolean=true , bBoolean=true,cBoolean=true,dBoolean=true;
    private String resultFilePath;
    public  void WriteCSV(String QorH, String time, String TotalFlow, String Q , String H, String mDateTime, String appName, String version, Context context){

        try {
            if (android.os.Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED)) {
                // 在4.0以下的低版本上/sdcard连接至/mnt/sdcard，而4.0以上版本则连接至/storage/sdcard0，所以有外接sdcard，/sdcard路径一定存在
                resultFilePath = "/sdcard" + File.separator + "traffic_monitor_"
                        +appName+ mDateTime + ".csv";
                System.out.println("BBB"+resultFilePath);
                // resultFilePath =
                // android.os.Environment.getExternalStorageDirectory() +
                // File.separator + "Emmagee_TestResult_" + mDateTime + ".csv";
            } else {
                resultFilePath = context.getFilesDir().getPath()
                        + File.separator + "traffic_monitor_" + appName+mDateTime
                        + ".csv";
                System.out.println("AAA"+resultFilePath);
            }
            File csv = new File(resultFilePath);
            BufferedWriter bw = new BufferedWriter(new FileWriter(csv, true)); // 附加
            // 添加新的数据行
            if (bBoolean){
                bw.write("应用名称" +","+appName+ "\r\n" + "应用版本" +","+version + "\r\n" + "Android 系统版本" +","+android.os.Build.VERSION.RELEASE+"\r\n" +"手机型号:"+","+android.os.Build.MODEL);
                bw.newLine();
                bBoolean=false;
            }
            if (aBoolean){
                bw.write("运行状态"+ "," + "时间"+ "," + "总流量(B)" + "," + "前台流量(B)" +"," +"后台流量(B)");
                bw.newLine();
                aBoolean=false;
            }
            bw.write(QorH + "," +time + "," + TotalFlow + "," + Q +"," +H);
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
    public  void cpuWriteCSV( String time, String process, String PSS , String cpu, String mDateTime, String appName, String version, Context context){

        try {
            if (android.os.Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED)) {
                // 在4.0以下的低版本上/sdcard连接至/mnt/sdcard，而4.0以上版本则连接至/storage/sdcard0，所以有外接sdcard，/sdcard路径一定存在
                resultFilePath = "/sdcard" + File.separator + "traffic_cpuInfo_"
                        +appName+ mDateTime + ".csv";
                System.out.println("BBB"+resultFilePath);
                // resultFilePath =
                // android.os.Environment.getExternalStorageDirectory() +
                // File.separator + "Emmagee_TestResult_" + mDateTime + ".csv";
            } else {
                resultFilePath = context.getFilesDir().getPath()
                        + File.separator + "traffic_cpuInfo_" + appName+mDateTime
                        + ".csv";
                System.out.println("AAA"+resultFilePath);
            }
            File csv = new File(resultFilePath);
            BufferedWriter bw = new BufferedWriter(new FileWriter(csv, true)); // 附加
            // 添加新的数据行
            if (cBoolean){
                bw.write("应用名称" +","+appName+ "\r\n" + "应用版本" +","+version + "\r\n" + "Android 系统版本" +","+android.os.Build.VERSION.RELEASE+"\r\n" +"手机型号:"+","+android.os.Build.MODEL);
                bw.newLine();
                cBoolean=false;
            }
            if (dBoolean){
                bw.write("时间" + ","+ "进程"+ "," + "占用内存比(%)" + "," + "占用CPU率(%)"  );
                bw.newLine();
                dBoolean=false;
            }
            bw.write(time + "," + process + "," + PSS +"," +cpu);
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
