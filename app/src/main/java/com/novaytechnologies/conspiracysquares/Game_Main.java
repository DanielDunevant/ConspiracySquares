//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.graphics.Canvas;

import java.util.ArrayList;

class Game_Main
{
    private static boolean sm_bStarted = false;
    private static boolean sm_bSyncInProgress = false;

    static String sm_strServerName;
    static ArrayList<Game_Player> sm_PlayersArray = new ArrayList<>();;

    static void StartGame(Context ctx)
    {
        if (!sm_bStarted)
        {
            Game_Player.CreateSelf(ctx);
            Game_Main.PopulateFromServer();
            sm_bStarted = true;
        }
    }

    static void EndGame()
    {
        sm_PlayersArray.clear();
        sm_bSyncInProgress = false;
        sm_bStarted = false;
    }

    static void PopulateFromServer()
    {
        //TODO Get Players & Index From Server, then Post Game_Player.Self data
    }

    static void SyncWithServer()
    {
        //
    }

    static void DrawGrid(Canvas canvas)
    {
        //
    }
}
