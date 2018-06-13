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
    static public final float fSIZE_FRACTION = 0.05f;
    static private final float fDIST_PER_MILLI = 0.01f;

    static private final int FLAG_ALIVE = 0b1;

    private Game_Player() {}
    Game_Player(int nID) {m_nID = nID;}

    static private Paint sm_txtPaint;
    static private float sm_fBoxSize = 2f;
    static private float sm_fPlayer_Speed = 0f;
    static void UpdateAllSizes(float fSize)
    {
        sm_fBoxSize = fSize;
        sm_fPlayer_Speed = fSize * fDIST_PER_MILLI;
    }

    private Drawable m_SQUARE;

    private int m_nID;
    private String m_sync_strName;
    private float m_sync_fPosX = 0f;
    private float m_sync_fPosY = 0f;
    private int m_sync_nFlags = -1;

    private float m_fSpeedX = 0f;
    private float m_fSpeedY = 0f;

    static private int sm_nID = 0;
    static int GetSelfID() {return sm_nID;}
    static void SetSelfID(int nID) {sm_nID = nID;}
    static private Game_Player GetSelf() {return Game_Main.sm_PlayersArray.get(sm_nID);}

    private boolean isSelf() {return m_nID == sm_nID;}
    private boolean isAlive() {return (m_sync_nFlags & FLAG_ALIVE) > 0;}

    static float GetX() {return GetSelf().m_sync_fPosX;}
    static float GetY() {return GetSelf().m_sync_fPosY;}
    static int GetFlags() {return GetSelf().m_sync_nFlags;}

    static private int sm_nSelfColor;
    static int GetNewSelfColor()
    {
        Random randColor = new Random(System.currentTimeMillis());
        sm_nSelfColor = Color.rgb(randColor.nextInt(255), randColor.nextInt(255), randColor.nextInt(255));
        return sm_nSelfColor;
    }

    static void CreateSelf(Context ctx)
    {
        Game_Player Self = GetSelf();

        Self.m_sync_nFlags = 0b1;

        Self.m_sync_strName = Utility_SharedPrefs.get().loadName(ctx);
        sm_txtPaint = new Paint();
        sm_txtPaint.setColor(Color.rgb(0, 0, 0));

        Self.m_SQUARE = ctx.getResources().getDrawable(R.drawable.vec_square);
        Self.m_SQUARE.setColorFilter(sm_nSelfColor, PorterDuff.Mode.MULTIPLY);
    }

    static void MoveSelfToLocal(float fScreenX, float fScreenY)
    {
        Game_Player Self = GetSelf();

        if (fScreenX > Game_Camera.GetDrawX() + sm_fBoxSize || fScreenX < Game_Camera.GetDrawX() - sm_fBoxSize ||
            fScreenY > Game_Camera.GetDrawY() + sm_fBoxSize || fScreenY < Game_Camera.GetDrawY() - sm_fBoxSize)
        {
            float fPlayerXdist = fScreenX - Game_Camera.GetDrawX();
            float fPlayerYdist = fScreenY - Game_Camera.GetDrawY();
            float fPlayerDist = (float) (Math.sqrt(Math.pow(fPlayerXdist, 2) + Math.pow(fPlayerYdist, 2)));

            float fPlayer_Time = fPlayerDist / sm_fPlayer_Speed;

            Self.m_fSpeedX = 100 * (fPlayerXdist / fPlayer_Time) / Layout_Game_Draw.nMaxSide;
            Self.m_fSpeedY = 100 * (fPlayerYdist / fPlayer_Time) / Layout_Game_Draw.nMaxSide;
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
        m_SQUARE = ctx.getResources().getDrawable(R.drawable.square_broken);
    }

    void UpdateName(String strName) {m_sync_strName = strName;}
    void UpdateX(float fX) {m_sync_fPosX = fX;}
    void UpdateY(float fY) {m_sync_fPosY = fY;}
    void UpdateColor(int nColor, Context ctx)
    {
        m_SQUARE = ctx.getResources().getDrawable(R.drawable.vec_square);
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

    void DrawPlayer(Canvas canvas, long lDelta) {
        if (Game_Main.isStarted())
        {
            Game_Player Self = GetSelf();

            if (m_sync_nFlags >= 0)
            {
                float fDrawX;
                float fDrawY;

                if (isSelf() && isAlive())
                {
                    fDrawX = Game_Camera.GetDrawX();
                    fDrawY = Game_Camera.GetDrawY();

                    m_sync_fPosX = m_sync_fPosX + m_fSpeedX * lDelta;
                    m_sync_fPosY = m_sync_fPosY + m_fSpeedY * lDelta;
                    Game_Camera.UpdatePosition(m_sync_fPosX, m_sync_fPosY);
                }
                else
                {
                    if (isSelf()) Game_Camera.Move(m_fSpeedX, m_fSpeedY, lDelta);
                    fDrawX = Game_Camera.GetRelativeX(m_sync_fPosX);
                    fDrawY = Game_Camera.GetRelativeY(m_sync_fPosY);
                }

                m_SQUARE.setBounds((int) (fDrawX - sm_fBoxSize), (int) (fDrawY - sm_fBoxSize), (int) (fDrawX + sm_fBoxSize), (int) (fDrawY + sm_fBoxSize));
                m_SQUARE.draw(canvas);

                canvas.drawText(m_sync_strName, fDrawX, fDrawY, sm_txtPaint);
            }
            else if (isSelf()) Game_Camera.Move(m_fSpeedX, m_fSpeedY, lDelta);
        }
    }
}
