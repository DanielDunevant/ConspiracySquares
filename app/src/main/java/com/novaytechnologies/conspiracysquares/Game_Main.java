//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;

import java.util.ArrayList;

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

    // A array containing all possible players, initialized to be spectators until filled by an actual player.
    static ArrayList<Game_Player> sm_PlayersArray = new ArrayList<>();

    static boolean isStarted() {return sm_bStarted;}
    static boolean isRoundStarted() {return sm_bRoundStarted;}

    // Game initialization
    static void StartGame(final Context ctx)
    {
        if (!sm_bStarted)
        {
            sm_strIP = Utility_SharedPreferences.getIP();
            if (sm_strIP != null)
            {
                for (int nPlayer = 0; nPlayer < Utility_SharedPreferences.MAX_PLAYERS; nPlayer++)
                {
                    Game_Player newPlayer = new Game_Player(nPlayer);
                    newPlayer.m_ctx = ctx;
                    sm_PlayersArray.add(newPlayer);
                }
                Game_Player.sm_SPECTATE = ctx.getResources().getDrawable(R.drawable.vec_spectate);
                Server_Sync.PopulateFromServer(ctx);
                sm_bStarted = true;
            }
            else
            {
                Dialog_Popup.Connect_Error(ctx);
            }
        }
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

    // Game synchronization loop
    // Checks whether a new player joined every 4 seconds
    static void GameLoop(Context ctx)
    {
        if (System.currentTimeMillis() > sm_lLastSync)
        {
            Server_Sync.SyncWithServer(ctx, false);
            sm_lLastSync = System.currentTimeMillis() + 4000;
        }
    }

    // Runs when the Layout size changes.
    static void GameSizeChanged(Layout_Game_Draw Draw)
    {
        Game_Camera.sm_fScaleFactor = Draw.m_nMaxSide / 100f;
        Game_Player.UpdateAllSizes(Draw.m_nMaxSide);
        Game_Camera.UpdateCenter(Draw.m_nWidth_Center, Draw.m_nHeight_Center);
    }
}
