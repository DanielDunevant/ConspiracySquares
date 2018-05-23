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

public class Game_Draw extends FrameLayout {
    Paint paint = new Paint();

    int nWidth;
    int nHeight;
    int nWidth_Center;
    int nHeight_Center;

    int nBoxSize;

    int nMoveX = 0;
    int nMoveY = 0;

    float fPlayerSpeed = 0f;
    float fPlayerPosX = 0f;
    float fPlayerPosY = 0f;

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
        nWidth = widthMeasureSpec;
        nHeight = heightMeasureSpec;

        nWidth_Center = widthMeasureSpec/2;
        nHeight_Center = heightMeasureSpec/2;

        nBoxSize = (nWidth < nHeight) ? nWidth/20 : nHeight/20;
        nBoxSize = (nBoxSize > 4) ? nBoxSize : 5;

        fPlayerSpeed = nBoxSize * 20f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        nMoveX = (int)event.getX();
        nMoveY = (int)event.getY();
        if (event.getAction() == MotionEvent.ACTION_MOVE)
            performClick();
        return true;
    }

    @Override
    public boolean performClick()
    {
        super.performClick();
        //if (nMoveX > (nWidth_Center + nBoxSize) || nMoveX < (nWidth_Center - nBoxSize)
        //        || nMoveY > (nHeight_Center + nBoxSize) || nMoveY < (nHeight_Center - nBoxSize))
        //{
            ValueAnimator MoveBox_X = ValueAnimator.ofFloat(fPlayerPosX, (float)nMoveX);
            ValueAnimator MoveBox_Y = ValueAnimator.ofFloat(fPlayerPosY, (float)nMoveY);

            int nDuration = 1000 * (int)(Math.sqrt((fPlayerPosX-nMoveX)*(fPlayerPosX-nMoveX) + (fPlayerPosY-nMoveY)*(fPlayerPosY-nMoveY)) / fPlayerSpeed);
            MoveBox_X.setDuration(nDuration);
            MoveBox_Y.setDuration(nDuration);

            MoveBox_X.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    fPlayerPosX = (float)animation.getAnimatedValue();
                }
            });
            MoveBox_Y.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    fPlayerPosY = (float)animation.getAnimatedValue();
                }
            });

            MoveBox_X.start();
            MoveBox_Y.start();
        //}
        return true;
    }

    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(4);
        canvas.drawRect(fPlayerPosX-nBoxSize, fPlayerPosY-nBoxSize, fPlayerPosX+nBoxSize, fPlayerPosY+nBoxSize, paint);
    }
}
