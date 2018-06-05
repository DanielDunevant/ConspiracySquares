//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.util.Random;

class Game_Player
{
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

    private boolean m_sync_bAlive = true;

    private float m_fDrawX = 0f;
    private float m_fDrawY = 0f;
    private float m_fSpeedX = 0f;
    private float m_fSpeedY = 0f;

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

    void Create(Context ctx, int nColor, String strName)
    {
        Game_Player newPlayer = new Game_Player();
        newPlayer.m_sync_strName = strName;

        newPlayer.m_SQUARE = ctx.getResources().getDrawable(R.drawable.vec_square);
        newPlayer.m_sync_nSQUARE_COLOR = nColor;
        newPlayer.m_SQUARE.setColorFilter(nColor, PorterDuff.Mode.MULTIPLY);
    }

    void SetGlobalPos(float fX, float fY)
    {
        m_sync_fPosX = fX;
        m_sync_fPosY = fY;
    }

    void Kill(Context ctx)
    {
        m_SQUARE.clearColorFilter();
        m_SQUARE = ctx.getResources().getDrawable(R.drawable.Square_Broken);
        m_sync_bAlive = false;
    }

    void DrawPlayer(Canvas canvas, long lDelta)
    {
        if (this.equals(Self))
        {
            if (m_sync_bAlive)
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

        m_SQUARE.setBounds((int)(m_fDrawX - sm_fBoxSize), (int)(m_fDrawY - sm_fBoxSize), (int)(m_fDrawX + sm_fBoxSize), (int)(m_fDrawY + sm_fBoxSize));
        m_SQUARE.draw(canvas);
        canvas.drawText(m_sync_strName, m_fDrawX, m_fDrawY, sm_txtPaint);
    }
}
