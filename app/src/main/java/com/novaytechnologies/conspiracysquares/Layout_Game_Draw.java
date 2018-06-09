//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

// The Game Drawing Layout.
public class Layout_Game_Draw extends FrameLayout {
    // Layout dimension information, in pixels.
    int nWidth;
    int nHeight;
    int nWidth_Center;
    int nHeight_Center;

    // The dimensions of the smaller and larger lengths of the screen, in pixels.
    static int nMinSide;
    static int nMaxSide;

    // Half the grid's thickness, in pixels, and the grid's paint object.
    static int nGridThicknessHalf = 1;
    static Paint mGridPaint = new Paint(R.color.colorLine);

    // Timing information.
    long lMoveTick;   // Used to create a slight input delay.
    long lDrawLast;   // Used to give the time of the last draw.
    long lDrawDelta;  // Used to give the time passed since the last draw.

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

        nGridThicknessHalf = (int) Math.ceil(nMaxSide * 0.01);

        lDrawLast = lMoveTick = System.currentTimeMillis();

        Game_Player.UpdateAllSizes(nMinSide * Game_Player.fSIZE_FRACTION);
        Game_Camera.UpdateCenter(nWidth_Center, nHeight_Center);

        setMeasuredDimension(nWidth, nHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP || System.currentTimeMillis() - lMoveTick > 50L)
        {
            Game_Player.MoveSelfToLocal(event.getX(), event.getY());
            lDrawLast = lMoveTick = System.currentTimeMillis();
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
        float fGridStartX = (float) Math.floor(Game_Camera.GetGlobalX() - 52);
        int nGridStartX = (int) fGridStartX - Math.abs(Math.round(fGridStartX) % 20);
        float fGridEndX = (float) Math.ceil(Game_Camera.GetGlobalX() + 72);
        int nGridEndX = (int) fGridEndX - Math.abs(Math.round(fGridEndX) % 20);

        for (int nGridLine = nGridStartX; nGridLine <= nGridEndX; nGridLine += 20)
        {
            canvas.drawLine(Game_Camera.GetRelativeX(nGridStartX),
                    Game_Camera.GetRelativeY(nGridLine - nGridThicknessHalf),
                    Game_Camera.GetRelativeX(nGridEndX),
                    Game_Camera.GetRelativeY(nGridLine + nGridThicknessHalf),
                    mGridPaint);
        }

        float fGridStartY = (float) Math.floor(Game_Camera.GetGlobalY() - 52);
        int nGridStartY = (int) fGridStartY - Math.abs(Math.round(fGridStartY) % 20);
        float fGridEndY = (float) Math.ceil(Game_Camera.GetGlobalY() + 72);
        int nGridEndY = (int) fGridEndY - Math.abs(Math.round(fGridEndY) % 20);

        for (int nGridLine = nGridStartY; nGridLine <= nGridEndY; nGridLine += 20)
        {
            canvas.drawLine(Game_Camera.GetRelativeX(nGridLine - nGridThicknessHalf),
                    Game_Camera.GetRelativeY(nGridStartY),
                    Game_Camera.GetRelativeX(nGridLine + nGridThicknessHalf),
                    Game_Camera.GetRelativeY(nGridEndY),
                    mGridPaint);
        }
    }

    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        lDrawDelta = System.currentTimeMillis() - lDrawLast;

        Game_Main.SyncWithServer(this.getContext(), false);
        DrawGrid(canvas);
        for (Game_Player Player : Game_Main.sm_PlayersArray)
        {
            Player.DrawPlayer(canvas, lDrawDelta);
        }

        lDrawLast = System.currentTimeMillis();

        this.invalidate();
    }
}
