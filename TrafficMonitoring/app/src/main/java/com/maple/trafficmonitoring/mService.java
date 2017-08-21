package com.maple.trafficmonitoring;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by liu on 6/12/16.
 */
public class mService extends Service {
    private static final String TAG="Test";
    LinearLayout mFloatLayout;
    WindowManager.LayoutParams wmParams ;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager ;
    Button mFloatView;
    private TextView text,text1;
    int startX = 0;
    int startY = 0;
    public float rx = 0;
    public float ry = 0;
    public boolean isClick = true,BtnIsClick=true,saveCSV=false,isTime=true;
    private Handler handler = new Handler();
    private  int pid=0 ;
    private String pk,time,testTime, AppName, version;
    private int su =0,cont = 0,timecon=1;
    private long CTms=0;
    private List<String> list=new LinkedList<String>();
    private CpuInfo cpuInfo= new CpuInfo();
    private WriteCSV write= new WriteCSV();
    private String    mDateTime;
    @Override
    //Service时被调用
    public void onCreate() {
        super.onCreate();
        createFloatView(0,0);

    }
    private Runnable task = new Runnable() {

        public void run() {
            dataRefresh();
            if (mFloatLayout != null) {
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
            }
            CTms = timecon*1000*Integer.parseInt(time);
            timecon = timecon+1;
            handler.postDelayed(this, 1000*Integer.parseInt(time));
            if (!testTime.equals(getString(R.string.timeless))){
                int lg = Integer.parseInt(testTime);
                System.out.println(lg*60*60*1000+"  "+CTms);
                if (lg*60*60*1000 <= CTms){
                    stop();
                    text.setText("测试结束");
                    text1.setText(" ");
                }
            }

        }
    };
    private  long pidMemory;
    private String processMemory,Tote,ToCpu;
    private void dataRefresh(){
        if (cont!=0) {
            Tote="内存占用:";
            ToCpu="CPU占用:";
            for (su = 0; su < cont; su++) {
                Log.e("su", String.valueOf(su));
                String roomName= list.get(su);
                String[] s = divideRoomName(roomName);
                pid = Integer.parseInt(s[0]);
                String procName = s[1];
                pidMemory = getPidMemorySize(pid, getBaseContext()) * 1024;
                processMemory = procName+" : "+bytes2kb(pidMemory);
                Tote = Tote+"\n"+processMemory;

                String cup = cpuInfo.getProcessCpuRate(pid);
                ToCpu = ToCpu+"\n"+procName+" : "+cup;

                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                String hms = formatter.format(CTms);
                writeData(hms,procName, String.valueOf(bytes2kb(pidMemory)),cup);
            }

            System.out.println( Tote);
            if (Tote != null) {
                text.setText(Tote);
                text1.setText(ToCpu);
            }
        }
    }

