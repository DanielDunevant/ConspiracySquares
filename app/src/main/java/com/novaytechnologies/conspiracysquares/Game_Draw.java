//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class Game_Draw extends FrameLayout {

    int nWidth;
    int nHeight;

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
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
