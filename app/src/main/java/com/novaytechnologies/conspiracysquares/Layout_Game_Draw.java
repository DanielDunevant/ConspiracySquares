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
    // The grid's paint object.
    static Paint sm_GridPaint = new Paint(R.color.colorLine);

    // Layout dimension information, in pixels.
    static int sm_nWidth;
    static int sm_nHeight;
    static int sm_nWidth_Center;
    static int sm_nHeight_Center;

    // The dimensions of the smaller and larger lengths of the screen, in pixels.
    static int sm_nMinSide;
    static int sm_nMaxSide;

    // Timing information for drawing and touch input.
    long m_lMoveTick;   // Used to create a slight input delay.
    long m_lDrawLast;   // Used to give the time of the last draw.
    long m_lDrawDelta;  // Used to give the time passed since the last draw.

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

    // Sets all relevant size variables whenever the layout size changes.
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        sm_nWidth = MeasureSpec.getSize(widthMeasureSpec);
        sm_nHeight = MeasureSpec.getSize(heightMeasureSpec);
        sm_nWidth_Center = (int)Math.round(sm_nWidth /2.0);
        sm_nHeight_Center = (int)Math.round(sm_nHeight /2.0);

        sm_nMinSide = (sm_nWidth < sm_nHeight) ? sm_nWidth : sm_nHeight;
        sm_nMaxSide = (sm_nWidth > sm_nHeight) ? sm_nWidth : sm_nHeight;

        m_lDrawLast = m_lMoveTick = System.currentTimeMillis();

        setMeasuredDimension(sm_nWidth, sm_nHeight);

        Game_Main.GameSizeChanged();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (Game_Main.isStarted() && (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP ||
                (event.getAction() == MotionEvent.ACTION_MOVE && System.currentTimeMillis() - m_lMoveTick > 100L)))
        {
            Game_Main.TouchUpdated(event.getX(), event.getY());
            m_lDrawLast = m_lMoveTick = System.currentTimeMillis();
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

    // Draws the Grid using the location information given by Game_Camera
    static void DrawGrid(Canvas canvas)
    {
        // The screen-relative position of the next line
        float fPos;

        // The beginning and end positions of the vertical grid lines
        // Each line has a global position that is a multiple of 10
        // As in: ..., -20, -10, 0, 10, 20, 30, ...
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
                    sm_nHeight,
                    sm_GridPaint);
        }

        // The beginning and end positions of the horizontal grid lines
        // Calculated using the same method as the vertical lines.
        float fGridStartY = Game_Camera.GetGlobalY() - 70;
        fGridStartY -= fGridStartY % 10;
        float fGridEndY = Game_Camera.GetGlobalY() + 70;
        fGridEndY -= fGridEndY % 10;

        for (float fGridLine = fGridStartY; fGridLine <= fGridEndY; fGridLine += 10f)
        {
            fPos = Game_Camera.GetRelativeY(fGridLine);
            canvas.drawLine(0,
                    fPos,
                    sm_nWidth,
                    fPos,
                    sm_GridPaint);
        }
    }

    // Runs the game loop every draw cycle.
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawColor(getResources().getColor(R.color.colorGameBackground));

        m_lDrawDelta = System.currentTimeMillis() - m_lDrawLast;
        Game_Main.GameLoop(m_lDrawDelta, canvas, this.getContext());
        m_lDrawLast = System.currentTimeMillis();

        this.invalidate();
    }
}
