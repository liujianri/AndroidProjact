package com.maple.com.gobang;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 80087647 on 2016-04-13.
 */
public class GobangView extends View{

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

    private boolean mIsGameOver=false;
    private boolean mWhiteIsWinner;
    private boolean showDialog=true;

    private SoundPool mSoundPool;
    private int soundId;
    private boolean soundst =true;

    public GobangView(Context context, AttributeSet attrs) {
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
        mWhitePiece = BitmapFactory.decodeResource(getResources(),R.drawable.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(),R.drawable.stone_b1);
        mBlackFirstPiece =BitmapFactory.decodeResource(getResources(),R.drawable.stone_fb);
        mWhiteFirstPiece=BitmapFactory.decodeResource(getResources(),R.drawable.stone_fw);
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        soundId = mSoundPool.load(getContext(), R.raw.clicksound, 1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
       // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取屏幕的宽带和高度
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize,heightSize);
        if (widthMode==MeasureSpec.UNSPECIFIED){
            width=heightSize;
        }else if (heightMode==MeasureSpec.UNSPECIFIED){
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
        int action = event.getAction();
        if (action==MotionEvent.ACTION_UP){
            //获取点击地方的坐标
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point mPoint = getValidPoint(x,y);
            if (mWhiteArray.contains(mPoint)|| mBlackArray.contains(mPoint)){
                return false;
            }
            if (mIsWhite){
                //如果是要下白子 就往这个往白子的坐标集合中添加这个坐标
                mWhiteArray.add(mPoint);
                mUserPointArray.add(mPoint);
                clickSound();
            }else {
                //往黑子的坐标集合中添加这个坐标
                mBlackArray.add(mPoint);
                mUserPointArray.add(mPoint);
                clickSound();
            }
            invalidate();//重新绘制集合中的点
            mIsWhite = !mIsWhite;//改变当前要下的子，使得点击第二下的时候下的是和第一次下的子不一样的颜色

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

    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";
    private Parcelable parcelable;
    @Override
    protected Parcelable onSaveInstanceState() {
        //存储当前View 的数据
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, mBlackArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        //读取当前View 的数据
        if (state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            parcelable = bundle.getParcelable(INSTANCE);
            super.onRestoreInstanceState(parcelable);
            return;
        }
        super.onRestoreInstanceState(state);
    }
    public void startAgain(){
        mBlackArray.clear();
        mWhiteArray.clear();
        mIsGameOver = false;
        mWhiteIsWinner = false;
        showDialog=true;
        mUserPointArray.clear();
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
        if (mUserPointArray.size()!=0){
            Point i = mUserPointArray.get(mUserPointArray.size() - 1);
            if (mIsWhite){
                mBlackArray.remove(i);
            }else{
               mWhiteArray.remove(i);
            }
            mUserPointArray.remove(i);
            invalidate();//重新绘制集合中的点
            mIsWhite = !mIsWhite;//改变当前要下的子，使得点击第二下的时候下的是和第一次下的子不一样的颜色
        }

    }

}
