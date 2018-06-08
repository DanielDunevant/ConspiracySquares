//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class Layout_Game_Draw extends FrameLayout {
    int nWidth;
    int nHeight;
    int nWidth_Center;
    int nHeight_Center;

    static int nMinSide;
    static int nMaxSide;

    long lMoveTick;
    long lMoveLast;
    long lMoveDelta;

    public Layout_Game_Draw(Context ctx) {
        this(ctx, null);
    }

    public Layout_Game_Draw(Context ctx, @Nullable AttributeSet attrs) {
        super(ctx, attrs);
        setWillNotDraw(false);
    }

    public Layout_Game_Draw(Context ctx, @Nullable AttributeSet attrs, int defstyle) {
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

        nMinSide = (nWidth < nHeight) ? nWidth : nHeight;
        nMaxSide = (nWidth > nHeight) ? nWidth : nHeight;

        lMoveLast = System.currentTimeMillis();

        Game_Player.UpdateAllSizes(nMinSide * Game_Player.fSIZE_FRACTION);
        Game_Camera.UpdateCenter(nWidth_Center, nHeight_Center);

        setMeasuredDimension(nWidth, nHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN || System.currentTimeMillis() - lMoveTick > 50L)
        {
            Game_Player.MoveSelfToLocal(event.getX(), event.getY());
            lMoveLast = lMoveTick = System.currentTimeMillis();
        }
        performClick();
        return true;
    }

    @Override
    public boolean performClick()
    {
        super.performClick();
        return true;
    }

    void DrawGrid(Canvas canvas)
    {
        //TODO Grid Drawing
    }

    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        lMoveDelta = System.currentTimeMillis() - lMoveLast;

        Game_Main.SyncWithServer(this.getContext(), false);
        DrawGrid(canvas);
        for (Game_Player Player : Game_Main.sm_PlayersArray)
        {
            Player.DrawPlayer(canvas, lMoveTick);
        }

        lMoveLast = System.currentTimeMillis();

        this.invalidate();
    }
}
