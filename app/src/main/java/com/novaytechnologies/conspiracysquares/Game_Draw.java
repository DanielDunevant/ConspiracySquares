//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import java.util.ArrayList;

public class Game_Draw extends FrameLayout {
    Paint paintPlayer = new Paint(Paint.ANTI_ALIAS_FLAG);

    boolean bStarted = false;

    int nWidth;
    int nHeight;
    int nWidth_Center;
    int nHeight_Center;

    float fBoxSize;
    float fBoxMov;
    float fMoveX = 0;
    float fMoveY = 0;
    float fPlayerSpeedX = 1;
    float fPlayerSpeedY = 1;
    float fPlayerTime = 0;
    long lMoveStart;
    long lMoveDelta;

    float fPlayerSpeed = 20f;
    float fPlayerPosX = 0f;
    float fPlayerPosY = 0f;
    float fPlayerPosXstart = 0f;
    float fPlayerPosYstart = 0f;
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
            paintPlayer.setColor(Color.BLACK);
            paintPlayer.setStyle(Paint.Style.FILL);

            fPlayerPosX = nWidth_Center;
            fPlayerPosY = nHeight_Center;
            fMoveX = fPlayerPosX;
            fMoveY = fPlayerPosY;
            lMoveStart = System.currentTimeMillis();

            bStarted = true;
        }

        fBoxSize = (nWidth < nHeight) ? nWidth/20 : nHeight/20;
        fBoxMov = 1.5f * fBoxSize;

        fPlayerSpeed = fBoxSize / 50f;

        setMeasuredDimension(nWidth, nHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_UP || lMoveStart < System.currentTimeMillis() - 150)
        {
            fMoveX = event.getX();
            fMoveY = event.getY();
            fPlayerPosXstart = fPlayerPosX;
            fPlayerPosYstart = fPlayerPosY;

            float fPlayerXdist = fMoveX - fPlayerPosX;
            float fPlayerYdist = fMoveY - fPlayerPosY;
            float fPlayerDist = (float) (Math.sqrt(Math.pow(fPlayerXdist, 2) + Math.pow(fPlayerYdist, 2)));

            fPlayerTime = fPlayerDist / fPlayerSpeed;

            fPlayerSpeedX = fPlayerXdist / fPlayerTime;
            fPlayerSpeedY = fPlayerYdist / fPlayerTime;

            lMoveStart = System.currentTimeMillis();
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN)
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

    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        lMoveDelta = System.currentTimeMillis() - lMoveStart;

        if (fPlayerTime > lMoveDelta)
        {
            fPlayerPosX = fPlayerPosXstart + fPlayerSpeedX * lMoveDelta;
            fPlayerPosY = fPlayerPosYstart + fPlayerSpeedY * lMoveDelta;
        }
        else
        {
            fPlayerPosX = fMoveX;
            fPlayerPosY = fMoveY;
        }

        canvas.drawRect(fPlayerPosX-fBoxSize, fPlayerPosY-fBoxSize, fPlayerPosX+fBoxSize, fPlayerPosY+fBoxSize, paintPlayer);

        this.invalidate();
    }
}
