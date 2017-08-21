package com.maple.com.gobang;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by 80087647 on 2016-05-03.
 */
public class JoinGameView extends View{
    private int mPanelWidth;
    private float mLineHeight;
    private int MAX_LINE = 12;
    private int MAX_COUNT_IN_LINE = 5;

    private Paint mPaint = new Paint();

    private Bitmap mWhitePiece ;
    private Bitmap mBlackPiece;
    private Bitmap mWhiteFirstPiece;
    private Bitmap mBlackFirstPiece;
    private float PieceSize = 3*1.0f/4;

    private boolean mIsWhite = true ; //true 为白棋下子，false为黑棋下子
    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();
    private ArrayList<Point> mUserPointArray = new ArrayList<>();
    private ArrayList<Point> mAllPointArray = new ArrayList<>();

    private boolean mIsGameOver=false;
    private boolean mWhiteIsWinner;
    private boolean showDialog=true;
    private boolean getAllPoint=true;
    private android.os.Handler handler;
    private String s="";
    private String address=null;
    private boolean isAgain=false;

    private SoundPool mSoundPool;
    private int soundId;
    private boolean soundst =true;
    private AlertDialog alertDialog ;

    public JoinGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inti();//初始化

    }

    private void inti() {
        //初始化画笔
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        //初始化旗子
        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(),R.drawable.stone_b1);
        mBlackFirstPiece =BitmapFactory.decodeResource(getResources(),R.drawable.stone_fb);
        mWhiteFirstPiece=BitmapFactory.decodeResource(getResources(),R.drawable.stone_fw);
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,0);

        soundId = mSoundPool.load(getContext(), R.raw.clicksound, 1);
        alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setCanceledOnTouchOutside(false);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                if (msg.what==1){
                    alertDialog.setMessage(getContext().getString(R.string.Connection_please_wait));
                    alertDialog.show();
                }
                if (msg.what==2){
                    readThread readThread = new readThread();
                    readThread.start();
                    alertDialog.dismiss();
                    Toast.makeText(getContext(),getContext().getString(R.string.Connect_successfully),Toast.LENGTH_LONG).show();
                    if (getAllPoint){
                        allPoint();
                        getAllPoint=false;
                    }
                    isconnect=true;
                }
                if (msg.what==3) {
                    if (s.equals("end")) {
                        Toast.makeText(getContext(), getContext().getString(R.string.The_other_side_pulled_out_of_the_game), Toast.LENGTH_LONG).show();
                        try {
                            socket.close();
                            isconnect=false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (s.equals("again")){
                            startAgain();
                            clickSound();
                        }else {
                            if (s.equals("retract")){
                                retract();
                                clickSound();
                            }else {
                                Point a = mAllPointArray.get(Integer.parseInt(s));
                                Log.e("s", s);
                                mWhiteArray.add(a);
                                mUserPointArray.add(a);
                                mIsWhite=true;
                                isAgain = true;
                                clickSound();
                                invalidate();
                            }
                        }
                    }
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取屏幕的宽带和高度
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize,heightSize);
        if (widthMode== View.MeasureSpec.UNSPECIFIED){
            width=heightSize;
        }else if (heightMode== View.MeasureSpec.UNSPECIFIED){
            width=widthSize;
        }
        setMeasuredDimension(width, width);//设置我们View的高度和宽度
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth =w ;//棋盘的宽度
        mLineHeight = mPanelWidth*1.0f / MAX_LINE;//棋盘每一小格的高度

        int PieceWidth = (int) (mLineHeight*PieceSize);//设置棋子的大小
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece,PieceWidth,PieceWidth,false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece,PieceWidth,PieceWidth,false);
        mWhiteFirstPiece = Bitmap.createScaledBitmap(mWhiteFirstPiece,PieceWidth,PieceWidth,false);
        mBlackFirstPiece = Bitmap.createScaledBitmap(mBlackFirstPiece,PieceWidth,PieceWidth,false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //点击事件来处理下子问题
        if (mIsGameOver){
            return false;//判断游戏是否已经结束 ，如果已经结束不能再下子
        }
        if (!isconnect){
            Toast.makeText(getContext(),getContext().getString(R.string.Have_not_yet_joined_any_games),Toast.LENGTH_LONG).show();
            return false;
        }
        int action = event.getAction();
        if (action==MotionEvent.ACTION_UP){
            //获取点击地方的坐标
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point mPoint = getValidPoint(x, y);
            if (mWhiteArray.contains(mPoint)|| mBlackArray.contains(mPoint)){
                return false;
            }
            if (mIsWhite){
                //如果是要下白子 就往这个往白子的坐标集合中添加这个坐标
                int xy = mAllPointArray.indexOf(mPoint);
                sendMessageA(String.valueOf(xy));
                mBlackArray.add(mPoint);
                mUserPointArray.add(mPoint);
                isAgain=true;
                clickSound();
                invalidate();//重新绘制集合中的点
                mIsWhite=!mIsWhite;

            }
        }
        return true;
    }

    private Point getValidPoint(int x, int y) {
        //通过点击棋盘得到到的x,y坐标除以格子的宽度 取整精确到棋盘下子的位置 方便判断是否这个位置已经下子了
        return new Point((int)(x / mLineHeight), (int)(y / mLineHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);//绘制棋盘
        drawPiece(canvas);//绘制棋子
        checkGameOver();//判断游戏是否结束
    }
    private void checkGameOver() {
        Boolean whiteWin = checkFiveInLine(mWhiteArray);
        Boolean blackWin = checkFiveInLine(mBlackArray);
        if (whiteWin || blackWin){
            mIsGameOver = true;
            mWhiteIsWinner = whiteWin;
            String t = mWhiteIsWinner?getContext().getString(R.string.White_wins):getContext().getString(R.string.Black_wins);
            if (showDialog){
                customDialog(t);
                showDialog=false;
                mUserPointArray.clear();
            }
        }
    }

    private Boolean checkFiveInLine(List<Point> points) {
        for (Point p : points) {
            int x = p.x;
            int y = p.y;
            Boolean win = checkHorizontal(x,y,points);
            if (win){
                return true;
            }
            win = checkLeft(x,y,points);
            if (win){
                return true;
            }
            win = checkVertical(x,y,points);
            if (win){
                return true;
            }
            win = checkRight(x,y,points);
            if (win){
                return true;
            }

        }
        return false;
    }

    //判断横向是否已经五子连珠
    private Boolean checkHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x-i,y))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAX_COUNT_IN_LINE){
            return true;
        }
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x+i,y))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAX_COUNT_IN_LINE){
            return true;
        }
        return false;
    }
    //判断纵向是否已经五子连珠
    private Boolean checkVertical(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x,y-i))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAX_COUNT_IN_LINE){
            return true;
        }
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x,y+i))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAX_COUNT_IN_LINE){
            return true;
        }
        return false;
    }
    //判断坐斜是否已经五子连珠
    private Boolean checkLeft(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x-i,y+i))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAX_COUNT_IN_LINE){
            return true;
        }
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x+i,y-i))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAX_COUNT_IN_LINE){
            return true;
        }
        return false;
    }
    //判断友协是否已经五子连珠
    private Boolean checkRight(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x-i,y-i))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAX_COUNT_IN_LINE){
            return true;
        }
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x+i,y+i))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAX_COUNT_IN_LINE){
            return true;
        }
        return false;
    }
    private void drawPiece(Canvas canvas) {
        //绘制白子
        for (int i = 0,n = mWhiteArray.size(); i < n ; i++) {
            Point lastPoint = mWhiteArray.get(n-1);
            Point whitePoint = mWhiteArray.get(i);
            //绘制的棋子之间要有空隙
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x+(1-PieceSize)/2)*mLineHeight,
                    (whitePoint.y+(1-PieceSize)/2)*mLineHeight,null);

            canvas.drawBitmap(mWhiteFirstPiece,(lastPoint.x+(1-PieceSize)/2)*mLineHeight,
                    (lastPoint.y+(1-PieceSize)/2)*mLineHeight,null);
        }
        //绘制黑子
        for (int i = 0,n = mBlackArray.size(); i < n ; i++) {
            Point lastPoint = mBlackArray.get(n-1);
            Point blackPoint = mBlackArray.get(i);
            //绘制的棋子之间要有空隙
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x+(1-PieceSize)/2)*mLineHeight,
                    (blackPoint.y+(1-PieceSize)/2)*mLineHeight,null);
            canvas.drawBitmap(mBlackFirstPiece,(lastPoint.x+(1-PieceSize)/2)*mLineHeight,
                    (lastPoint.y+(1-PieceSize)/2)*mLineHeight,null);
        }

    }
    private void drawBoard(Canvas canvas) {
        //绘制棋盘
        int W = mPanelWidth;//棋盘的宽度赋值给成员变量w
        float LineHeight = mLineHeight;//棋盘每一小格长宽赋值给成员变量LineHeight

        for (int i = 0; i <MAX_LINE ; i++) {
            int startX= (int) (LineHeight/2);
            int endX = (int) (W - LineHeight/2);
            int y = (int) ((0.5+i)*LineHeight);
            canvas.drawLine(startX,y,endX,y,mPaint);//绘制横线
            canvas.drawLine(y,startX,y,endX,mPaint);//绘制纵线
        }

    }

    public void startAgain(){
        mBlackArray.clear();
        mWhiteArray.clear();
        mIsGameOver = false;
        mWhiteIsWinner = false;
        showDialog=true;
        mUserPointArray.clear();
        if (isAgain&&isconnect){
            sendMessageA("again");
            isAgain = false;
        }
        invalidate();
    }

    public void clickSound(){
        if (soundst)mSoundPool.play(soundId, 1, 1, 0, 0, 1);
    }
    public void setSoundst(boolean soundst){
        this.soundst = soundst;
    }
    public void customDialog(String string){
        final Dialog dialog = new Dialog(getContext(),R.style.CustomDialog);
        dialog.setContentView(R.layout.custom_dialog);
        TextView mTextView = (TextView) dialog.findViewById(R.id.tv);
        mTextView.setText(string);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public  void retract(){
        //悔棋
        if (isconnect) {
            if (mUserPointArray.size() != 0) {
                Point i = mUserPointArray.get(mUserPointArray.size() - 1);
                if (mBlackArray.contains(i)) {
                    mBlackArray.remove(i);
                } else {
                    mWhiteArray.remove(i);
                }
                mUserPointArray.remove(i);
                invalidate();//重新绘制集合中的点
                mIsWhite = !mIsWhite;//改变当前要下的子，使得点击第二下的时候下的是和第一次下的子不一样的颜色
            }
        }
    }
    private void allPoint() {
        float LineHeight = mLineHeight;
        for (int i = 0; i < MAX_LINE; i++) {
            int x = (int) ((0.5 + i) * LineHeight);
            for (int y = 0; y < MAX_LINE; y++) {
                int Y = (int) ((0.5 + y) * LineHeight);
                Point mPoint = getValidPoint(x, Y);
                mAllPointArray.add(mPoint);

            }
        }
        System.out.println(mAllPointArray);
    }
    public Thread mConnectThread;
    private  String MY_UUID = "8ce255c0-200a-11e0-ac64-0800200c9a66";
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket socket = null;
    private BluetoothDevice device = null;
    private boolean isconnect=false;


    private void conect() {
        //连接服务器
        System.out.println(address+"conect");
        device = mBluetoothAdapter.getRemoteDevice(address);
        mConnectThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    handler.sendEmptyMessage(1);
                    mBluetoothAdapter.cancelDiscovery();
                    socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                    socket.connect();
                    System.out.println("conectSusses");
                    handler.sendEmptyMessage(2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mConnectThread.start();
    }

    public void sendMessageA(String string){
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(getContext(), getContext().getString(R.string.Bluetooth_has_been_closed), Toast.LENGTH_LONG).show();
        }else if (isconnect) {
            try {
                OutputStream os = socket.getOutputStream();
                Log.e("send", string);
                os.write(string.getBytes());
                System.out.println(string);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public class readThread extends Thread{
        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream mmInStream = null;
            try {
                mmInStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    // Read from the InputStream
                    if ((bytes = mmInStream.read(buffer)) > 0) {
                        byte[] buf_data = new byte[bytes];
                        for (int i = 0; i < bytes; i++) {
                            buf_data[i] = buffer[i];
                        }
                        String sa = new String(buf_data);;
                        Message msg = new Message();
                        msg.obj = sa;
                        msg.what = 3;
                        s=sa;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    try {
                        mmInStream.close();
                    } catch (IOException e1) {

                        e1.printStackTrace();
                    }
                    break;
                }
            }   super.run();
        }
    }
    public void  setAddress(String string){
        this.address =string;
        System.out.println(string+"setaddress");
        conect();
    }
}


