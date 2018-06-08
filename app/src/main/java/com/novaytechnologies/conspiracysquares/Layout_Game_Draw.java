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
        /*PSEUDOCODE
        * for(int i = 0 ; i <map.y;i+gridWidthHeight)
        *   for(int j = 0; j <map.x;j+gridWidthHeight)
        *       If(i+gridWidthHeight ==map.y)
        *           Draw Rightward line of length gridWidthHeight;
        *       Else{
        *           If(j+gridWidthHeight == map.x)
        *               Draw Upward line of length gridWidthHeight;
        *           Else{
        *               Draw Upward line of length gridWidthHeight;
        *               Draw Rightward line of length gridWidthHeight;}
        *           }
        *
        * END PSEUDO
        *
        *           Some Explanation
        *
        *            about gridWidthHeight ~ this var has to be an int result of divisibility of the map's
        *            total width and height. Otherwise the  logic below  doesn't  work.
        *
        *            About the increments of the For statements (i+gridWidthHeight) ~  these would work if they were  post adders
        *            but they are  in-line adders. It may be best to use 2 while statements and add after execution of  commands
        *            listed below. I'll leave that  up to you though.
        *
        * */
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
