//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

public class Game_Camera
{
    private static float sm_fPosX = 0f;
    private static float sm_fPosY = 0f;
    private static float sm_fDrawX = 0f;
    private static float sm_fDrawY = 0f;

    static float GetDrawX() {return sm_fDrawX;}
    static float GetDrawY() {return sm_fDrawY;}
    static float GetGlobalX() {return sm_fPosX;}
    static float GetGlobalY() {return sm_fPosY;}

    static void UpdateCenter(int nCenterX, int nCenterY)
    {
        sm_fDrawX = (float) nCenterX;
        sm_fDrawY = (float) nCenterY;
    }

    static void UpdatePosition(float fX, float fY)
    {
        sm_fPosX = fX;
        sm_fPosY = fY;
    }

    static void Move(float m_fSpeedX, float m_fSpeedY, long lDelta)
    {
        sm_fPosX = sm_fPosX + m_fSpeedX * lDelta;
        sm_fPosY = sm_fPosY + m_fSpeedY * lDelta;
    }

    static float GetRelativeX(float fOtherX)
    {
        return sm_fDrawX - ((sm_fPosX - fOtherX) * Layout_Game_Draw.nMaxSide / 100f);
    }

    static float GetRelativeY(float fOtherY)
    {
        return sm_fDrawY - ((sm_fPosY - fOtherY) * Layout_Game_Draw.nMaxSide / 100f);
    }
}
