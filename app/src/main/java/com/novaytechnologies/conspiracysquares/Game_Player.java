//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

/**
 * All the code for a Player
 * @author Jesse Primiani
 */
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

    /**
     * Updates the scaling factors.
     * @author Jesse Primiani
     * @param fScreenMaxSize The new screen size to reference in scaling
     */
    static void UpdateAllSizes(float fScreenMaxSize)
    {
        sm_fBoxSize = fScreenMaxSize * fSIZE_FRACTION;
        sm_fPlayer_Speed = sm_fBoxSize * fDIST_PER_MILLI;
    }

    // The spectating drawable
    static Drawable sm_SPECTATE;

    // The player drawable and Context
    Context m_ctx;
    private Drawable m_SQUARE;

    // Change Tracking
    private long m_lMoveNum = 0L;
    private long m_lChangeNum = 0L;

    // All synchronized player information
    private int m_nID;
    private String m_sync_strName = "";
    private int m_sync_nSelfColor = 0;
    private float m_sync_fPosX = 0f;
    private float m_sync_fPosY = 0f;
    private int m_sync_nFlags = -1;

    /**
     * @author Jesse Primiani
     * @return The number of moves made since the round started
     */
    long GetMoveNum() {return m_lMoveNum;}

    /**
     * @author Jesse Primiani
     * @return The number of changes made since the round started
     */
    long GetChangeNum() {return m_lChangeNum;}

    // The X and Y components of the player's movement direction vector in global coordinates
    private float m_fSpeedX = 0f;
    private float m_fSpeedY = 0f;

    // Stores and Gets the index of the SELF player object, stored in Game_Main.sm_PlayersArray
    static private int sm_nID = 0;

    /**
     * Sets your ID, used by the server.
     * @author Jesse Primiani
     * @param nID Your new ID
     */
    static void SetSelfID(int nID) {sm_nID = nID;}

    /**
     * Returns your current player ID.
     * @author Jesse Primiani
     * @return Your current player ID
     */
    static int GetSelfID() {return sm_nID;}

    /**
     * Returns your current player instance.
     * @author Jesse Primiani
     * @return Your current player instance
     */
    static Game_Player GetSelf() {return Game_Main.sm_PlayersArray.get(sm_nID);}

    // Player state information
    private boolean m_bDrawableLoaded = false;

    /**
     * Determines if the given player is you.
     * @author Jesse Primiani
     * @return True if the player object is controlled by the local device
     */
    private boolean isSelf() {return m_nID == sm_nID;}

    /**
     * @author Jesse Primiani
     * @return Whether the player is currently alive
     */
    private boolean isAlive() {return (m_sync_nFlags & FLAG_ALIVE) > 0;}

    /**
     * The function to create the locally controlled player, SELF.
     * @author Jesse Primiani
     * @param ctx The application context handler
     * @param nColor the Color of your player
     */
    static void CreateSelf(Context ctx, int nColor)
    {
        Game_Player Self = GetSelf();
        Self.m_sync_nSelfColor = nColor;
        Self.m_ctx = ctx;

        Self.m_lMoveNum = 1L;
        Self.m_lChangeNum = 1L;
        Self.m_sync_nFlags = 0b1;

        Self.m_sync_strName = Utility_SharedPreferences.get().loadName(ctx);
        sm_txtPaint = new Paint();
        sm_txtPaint.setShadowLayer(8f, 0, 0, Color.rgb(0, 0, 0));
        sm_txtPaint.setColor(Color.rgb(255, 255, 255));
        sm_txtPaint.setTextSize(sm_fBoxSize / 16);
        sm_txtPaint.setTextAlign(Paint.Align.CENTER);

        Self.m_SQUARE = ctx.getResources().getDrawable(R.drawable.vec_player);
        Self.m_SQUARE.setColorFilter(Self.m_sync_nSelfColor, PorterDuff.Mode.MULTIPLY);

        Self.m_bDrawableLoaded = true;
    }

    /**
     * Sets the movement the player, SELF, using a local position on the screen to determine the global movement direction.
     * @author Jesse Primiani
     * @param fScreenX The X position of the screen tap
     * @param fScreenY The Y position of the screen tap
     */
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
            Self.m_fSpeedX = 5*(fPlayerXdist / fPlayer_Time) / Game_Camera.sm_fScaleFactor;
            Self.m_fSpeedY = 5*(fPlayerYdist / fPlayer_Time) / Game_Camera.sm_fScaleFactor;
        }
        else
        {
            // Stop if the center of the screen is touched
            Self.m_fSpeedX = 0f;
            Self.m_fSpeedY = 0f;
        }

        Server_Sync.SendMove(Self.m_sync_fPosX, Self.m_sync_fPosY, Self.m_fSpeedX, Self.m_fSpeedY, ++Self.m_lMoveNum, Self.m_lChangeNum);
    }

    /**
     * Sets the player drawable to the dead player drawable.
     * @author Jesse Primiani
     */
    private void Kill()
    {
        m_fSpeedX = 0f;
        m_fSpeedY = 0f;
        if (m_SQUARE != null) m_SQUARE.clearColorFilter();
        m_SQUARE = m_ctx.getResources().getDrawable(R.drawable.player_dead);
    }

    /**
     * Update synchronized player data, movement count.
     * @author Jesse Primiani
     * @param lN the new movement count
     */
    void UpdateMoveNum(long lN) {m_lMoveNum = lN;}
    /**
     * Update synchronized player data, change count.
     * @author Jesse Primiani
     * @param lN the new change count
     */
    void UpdateChangeNum(long lN) {m_lChangeNum = lN;}
    /**
     * Update synchronized player data, name.
     * @author Jesse Primiani
     * @param strName the new name
     */
    void UpdateName(String strName)
    {
        m_sync_strName = strName;
    }
    /**
     * Update synchronized player data, X-pos.
     * @author Jesse Primiani
     * @param fX the new X pos
     */
    void UpdateX(float fX) {m_sync_fPosX = fX;}
    /**
     * Update synchronized player data, Y-pos.
     * @author Jesse Primiani
     * @param fY the new Y pos
     */
    void UpdateY(float fY) {m_sync_fPosY = fY;}
    /**
     * Update synchronized player data, X velocity.
     * @author Jesse Primiani
     * @param fX the new X velocity
     */
    void UpdateSpdX(float fX) {m_fSpeedX = fX;}
    /**
     * Update synchronized player data, Y velocity.
     * @author Jesse Primiani
     * @param fY the new Y velocity
     */
    void UpdateSpdY(float fY) {m_fSpeedY = fY;}
    /**
     * Update synchronized player data, color.
     * @author Jesse Primiani
     * @param nColor the new color
     */
    void UpdateColor(int nColor)
    {
        if (isAlive())
        {
            m_SQUARE = m_ctx.getResources().getDrawable(R.drawable.vec_player);
            m_SQUARE.setColorFilter(nColor, PorterDuff.Mode.MULTIPLY);
            m_bDrawableLoaded = true;
        }
    }

    /**
     * Updates the player's flag information, and run the relevant functions when any flag changes.
     * @author Jesse Primiani
     * @param nFlags The updated flags
     */
    void UpdateF(int nFlags)
    {
        int nPrevFlags = m_sync_nFlags;
        m_sync_nFlags = nFlags;
        if (nPrevFlags != nFlags && nFlags >= 0)
        {
            if (!isAlive()) Kill();
        }
    }

    /**
     * Draws the player if the player isn't a spectator, then moves the player if said player is SELF.
     * If said player is SELF and is either dead or a spectator, this simply moves the camera and draws a centered square.
     * @author Jesse Primiani
     * @param canvas The canvas used by the layout
     * @param lDelta The time since the last movement
     */
    void DrawPlayer(Canvas canvas, long lDelta) {
        float fDrawX;
        float fDrawY;

        if (m_bDrawableLoaded && m_sync_nFlags >= 0)
        {
            if(m_sync_fPosX<=Game_Main.mapSize&&m_sync_fPosX>=-Game_Main.mapSize)
            {m_sync_fPosX += m_fSpeedX * lDelta; }
            else{
                if(m_sync_fPosX<=Game_Main.mapSize){m_sync_fPosX+=1;}
                if(m_sync_fPosX>=Game_Main.mapSize){m_sync_fPosX-=1;}
            }
            if(m_sync_fPosY<=Game_Main.mapSize&&m_sync_fPosY>=-Game_Main.mapSize)
            {m_sync_fPosY += m_fSpeedY * lDelta; }
            else{
                if(m_sync_fPosY<=Game_Main.mapSize){m_sync_fPosY+=1;}
                if(m_sync_fPosY>=Game_Main.mapSize){m_sync_fPosY-=1;}
            }
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

                if (isSelf())
                {
                    Game_Camera.Move(m_fSpeedX, m_fSpeedY, lDelta);
                    float fDrawXcam = Game_Camera.GetDrawX();
                    float fDrawYcam = Game_Camera.GetDrawY();
                    sm_SPECTATE.setBounds((int) (fDrawXcam - sm_fBoxSize), (int) (fDrawYcam - sm_fBoxSize), (int) (fDrawXcam + sm_fBoxSize), (int) (fDrawYcam + sm_fBoxSize));
                    sm_SPECTATE.draw(canvas);
                }
            }

            m_SQUARE.setBounds((int) (fDrawX - sm_fBoxSize), (int) (fDrawY - sm_fBoxSize), (int) (fDrawX + sm_fBoxSize), (int) (fDrawY + sm_fBoxSize));
            m_SQUARE.draw(canvas);

            canvas.drawText(m_sync_strName, fDrawX, fDrawY, sm_txtPaint);
        }
        else if (isSelf() && Game_Main.isStarted())
        {
            Game_Camera.Move(m_fSpeedX, m_fSpeedY, lDelta);
            fDrawX = Game_Camera.GetDrawX();
            fDrawY = Game_Camera.GetDrawY();
            sm_SPECTATE.setBounds((int) (fDrawX - sm_fBoxSize), (int) (fDrawY - sm_fBoxSize), (int) (fDrawX + sm_fBoxSize), (int) (fDrawY + sm_fBoxSize));
            sm_SPECTATE.draw(canvas);
        }
    }
}
