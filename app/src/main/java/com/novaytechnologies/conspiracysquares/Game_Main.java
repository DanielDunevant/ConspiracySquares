//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.HashMap;

import static com.novaytechnologies.conspiracysquares.Server_Sync.ResolveEncryption;

// The primary functions for the game itself
class Game_Main
{
    static public float mapSize = 500;
    // Whether the game information is initialized.
    static private boolean sm_bStarted = false;

    //Whether round is starting due to timer running out of time
    static public boolean sm_brRoundStarting= false;

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

    //Round start Time variable
    private static long roundStartTimer= 0;
    //public static long timeTillRoundStarts = 120000;
    public static long timeTillRoundStarts = 12000;

    public static long timeElasped;

    // Joins the given server and starts the game
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

    /*
        DESCRIPTION:
            Called after ServerSync.PopulateFromServer, when the player ID is obtained but before other player IPs are received
        POST-CONDITION:
            The Player's color will be set to a random color received from the server.
            The Player's name will be set to the name they chose.
            The Player's ID will be received from the server.
            The Server synchronization process will then be started.
            The player will spectate if the round already started.
    */
    static void ServerJoinComplete(int nID, boolean bRoundStarted, int nColor, Context ctx)
    {
        roundStartTimer = System.currentTimeMillis();
        sm_bRoundStarted = bRoundStarted;

        Game_Player.SetSelfID(nID);
        Game_Player.CreateSelf(ctx, nColor);

        if (bRoundStarted) {Game_Player.GetSelf().UpdateF(-1);}

        Server_Sync.sm_bInitialJoinDone = true;
    }

    // Game shutdown
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
    static void GameLoop(long lDrawDelta, Canvas canvas, Context ctx)
    {
        Server_Sync.CheckAndUpdate(ctx);
        Layout_Game_Draw.DrawGrid(canvas);
        for (Game_Player Player : Game_Main.sm_PlayersArray)
        {
            Player.DrawPlayer(canvas, lDrawDelta);
        }
        if(timeElasped>=timeTillRoundStarts) {
            if (Game_Main.sm_PlayersArray.size() >= 3) {
                Utility_Post gameStartPost = new Utility_Post();
                ArrayList<String> params = new ArrayList<>();
                params.add("ReqPass");
                params.add(ResolveEncryption());
                params.add("ServerName");
                params.add(Game_Main.sm_strServerName);
                params.add("ServerPassword");
                params.add(Game_Main.sm_strServerPass);
                String ParamsString = Utility_Post.GetParamsString(params);
                Game_Main.sm_brRoundStarting=true;
                gameStartPost.execute("https://conspiracy-squares.appspot.com/Servlet_StartRound", ParamsString);
            } else {
                timeElasped = 0;
            }
        } else{ timeElasped = System.currentTimeMillis() - roundStartTimer;}
    }
}

