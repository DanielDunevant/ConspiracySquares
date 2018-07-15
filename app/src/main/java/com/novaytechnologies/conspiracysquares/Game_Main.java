//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.HashMap;

// The primary functions for the game itself
class Game_Main
{
    // Whether the game information is initialized.
    static private boolean sm_bStarted = false;

    // Whether the round has officially started
    static private boolean sm_bRoundStarted = false;

    // The timer to check whether someone joined or left the server
    static private long sm_lLastSync = 0L;

    // The joined server name and password, along with the phone's IP
    static String sm_strServerName;
    static String sm_strServerPass;
    static String sm_strIP = "";
    static int sm_nPort = -1;

    // A array containing all possible players, initialized to be spectators until filled by an actual player.
    static ArrayList<Game_Player> sm_PlayersArray = new ArrayList<>();

    static boolean isStarted() {return sm_bStarted;}
    static boolean isRoundStarted() {return sm_bRoundStarted;}

    // Joins the given server and starts the game
    static void JoinServer(String strServer, String strPass, Context ctx)
    {
        if (!sm_bStarted)
        {
            sm_bStarted = true;
            Game_Main.sm_strServerName = strServer;
            Game_Main.sm_strServerPass = strPass;

            for (int nPlayer = 0; nPlayer < Utility_SharedPreferences.MAX_PLAYERS; nPlayer++)
            {
                Game_Player newPlayer = new Game_Player(nPlayer);
                newPlayer.m_ctx = ctx;
                sm_PlayersArray.add(newPlayer);
            }
            Game_Player.sm_SPECTATE = ctx.getResources().getDrawable(R.drawable.vec_spectate);
            Server_P2P_ThreadManager.sm_PlayerIPs = new ArrayList<>();
            Server_P2P_ThreadManager.sm_Player_Ports = new HashMap<>();
            Server_P2P_ThreadManager.sm_Player_Threads = new HashMap<>();
            Server_Sync.PopulateFromServer(ctx);
        }
    }

    /*
        DESCRIPTION:
            Called after ServerSync.PopulateFromServer, when the player ID is obtained but before other player IPs are received
        POST-CONDITION:
            The Player's color will be set to a random color.
            The Player's name will be set to the name they chose.
            The Player's ID was received from the server.
            The Server synchronization process will then be started.
    */
    static void ServerJoinComplete(int nID, boolean bRoundStarted, String strIP, Context ctx)
    {
        sm_bRoundStarted = bRoundStarted;
        sm_strIP = strIP;

        Game_Player.SetSelfID(nID);
        Game_Player.GetNewSelfColor();
        Game_Player.CreateSelf(ctx);

        Server_P2P_ThreadManager.SpawnServerThread();
        Server_Sync.SyncWithServer(true);
    }

    // Game shutdown
    static void EndGame()
    {
        if (sm_bStarted)
        {
            if (Server_Sync.sm_newPost != null) Server_Sync.sm_newPost.cancel(true);
            if (Server_Sync.sm_syncPost != null) Server_Sync.sm_syncPost.cancel(true);
            Server_Sync.LeaveServer(sm_strIP);
            Server_Sync.sm_bSyncInProgress = false;
            Server_P2P_ThreadManager.StopAllThreads();
            sm_PlayersArray.clear();
            sm_bStarted = false;
        }
    }

    // Runs when the Layout size changes or is initialized.
    static void GameSizeChanged()
    {
        Game_Camera.sm_fScaleFactor = Layout_Game_Draw.sm_nMaxSide / 100f;
        Game_Player.UpdateAllSizes(Layout_Game_Draw.sm_nMaxSide);
        Game_Camera.UpdateCenter(Layout_Game_Draw.sm_nWidth_Center, Layout_Game_Draw.sm_nHeight_Center);
    }

    // Called when Layout_Game_Draw is touched
    static void TouchUpdated(float fX, float fY)
    {
        Game_Player.MoveSelfToLocal(fX, fY);
    }

    // Game loop, called in Layout_Game_Draw
    // Also checks whether a new player joined every few seconds
    static void GameLoop(Context ctx, long lDrawDelta, Canvas canvas)
    {
        Layout_Game_Draw.DrawGrid(canvas);
        for (Game_Player Player : Game_Main.sm_PlayersArray)
        {
            Player.DrawPlayer(canvas, lDrawDelta);
        }

        if (System.currentTimeMillis() > sm_lLastSync)
        {
            Server_Sync.SyncWithServer(false);
            sm_lLastSync = System.currentTimeMillis() + 4000;
        }
    }

    // Runs whenever new information is received from another player
    static void GotPlayerInfo(int nPlayerID)
    {
        //
    }

    // Runs whenever local information is sent to another player
    static void SentSelfInfo()
    {
        //
    }
}