    public static String bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        if (returnValue > 1)
            return (returnValue + "MB");
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        return (returnValue + "KB");
    }


    public String[] divideRoomName(String roomName) {
        String[] name_Id = new String[2];
        if (roomName.length() > 0) {
            String[] strarray=roomName.split("[|]");
            for (int i = 0; i < strarray.length; i++){
                name_Id[i] = strarray[i];
            }
            return name_Id;
        }
        return null;

    }



    private void getlist(String packageName){
        try {
            Process p = Runtime.getRuntime().exec("ps");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(packageName)) {
                        line = line.trim();
                        String[] splitLine = line.split("\\s+");
                        if (splitLine[splitLine.length - 1].contains(packageName)) {
                            Log.i("aaaa", splitLine[1] + splitLine[splitLine.length - 1]);
                            list.add(splitLine[1] + "|" + splitLine[splitLine.length - 1]);
                        }
                }
            }
            cont = list.size();
            Log.e("con", String.valueOf(cont));
            if (list!=null){
                handler.postDelayed(task,1000);
                mFloatView.setClickable(true);
            }else {
                Toast.makeText(mService.this, "未找到进程,请启动应用后开始测试", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    public int getPidMemorySize(int pid, Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int[] myMempid = new int[] { pid };
        Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(myMempid);
        memoryInfo[0].getTotalSharedDirty();
        int memSize = memoryInfo[0].getTotalPss();
        return memSize;
    }

    private void createFloatView(int fx,int fy){

        wmParams = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        Log.i(TAG, "mWindowManager--->" + mWindowManager);
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity

        wmParams.x = fx;
        wmParams.y = fy;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

         /*// 设置悬浮窗口长宽数据
        wmParams.width = 200;
        wmParams.height = 80;*/

        final LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.floating, null);
        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        //浮动窗口按钮
        mFloatView = (Button) mFloatLayout.findViewById(R.id.btnStop);
        text = (TextView) mFloatLayout.findViewById(R.id.textView9);
        text1 = (TextView) mFloatLayout.findViewById(R.id.textView10);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth()/2);
        Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight() / 2);

        //设置监听浮动窗口的触摸移动

        mFloatLayout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    // TODO Auto-generated method stub
                    case MotionEvent.ACTION_DOWN:
                        rx = event.getRawX();
                        ry = event.getRawY() - 25;
                        Log.e("w X", String.valueOf(rx));
                        Log.e("w Y" , String.valueOf(ry));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs(rx-event.getRawX())>=30 || Math.abs(ry-event.getRawY())>=30){

                            //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                            wmParams.x = (int) event.getRawX() - mFloatLayout.getMeasuredWidth() / 2;
                            //减25为状态栏的高度
                            startX = (int) event.getRawX() - mFloatLayout.getMeasuredWidth() / 2;
                            startY = (int)event.getRawY() - mFloatLayout.getMeasuredHeight() / 2 - 25;
                            wmParams.y = (int) event.getRawY() - mFloatLayout.getMeasuredHeight() / 2 - 25;
                            //刷新
                            mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                            isClick =false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e("w", "up");
                        if (isClick) {

                            Log.e("w", "start big float");
                        }else {
                            isClick = !isClick;
                        }
                        break;
                    default:
                        break;
                }
                return false;  //此处必须返回false，否则OnClickListener获取不到监听
            }
        });


        mFloatView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    // TODO Auto-generated method stub
                    case MotionEvent.ACTION_DOWN:
                        rx = event.getRawX();
                        ry = event.getRawY() - 25;
                        Log.e("w X", String.valueOf(rx));
                        Log.e("w Y" , String.valueOf(ry));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs(rx-event.getRawX())>=30 || Math.abs(ry-event.getRawY())>=30){

                            //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                            wmParams.x = (int) event.getRawX() - mFloatLayout.getMeasuredWidth() / 2;
                            //减25为状态栏的高度
                            startX = (int) event.getRawX() - mFloatLayout.getMeasuredWidth() / 2;
                            startY = (int)event.getRawY() - mFloatLayout.getMeasuredHeight() / 2 - 25;
                            wmParams.y = (int) event.getRawY() - mFloatLayout.getMeasuredHeight() / 2 - 25;
                            //刷新
                            mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                            BtnIsClick =false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e("w", "up");
                        if (BtnIsClick) {
                            removeSmallFloat();
                            Log.e("w", "stop test");
                        }else {
                            BtnIsClick = !BtnIsClick;
                        }
                        break;
                    default:
                        break;
                }
                return false;  //此处必须返回false，否则OnClickListener获取不到监听
            }
        });

    }

    public void removeSmallFloat(){
        if(mFloatLayout != null)
        {
            //移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
           stop();
        }

    }

    private void stop(){
        Toast.makeText(mService.this, "测试结束", Toast.LENGTH_SHORT).show();
        handler.removeCallbacks(task);
        stopSelf();
    }

    @Override
    //当调用者使用startService()方法启动Service时，该方法被调用
    public void onStart(Intent intent, int startId)
    {
        pk = intent.getExtras().getString("packageName");
        time = intent.getExtras().getString("time");
        testTime = intent.getExtras().getString("testTime");
        saveCSV = intent.getExtras().getBoolean("saveCSV");
        AppName = intent.getExtras().getString("AppName");
        version = intent.getExtras().getString("version");
        Log.e("getExtras", pk+"--"+time+"--"+testTime+"--"+saveCSV+"--"+AppName+"--"+version);

        if (pid==0){
            Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(pk);
            startActivity(LaunchIntent);
            new Thread(){
                public void run(){
                    try {
                        System.out.println("abc");
                        mFloatView.setClickable(false);
                        sleep(10000);
                        System.out.println("abcd");
                        getlist(pk);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        }

        super.onStart(intent, startId);
    }

    @Override
    //当Service不在使用时调用
    public void onDestroy()
    {
        Log.i(TAG, "Service onDestroy--->");
        super.onDestroy();
    }

    @Override
    //当使用startService()方法启动Service时，方法体内只需写return null
    public IBinder onBind(Intent intent)
    {
        return null;
    }





    private void writeData(String time,String process,String pss ,String cpu){

        if (saveCSV) {
            if (isTime) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                mDateTime = formatter.format(curDate);
                isTime = false;
            }
            write.cpuWriteCSV(time,process,pss,cpu,mDateTime,AppName,version,getBaseContext());
        }
    }
}
