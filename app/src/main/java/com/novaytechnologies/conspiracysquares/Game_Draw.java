//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Random;

public class Game_Draw extends FrameLayout {
    Drawable SQUARE;
    ArrayList<Integer> ColorOtherPlayer = new ArrayList<>();

    boolean bStarted = false;

    int nCurrentPlayers = 0;

    int nWidth;
    int nHeight;
    int nWidth_Center;
    int nHeight_Center;
    long lMoveTick;
    long lMoveLast;
    long lMoveDelta;
    float fBoxSize;

    int nPlayer_SQUARE_COLOR;
    float fPlayer_PosX = 0f;
    float fPlayer_PosY = 0f;
    float fPlayer_MoveX = 0f;
    float fPlayer_MoveY = 0f;
    float fPlayer_Speed = 0f;
    float fPlayer_SpeedX = 0f;
    float fPlayer_SpeedY = 0f;
    float fPlayer_Time = 0f;

    ArrayList<Float> fPlayersX = new ArrayList<>();
    ArrayList<Float> fPlayersY = new ArrayList<>();

    public Game_Draw(Context ctx) {
        this(ctx, null);
    }

    public Game_Draw(Context ctx, @Nullable AttributeSet attrs) {
        super(ctx, attrs);
        setWillNotDraw(false);
    }

    public Game_Draw(Context ctx, @Nullable AttributeSet attrs, int defstyle) {
        super(ctx, attrs, defstyle);
        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        nWidth = MeasureSpec.getSize(widthMeasureSpec);
        nHeight = MeasureSpec.getSize(heightMeasureSpec);

        nWidth_Center = (int)Math.round(nWidth/2.0);
        nHeight_Center = (int)Math.round(nHeight/2.0);
        if (!bStarted)
        {
            SQUARE = this.getResources().getDrawable(R.drawable.vec_square);
            Random randColor = new Random(System.currentTimeMillis());
            nPlayer_SQUARE_COLOR = Color.rgb(randColor.nextInt(255), randColor.nextInt(255), randColor.nextInt(255));
            SQUARE.setColorFilter(nPlayer_SQUARE_COLOR, PorterDuff.Mode.MULTIPLY);

            fPlayer_PosX = nWidth_Center;
            fPlayer_PosY = nHeight_Center;
            fPlayer_MoveX = fPlayer_PosX;
            fPlayer_MoveY = fPlayer_PosY;
            lMoveLast = System.currentTimeMillis();

            bStarted = true;
        }

        fBoxSize = (nWidth < nHeight) ? nWidth/20f : nHeight/20f;

        fPlayer_Speed = fBoxSize / 50f;

        setMeasuredDimension(nWidth, nHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN || System.currentTimeMillis() - lMoveTick > 100L)
        {
            fPlayer_MoveX = event.getX();
            fPlayer_MoveY = event.getY();

            float fPlayerXdist = fPlayer_MoveX - fPlayer_PosX;
            float fPlayerYdist = fPlayer_MoveY - fPlayer_PosY;
            float fPlayerDist = (float) (Math.sqrt(Math.pow(fPlayerXdist, 2) + Math.pow(fPlayerYdist, 2)));

            fPlayer_Time = fPlayerDist / fPlayer_Speed;

            fPlayer_SpeedX = fPlayerXdist / fPlayer_Time;
            fPlayer_SpeedY = fPlayerYdist / fPlayer_Time;

            lMoveLast = lMoveTick = System.currentTimeMillis();
        }
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            fPlayer_SpeedX = 0f;
            fPlayer_SpeedY = 0f;
            performClick();
        }
        return true;
    }

    @Override
    public boolean performClick()
    {
        super.performClick();
        return true;
    }

    private void DrawPlayer(Canvas canvas, Drawable square, float fX, float fY)
    {
        square.setBounds((int)(fX - fBoxSize), (int)(fY - fBoxSize), (int)(fX + fBoxSize), (int)(fY + fBoxSize));
        square.draw(canvas);
    }

    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        lMoveDelta = System.currentTimeMillis() - lMoveLast;

        fPlayer_PosX = fPlayer_PosX + fPlayer_SpeedX * lMoveDelta;
        fPlayer_PosY = fPlayer_PosY + fPlayer_SpeedY * lMoveDelta;

        lMoveLast = System.currentTimeMillis();

        DrawPlayer(canvas, SQUARE, fPlayer_PosX, fPlayer_PosY);

        this.invalidate();
    }
}
