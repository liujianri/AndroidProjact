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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 80087647 on 2016-04-13.
 */
public class HumanBattleView extends View {

    private int mPanelWidth;
    private float mLineHeight;
    private int MAX_LINE = 12;
    private int MAX_COUNT_IN_LINE = 5;

    private Paint mPaint = new Paint();

    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;
    private Bitmap mBlackFirstPiece;
    private float PieceSize = 3 * 1.0f / 4;

    private boolean mIsWhite = true; //true 为白棋下子，false为黑棋下子
    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();
    private ArrayList<Point> mAllPointArray = new ArrayList<>();
    private ArrayList<Point> mUserPointArray = new ArrayList<>();

    private boolean mIsGameOver = false;
    private boolean mWhiteIsWinner;
    private boolean showDialog = true;

    private SoundPool mSoundPool;
    private int soundId;
    private boolean soundst = true;
    private boolean getAllPoint = true;

    public HumanBattleView(Context context, AttributeSet attrs) {
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
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);
        mBlackFirstPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_fb);
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
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

        int width = Math.min(widthSize, heightSize);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        setMeasuredDimension(width, width);//设置我们View的高度和宽度
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;//棋盘的宽度
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;//棋盘每一小格的高度

        int PieceWidth = (int) (mLineHeight * PieceSize);//设置棋子的大小
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, PieceWidth, PieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, PieceWidth, PieceWidth, false);
        mBlackFirstPiece = Bitmap.createScaledBitmap(mBlackFirstPiece, PieceWidth, PieceWidth, false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //点击事件来处理下子问题
        if (mIsGameOver) {
            return false;//判断游戏是否已经结束 ，如果已经结束不能再下子
        }
        if (getAllPoint) {
            allPoint();
            getAllPoint = false;
        }
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            //获取点击地方的坐标
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point mPoint = getValidPoint(x, y);
            if (mWhiteArray.contains(mPoint) || mBlackArray.contains(mPoint)) {
                return false;
            }
            if (mIsWhite && mAllPointArray.contains(mPoint)) {
                //如果是要下白子 就往这个往白子的坐标集合中添加这个坐标
                mWhiteArray.add(mPoint);
                clickSound();
                mIsWhite = !mIsWhite;
                mUserPointArray.add(mPoint);
                mAllPointArray.remove(mPoint);
                invalidate();//重新绘制集合中的点
                
                checkGameOver();//判断游戏是否结束
                if (!mIsGameOver) {
                    //simple();
                    normal();
                    checkGameOver();
                }
            }
        }
        return true;
    }

    private Point getValidPoint(int x, int y) {
        //通过点击棋盘得到到的x,y坐标除以格子的宽度 取整精确到棋盘下子的位置 方便判断是否这个位置已经下子了
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);//绘制棋盘
        drawPiece(canvas);//绘制棋子
    }

    private void checkGameOver() {
        Boolean whiteWin = checkFiveInLine(MAX_COUNT_IN_LINE, mWhiteArray);
        Boolean blackWin = checkFiveInLine(MAX_COUNT_IN_LINE, mBlackArray);
        if (whiteWin || blackWin) {
            mUserPointArray.clear();
            mIsGameOver = true;
            mWhiteIsWinner = whiteWin;
            String t = mWhiteIsWinner ? getContext().getString(R.string.Human_wins) : getContext().getString(R.string.AI_wins);
            if (showDialog) {
                customDialog(t);
                showDialog = false;
            }
        }
    }

    //游戏难度
    private void simple() {
        //遍历所以的可落子的点
        boolean aiFive = false, humanFour = false, aiFour = false, aiThree = false, humanThree = false, humanTwo = false;
        boolean humanFive = false;
        boolean aiLiveThreeAndTwo = false, altath = false, altatl = false, altatv = false, altatr = false,
                altatH = false, altatL = false, altatV = false, altatR = false;
        boolean humanLiveThreeAndTwo = false, hltath = false, hltatl = false, hltatv = false, hltatr = false,
                hltatH = false, hltatL = false, hltatV = false, hltatR = false;
        boolean humanLiveThree = false, hliveThreeH = false, hliveThreeV = false, hliveThreeL = false, hliveThreeR = false;
        boolean aiLiveThree = false, ailiveThreeH = false, ailiveThreeV = false, ailiveThreeL = false, ailiveThreeR = false;
        boolean H = false, V = false, L = false, R = false, h = false, v = false, l = false, r = false, wFt = false;
        boolean Ha = false, Va = false, La = false, Ra = false, wFata = false;
        boolean Hai = false, Vai = false, Lai = false, Rai = false, hai = false, vai = false, lai = false, rai = false, wFait = false;
        boolean Hi = false, Vi = false, Li = false, Ri = false, wFaitai = false;
        boolean aiTwo = false;
        for (int i = 0; i < mAllPointArray.size(); i++) {
            //查看AI是否有形成五个的地方
            Point addPoint = mAllPointArray.get(i);
            aiFive = checkNumInLine(5, mBlackArray, addPoint);
            if (aiFive) {
                mBlackArray.add(addPoint);
                mUserPointArray.add(addPoint);
                mAllPointArray.remove(addPoint);
                Log.e("ai", "Five");
                break;
            }
        }
        if (!aiFive) {
            //查看human可以形成的五个的地方
            for (int i = 0; i < mAllPointArray.size(); i++) {
                Point addPoint = mAllPointArray.get(i);
                humanFive = checkNumInLine(5, mWhiteArray, addPoint);
                if (humanFive) {
                    mUserPointArray.add(addPoint);
                    mAllPointArray.remove(addPoint);
                    mBlackArray.add(addPoint);
                    Log.e("human", "Five");
                    break;
                }
            }
            if (!humanFive) {
                for (int i = 0; i < mAllPointArray.size(); i++) {
                    //查看AI有没有活三存在且又可以形成三个的地方
                    Point addPoint = mAllPointArray.get(i);
                    altatH = LiveThreeHorizontal(addPoint.x, addPoint.y, mBlackArray);
                    altatV = LiveThreeVertical(addPoint.x, addPoint.y, mBlackArray);
                    altatL = LiveThreeLeft(addPoint.x, addPoint.y, mBlackArray);
                    altatR = LiveThreeRight(addPoint.x, addPoint.y, mBlackArray);

                    altath = checkHorizontal(3, addPoint.x, addPoint.y, mBlackArray);
                    altatv = checkVertical(3, addPoint.x, addPoint.y, mBlackArray);
                    altatl = checkLeft(3, addPoint.x, addPoint.y, mBlackArray);
                    altatr = checkRight(3, addPoint.x, addPoint.y, mBlackArray);
                    aiLiveThreeAndTwo = (altatH && altatv) || (altatH && altatl) || (altatH && altatr) || (altatV && altatl) ||
                            (altatV && altath) || (altatV && altatr) || (altatR && altath) || (altatR && altatv) ||
                            (altatR && altatl) || (altatL && altath) || (altatL && altatv) || (altatL && altatr);
                    if (aiLiveThreeAndTwo) {
                        mUserPointArray.add(addPoint);
                        mAllPointArray.remove(addPoint);
                        mBlackArray.add(addPoint);
                        Log.e("ai", "aiLiveThreeAndTwo");
                        break;
                    }
                }
                if (!aiLiveThreeAndTwo) {
                    for (int i = 0; i < mAllPointArray.size(); i++) {
                        //查看AI有没有活三存在，
                        Point addPoint = mAllPointArray.get(i);
                        ailiveThreeH = LiveThreeHorizontal(addPoint.x, addPoint.y, mBlackArray);
                        ailiveThreeV = LiveThreeVertical(addPoint.x, addPoint.y, mBlackArray);
                        ailiveThreeL = LiveThreeLeft(addPoint.x, addPoint.y, mBlackArray);
                        ailiveThreeR = LiveThreeRight(addPoint.x, addPoint.y, mBlackArray);
                        aiLiveThree = ailiveThreeH || ailiveThreeV || ailiveThreeL || ailiveThreeR;
                        if (aiLiveThree) {
                            mUserPointArray.add(addPoint);
                            mAllPointArray.remove(addPoint);
                            mBlackArray.add(addPoint);
                            Log.e("ai", "aiLiveThree");
                            break;
                        }
                    }
                    if (!aiLiveThree) {
                        for (int i = 0; i < mAllPointArray.size(); i++) {
                            //查看human有没有活三存在且又可以形成三个的地方
                            Point addPoint = mAllPointArray.get(i);
                            hltatH = LiveThreeHorizontal(addPoint.x, addPoint.y, mWhiteArray);
                            hltatV = LiveThreeVertical(addPoint.x, addPoint.y, mWhiteArray);
                            hltatL = LiveThreeLeft(addPoint.x, addPoint.y, mWhiteArray);
                            hltatR = LiveThreeRight(addPoint.x, addPoint.y, mWhiteArray);

                            hltath = checkHorizontal(3, addPoint.x, addPoint.y, mWhiteArray);
                            hltatv = checkVertical(3, addPoint.x, addPoint.y, mWhiteArray);
                            hltatl = checkLeft(3, addPoint.x, addPoint.y, mWhiteArray);
                            hltatr = checkRight(3, addPoint.x, addPoint.y, mWhiteArray);
                            humanLiveThreeAndTwo = (hltatH && hltatv) || (hltatH && hltatl) || (hltatH && hltatr) || (hltatV && hltatl) ||
                                    (hltatV && hltath) || (hltatV && hltatr) || (hltatR && hltath) || (hltatR && hltatv) ||
                                    (hltatR && hltatl) || (hltatL && hltath) || (hltatL && hltatv) || (hltatL && hltatr);
                            if (humanLiveThreeAndTwo) {
                                mUserPointArray.add(addPoint);
                                mAllPointArray.remove(addPoint);
                                mBlackArray.add(addPoint);
                                Log.e("human", "humanLiveThreeAndTwo");
                                break;
                            }
                        }
                        if (!humanLiveThreeAndTwo) {
                            for (int i = 0; i < mAllPointArray.size(); i++) {
                                //查看human有没有活三存在
                                Point addPoint = mAllPointArray.get(i);
                                hliveThreeH = LiveThreeHorizontal(addPoint.x, addPoint.y, mWhiteArray);
                                hliveThreeV = LiveThreeVertical(addPoint.x, addPoint.y, mWhiteArray);
                                hliveThreeL = LiveThreeLeft(addPoint.x, addPoint.y, mWhiteArray);
                                hliveThreeR = LiveThreeRight(addPoint.x, addPoint.y, mWhiteArray);
                                humanLiveThree = hliveThreeH || hliveThreeV || hliveThreeL || hliveThreeR;
                                if (humanLiveThree) {
                                    mUserPointArray.add(addPoint);
                                    mAllPointArray.remove(addPoint);
                                    mBlackArray.add(addPoint);
                                    Log.e("human", "humanLiveThree");
                                    break;
                                }
                            }
                            if (!humanLiveThree) {
                                //查看AI有什么地方可以形成4个的地方
                                for (int i = 0; i < mAllPointArray.size(); i++) {
                                    Point addPoint = mAllPointArray.get(i);
                                    aiFour = checkNumInLine(4, mBlackArray, addPoint);
                                    if (aiFour) {
                                        mUserPointArray.add(addPoint);
                                        mAllPointArray.remove(addPoint);
                                        mBlackArray.add(addPoint);
                                        Log.e("ai", "Four");
                                        break;
                                    }
                                }
                                if (!aiFour) {
                                    //检查human可以形成4个 又可以形成3个的地方
                                    for (int i = 0; i < mAllPointArray.size(); i++) {
                                        Point addPoint = mAllPointArray.get(i);
                                        int x = addPoint.x;
                                        int y = addPoint.y;
                                        H = LiveThreeHorizontal(x, y, mWhiteArray);
                                        V = LiveThreeVertical(x, y, mWhiteArray);
                                        L = LiveThreeLeft(x, y, mWhiteArray);
                                        R = LiveThreeRight(x, y, mWhiteArray);
                                        h = checkHorizontal(3, x, y, mWhiteArray);
                                        v = checkVertical(3, x, y, mWhiteArray);
                                        l = checkLeft(3, x, y, mWhiteArray);
                                        r = checkRight(3, x, y, mWhiteArray);
                                        wFt = (H && v) || (H && l) || (H && r) || (V && l) || (V && h) || (V && r) || (R && h) || (R && v) || (R && l) || (L && h) || (L && v) || (L && r);
                                        if (wFt) {
                                            mUserPointArray.add(addPoint);
                                            mAllPointArray.remove(addPoint);
                                            mBlackArray.add(addPoint);
                                            Log.e("wFt", "LiveThree and three");
                                            break;
                                        }
                                    }
                                    if (!wFt) {
                                        //检查human可以形成两边都是活三的地方
                                        for (int i = 0; i < mAllPointArray.size(); i++) {
                                            Point addPoint = mAllPointArray.get(i);
                                            int x = addPoint.x;
                                            int y = addPoint.y;
                                            Ha = LiveTwoHorizontal(x, y, mWhiteArray);
                                            Va = LiveTwoVertical(x, y, mWhiteArray);
                                            La = LiveTwoLeft(x, y, mWhiteArray);
                                            Ra = LiveTwoRight(x, y, mWhiteArray);
                                            wFata = (Ha && La) || (Ha && Va) || (Ha && Ra) || (Va && La) || (Va && Ra) || (La && Ra);
                                            if (wFata) {
                                                mUserPointArray.add(addPoint);
                                                mAllPointArray.remove(addPoint);
                                                mBlackArray.add(addPoint);
                                                Log.e("wFata", "Human Two live three AthreeA");
                                                break;
                                            }
                                        }
                                        if (!wFata) {
                                            //检查AI 有没有能够形成四个 又可以形成三个的地方
                                            for (int i = 0; i < mAllPointArray.size(); i++) {
                                                Point addPoint = mAllPointArray.get(i);
                                                int x = addPoint.x;
                                                int y = addPoint.y;
                                                Hai = checkHorizontal(4, x, y, mBlackArray);
                                                Vai = checkVertical(4, x, y, mBlackArray);
                                                Lai = checkLeft(4, x, y, mBlackArray);
                                                Rai = checkRight(4, x, y, mBlackArray);
                                                hai = checkHorizontal(3, x, y, mBlackArray);
                                                vai = checkVertical(3, x, y, mBlackArray);
                                                lai = checkLeft(3, x, y, mBlackArray);
                                                rai = checkRight(3, x, y, mBlackArray);
                                                wFait = (Hai && vai) || (Hai && lai) || (Hai && rai) || (Vai && lai) || (Vai && hai) || (Vai && rai)
                                                        || (Rai && hai) || (Rai && vai) || (Rai && lai) || (Lai && hai) || (Lai && vai) || (Lai && rai);
                                                if (wFait) {
                                                    mUserPointArray.add(addPoint);
                                                    mAllPointArray.remove(addPoint);
                                                    mBlackArray.add(addPoint);
                                                    Log.e("wFait", "FouraiThree");
                                                    break;
                                                }
                                            }
                                            if (!wFait) {
                                                //检查AI有没有可以形成两个方向是活三个的地方
                                                for (int i = 0; i < mAllPointArray.size(); i++) {
                                                    Point addPoint = mAllPointArray.get(i);
                                                    int x = addPoint.x;
                                                    int y = addPoint.y;
                                                    Hi = LiveTwoHorizontal(x, y, mBlackArray);
                                                    Vi = LiveTwoVertical(x, y, mBlackArray);
                                                    Li = LiveTwoLeft(x, y, mBlackArray);
                                                    Ri = LiveTwoRight(x, y, mBlackArray);
                                                    wFaitai = (Hi && Li) || (Hi && Vi) || (Hi && Ri) || (Vi && Li) || (Vi && Ri) || (Li && Ri);
                                                    if (wFaitai) {
                                                        mUserPointArray.add(addPoint);
                                                        mAllPointArray.remove(addPoint);
                                                        mBlackArray.add(addPoint);
                                                        Log.e("wFata", "ai Two three FourAthreeA");
                                                        break;
                                                    }
                                                }
                                                if (!wFaitai) {
                                                    //查看AI是否有能形成活三的地方
                                                    for (int i = 0; i < mAllPointArray.size(); i++) {
                                                        Point addPoint = mAllPointArray.get(i);
                                                        aiThree = checkLiveTwo(addPoint.x, addPoint.y, mBlackArray);
                                                        if (aiThree) {
                                                            mUserPointArray.add(addPoint);
                                                            mAllPointArray.remove(addPoint);
                                                            mBlackArray.add(addPoint);
                                                            Log.e("ai", "Three");
                                                            break;
                                                        }
                                                    }
                                                    if (!aiThree) {
                                                        //检查human可以形成活三的地方
                                                        for (int i = 0; i < mAllPointArray.size(); i++) {
                                                            Point addPoint = mAllPointArray.get(i);
                                                            humanThree = checkLiveTwo(addPoint.x, addPoint.y, mWhiteArray);
                                                            if (humanThree) {
                                                                mUserPointArray.add(addPoint);
                                                                mAllPointArray.remove(addPoint);
                                                                mBlackArray.add(addPoint);
                                                                Log.e("human", "Three");
                                                                break;
                                                            }
                                                        }
                                                        if (!humanThree) {
                                                            //检查ai可以形成2个的地方
                                                            for (int i = 0; i < mAllPointArray.size(); i++) {
                                                                Point addPoint = mAllPointArray.get(i);
                                                                aiTwo = checkNumInLine(2, mBlackArray, addPoint);
                                                                if (aiTwo) {
                                                                    mUserPointArray.add(addPoint);
                                                                    mAllPointArray.remove(addPoint);
                                                                    mBlackArray.add(addPoint);
                                                                    Log.e("AI", "Two");
                                                                    break;
                                                                }
                                                            }
                                                            if (!aiTwo) {
                                                                //检查human可以形成2个的地方
                                                                for (int i = 0; i < mAllPointArray.size(); i++) {
                                                                    Point addPoint = mAllPointArray.get(i);
                                                                    humanTwo = checkNumInLine(2, mWhiteArray, addPoint);
                                                                    if (humanTwo) {
                                                                        mUserPointArray.add(addPoint);
                                                                        mAllPointArray.remove(addPoint);
                                                                        mBlackArray.add(addPoint);
                                                                        Log.e("human", "Two");
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        invalidate();
        mIsWhite = !mIsWhite;
    }

    private void normal() {
        //遍历所以的可落子的点
        boolean aiFive = false, humanFour = false, aiFour = false, aiThree = false, humanThree = false, humanTwo = false;
        boolean humanFive = false;
        boolean aiLiveThreeAndTwo = false, altath = false, altatl = false, altatv = false, altatr = false,
                altatH = false, altatL = false, altatV = false, altatR = false;
        boolean humanLiveThreeAndTwo = false, hltath = false, hltatl = false, hltatv = false, hltatr = false,
                hltatH = false, hltatL = false, hltatV = false, hltatR = false;
        boolean humanLiveThree = false, hliveThreeH = false, hliveThreeV = false, hliveThreeL = false, hliveThreeR = false;
        boolean aiLiveThree = false, ailiveThreeH = false, ailiveThreeV = false, ailiveThreeL = false, ailiveThreeR = false;
        boolean H = false, V = false, L = false, R = false, h = false, v = false, l = false, r = false, wFt = false;
        boolean Ha = false, Va = false, La = false, Ra = false, wFata = false;
        boolean Hai = false, Vai = false, Lai = false, Rai = false, hai = false, vai = false, lai = false, rai = false, wFait = false;
        boolean Hi = false, Vi = false, Li = false, Ri = false, wFaitai = false;
        boolean aiTwo = false;
        boolean allLiveThreeAndForua = false, humanLiveThreeAndForua = false, AiallThreeAndLiveThree = false, AiallThreeAndLiveThreeA = false;
        boolean humanallThreeAndLiveThree = false, humanallThreeAndLiveThreeA = false;
        for (int i = 0; i < mAllPointArray.size(); i++) {
            //查看AI是否有形成五个的地方
            Point addPoint = mAllPointArray.get(i);
            aiFive = checkNumInLine(5, mBlackArray, addPoint);
            if (aiFive) {
                mBlackArray.add(addPoint);
                mUserPointArray.add(addPoint);
                mAllPointArray.remove(addPoint);
                Log.e("ai", "Five");
                break;
            }
        }
        if (!aiFive) {
            //查看human可以形成的五个的地方
            for (int i = 0; i < mAllPointArray.size(); i++) {
                Point addPoint = mAllPointArray.get(i);
                humanFive = checkNumInLine(5, mWhiteArray, addPoint);
                if (humanFive) {
                    mUserPointArray.add(addPoint);
                    mAllPointArray.remove(addPoint);
                    mBlackArray.add(addPoint);
                    Log.e("human", "Five");
                    break;
                }
            }
            if (!humanFive) {
                //查看AI有没有可以形成四个 又可以形成活三的地方
                for (int i = 0; i < mAllPointArray.size(); i++) {
                    Point addPoint = mAllPointArray.get(i);
                    allLiveThreeAndForua = allLiveThreeAndForu(addPoint.x, addPoint.y, mBlackArray);
                    if (allLiveThreeAndForua) {
                        mUserPointArray.add(addPoint);
                        mAllPointArray.remove(addPoint);
                        mBlackArray.add(addPoint);
                        Log.e("ai", "allLiveThreeAndForua");
                        break;
                    }
                }
                if (!allLiveThreeAndForua) {
                    //查看human有没有可以形成四个 又可以形成活三的地方
                    for (int i = 0; i < mAllPointArray.size(); i++) {
                        Point addPoint = mAllPointArray.get(i);
                        humanLiveThreeAndForua = allLiveThreeAndForu(addPoint.x, addPoint.y, mWhiteArray);
                        if (humanLiveThreeAndForua) {
                            mUserPointArray.add(addPoint);
                            mAllPointArray.remove(addPoint);
                            mBlackArray.add(addPoint);
                            Log.e("human", "humanLiveThreeAndForua");
                            break;
                        }
                    }
                    if (!humanLiveThreeAndForua) {
                        //查看AI有没有可能形成一个4 一个活三
                        for (int i = 0; i < mAllPointArray.size(); i++) {
                            Point addPoint = mAllPointArray.get(i);
                            AiallThreeAndLiveThree = allThreeAndLiveThree(addPoint.x, addPoint.y, mBlackArray);
                            if (AiallThreeAndLiveThree) {
                                mUserPointArray.add(addPoint);
                                mAllPointArray.remove(addPoint);
                                mBlackArray.add(addPoint);
                                Log.e("AI", "AiallThreeAndLiveThree");
                                break;
                            }
                        }
                        if (!AiallThreeAndLiveThree) {
                            //查看human有没有可能形成一个4 一个活三
                            for (int i = 0; i < mAllPointArray.size(); i++) {
                                Point addPoint = mAllPointArray.get(i);
                                humanallThreeAndLiveThree = allThreeAndLiveThree(addPoint.x, addPoint.y, mWhiteArray);
                                if (humanallThreeAndLiveThree) {
                                    mUserPointArray.add(addPoint);
                                    mAllPointArray.remove(addPoint);
                                    mBlackArray.add(addPoint);
                                    Log.e("human", "AiallThreeAndLiveThree");
                                    break;
                                }
                            }
                            if (!humanallThreeAndLiveThree) {
                                for (int i = 0; i < mAllPointArray.size(); i++) {
                                    Point addPoint = mAllPointArray.get(i);
                                    AiallThreeAndLiveThreeA = allThreeAndLiveThreeA(addPoint.x, addPoint.y, mWhiteArray);
                                    if (AiallThreeAndLiveThreeA) {
                                        mUserPointArray.add(addPoint);
                                        mAllPointArray.remove(addPoint);
                                        mBlackArray.add(addPoint);
                                        Log.e("AI", "AiallThreeAndLiveThreeA");
                                        break;
                                    }
                                }
                                if (!AiallThreeAndLiveThreeA) {
                                    for (int i = 0; i < mAllPointArray.size(); i++) {
                                        Point addPoint = mAllPointArray.get(i);
                                        humanallThreeAndLiveThreeA = allThreeAndLiveThreeA(addPoint.x, addPoint.y, mWhiteArray);
                                        if (humanallThreeAndLiveThreeA) {
                                            mUserPointArray.add(addPoint);
                                            mAllPointArray.remove(addPoint);
                                            mBlackArray.add(addPoint);
                                            Log.e("Human", "humanallThreeAndLiveThreeA");
                                            break;
                                        }
                                    }
                                    if (!humanallThreeAndLiveThreeA) {

                                        for (int i = 0; i < mAllPointArray.size(); i++) {
                                            //查看AI有没有活三存在且又可以形成三个的地方
                                            Point addPoint = mAllPointArray.get(i);
                                            altatH = LiveThreeHorizontal(addPoint.x, addPoint.y, mBlackArray);
                                            altatV = LiveThreeVertical(addPoint.x, addPoint.y, mBlackArray);
                                            altatL = LiveThreeLeft(addPoint.x, addPoint.y, mBlackArray);
                                            altatR = LiveThreeRight(addPoint.x, addPoint.y, mBlackArray);

                                            altath = checkHorizontal(3, addPoint.x, addPoint.y, mBlackArray);
                                            altatv = checkVertical(3, addPoint.x, addPoint.y, mBlackArray);
                                            altatl = checkLeft(3, addPoint.x, addPoint.y, mBlackArray);
                                            altatr = checkRight(3, addPoint.x, addPoint.y, mBlackArray);
                                            aiLiveThreeAndTwo = (altatH && altatv) || (altatH && altatl) || (altatH && altatr) || (altatV && altatl) ||
                                                    (altatV && altath) || (altatV && altatr) || (altatR && altath) || (altatR && altatv) ||
                                                    (altatR && altatl) || (altatL && altath) || (altatL && altatv) || (altatL && altatr);
                                            if (aiLiveThreeAndTwo) {
                                                mUserPointArray.add(addPoint);
                                                mAllPointArray.remove(addPoint);
                                                mBlackArray.add(addPoint);
                                                Log.e("ai", "aiLiveThreeAndTwo");
                                                break;
                                            }
                                        }
                                        if (!aiLiveThreeAndTwo) {
                                            for (int i = 0; i < mAllPointArray.size(); i++) {
                                                //查看AI有没有活三存在，
                                                Point addPoint = mAllPointArray.get(i);
                                                ailiveThreeH = LiveThreeHorizontal(addPoint.x, addPoint.y, mBlackArray);
                                                ailiveThreeV = LiveThreeVertical(addPoint.x, addPoint.y, mBlackArray);
                                                ailiveThreeL = LiveThreeLeft(addPoint.x, addPoint.y, mBlackArray);
                                                ailiveThreeR = LiveThreeRight(addPoint.x, addPoint.y, mBlackArray);
                                                aiLiveThree = ailiveThreeH || ailiveThreeV || ailiveThreeL || ailiveThreeR;
                                                if (aiLiveThree) {
                                                    mUserPointArray.add(addPoint);
                                                    mAllPointArray.remove(addPoint);
                                                    mBlackArray.add(addPoint);
                                                    Log.e("ai", "aiLiveThree");
                                                    break;
                                                }
                                            }
                                            if (!aiLiveThree) {
                                                for (int i = 0; i < mAllPointArray.size(); i++) {
                                                    //查看human有没有活三存在且又可以形成三个的地方
                                                    Point addPoint = mAllPointArray.get(i);
                                                    hltatH = LiveThreeHorizontal(addPoint.x, addPoint.y, mWhiteArray);
                                                    hltatV = LiveThreeVertical(addPoint.x, addPoint.y, mWhiteArray);
                                                    hltatL = LiveThreeLeft(addPoint.x, addPoint.y, mWhiteArray);
                                                    hltatR = LiveThreeRight(addPoint.x, addPoint.y, mWhiteArray);

                                                    hltath = checkHorizontal(3, addPoint.x, addPoint.y, mWhiteArray);
                                                    hltatv = checkVertical(3, addPoint.x, addPoint.y, mWhiteArray);
                                                    hltatl = checkLeft(3, addPoint.x, addPoint.y, mWhiteArray);
                                                    hltatr = checkRight(3, addPoint.x, addPoint.y, mWhiteArray);
                                                    humanLiveThreeAndTwo = (hltatH && hltatv) || (hltatH && hltatl) || (hltatH && hltatr) || (hltatV && hltatl) ||
                                                            (hltatV && hltath) || (hltatV && hltatr) || (hltatR && hltath) || (hltatR && hltatv) ||
                                                            (hltatR && hltatl) || (hltatL && hltath) || (hltatL && hltatv) || (hltatL && hltatr);
                                                    if (humanLiveThreeAndTwo) {
                                                        mUserPointArray.add(addPoint);
                                                        mAllPointArray.remove(addPoint);
                                                        mBlackArray.add(addPoint);
                                                        Log.e("human", "humanLiveThreeAndTwo");
                                                        break;
                                                    }
                                                }
                                                if (!humanLiveThreeAndTwo) {
                                                    for (int i = 0; i < mAllPointArray.size(); i++) {
                                                        //查看human有没有活三存在
                                                        Point addPoint = mAllPointArray.get(i);
                                                        hliveThreeH = LiveThreeHorizontal(addPoint.x, addPoint.y, mWhiteArray);
                                                        hliveThreeV = LiveThreeVertical(addPoint.x, addPoint.y, mWhiteArray);
                                                        hliveThreeL = LiveThreeLeft(addPoint.x, addPoint.y, mWhiteArray);
                                                        hliveThreeR = LiveThreeRight(addPoint.x, addPoint.y, mWhiteArray);
                                                        humanLiveThree = hliveThreeH || hliveThreeV || hliveThreeL || hliveThreeR;
                                                        if (humanLiveThree) {
                                                            mUserPointArray.add(addPoint);
                                                            mAllPointArray.remove(addPoint);
                                                            mBlackArray.add(addPoint);
                                                            Log.e("human", "humanLiveThree");
                                                            break;
                                                        }
                                                    }
                                                    if (!humanLiveThree) {
                                                        //查看AI有什么地方可以形成4个的地方
                                                        for (int i = 0; i < mAllPointArray.size(); i++) {
                                                            Point addPoint = mAllPointArray.get(i);
                                                            aiFour = checkNumInLine(4, mBlackArray, addPoint);
                                                            if (aiFour) {
                                                                mUserPointArray.add(addPoint);
                                                                mAllPointArray.remove(addPoint);
                                                                mBlackArray.add(addPoint);
                                                                Log.e("ai", "Four");
                                                                break;
                                                            }
                                                        }
                                                        if (!aiFour) {
                                                            //检查human可以形成4个 又可以形成3个的地方
                                                            for (int i = 0; i < mAllPointArray.size(); i++) {
                                                                Point addPoint = mAllPointArray.get(i);
                                                                int x = addPoint.x;
                                                                int y = addPoint.y;
                                                                H = LiveThreeHorizontal(x, y, mWhiteArray);
                                                                V = LiveThreeVertical(x, y, mWhiteArray);
                                                                L = LiveThreeLeft(x, y, mWhiteArray);
                                                                R = LiveThreeRight(x, y, mWhiteArray);
                                                                h = checkHorizontal(3, x, y, mWhiteArray);
                                                                v = checkVertical(3, x, y, mWhiteArray);
                                                                l = checkLeft(3, x, y, mWhiteArray);
                                                                r = checkRight(3, x, y, mWhiteArray);
                                                                wFt = (H && v) || (H && l) || (H && r) || (V && l) || (V && h) || (V && r) || (R && h) || (R && v) || (R && l) || (L && h) || (L && v) || (L && r);
                                                                if (wFt) {
                                                                    mUserPointArray.add(addPoint);
                                                                    mAllPointArray.remove(addPoint);
                                                                    mBlackArray.add(addPoint);
                                                                    Log.e("wFt", "LiveThree and three");
                                                                    break;
                                                                }
                                                            }
                                                            if (!wFt) {
                                                                //检查human可以形成两边都是活三的地方
                                                                for (int i = 0; i < mAllPointArray.size(); i++) {
                                                                    Point addPoint = mAllPointArray.get(i);
                                                                    int x = addPoint.x;
                                                                    int y = addPoint.y;
                                                                    Ha = LiveTwoHorizontal(x, y, mWhiteArray);
                                                                    Va = LiveTwoVertical(x, y, mWhiteArray);
                                                                    La = LiveTwoLeft(x, y, mWhiteArray);
                                                                    Ra = LiveTwoRight(x, y, mWhiteArray);
                                                                    wFata = (Ha && La) || (Ha && Va) || (Ha && Ra) || (Va && La) || (Va && Ra) || (La && Ra);
                                                                    if (wFata) {
                                                                        mUserPointArray.add(addPoint);
                                                                        mAllPointArray.remove(addPoint);
                                                                        mBlackArray.add(addPoint);
                                                                        Log.e("wFata", "Human Two live three AthreeA");
                                                                        break;
                                                                    }
                                                                }
                                                                if (!wFata) {
                                                                    //检查AI 有没有能够形成四个 又可以形成三个的地方
                                                                    for (int i = 0; i < mAllPointArray.size(); i++) {
                                                                        Point addPoint = mAllPointArray.get(i);
                                                                        int x = addPoint.x;
                                                                        int y = addPoint.y;
                                                                        Hai = checkHorizontal(4, x, y, mBlackArray);
                                                                        Vai = checkVertical(4, x, y, mBlackArray);
                                                                        Lai = checkLeft(4, x, y, mBlackArray);
                                                                        Rai = checkRight(4, x, y, mBlackArray);
                                                                        hai = checkHorizontal(3, x, y, mBlackArray);
                                                                        vai = checkVertical(3, x, y, mBlackArray);
                                                                        lai = checkLeft(3, x, y, mBlackArray);
                                                                        rai = checkRight(3, x, y, mBlackArray);
                                                                        wFait = (Hai && vai) || (Hai && lai) || (Hai && rai) || (Vai && lai) || (Vai && hai) || (Vai && rai)
                                                                                || (Rai && hai) || (Rai && vai) || (Rai && lai) || (Lai && hai) || (Lai && vai) || (Lai && rai);
                                                                        if (wFait) {
                                                                            mUserPointArray.add(addPoint);
                                                                            mAllPointArray.remove(addPoint);
                                                                            mBlackArray.add(addPoint);
                                                                            Log.e("wFait", "FouraiThree");
                                                                            break;
                                                                        }
                                                                    }
                                                                    if (!wFait) {
                                                                        //检查AI有没有可以形成两个方向是活三个的地方
                                                                        for (int i = 0; i < mAllPointArray.size(); i++) {
                                                                            Point addPoint = mAllPointArray.get(i);
                                                                            int x = addPoint.x;
                                                                            int y = addPoint.y;
                                                                            Hi = LiveTwoHorizontal(x, y, mBlackArray);
                                                                            Vi = LiveTwoVertical(x, y, mBlackArray);
                                                                            Li = LiveTwoLeft(x, y, mBlackArray);
                                                                            Ri = LiveTwoRight(x, y, mBlackArray);
                                                                            wFaitai = (Hi && Li) || (Hi && Vi) || (Hi && Ri) || (Vi && Li) || (Vi && Ri) || (Li && Ri);
                                                                            if (wFaitai) {
                                                                                mUserPointArray.add(addPoint);
                                                                                mAllPointArray.remove(addPoint);
                                                                                mBlackArray.add(addPoint);
                                                                                Log.e("wFata", "ai Two three FourAthreeA");
                                                                                break;
                                                                            }
                                                                        }
                                                                        if (!wFaitai) {
                                                                            //查看AI是否有能形成活三的地方
                                                                            for (int i = 0; i < mAllPointArray.size(); i++) {
                                                                                Point addPoint = mAllPointArray.get(i);
                                                                                aiThree = checkLiveTwo(addPoint.x, addPoint.y, mBlackArray);
                                                                                if (aiThree) {
                                                                                    mUserPointArray.add(addPoint);
                                                                                    mAllPointArray.remove(addPoint);
                                                                                    mBlackArray.add(addPoint);
                                                                                    Log.e("ai", "Three");
                                                                                    break;
                                                                                }
                                                                            }
                                                                            if (!aiThree) {
                                                                                //检查human可以形成活三的地方
                                                                                for (int i = 0; i < mAllPointArray.size(); i++) {
                                                                                    Point addPoint = mAllPointArray.get(i);
                                                                                    humanThree = checkLiveTwo(addPoint.x, addPoint.y, mWhiteArray);
                                                                                    if (humanThree) {
                                                                                        mUserPointArray.add(addPoint);
                                                                                        mAllPointArray.remove(addPoint);
                                                                                        mBlackArray.add(addPoint);
                                                                                        Log.e("human", "Three");
                                                                                        break;
                                                                                    }
                                                                                }
                                                                                if (!humanThree) {
                                                                                    //检查ai可以形成2个的地方
                                                                                    for (int i = 0; i < mAllPointArray.size(); i++) {
                                                                                        Point addPoint = mAllPointArray.get(i);
                                                                                        aiTwo = checkNumInLine(2, mBlackArray, addPoint);
                                                                                        if (aiTwo) {
                                                                                            mUserPointArray.add(addPoint);
                                                                                            mAllPointArray.remove(addPoint);
                                                                                            mBlackArray.add(addPoint);
                                                                                            Log.e("AI", "Two");
                                                                                            break;
                                                                                        }
                                                                                    }
                                                                                    if (!aiTwo) {
                                                                                        //检查human可以形成2个的地方
                                                                                        for (int i = 0; i < mAllPointArray.size(); i++) {
                                                                                            Point addPoint = mAllPointArray.get(i);
                                                                                            humanTwo = checkNumInLine(2, mWhiteArray, addPoint);
                                                                                            if (humanTwo) {
                                                                                                mUserPointArray.add(addPoint);
                                                                                                mAllPointArray.remove(addPoint);
                                                                                                mBlackArray.add(addPoint);
                                                                                                Log.e("human", "Two");
                                                                                                break;
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
        invalidate();
        mIsWhite = !mIsWhite;
    }

    //判断各方向是否为活二 及是否能成为活三 还能成为五个的点
    private Boolean checkLiveTwo(int x, int y, List<Point> points) {

        Boolean win = LiveTwoHorizontal(x, y, points);
        if (win) {
            return true;
        }
        win = LiveTwoVertical(x, y, points);
        if (win) {
            return true;
        }
        win = LiveTwoLeft(x, y, points);
        if (win) {
            return true;
        }
        win = LiveTwoRight(x, y, points);
        if (win) {
            return true;
        }


        return false;
    }

    private Boolean LiveTwoHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        int counta = 1;
        int num = 3;
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == num) {
            if (mAllPointArray.contains(new Point(x + 3, y)) && mAllPointArray.contains(new Point(x - 1, y))) {
                return true;
            }
        }
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x - i, y))) {
                counta++;
            } else {
                break;
            }
        }
        if (counta == num) {
            if (mAllPointArray.contains(new Point(x - 3, y)) && mAllPointArray.contains(new Point(x + 1, y))) {
                return true;
            }
        }
        if (count == 2 && counta == 2) {
            if (mAllPointArray.contains(new Point(x + 2, y)) && mAllPointArray.contains(new Point(x - 2, y))) {
                return true;
            }
        }
        return false;
    }

    private Boolean LiveTwoVertical(int x, int y, List<Point> points) {
        int count = 1;
        int counta = 1;
        int num = 3;
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == num) {
            if (mAllPointArray.contains(new Point(x, y + 3)) && mAllPointArray.contains(new Point(x, y - 1))) {
                return true;
            }
        }
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x, y - i))) {
                counta++;
            } else {
                break;
            }
        }
        if (counta == num) {
            if (mAllPointArray.contains(new Point(x, y - 3)) && mAllPointArray.contains(new Point(x, y + 1))) {
                return true;
            }
        }
        if (count == 2 && counta == 2) {
            if (mAllPointArray.contains(new Point(x, y + 2)) && mAllPointArray.contains(new Point(x, y - 2))) {
                return true;
            }
        }
        return false;
    }

    private Boolean LiveTwoLeft(int x, int y, List<Point> points) {
        int count = 1;
        int counta = 1;
        int num = 3;
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == num) {
            if (mAllPointArray.contains(new Point(x - 3, y + 3)) && mAllPointArray.contains(new Point(x + 1, y - 1))) {
                return true;
            }
        }
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                counta++;
            } else {
                break;
            }
        }
        if (counta == num) {
            if (mAllPointArray.contains(new Point(x + 3, y - 3)) && mAllPointArray.contains(new Point(x - 1, y + 1))) {
                return true;
            }
        }
        if (count == 2 && counta == 2) {
            if (mAllPointArray.contains(new Point(x - 2, y + 2)) && mAllPointArray.contains(new Point(x + 2, y - 2))) {
                return true;
            }
        }
        return false;
    }

    private Boolean LiveTwoRight(int x, int y, List<Point> points) {
        int count = 1;
        int counta = 1;
        int num = 3;
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == num) {
            if (mAllPointArray.contains(new Point(x - 3, y - 3)) && mAllPointArray.contains(new Point(x + 1, y + 1))) {
                return true;
            }
        }
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                counta++;
            } else {
                break;
            }
        }
        if (counta == num) {
            if (mAllPointArray.contains(new Point(x + 3, y + 3)) && mAllPointArray.contains(new Point(x - 1, y - 1))) {
                return true;
            }
        }
        if (count == 2 && counta == 2) {
            if (mAllPointArray.contains(new Point(x - 2, y - 2)) && mAllPointArray.contains(new Point(x + 2, y + 2))) {
                return true;
            }
        }
        return false;
    }

    //各方向判断这个点是不是活三
    private Boolean LiveThreeHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        int counta = 1;
        int num = 4;
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == num) {
            if (mAllPointArray.contains(new Point(x + 4, y)) && mAllPointArray.contains(new Point(x - 1, y))) {
                return true;
            }
        }
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x - i, y))) {
                counta++;
            } else {
                break;
            }
        }
        if (counta == num) {
            if (mAllPointArray.contains(new Point(x - 4, y)) && mAllPointArray.contains(new Point(x + 1, y))) {
                return true;
            }
        }
        if (count == 3 && counta == 2) {
            if (mAllPointArray.contains(new Point(x + 3, y)) && mAllPointArray.contains(new Point(x - 2, y))) {
                return true;
            }
        }
        if (count == 2 && counta == 3) {
            if (mAllPointArray.contains(new Point(x + 2, y)) && mAllPointArray.contains(new Point(x + 3, y))) {
                return true;
            }
        }
        return false;
    }

    private Boolean LiveThreeVertical(int x, int y, List<Point> points) {
        int count = 1;
        int counta = 1;
        int num = 4;
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == num) {
            if (mAllPointArray.contains(new Point(x, y + 4)) && mAllPointArray.contains(new Point(x, y - 1))) {
                return true;
            }
        }
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x, y - i))) {
                counta++;
            } else {
                break;
            }
        }
        if (counta == num) {
            if (mAllPointArray.contains(new Point(x, y - 4)) && mAllPointArray.contains(new Point(x, y + 1))) {
                return true;
            }
        }
        if (count == 3 && counta == 2) {
            if (mAllPointArray.contains(new Point(x, y + 3)) && mAllPointArray.contains(new Point(x, y - 2))) {
                return true;
            }
        }
        if (count == 2 && counta == 3) {
            if (mAllPointArray.contains(new Point(x, y + 2)) && mAllPointArray.contains(new Point(x, y - 3))) {
                return true;
            }
        }
        return false;
    }

    private Boolean LiveThreeLeft(int x, int y, List<Point> points) {
        int count = 1;
        int counta = 1;
        int num = 4;
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == num) {
            if (mAllPointArray.contains(new Point(x - 4, y + 4)) && mAllPointArray.contains(new Point(x + 1, y - 1))) {
                return true;
            }
        }
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                counta++;
            } else {
                break;
            }
        }
        if (counta == num) {
            if (mAllPointArray.contains(new Point(x + 4, y - 4)) && mAllPointArray.contains(new Point(x - 1, y + 1))) {
                return true;
            }
        }
        if (count == 3 && counta == 2) {
            if (mAllPointArray.contains(new Point(x - 3, y + 3)) && mAllPointArray.contains(new Point(x + 2, y - 2))) {
                return true;
            }
        }
        if (count == 2 && counta == 3) {
            if (mAllPointArray.contains(new Point(x - 2, y + 2)) && mAllPointArray.contains(new Point(x + 3, y - 3))) {
                return true;
            }
        }
        return false;
    }

    private Boolean LiveThreeRight(int x, int y, List<Point> points) {
        int count = 1;
        int counta = 1;
        int num = 4;
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == num) {
            if (mAllPointArray.contains(new Point(x - 4, y - 4)) && mAllPointArray.contains(new Point(x + 1, y + 1))) {
                return true;
            }
        }
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                counta++;
            } else {
                break;
            }
        }
        if (counta == num) {
            if (mAllPointArray.contains(new Point(x + 4, y + 4)) && mAllPointArray.contains(new Point(x - 1, y - 1))) {
                return true;
            }
        }
        if (count == 3 && counta == 2) {
            if (mAllPointArray.contains(new Point(x - 3, y - 3)) && mAllPointArray.contains(new Point(x + 2, y + 2))) {
                return true;
            }
        }
        if (count == 2 && counta == 3) {
            if (mAllPointArray.contains(new Point(x - 2, y - 2)) && mAllPointArray.contains(new Point(x + 3, y + 3))) {
                return true;
            }
        }
        return false;
    }


    //判断是否可以形成两个活三的，第一种情况
    private Boolean allThreeAndLiveThree(int x, int y, List<Point> points) {
        boolean H = false, V = false, L = false, R = false;
        H = LiveThreeAndLiveThreeHorizontal(x, y, points);
        V = LiveThreeAndLiveThreeVertical(x, y, points);
        L = LiveThreeAndLiveThreeLeft(x, y, points);
        R = LiveThreeAndLiveThreeRight(x, y, points);
        if (H || L || V || R) {
            return true;
        }
        return false;
    }

    private Boolean LiveThreeAndLiveThreeHorizontal(int x, int y, List<Point> points) {
        if (mAllPointArray.contains(new Point(x - 4, y)) && mAllPointArray.contains(new Point(x - 1, y)) && mAllPointArray.contains(new Point(x + 1, y))
                && points.contains(new Point(x - 2, y)) && points.contains(new Point(x - 3, y))) {
            boolean dtvh = false;
            boolean dtvl = false;
            boolean dtvr = false;
            dtvh = LiveThreeVertical(x, y, points);
            dtvl = LiveThreeLeft(x, y, points);
            dtvr = LiveThreeRight(x, y, points);
            if (dtvh || dtvl || dtvr) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x - 4, y)) && mAllPointArray.contains(new Point(x - 2, y)) && mAllPointArray.contains(new Point(x + 1, y))
                && points.contains(new Point(x - 1, y)) && points.contains(new Point(x - 3, y))) {
            boolean dtvha = false;
            boolean dtvla = false;
            boolean dtvra = false;
            dtvha = LiveThreeVertical(x, y, points);
            dtvla = LiveThreeLeft(x, y, points);
            dtvra = LiveThreeRight(x, y, points);
            if (dtvha || dtvla || dtvra) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x + 4, y)) && mAllPointArray.contains(new Point(x - 1, y)) && mAllPointArray.contains(new Point(x + 1, y))
                && points.contains(new Point(x + 2, y)) && points.contains(new Point(x + 3, y))) {
            boolean dtvhb = false;
            boolean dtvlb = false;
            boolean dtvrb = false;
            dtvhb = LiveThreeVertical(x, y, points);
            dtvlb = LiveThreeLeft(x, y, points);
            dtvrb = LiveThreeRight(x, y, points);
            if (dtvhb || dtvlb || dtvrb) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x + 4, y)) && mAllPointArray.contains(new Point(x - 1, y)) && mAllPointArray.contains(new Point(x + 2, y))
                && points.contains(new Point(x + 1, y)) && points.contains(new Point(x + 3, y))) {
            boolean dtvhc = false;
            boolean dtvlc = false;
            boolean dtvrc = false;
            dtvhc = LiveThreeVertical(x, y, points);
            dtvlc = LiveThreeLeft(x, y, points);
            dtvrc = LiveThreeRight(x, y, points);
            if (dtvhc || dtvlc || dtvrc) {
                return true;
            }
        }
        return false;
    }

    private Boolean LiveThreeAndLiveThreeVertical(int x, int y, List<Point> points) {
        if (mAllPointArray.contains(new Point(x, y - 4)) && mAllPointArray.contains(new Point(x, y - 1)) && mAllPointArray.contains(new Point(x, y + 1))
                && points.contains(new Point(x, y - 2)) && points.contains(new Point(x, y - 3))) {
            boolean dtvh = false;
            boolean dtvl = false;
            boolean dtvr = false;
            dtvh = LiveThreeHorizontal(x, y, points);
            dtvl = LiveThreeLeft(x, y, points);
            dtvr = LiveThreeRight(x, y, points);
            if (dtvh || dtvl || dtvr) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x, y - 4)) && mAllPointArray.contains(new Point(x, y - 2)) && mAllPointArray.contains(new Point(x, y + 1))
                && points.contains(new Point(x, y - 1)) && points.contains(new Point(x, y - 3))) {
            boolean dtvha = false;
            boolean dtvla = false;
            boolean dtvra = false;
            dtvha = LiveThreeHorizontal(x, y, points);
            dtvla = LiveThreeLeft(x, y, points);
            dtvra = LiveThreeRight(x, y, points);
            if (dtvha || dtvla || dtvra) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x, y + 4)) && mAllPointArray.contains(new Point(x, y - 1)) && mAllPointArray.contains(new Point(x, y + 1))
                && points.contains(new Point(x, y + 2)) && points.contains(new Point(x, y + 3))) {
            boolean dtvhb = false;
            boolean dtvlb = false;
            boolean dtvrb = false;
            dtvhb = LiveThreeHorizontal(x, y, points);
            dtvlb = LiveThreeLeft(x, y, points);
            dtvrb = LiveThreeRight(x, y, points);
            if (dtvhb || dtvlb || dtvrb) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x, y + 4)) && mAllPointArray.contains(new Point(x, y - 1)) && mAllPointArray.contains(new Point(x, y + 2))
                && points.contains(new Point(x, y + 1)) && points.contains(new Point(x, y + 3))) {
            boolean dtvhc = false;
            boolean dtvlc = false;
            boolean dtvrc = false;
            dtvhc = LiveThreeHorizontal(x, y, points);
            dtvlc = LiveThreeLeft(x, y, points);
            dtvrc = LiveThreeRight(x, y, points);
            if (dtvhc || dtvlc || dtvrc) {
                return true;
            }
        }
        return false;
    }

    private Boolean LiveThreeAndLiveThreeLeft(int x, int y, List<Point> points) {
        if (mAllPointArray.contains(new Point(x - 4, y + 4)) && mAllPointArray.contains(new Point(x - 1, y + 1)) && mAllPointArray.contains(new Point(x + 1, y - 1))
                && points.contains(new Point(x - 2, y + 2)) && points.contains(new Point(x - 3, y + 3))) {
            boolean dtvh = false;
            boolean dtvl = false;
            boolean dtvr = false;
            dtvh = LiveThreeHorizontal(x, y, points);
            dtvl = LiveThreeVertical(x, y, points);
            dtvr = LiveThreeRight(x, y, points);
            if (dtvh || dtvl || dtvr) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x - 4, y + 4)) && mAllPointArray.contains(new Point(x - 2, y + 2)) && mAllPointArray.contains(new Point(x + 1, y - 1))
                && points.contains(new Point(x - 1, y + 1)) && points.contains(new Point(x - 3, y + 3))) {
            boolean dtvha = false;
            boolean dtvla = false;
            boolean dtvra = false;
            dtvha = LiveThreeHorizontal(x, y, points);
            dtvla = LiveThreeVertical(x, y, points);
            dtvra = LiveThreeRight(x, y, points);
            if (dtvha || dtvla || dtvra) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x + 4, y - 4)) && mAllPointArray.contains(new Point(x + 1, y - 1)) && mAllPointArray.contains(new Point(x - 1, y + 1))
                && points.contains(new Point(x + 2, y - 2)) && points.contains(new Point(x + 3, y - 3))) {
            boolean dtvhb = false;
            boolean dtvlb = false;
            boolean dtvrb = false;
            dtvhb = LiveThreeHorizontal(x, y, points);
            dtvlb = LiveThreeVertical(x, y, points);
            dtvrb = LiveThreeRight(x, y, points);
            if (dtvhb || dtvlb || dtvrb) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x + 4, y - 4)) && mAllPointArray.contains(new Point(x + 2, y - 2)) && mAllPointArray.contains(new Point(x - 1, y + 1))
                && points.contains(new Point(x + 1, y - 1)) && points.contains(new Point(x + 3, y - 3))) {
            boolean dtvhc = false;
            boolean dtvlc = false;
            boolean dtvrc = false;
            dtvhc = LiveThreeHorizontal(x, y, points);
            dtvlc = LiveThreeVertical(x, y, points);
            dtvrc = LiveThreeRight(x, y, points);
            if (dtvhc || dtvlc || dtvrc) {
                return true;
            }
        }
        return false;
    }

    private Boolean LiveThreeAndLiveThreeRight(int x, int y, List<Point> points) {
        if (mAllPointArray.contains(new Point(x + 4, y + 4)) && mAllPointArray.contains(new Point(x + 1, y + 1)) && mAllPointArray.contains(new Point(x - 1, y - 1))
                && points.contains(new Point(x + 2, y + 2)) && points.contains(new Point(x + 3, y + 3))) {
            boolean dtvh = false;
            boolean dtvl = false;
            boolean dtvr = false;
            dtvh = LiveThreeHorizontal(x, y, points);
            dtvl = LiveThreeLeft(x, y, points);
            dtvr = LiveThreeVertical(x, y, points);
            if (dtvh || dtvl || dtvr) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x + 4, y + 4)) && mAllPointArray.contains(new Point(x + 2, y + 2)) && mAllPointArray.contains(new Point(x - 1, y - 1))
                && points.contains(new Point(x + 1, y + 1)) && points.contains(new Point(x + 3, y + 3))) {
            boolean dtvha = false;
            boolean dtvla = false;
            boolean dtvra = false;
            dtvha = LiveThreeHorizontal(x, y, points);
            dtvla = LiveThreeLeft(x, y, points);
            dtvra = LiveThreeVertical(x, y, points);
            if (dtvha || dtvla || dtvra) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x - 4, y - 4)) && mAllPointArray.contains(new Point(x - 1, y - 1)) && mAllPointArray.contains(new Point(x + 1, y + 1))
                && points.contains(new Point(x - 2, y - 2)) && points.contains(new Point(x - 3, y - 3))) {
            boolean dtvhb = false;
            boolean dtvlb = false;
            boolean dtvrb = false;
            dtvhb = LiveThreeHorizontal(x, y, points);
            dtvlb = LiveThreeLeft(x, y, points);
            dtvrb = LiveThreeVertical(x, y, points);
            if (dtvhb || dtvlb || dtvrb) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x - 4, y - 4)) && mAllPointArray.contains(new Point(x - 2, y - 2)) && mAllPointArray.contains(new Point(x + 1, y + 1))
                && points.contains(new Point(x - 1, y - 1)) && points.contains(new Point(x - 3, y - 3))) {
            boolean dtvhc = false;
            boolean dtvlc = false;
            boolean dtvrc = false;
            dtvhc = LiveThreeHorizontal(x, y, points);
            dtvlc = LiveThreeLeft(x, y, points);
            dtvrc = LiveThreeVertical(x, y, points);
            if (dtvhc || dtvlc || dtvrc) {
                return true;
            }
        }
        return false;
    }

    //判断是否可以形成两个活三的，第二种情况 有问题 待解决
    private Boolean allThreeAndLiveThreeA(int x, int y, List<Point> points) {
        boolean H = false, V = false, L = false, R = false;
        H = LiveThreeAndLiveThreeHorizontalA(x, y, points);
        V = LiveThreeAndLiveThreeVerticalA(x, y, points);
        L = LiveThreeAndLiveThreeLeftA(x, y, points);
        R = LiveThreeAndLiveThreeRightA(x, y, points);
        if ((H && V) || (H && L) || (H && R) || (L && V) || (L && R) || (V && R)) {
            return true;
        }
        return false;
    }

    private Boolean LiveThreeAndLiveThreeHorizontalA(int x, int y, List<Point> points) {
        if (mAllPointArray.contains(new Point(x - 4, y)) && mAllPointArray.contains(new Point(x - 1, y)) && mAllPointArray.contains(new Point(x + 1, y))
                && points.contains(new Point(x - 2, y)) && points.contains(new Point(x - 3, y))) {

            return true;

        }
        if (mAllPointArray.contains(new Point(x - 4, y)) && mAllPointArray.contains(new Point(x - 2, y)) && mAllPointArray.contains(new Point(x + 1, y))
                && points.contains(new Point(x - 1, y)) && points.contains(new Point(x - 3, y))) {

            return true;

        }
        if (mAllPointArray.contains(new Point(x + 4, y)) && mAllPointArray.contains(new Point(x - 1, y)) && mAllPointArray.contains(new Point(x + 1, y))
                && points.contains(new Point(x + 2, y)) && points.contains(new Point(x + 3, y))) {

            return true;
        }

        if (mAllPointArray.contains(new Point(x + 4, y)) && mAllPointArray.contains(new Point(x - 1, y)) && mAllPointArray.contains(new Point(x + 2, y))
                && points.contains(new Point(x + 1, y)) && points.contains(new Point(x + 3, y))) {

            return true;

        }
        return false;
    }

    private Boolean LiveThreeAndLiveThreeVerticalA(int x, int y, List<Point> points) {
        if (mAllPointArray.contains(new Point(x, y - 4)) && mAllPointArray.contains(new Point(x, y - 1)) && mAllPointArray.contains(new Point(x, y + 1))
                && points.contains(new Point(x, y - 2)) && points.contains(new Point(x, y - 3))) {

            return true;

        }
        if (mAllPointArray.contains(new Point(x, y - 4)) && mAllPointArray.contains(new Point(x, y - 2)) && mAllPointArray.contains(new Point(x, y + 1))
                && points.contains(new Point(x, y - 1)) && points.contains(new Point(x, y - 3))) {

            return true;

        }
        if (mAllPointArray.contains(new Point(x, y + 4)) && mAllPointArray.contains(new Point(x, y - 1)) && mAllPointArray.contains(new Point(x, y + 1))
                && points.contains(new Point(x, y + 2)) && points.contains(new Point(x, y + 3))) {

            return true;

        }
        if (mAllPointArray.contains(new Point(x, y + 4)) && mAllPointArray.contains(new Point(x, y - 1)) && mAllPointArray.contains(new Point(x, y + 2))
                && points.contains(new Point(x, y + 1)) && points.contains(new Point(x, y + 3))) {

            return true;

        }
        return false;
    }

    private Boolean LiveThreeAndLiveThreeLeftA(int x, int y, List<Point> points) {
        if (mAllPointArray.contains(new Point(x - 4, y + 4)) && mAllPointArray.contains(new Point(x - 1, y + 1)) && mAllPointArray.contains(new Point(x + 1, y - 1))
                && points.contains(new Point(x - 2, y + 2)) && points.contains(new Point(x - 3, y + 3))) {

            return true;

        }
        if (mAllPointArray.contains(new Point(x - 4, y + 4)) && mAllPointArray.contains(new Point(x - 2, y + 2)) && mAllPointArray.contains(new Point(x + 1, y - 1))
                && points.contains(new Point(x - 1, y + 1)) && points.contains(new Point(x - 3, y + 3))) {

            return true;

        }
        if (mAllPointArray.contains(new Point(x + 4, y - 4)) && mAllPointArray.contains(new Point(x + 1, y - 1)) && mAllPointArray.contains(new Point(x - 1, y + 1))
                && points.contains(new Point(x + 2, y - 2)) && points.contains(new Point(x + 3, y - 3))) {

            return true;

        }
        if (mAllPointArray.contains(new Point(x + 4, y - 4)) && mAllPointArray.contains(new Point(x + 2, y - 2)) && mAllPointArray.contains(new Point(x - 1, y + 1))
                && points.contains(new Point(x + 1, y - 1)) && points.contains(new Point(x + 3, y - 3))) {


            return true;

        }
        return false;
    }

    private Boolean LiveThreeAndLiveThreeRightA(int x, int y, List<Point> points) {
        if (mAllPointArray.contains(new Point(x + 4, y + 4)) && mAllPointArray.contains(new Point(x + 1, y + 1)) && mAllPointArray.contains(new Point(x - 1, y - 1))
                && points.contains(new Point(x + 2, y + 2)) && points.contains(new Point(x + 3, y + 3))) {

            return true;

        }
        if (mAllPointArray.contains(new Point(x + 4, y + 4)) && mAllPointArray.contains(new Point(x + 2, y + 2)) && mAllPointArray.contains(new Point(x - 1, y - 1))
                && points.contains(new Point(x + 1, y + 1)) && points.contains(new Point(x + 3, y + 3))) {

            return true;

        }
        if (mAllPointArray.contains(new Point(x - 4, y - 4)) && mAllPointArray.contains(new Point(x - 1, y - 1)) && mAllPointArray.contains(new Point(x + 1, y + 1))
                && points.contains(new Point(x - 2, y - 2)) && points.contains(new Point(x - 3, y - 3))) {

            return true;

        }
        if (mAllPointArray.contains(new Point(x - 4, y - 4)) && mAllPointArray.contains(new Point(x - 2, y - 2)) && mAllPointArray.contains(new Point(x + 1, y + 1))
                && points.contains(new Point(x - 1, y - 1)) && points.contains(new Point(x - 3, y - 3))) {

            return true;

        }
        return false;
    }


    //判断是否可以变成冲四又可以形成活三
    private Boolean allLiveThreeAndForu(int x, int y, List<Point> points) {
        boolean H = false, V = false, L = false, R = false;
        H = LiveThreeAndFourHorizontal(x, y, points);
        V = LiveThreeAndFourVertical(x, y, points);
        L = LiveThreeAndFourLeft(x, y, points);
        R = LiveThreeAndFourRight(x, y, points);
        if (H || L || V || R) {
            return true;
        }
        return false;
    }

    private Boolean LiveThreeAndFourHorizontal(int x, int y, List<Point> points) {
        if (mAllPointArray.contains(new Point(x - 4, y)) && mAllPointArray.contains(new Point(x - 1, y)) && mAllPointArray.contains(new Point(x + 1, y))
                && points.contains(new Point(x - 2, y)) && points.contains(new Point(x - 3, y))) {
            boolean dtvh = false;
            boolean dtvl = false;
            boolean dtvr = false;
            dtvh = DeadThreeVertical(x, y, points);
            dtvl = DeadThreeLeft(x, y, points);
            dtvr = DeadThreeRight(x, y, points);
            if (dtvh || dtvl || dtvr) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x - 4, y)) && mAllPointArray.contains(new Point(x - 2, y)) && mAllPointArray.contains(new Point(x + 1, y))
                && points.contains(new Point(x - 1, y)) && points.contains(new Point(x - 3, y))) {
            boolean dtvha = false;
            boolean dtvla = false;
            boolean dtvra = false;
            dtvha = DeadThreeVertical(x, y, points);
            dtvla = DeadThreeLeft(x, y, points);
            dtvra = DeadThreeRight(x, y, points);
            if (dtvha || dtvla || dtvra) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x + 4, y)) && mAllPointArray.contains(new Point(x - 1, y)) && mAllPointArray.contains(new Point(x + 1, y))
                && points.contains(new Point(x + 2, y)) && points.contains(new Point(x + 3, y))) {
            boolean dtvhb = false;
            boolean dtvlb = false;
            boolean dtvrb = false;
            dtvhb = DeadThreeVertical(x, y, points);
            dtvlb = DeadThreeLeft(x, y, points);
            dtvrb = DeadThreeRight(x, y, points);
            if (dtvhb || dtvlb || dtvrb) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x + 4, y)) && mAllPointArray.contains(new Point(x - 1, y)) && mAllPointArray.contains(new Point(x + 2, y))
                && points.contains(new Point(x + 1, y)) && points.contains(new Point(x + 3, y))) {
            boolean dtvhc = false;
            boolean dtvlc = false;
            boolean dtvrc = false;
            dtvhc = DeadThreeVertical(x, y, points);
            dtvlc = DeadThreeLeft(x, y, points);
            dtvrc = DeadThreeRight(x, y, points);
            if (dtvhc || dtvlc || dtvrc) {
                return true;
            }
        }
        return false;
    }

    private Boolean LiveThreeAndFourVertical(int x, int y, List<Point> points) {
        if (mAllPointArray.contains(new Point(x, y - 4)) && mAllPointArray.contains(new Point(x, y - 1)) && mAllPointArray.contains(new Point(x, y + 1))
                && points.contains(new Point(x, y - 2)) && points.contains(new Point(x, y - 3))) {
            boolean dtvh = false;
            boolean dtvl = false;
            boolean dtvr = false;
            dtvh = DeadThreeHorizontal(x, y, points);
            dtvl = DeadThreeLeft(x, y, points);
            dtvr = DeadThreeRight(x, y, points);
            if (dtvh || dtvl || dtvr) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x, y - 4)) && mAllPointArray.contains(new Point(x, y - 2)) && mAllPointArray.contains(new Point(x, y + 1))
                && points.contains(new Point(x, y - 1)) && points.contains(new Point(x, y - 3))) {
            boolean dtvha = false;
            boolean dtvla = false;
            boolean dtvra = false;
            dtvha = DeadThreeHorizontal(x, y, points);
            dtvla = DeadThreeLeft(x, y, points);
            dtvra = DeadThreeRight(x, y, points);
            if (dtvha || dtvla || dtvra) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x, y + 4)) && mAllPointArray.contains(new Point(x, y - 1)) && mAllPointArray.contains(new Point(x, y + 1))
                && points.contains(new Point(x, y + 2)) && points.contains(new Point(x, y + 3))) {
            boolean dtvhb = false;
            boolean dtvlb = false;
            boolean dtvrb = false;
            dtvhb = DeadThreeHorizontal(x, y, points);
            dtvlb = DeadThreeLeft(x, y, points);
            dtvrb = DeadThreeRight(x, y, points);
            if (dtvhb || dtvlb || dtvrb) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x, y + 4)) && mAllPointArray.contains(new Point(x, y - 1)) && mAllPointArray.contains(new Point(x, y + 2))
                && points.contains(new Point(x, y + 1)) && points.contains(new Point(x, y + 3))) {
            boolean dtvhc = false;
            boolean dtvlc = false;
            boolean dtvrc = false;
            dtvhc = DeadThreeHorizontal(x, y, points);
            dtvlc = DeadThreeLeft(x, y, points);
            dtvrc = DeadThreeRight(x, y, points);
            if (dtvhc || dtvlc || dtvrc) {
                return true;
            }
        }
        return false;
    }

    private Boolean LiveThreeAndFourLeft(int x, int y, List<Point> points) {
        if (mAllPointArray.contains(new Point(x - 4, y + 4)) && mAllPointArray.contains(new Point(x - 1, y + 1)) && mAllPointArray.contains(new Point(x + 1, y - 1))
                && points.contains(new Point(x - 2, y + 2)) && points.contains(new Point(x - 3, y + 3))) {
            boolean dtvh = false;
            boolean dtvl = false;
            boolean dtvr = false;
            dtvh = DeadThreeHorizontal(x, y, points);
            dtvl = DeadThreeVertical(x, y, points);
            dtvr = DeadThreeRight(x, y, points);
            if (dtvh || dtvl || dtvr) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x - 4, y + 4)) && mAllPointArray.contains(new Point(x - 2, y + 2)) && mAllPointArray.contains(new Point(x + 1, y - 1))
                && points.contains(new Point(x - 1, y + 1)) && points.contains(new Point(x - 3, y + 3))) {
            boolean dtvha = false;
            boolean dtvla = false;
            boolean dtvra = false;
            dtvha = DeadThreeHorizontal(x, y, points);
            dtvla = DeadThreeVertical(x, y, points);
            dtvra = DeadThreeRight(x, y, points);
            if (dtvha || dtvla || dtvra) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x + 4, y - 4)) && mAllPointArray.contains(new Point(x + 1, y - 1)) && mAllPointArray.contains(new Point(x - 1, y + 1))
                && points.contains(new Point(x + 2, y - 2)) && points.contains(new Point(x + 3, y - 3))) {
            boolean dtvhb = false;
            boolean dtvlb = false;
            boolean dtvrb = false;
            dtvhb = DeadThreeHorizontal(x, y, points);
            dtvlb = DeadThreeVertical(x, y, points);
            dtvrb = DeadThreeRight(x, y, points);
            if (dtvhb || dtvlb || dtvrb) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x + 4, y - 4)) && mAllPointArray.contains(new Point(x + 2, y - 2)) && mAllPointArray.contains(new Point(x - 1, y + 1))
                && points.contains(new Point(x + 1, y - 1)) && points.contains(new Point(x + 3, y - 3))) {
            boolean dtvhc = false;
            boolean dtvlc = false;
            boolean dtvrc = false;
            dtvhc = DeadThreeHorizontal(x, y, points);
            dtvlc = DeadThreeVertical(x, y, points);
            dtvrc = DeadThreeRight(x, y, points);
            if (dtvhc || dtvlc || dtvrc) {
                return true;
            }
        }
        return false;
    }

    private Boolean LiveThreeAndFourRight(int x, int y, List<Point> points) {
        if (mAllPointArray.contains(new Point(x + 4, y + 4)) && mAllPointArray.contains(new Point(x + 1, y + 1)) && mAllPointArray.contains(new Point(x - 1, y - 1))
                && points.contains(new Point(x + 2, y + 2)) && points.contains(new Point(x + 3, y + 3))) {
            boolean dtvh = false;
            boolean dtvl = false;
            boolean dtvr = false;
            dtvh = DeadThreeHorizontal(x, y, points);
            dtvl = DeadThreeLeft(x, y, points);
            dtvr = DeadThreeVertical(x, y, points);
            if (dtvh || dtvl || dtvr) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x + 4, y + 4)) && mAllPointArray.contains(new Point(x + 2, y + 2)) && mAllPointArray.contains(new Point(x - 1, y - 1))
                && points.contains(new Point(x + 1, y + 1)) && points.contains(new Point(x + 3, y + 3))) {
            boolean dtvha = false;
            boolean dtvla = false;
            boolean dtvra = false;
            dtvha = DeadThreeHorizontal(x, y, points);
            dtvla = DeadThreeLeft(x, y, points);
            dtvra = DeadThreeVertical(x, y, points);
            if (dtvha || dtvla || dtvra) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x - 4, y - 4)) && mAllPointArray.contains(new Point(x - 1, y - 1)) && mAllPointArray.contains(new Point(x + 1, y + 1))
                && points.contains(new Point(x - 2, y - 2)) && points.contains(new Point(x - 3, y - 3))) {
            boolean dtvhb = false;
            boolean dtvlb = false;
            boolean dtvrb = false;
            dtvhb = DeadThreeHorizontal(x, y, points);
            dtvlb = DeadThreeLeft(x, y, points);
            dtvrb = DeadThreeVertical(x, y, points);
            if (dtvhb || dtvlb || dtvrb) {
                return true;
            }
        }
        if (mAllPointArray.contains(new Point(x - 4, y - 4)) && mAllPointArray.contains(new Point(x - 2, y - 2)) && mAllPointArray.contains(new Point(x + 1, y + 1))
                && points.contains(new Point(x - 1, y - 1)) && points.contains(new Point(x - 3, y - 3))) {
            boolean dtvhc = false;
            boolean dtvlc = false;
            boolean dtvrc = false;
            dtvhc = DeadThreeHorizontal(x, y, points);
            dtvlc = DeadThreeLeft(x, y, points);
            dtvrc = DeadThreeVertical(x, y, points);
            if (dtvhc || dtvlc || dtvrc) {
                return true;
            }
        }
        return false;
    }

    //判断是否为死三 只要能形成冲四就可以
    private Boolean DeadThreeHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        int counta = 1;
        int num = 4;
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == num) {
            if (mAllPointArray.contains(new Point(x + 4, y)) || mAllPointArray.contains(new Point(x - 1, y))) {
                return true;
            }
        }
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x - i, y))) {
                counta++;
            } else {
                break;
            }
        }
        if (counta == num) {
            if (mAllPointArray.contains(new Point(x - 4, y)) || mAllPointArray.contains(new Point(x + 1, y))) {
                return true;
            }
        }
        if (count == 3 && counta == 2) {
            if (mAllPointArray.contains(new Point(x + 3, y)) || mAllPointArray.contains(new Point(x - 2, y))) {
                return true;
            }
        }
        if (count == 2 && counta == 3) {
            if (mAllPointArray.contains(new Point(x + 2, y)) || mAllPointArray.contains(new Point(x + 3, y))) {
                return true;
            }
        }
        return false;
    }

    private Boolean DeadThreeVertical(int x, int y, List<Point> points) {
        int count = 1;
        int counta = 1;
        int num = 4;
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == num) {
            if (mAllPointArray.contains(new Point(x, y + 4)) || mAllPointArray.contains(new Point(x, y - 1))) {
                return true;
            }
        }
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x, y - i))) {
                counta++;
            } else {
                break;
            }
        }
        if (counta == num) {
            if (mAllPointArray.contains(new Point(x, y - 4)) || mAllPointArray.contains(new Point(x, y + 1))) {
                return true;
            }
        }
        if (count == 3 && counta == 2) {
            if (mAllPointArray.contains(new Point(x, y + 3)) || mAllPointArray.contains(new Point(x, y - 2))) {
                return true;
            }
        }
        if (count == 2 && counta == 3) {
            if (mAllPointArray.contains(new Point(x, y + 2)) || mAllPointArray.contains(new Point(x, y - 3))) {
                return true;
            }
        }
        return false;
    }

    private Boolean DeadThreeLeft(int x, int y, List<Point> points) {
        int count = 1;
        int counta = 1;
        int num = 4;
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == num) {
            if (mAllPointArray.contains(new Point(x - 4, y + 4)) || mAllPointArray.contains(new Point(x + 1, y - 1))) {
                return true;
            }
        }
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                counta++;
            } else {
                break;
            }
        }
        if (counta == num) {
            if (mAllPointArray.contains(new Point(x + 4, y - 4)) || mAllPointArray.contains(new Point(x - 1, y + 1))) {
                return true;
            }
        }
        if (count == 3 && counta == 2) {
            if (mAllPointArray.contains(new Point(x - 3, y + 3)) || mAllPointArray.contains(new Point(x + 2, y - 2))) {
                return true;
            }
        }
        if (count == 2 && counta == 3) {
            if (mAllPointArray.contains(new Point(x - 2, y + 2)) || mAllPointArray.contains(new Point(x + 3, y - 3))) {
                return true;
            }
        }
        return false;
    }

    private Boolean DeadThreeRight(int x, int y, List<Point> points) {
        int count = 1;
        int counta = 1;
        int num = 4;
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == num) {
            if (mAllPointArray.contains(new Point(x - 4, y - 4)) || mAllPointArray.contains(new Point(x + 1, y + 1))) {
                return true;
            }
        }
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                counta++;
            } else {
                break;
            }
        }
        if (counta == num) {
            if (mAllPointArray.contains(new Point(x + 4, y + 4)) || mAllPointArray.contains(new Point(x - 1, y - 1))) {
                return true;
            }
        }
        if (count == 3 && counta == 2) {
            if (mAllPointArray.contains(new Point(x - 3, y - 3)) || mAllPointArray.contains(new Point(x + 2, y + 2))) {
                return true;
            }
        }
        if (count == 2 && counta == 3) {
            if (mAllPointArray.contains(new Point(x - 2, y - 2)) || mAllPointArray.contains(new Point(x + 3, y + 3))) {
                return true;
            }
        }
        return false;
    }


    //判断横向是否已经num子连珠
    private Boolean checkHorizontal(int num, int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == num) {
            return true;
        }
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == num) {
            return true;
        }
        return false;
    }

    //判断纵向是否已经num子连珠
    private Boolean checkVertical(int num, int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == num) {
            return true;
        }
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == num) {
            return true;
        }
        return false;
    }

    //判断坐斜是否已经num子连珠
    private Boolean checkLeft(int num, int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == num) {
            return true;
        }
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == num) {
            return true;
        }
        return false;
    }

    //判断友协是否已经num子连珠
    private Boolean checkRight(int num, int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == num) {
            return true;
        }
        for (int i = 1; i < num; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == num) {
            return true;
        }
        return false;
    }

    private Boolean checkNumInLine(int num, List<Point> points, Point p) {

        int x = p.x;
        int y = p.y;

        Boolean win = checkHorizontal(num, x, y, points);
        if (win) {
            return true;
        }
        win = checkLeft(num, x, y, points);
        if (win) {
            return true;
        }
        win = checkVertical(num, x, y, points);
        if (win) {
            return true;
        }
        win = checkRight(num, x, y, points);
        if (win) {
            return true;
        }
        return false;
    }

    private Boolean checkFiveInLine(int num, List<Point> points) {
        for (Point p : points) {
            int x = p.x;
            int y = p.y;
            Boolean win = checkHorizontal(num, x, y, points);
            if (win) {
                return true;
            }
            win = checkLeft(num, x, y, points);
            if (win) {
                return true;
            }
            win = checkVertical(num, x, y, points);
            if (win) {
                return true;
            }
            win = checkRight(num, x, y, points);
            if (win) {
                return true;
            }

        }
        return false;
    }


    private void drawPiece(Canvas canvas) {
        //绘制白子
        for (int i = 0, n = mWhiteArray.size(); i < n; i++) {

            Point whitePoint = mWhiteArray.get(i);
            //绘制的棋子之间要有空隙
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x + (1 - PieceSize) / 2) * mLineHeight,
                    (whitePoint.y + (1 - PieceSize) / 2) * mLineHeight, null);
        }
        //绘制黑子
        for (int i = 0, n = mBlackArray.size(); i < n; i++) {
            Point lastPoint = mBlackArray.get(n - 1);
            Point blackPoint = mBlackArray.get(i);
            //绘制的棋子之间要有空隙
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x + (1 - PieceSize) / 2) * mLineHeight,
                    (blackPoint.y + (1 - PieceSize) / 2) * mLineHeight, null);
            canvas.drawBitmap(mBlackFirstPiece, (lastPoint.x + (1 - PieceSize) / 2) * mLineHeight,
                    (lastPoint.y + (1 - PieceSize) / 2) * mLineHeight, null);
        }


    }

    private void drawBoard(Canvas canvas) {
        //绘制棋盘
        int W = mPanelWidth;//棋盘的宽度赋值给成员变量w
        float LineHeight = mLineHeight;//棋盘每一小格长宽赋值给成员变量LineHeight

        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (LineHeight / 2);
            int endX = (int) (W - LineHeight / 2);
            int y = (int) ((0.5 + i) * LineHeight);
            canvas.drawLine(startX, y, endX, y, mPaint);//绘制横线
            canvas.drawLine(y, startX, y, endX, mPaint);//绘制纵线
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
        if (state instanceof Bundle) {
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

    public void startAgain() {
        mBlackArray.clear();
        mWhiteArray.clear();
        mIsGameOver = false;
        Log.e("isGameOver", "false");
        mWhiteIsWinner = false;
        showDialog = true;
        mUserPointArray.clear();
        getAllPoint = true;
        mIsWhite = true;
        mAllPointArray.clear();
        invalidate();
    }

    public void clickSound() {
        if (soundst) mSoundPool.play(soundId, 1, 1, 0, 0, 1);
    }

    public void setSoundst(boolean soundst) {
        this.soundst = soundst;
    }

    public void customDialog(String string) {
        final Dialog dialog = new Dialog(getContext(), R.style.CustomDialog);
        dialog.setContentView(R.layout.custom_dialog);
        TextView mTextView = (TextView) dialog.findViewById(R.id.tv);
        mTextView.setText(string);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void retract() {
        //悔棋
        if (mUserPointArray.size() != 0) {
            Point i = mUserPointArray.get(mUserPointArray.size() - 1);
            Point x = mUserPointArray.get(mUserPointArray.size() - 2);
            mBlackArray.remove(i);
            mWhiteArray.remove(x);
            mUserPointArray.remove(i);
            mUserPointArray.remove(x);
            mAllPointArray.add(i);
            mAllPointArray.add(x);
            invalidate();//重新绘制集合中的点
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

    }


}
