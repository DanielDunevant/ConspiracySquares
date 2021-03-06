//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The Game Drawing Layout.
 * @author Jesse Primiani
 */
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

    /**
     * Sets all relevant size variables whenever the layout size changes.
     * @author Jesse Primiani
     * @param widthMeasureSpec The device's internal screen width
     * @param heightMeasureSpec The device's internal screen height
     */
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

    /**
     * Used to signal to the appropriate place whenever the screen is touched.
     * @author Jesse Primiani
     * @param event The touch event
     * @return true
     */
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

    /**
     * Draws the Grid using the location information given by Game_Camera.
     * @author Jesse Primiani
     * @param canvas The canvas used by the layout
     */
    static void DrawGrid(final Canvas canvas)
    {
        // The screen-relative position of the next line
        float fPos;

        // The beginning and end positions of the vertical grid lines
        // Each line has a global position that is a multiple of 10
        // As in: ..., -20, -10, 0, 10, 20, 30, ...
        float fGridStartX = Game_Camera.GetGlobalX() - 70;
        if(fGridStartX+70>=-Game_Main.mapSize)
        {fGridStartX -= fGridStartX % 10;}
        else{fGridStartX+=1;}

        float fGridEndX = Game_Camera.GetGlobalX() + 70;
        if(fGridEndX<=Game_Main.mapSize)
        { fGridEndX += fGridEndX % 10;}
        else{fGridStartX-=1;}
        for (float fGridLine = fGridStartX; fGridLine <= fGridEndX; fGridLine += 10f)
        {

            fPos = Game_Camera.GetRelativeX(fGridLine);
            canvas.drawLine(fPos,
                    0,
                    fPos,
                    sm_nHeight,
                    sm_GridPaint);
        }

        // The beginning and end positions of the horizontal grid lines.
        // Calculated using the same method as the vertical lines.
        float fGridStartY = Game_Camera.GetGlobalY() - 70;
        if(fGridStartY+70>=-Game_Main.mapSize)
        {fGridStartY -= fGridStartY % 10;}
        else{fGridStartY+=1;}

        float fGridEndY = Game_Camera.GetGlobalY() + 70;
        if(fGridEndY<=Game_Main.mapSize)
        { fGridEndY += fGridEndY % 10;}
        else{fGridStartY-=1;}
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

    /** Draws the Minimap using the location information given by Game_Camera.
     * @author Daniel Dunevant
     * @param canvas The canvas used by the layout
     */
    static void DrawMinimap(final Canvas canvas)
    {
        //Draw map grid rectangle
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(0);
        canvas.drawRect(30,30,230,230,paint);
        paint.setColor(Color.BLACK);
        //Game_Camera.GetGlobalX/Y() represents the player's actual position on the map
        float xPos = Game_Camera.GetGlobalX();
        float yPos = Game_Camera.GetGlobalY();
        float x = (xPos/5)+130;
        float y = (yPos/5)+130;
        float radius = 5;
        paint.setColor(Color.BLUE);
        canvas.drawCircle(x,y,radius,paint);
    }

    /**
     * Draws the Timer and any notifications.
     * @author Daniel Dunevant
     * param@ canvas The canvas used by the layout
     * @param ctx The application context handler
     */
    static void DrawPlayerNotifications(final Canvas canvas, Context ctx)
    {
        LinearLayout layout = new LinearLayout(ctx);
        TextView textView = new TextView(ctx);
        textView.setHorizontallyScrolling(false);
        textView.setWidth(600);
        textView.setHeight(100);
        layout.addView(textView);
        layout.measure(canvas.getWidth(), canvas.getHeight());
        layout.layout(0, 0, 0, 0);

        Paint paint = new Paint();

        if (Game_Timer.startRoundTimer != null && Game_Timer.notificationTimer != null) {
            if (!Game_Timer.startRoundTimer.timerComplete) {
                paint.setColor(Color.BLACK);
                paint.setTextSize(50);
                canvas.drawText(Long.toString((Game_Timer.startRoundTimer.countDown) / 1000), 120, 280, paint);
                canvas.drawText(Boolean.toString(Game_Timer.startRoundTimer.timerComplete), 120, 240, paint);
            } else {
                Game_Timer.notificationTimer.setTimer(3000);
                canvas.drawText(Long.toString(Game_Timer.notificationTimer.countDown / 1000), 120, 270, paint);
                if (!Game_Timer.notificationTimer.timerComplete) {
                    textView.setText(R.string.RoundStarting);
                    textView.setVisibility(View.VISIBLE);
                    canvas.translate(280, 30);
                    layout.draw(canvas);
                } else {
                    textView.setVisibility(View.INVISIBLE);
                    layout.draw(canvas);
                }
            }
        }
    }

    /** Runs the game loop every draw cycle.
     * @author Jesse Primiani
     * @param canvas The canvas used by the layout
     */
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
