//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

/**
 * Camera related code and functions.
 * @author Jesse Primiani
 */
public class Game_Camera
{
    // The local to global coordinate scaling factor
    // Always set to 1/100th of the longest side of the game's layout
    static float sm_fScaleFactor = 1f;

    // Camera's Global Position
    private static float sm_fPosX = 0f;
    private static float sm_fPosY = 0f;

    // Center of the screen / Player's usual location
    private static float sm_fDrawX = 0f;
    private static float sm_fDrawY = 0f;

    static float GetDrawX() {return sm_fDrawX;}
    static float GetDrawY() {return sm_fDrawY;}
    static float GetGlobalX() {return sm_fPosX;}
    static float GetGlobalY() {return sm_fPosY;}

    /**
     * Updates the position of the center of the screen.
     * @author Jesse Primiani
     * @param nCenterX The X position of the center
     * @param nCenterY The Y position of the center
     */
    static void UpdateCenter(int nCenterX, int nCenterY)
    {
        sm_fDrawX = (float) nCenterX;
        sm_fDrawY = (float) nCenterY;
    }

    /**
     * Set the global position of the camera using the given coordinates.
     * @author Jesse Primiani
     * @param fX The global X position of the camera
     * @param fY The global Y position of the camera
     */
    static void UpdatePosition(float fX, float fY)
    {
        sm_fPosX = fX;
        sm_fPosY = fY;
    }

    /**
     * Move the global position of the camera using the given speed and change in time.
     * @author Jesse Primiani
     * @param m_fSpeedX The X velocity of the camera
     * @param m_fSpeedY The Y velocity of the camera
     * @param lDelta The change in time since the last movement
     */
    static void Move(float m_fSpeedX, float m_fSpeedY, long lDelta)
    {
        sm_fPosX += m_fSpeedX * lDelta;
        sm_fPosY += m_fSpeedY * lDelta;
    }

    /**
     * Converts a global X coordinate to its corresponding position on the screen.
     * @author Jesse Primiani
     * @param fGlobalX The global object's X coordinate
     * @return The screen relative X position
     */
    static float GetRelativeX(float fGlobalX)
    {
        return sm_fDrawX - ((sm_fPosX - fGlobalX) * sm_fScaleFactor);
    }

    /**
     * Converts a global Y coordinate to its corresponding position on the screen.
     * @author Jesse Primiani
     * @param fGlobalY The global object's Y coordinate
     * @return The screen relative Y position
     */
    static float GetRelativeY(float fGlobalY)
    {
        return sm_fDrawY - ((sm_fPosY - fGlobalY) * sm_fScaleFactor);
    }
}
