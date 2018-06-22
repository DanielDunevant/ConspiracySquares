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
    static private final float fSIZE_FRACTION = 0.04f; // The size of the player, given in the fraction of the game layout's max size
    static private final float fDIST_PER_MILLI = 0.01f; // The speed of the player, given in player size per millisecond

    // If m_sync_nFlags == -1, then the player is a spectator and will not be drawn
    static private final int FLAG_ALIVE = 0b1; // Flag to determine whether the player is dead

    private Game_Player() {}
    Game_Player(int nID) {m_nID = nID;}

    // The static size, speed, and name color for all the players
    static private Paint sm_txtPaint;
    static private float sm_fBoxSize = 2f;
    static private float sm_fPlayer_Speed = 0f;
    static void UpdateAllSizes(float fScreenMaxSize)
    {
        sm_fBoxSize = fScreenMaxSize * fSIZE_FRACTION;
        sm_fPlayer_Speed = sm_fBoxSize * fDIST_PER_MILLI;
    }

    // The spectating drawable
    static Drawable sm_SPECTATE;

    // The player drawable
    private Drawable m_SQUARE;

    // All synchronized player information
    private int m_nID;
    private String m_sync_strName = "";
    private float m_sync_fPosX = 0f;
    private float m_sync_fPosY = 0f;
    private int m_sync_nFlags = -1;

    // The X and Y coordinates of the player's movement direction with speed sm_fPlayer_Speed
    private float m_sync_fSpeedX = 0f;
    private float m_sync_fSpeedY = 0f;

    // Stores and Gets the index of the SELF player object, stored in Game_Main.sm_PlayersArray
    static private int sm_nID = 0;
    static int GetSelfID() {return sm_nID;}
    static void SetSelfID(int nID) {sm_nID = nID;}
    static private Game_Player GetSelf() {return Game_Main.sm_PlayersArray.get(sm_nID);}

    // Player state information
    private boolean m_bDrawableLoaded = false;
    private boolean isSelf() {return m_nID == sm_nID;}
    private boolean isAlive() {return (m_sync_nFlags & FLAG_ALIVE) > 0;}

    static float GetX() {return GetSelf().m_sync_fPosX;}
    static float GetY() {return GetSelf().m_sync_fPosY;}
    static int GetFlags() {return GetSelf().m_sync_nFlags;}

    // SELF color and random color chooser function
    static private int sm_nSelfColor;
    static int GetNewSelfColor()
    {
        Random randColor = new Random(System.currentTimeMillis());
        sm_nSelfColor = Color.rgb(randColor.nextInt(255), randColor.nextInt(255), randColor.nextInt(255));
        return sm_nSelfColor;
    }

    // The function to create the locally controlled player, SELF.
    static void CreateSelf(Context ctx)
    {
        Game_Player Self = GetSelf();

        Self.m_sync_nFlags = 0b1;

        Self.m_sync_strName = Utility_SharedPreferences.get().loadName(ctx);
        sm_txtPaint = new Paint();
        sm_txtPaint.setShadowLayer(8f, 0, 0, Color.rgb(0, 0, 0));
        sm_txtPaint.setColor(Color.rgb(255, 255, 255));
        sm_txtPaint.setTextAlign(Paint.Align.CENTER);

        Self.m_SQUARE = ctx.getResources().getDrawable(R.drawable.vec_player);
        Self.m_SQUARE.setColorFilter(sm_nSelfColor, PorterDuff.Mode.MULTIPLY);

        Self.m_bDrawableLoaded = true;
    }

    // Sets the movement the player, SELF, using a local position on the screen to determine the global movement direction.
    static void MoveSelfToLocal(float fScreenX, float fScreenY)
    {
        Game_Player Self = GetSelf();

        if (fScreenX > Game_Camera.GetDrawX() + sm_fBoxSize || fScreenX < Game_Camera.GetDrawX() - sm_fBoxSize ||
            fScreenY > Game_Camera.GetDrawY() + sm_fBoxSize || fScreenY < Game_Camera.GetDrawY() - sm_fBoxSize)
        {
            // Calculate the components' and total distance from the center of the screen to the touched point
            float fPlayerXdist = fScreenX - Game_Camera.GetDrawX();
            float fPlayerYdist = fScreenY - Game_Camera.GetDrawY();
            float fPlayerDist = (float) (Math.sqrt(Math.pow(fPlayerXdist, 2f) + Math.pow(fPlayerYdist, 2f)));

            // Determine the time it would take to move to the touched point using the static constant player speed
            float fPlayer_Time = fPlayerDist / sm_fPlayer_Speed;

            // Split the constant speed into 2 components of a constant-speed velocity vector, using the above calculations
            // These speed components are then converted from local to global coordinates via division
            Self.m_sync_fSpeedX = (fPlayerXdist / fPlayer_Time) / Game_Camera.sm_fScaleFactor;
            Self.m_sync_fSpeedY = (fPlayerYdist / fPlayer_Time) / Game_Camera.sm_fScaleFactor;
        }
        else
        {
            // Stop if the center of the screen is touched
            Self.m_sync_fSpeedX = 0f;
            Self.m_sync_fSpeedY = 0f;
        }
    }

    // Sets the player drawable to the dead player drawable
    private void Kill(Context ctx)
    {
        m_sync_fSpeedX = 0f;
        m_sync_fSpeedY = 0f;
        if (m_SQUARE != null) m_SQUARE.clearColorFilter();
        m_SQUARE = ctx.getResources().getDrawable(R.drawable.player_dead);
    }

    // Update synchronized player data
    void UpdateName(String strName) {m_sync_strName = strName;}
    void UpdateX(float fX) {m_sync_fPosX = fX;}
    void UpdateY(float fY) {m_sync_fPosY = fY;}
    void UpdateColor(int nColor, Context ctx)
    {
        m_SQUARE = ctx.getResources().getDrawable(R.drawable.vec_player);
        m_SQUARE.setColorFilter(nColor, PorterDuff.Mode.MULTIPLY);
        m_bDrawableLoaded = true;
    }

    // Updates the player's flag information, and run the relevant functions when any flag changes
    void UpdateF(int nFlags, Context ctx)
    {
        int nPrevFlags = m_sync_nFlags;
        m_sync_nFlags = nFlags;
        if (nPrevFlags != nFlags && nFlags >= 0)
        {
            if (!isAlive()) Kill(ctx);
        }
    }

    // Draws the player if the player isn't a spectator, then moves the player if said player is SELF.
    // If said player is SELF and is either dead or a spectator, this simply moves the camera and draws a centered square.
    void DrawPlayer(Canvas canvas, long lDelta) {
        float fDrawX;
        float fDrawY;

        if (m_bDrawableLoaded && m_sync_nFlags >= 0)
        {
            m_sync_fPosX += m_sync_fSpeedX * lDelta;
            m_sync_fPosY += m_sync_fSpeedY * lDelta;

            if (isSelf() && isAlive())
            {
                fDrawX = Game_Camera.GetDrawX();
                fDrawY = Game_Camera.GetDrawY();

                Game_Camera.UpdatePosition(m_sync_fPosX, m_sync_fPosY);
            }
            else
            {
                fDrawX = Game_Camera.GetRelativeX(m_sync_fPosX);
                fDrawY = Game_Camera.GetRelativeY(m_sync_fPosY);

                if (isSelf()) Game_Camera.Move(m_sync_fSpeedX, m_sync_fSpeedY, lDelta);
            }

            m_SQUARE.setBounds((int) (fDrawX - sm_fBoxSize), (int) (fDrawY - sm_fBoxSize), (int) (fDrawX + sm_fBoxSize), (int) (fDrawY + sm_fBoxSize));
            m_SQUARE.draw(canvas);

            canvas.drawText(m_sync_strName, fDrawX, fDrawY, sm_txtPaint);
        }
        else if (isSelf() && Game_Main.isStarted())
        {
            Game_Camera.Move(m_sync_fSpeedX, m_sync_fSpeedY, lDelta);
            fDrawX = Game_Camera.GetDrawX();
            fDrawY = Game_Camera.GetDrawY();
            sm_SPECTATE.setBounds((int) (fDrawX - sm_fBoxSize), (int) (fDrawY - sm_fBoxSize), (int) (fDrawX + sm_fBoxSize), (int) (fDrawY + sm_fBoxSize));
            sm_SPECTATE.draw(canvas);
        }
    }
}
