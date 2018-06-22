//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;

import java.util.ArrayList;

class Game_Main
{
    // Whether the game information is initialized.
    static private boolean sm_bStarted = false;

    // The joined server name and password
    static String sm_strServerName;
    static String sm_strServerPass;

    // A array containing all possible players, initialized to be spectators until filled by an actual player.
    static ArrayList<Game_Player> sm_PlayersArray = new ArrayList<>();

    static boolean isStarted() {return sm_bStarted;}

    // Game initialization
    static void StartGame(Context ctx)
    {
        if (!sm_bStarted)
        {
            for (int nPlayer = 0; nPlayer < Utility_SharedPreferences.MAX_PLAYERS; nPlayer++)
            {
                sm_PlayersArray.add(new Game_Player(nPlayer));
            }
            Game_Player.sm_SPECTATE = ctx.getResources().getDrawable(R.drawable.vec_spectate);
            Server_Sync.PopulateFromServer(ctx);
            sm_bStarted = true;
        }
    }

    // Game shutdown
    static void EndGame()
    {
        if (sm_bStarted)
        {
            Server_Sync.newPost.cancel(true);
            Server_Sync.syncPost.cancel(true);
            Server_Sync.LeaveServer();
            Server_Sync.sm_bSyncInProgress = false;
            sm_PlayersArray.clear();
            sm_bStarted = false;
        }
    }

    // Game synchronization loop
    static void GameLoop(Context ctx)
    {
        Server_Sync.SyncWithServer(ctx, false);
    }

    // Runs when the Layout size changes.
    static void GameSizeChanged(Layout_Game_Draw Draw)
    {
        Game_Camera.sm_fScaleFactor = Draw.m_nMaxSide / 100f;
        Game_Player.UpdateAllSizes(Draw.m_nMaxSide);
        Game_Camera.UpdateCenter(Draw.m_nWidth_Center, Draw.m_nHeight_Center);
    }
}
