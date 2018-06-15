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

    // The local to global coordinate scaling factor
    static float sm_fScaleFactor = 1f;

    // The grid's paint object.
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

        sm_fScaleFactor = Layout_Game_Draw.nMaxSide / 100f;

        lDrawLast = lMoveTick = System.currentTimeMillis();

        Game_Player.UpdateAllSizes(nMinSide * Game_Player.fSIZE_FRACTION);
        Game_Camera.UpdateCenter(nWidth_Center, nHeight_Center);

        setMeasuredDimension(nWidth, nHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (Game_Main.isStarted() && (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP ||
                (event.getAction() == MotionEvent.ACTION_MOVE && System.currentTimeMillis() - lMoveTick > 50L)))
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
        float fPos;

        float fGridStartX = Game_Camera.GetGlobalX() - 70;
        fGridStartX -= fGridStartX % 10;
        float fGridEndX = Game_Camera.GetGlobalX() + 70;
        fGridEndX -= fGridEndX % 10;

        for (float fGridLine = fGridStartX; fGridLine <= fGridEndX; fGridLine += 10f)
        {
            fPos = Game_Camera.GetRelativeX(fGridLine);
            canvas.drawLine(fPos,
                    0,
                    fPos,
                    nHeight,
                    mGridPaint);
        }

        float fGridStartY = Game_Camera.GetGlobalY() - 70;
        fGridStartY -= fGridStartY % 10;
        float fGridEndY = Game_Camera.GetGlobalY() + 70;
        fGridEndY -= fGridEndY % 10;

        for (float fGridLine = fGridStartY; fGridLine <= fGridEndY; fGridLine += 10f)
        {
            fPos = Game_Camera.GetRelativeY(fGridLine);
            canvas.drawLine(0,
                    fPos,
                    nWidth,
                    fPos,
                    mGridPaint);
        }
    }

    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawColor(getResources().getColor(R.color.colorBackground));

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
