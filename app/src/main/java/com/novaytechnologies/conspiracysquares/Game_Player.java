//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import java.util.Random;

class Game_Player
{
    static private final int FLAG_ALIVE = 0b1;

    static private Game_Player Self;

    static private Paint sm_txtPaint;
    static private float sm_fBoxSize = 2f;
    static private float sm_fPlayer_Speed = 0f;
    static void UpdateAllSizes(float fSize)
    {
        sm_fBoxSize = fSize;
        sm_fPlayer_Speed = fSize / 20f;
    }

    private Drawable m_SQUARE;

    private String m_sync_strName;
    private int m_sync_nSQUARE_COLOR;
    private float m_sync_fPosX = 0f;
    private float m_sync_fPosY = 0f;
    private int m_sync_nFlags = -1;

    private float m_fDrawX = 0f;
    private float m_fDrawY = 0f;
    private float m_fSpeedX = 0f;
    private float m_fSpeedY = 0f;

    private boolean isAlive() {return (m_sync_nFlags & FLAG_ALIVE) > 0;}

    static float GetX() {return Self.m_sync_fPosX;}
    static float GetY() {return Self.m_sync_fPosY;}
    static int GetFlags() {return Self.m_sync_nFlags;}
    static int GetColor() {return Self.m_sync_nSQUARE_COLOR;}
    static String GetName() {return Self.m_sync_strName;}

    static void CreateSelf(Context ctx)
    {
        Self = new Game_Player();

        Self.m_sync_strName = Layout_Main.sm_strName;
        sm_txtPaint = new Paint();
        sm_txtPaint.setColor(Color.rgb(0, 0, 0));

        Self.m_SQUARE = ctx.getResources().getDrawable(R.drawable.vec_square);
        Random randColor = new Random(System.currentTimeMillis());
        Self.m_sync_nSQUARE_COLOR = Color.rgb(randColor.nextInt(255), randColor.nextInt(255), randColor.nextInt(255));
        Self.m_SQUARE.setColorFilter(Self.m_sync_nSQUARE_COLOR, PorterDuff.Mode.MULTIPLY);
    }

    static void UpdateSelfCenter(float fCenterX, float fCenterY)
    {
        Self.m_fDrawX = fCenterX;
        Self.m_fDrawY = fCenterY;
    }

    static void MoveSelfToLocal(float fScreenX, float fScreenY)
    {
        if (fScreenX > Self.m_fDrawX + sm_fBoxSize || fScreenX < Self.m_fDrawX - sm_fBoxSize ||
            fScreenY > Self.m_fDrawY + sm_fBoxSize || fScreenY < Self.m_fDrawY - sm_fBoxSize)
        {
            float fPlayerXdist = fScreenX - Self.m_fDrawX;
            float fPlayerYdist = fScreenY - Self.m_fDrawY;
            float fPlayerDist = (float) (Math.sqrt(Math.pow(fPlayerXdist, 2) + Math.pow(fPlayerYdist, 2)));

            float fPlayer_Time = fPlayerDist / sm_fPlayer_Speed;

            Self.m_fSpeedX = fPlayerXdist / fPlayer_Time;
            Self.m_fSpeedY = fPlayerYdist / fPlayer_Time;
        }
        else
        {
            Self.m_fSpeedX = 0f;
            Self.m_fSpeedY = 0f;
        }
    }

    private void Kill(Context ctx)
    {
        m_SQUARE.clearColorFilter();
        m_SQUARE = ctx.getResources().getDrawable(R.drawable.Square_Broken);
        m_sync_nFlags -= FLAG_ALIVE;
    }

    void UpdateName(String strName) {m_sync_strName = strName;}
    void UpdateX(float fX) {m_sync_fPosX = fX;}
    void UpdateY(float fY) {m_sync_fPosY = fY;}
    void UpdateColor(int nColor, Context ctx)
    {
        m_SQUARE = ctx.getResources().getDrawable(R.drawable.vec_square);
        m_sync_nSQUARE_COLOR = nColor;
        m_SQUARE.setColorFilter(nColor, PorterDuff.Mode.MULTIPLY);
    }
    void UpdateF(int nFlags, Context ctx)
    {
        int nPrevFlags = m_sync_nFlags;
        m_sync_nFlags = nFlags;
        if (nPrevFlags != nFlags)
        {
            if (!isAlive()) Kill(ctx);
        }
    }

    void DrawPlayer(Canvas canvas, long lDelta)
    {
        if (m_sync_nFlags >= 0)
        {
            if (this.equals(Self))
            {
                if (isAlive())
                {
                    m_sync_fPosX = m_sync_fPosX + m_fSpeedX * lDelta;
                    m_sync_fPosY = m_sync_fPosY + m_fSpeedY * lDelta;
                }
            }
            else
            {
                m_fDrawX = Self.m_fDrawX - Self.m_sync_fPosX - m_sync_fPosX;
                m_fDrawY = Self.m_fDrawY - Self.m_sync_fPosY - m_sync_fPosY;
            }

            m_SQUARE.setBounds((int) (m_fDrawX - sm_fBoxSize), (int) (m_fDrawY - sm_fBoxSize), (int) (m_fDrawX + sm_fBoxSize), (int) (m_fDrawY + sm_fBoxSize));
            m_SQUARE.draw(canvas);
            canvas.drawText(m_sync_strName, m_fDrawX, m_fDrawY, sm_txtPaint);
        }
    }
}
