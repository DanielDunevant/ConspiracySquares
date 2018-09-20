//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.graphics.Canvas;


import java.util.ArrayList;

import static com.novaytechnologies.conspiracysquares.Server_Sync.ResolveEncryption;

/**
 * The primary functions for the game itself
 * @author Jesse Primiani
 */
class Game_Main
{
    static public float mapSize = 500;

    // Whether the game information is initialized.
    static private boolean sm_bStarted = false;

    // Whether the round has officially started
    static private boolean sm_bRoundStarted = false;

    // The joined server name and password
    static String sm_strServerName;
    static String sm_strServerPass;

    // A array containing all possible players, initialized to be spectators until filled by an actual player.
    static ArrayList<Game_Player> sm_PlayersArray = new ArrayList<>();

    // The server-side last update time
    static long lLastUpdate = 0;

    static boolean isStarted() {return sm_bStarted;}
    static boolean isRoundStarted() {return sm_bRoundStarted;}

    /**
     * Joins the given server and starts the game.
     * @author Jesse Primiani
     * @param strServer The server to join
     * @param strPass The server's password
     * @param ctx The application context handler
     */
    static void JoinServer(String strServer, String strPass, Context ctx)
    {
        if (!sm_bStarted)
        {
            sm_bStarted = true;
            Game_Main.sm_strServerName = strServer;
            Game_Main.sm_strServerPass = strPass;

            for (int nPlayer = 0; nPlayer < Utility_SharedPreferences.MAX_PLAYERS + 1; nPlayer++)
            {
                Game_Player newPlayer = new Game_Player(nPlayer);
                newPlayer.m_ctx = ctx;
                sm_PlayersArray.add(newPlayer);
            }
            Game_Player.sm_SPECTATE = ctx.getResources().getDrawable(R.drawable.vec_spectate);
            Server_Sync.PopulateFromServer(ctx);
        }
    }

    /**
        DESCRIPTION:
            Called after ServerSync.PopulateFromServer, when the player ID is obtained but before other player IPs are received
        POST-CONDITION:
            The Player's color will be set to a random color received from the server.
            The Player's name will be set to the name they chose.
            The Player's ID will be received from the server.
            The Server synchronization process will then be started.
            The player will spectate if the round already started.
     @author Jesse Primiani
     @param nID Your player's ID
     @param bRoundStarted Whether the round has started when you joined
     @param nColor Your player's color
     @param  ctx The application context handler
    */
    static void ServerJoinComplete(int nID, boolean bRoundStarted, int nColor, Context ctx)
    {
        Game_Timer.init();

        sm_bRoundStarted = bRoundStarted;

        Game_Player.SetSelfID(nID);
        Game_Player.CreateSelf(ctx, nColor);

        if (bRoundStarted) {Game_Player.GetSelf().UpdateF(-1);}

        Server_Sync.sm_bInitialJoinDone = true;
    }

    /**
     * Game shutdown
     * @author Jesse Primiani
     */
    static void EndGame()
    {
        if (sm_bStarted)
        {
            sm_bStarted = false;
            if (Server_Sync.sm_newPost != null) Server_Sync.sm_newPost.cancel(true);
            if (Server_Sync.sm_syncPost != null) Server_Sync.sm_syncPost.cancel(true);
            Server_Sync.LeaveServer();
            Server_Sync.sm_bSyncInProgress = false;
            Server_Sync.sm_bInitialJoinDone = false;
            sm_PlayersArray.clear();
        }
    }

    /**
     * Runs when the Layout size changes or is initialized.
     * @author Jesse Primiani
     */
    static void GameSizeChanged()
    {
        Game_Camera.sm_fScaleFactor = Layout_Game_Draw.sm_nMaxSide / 100f;
        Game_Player.UpdateAllSizes(Layout_Game_Draw.sm_nMaxSide);
        Game_Camera.UpdateCenter(Layout_Game_Draw.sm_nWidth_Center, Layout_Game_Draw.sm_nHeight_Center);
    }

    /**
     * Called when Layout_Game_Draw is touched.
     * @author Jesse Primiani
     * @param fX The X position that was touched
     * @param fY The Y position that was touched
     */
    static void TouchUpdated(float fX, float fY)
    {
        Game_Player.MoveSelfToLocal(fX, fY);
    }

    /**
     * Game loop, called in Layout_Game_Draw.
     * @author Jesse Primiani
     * @param lDrawDelta The time since the last loop iteration
     * @param canvas The layout's canvas
     * @param ctx The application's context handler
     */
    static void GameLoop(long lDrawDelta, Canvas canvas, Context ctx)
    {
        Server_Sync.CheckAndUpdate(ctx);
        Layout_Game_Draw.DrawGrid(canvas);
        for (Game_Player Player : Game_Main.sm_PlayersArray)
        {
            Player.DrawPlayer(canvas, lDrawDelta);
        }
        Layout_Game_Draw.DrawPlayerNotifications(canvas,ctx);
        Layout_Game_Draw.DrawMinimap(canvas);
        Game_Timer.runTimer();
    }
}