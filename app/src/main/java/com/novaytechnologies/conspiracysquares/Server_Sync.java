package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class Server_Sync
{
    static Utility_Post sm_newPost;
    static Utility_Post sm_syncPost;

    static boolean sm_bSyncInProgress = false;

    /*
        DESCRIPTION:
            Performs initial join tasks when starting a game, then syncs with the server.
            Exits the server if there is an error when joining.
        POST-CONDITION:
            The Player's color will be set to a random color.
            The Player's name will be set to the name they chose.
            The Player's ID will be received from the server.
            The Server synchronization process will then be started.
    */
    static void PopulateFromServer(final Context ctx)
    {
        Server_P2P_ThreadManager.sm_PlayerIPs = new ArrayList<>();
        Server_P2P_ThreadManager.sm_Player_Threads = new HashMap<>();

        ArrayList<String> params = new ArrayList<>();
        params.add("ReqPass");
        params.add("X");
        params.add("ServerName");
        params.add(Game_Main.sm_strServerName);
        params.add("ServerPassword");
        params.add(Game_Main.sm_strServerPass);
        params.add("ServerJoined");
        params.add("TRUE");
        params.add("IP");
        params.add(Game_Main.sm_strIP);
        String ParemsString = Utility_Post.GetParemsString(params);

        sm_newPost = new Utility_Post();
        sm_newPost.SetRunnable(new Utility_Post.RunnableArgs() {
            @Override
            public void run() {
                String LastResult = GetArgs()[0];
                if (LastResult != null)
                {
                    int nGetIDindex = LastResult.indexOf('=', 0);
                    if (nGetIDindex != -1)
                    {
                        String strGet = LastResult.substring(nGetIDindex + 1);
                        Game_Player.SetSelfID(Integer.parseInt(strGet));
                        Game_Player.GetNewSelfColor();
                        Game_Player.CreateSelf(ctx);
                        Server_P2P_ThreadManager.SpawnServerThread();
                        SyncWithServer(ctx, true);
                    }
                    else {
                        Dialog_Popup.Connect_Error(ctx);
                        Game_Main.EndGame();
                    }
                }
                else {
                    Dialog_Popup.Connect_Error(ctx);
                    Game_Main.EndGame();
                }
            }
        });
        sm_newPost.SetRunnableError(new Utility_Post.RunnableArgs() {
            @Override
            public void run() {
                Dialog_Popup.Connect_Error(ctx);
                Game_Main.EndGame();
            }
        });

        sm_bSyncInProgress = true;
        sm_newPost.execute("https://conspiracy-squares.appspot.com/Servlet_GetServerInfo", ParemsString);
    }

    // Leaves the currently connected server.
    static void LeaveServer(String strIPtoDisconnect)
    {
        ArrayList<String> params = new ArrayList<>();
        params.add("ReqPass");
        params.add("X");
        params.add("ServerName");
        params.add(Game_Main.sm_strServerName);
        params.add("ServerPassword");
        params.add(Game_Main.sm_strServerPass);
        params.add("ServerJoined");
        params.add("LEFT");
        params.add("IP");
        params.add(strIPtoDisconnect);
        String ParemsString = Utility_Post.GetParemsString(params);

        Utility_Post endPost = new Utility_Post();
        endPost.execute("https://conspiracy-squares.appspot.com/Servlet_GetServerInfo", ParemsString);
    }

    /*
        DESCRIPTION:
            Performs synchronization tasks with the server.
        POST-CONDITION:
            Updates all player IPs on success.
            Retries synchronization on failure.
    */
    static private void DoSync()
    {
        ArrayList<String> params = new ArrayList<>();
        params.add("ReqPass");
        params.add("X");
        params.add("ServerName");
        params.add(Game_Main.sm_strServerName);
        params.add("ServerPassword");
        params.add(Game_Main.sm_strServerPass);
        params.add("ServerJoined");
        params.add("SYNC");
        params.add("IP");
        params.add(Game_Main.sm_strIP);
        String ParemsString = Utility_Post.GetParemsString(params);

        sm_syncPost = new Utility_Post();
        sm_syncPost.SetRunnable(new Utility_Post.RunnableArgs() {
            @Override
            public void run() {
                String LastResult = GetArgs()[0];
                if (LastResult != null)
                {
                    String strGet;
                    int nEndIndex;
                    int nNextIndex = LastResult.indexOf('+', 0);
                    Server_P2P_ThreadManager.sm_PlayerIPs = new ArrayList<>();
                    while (nNextIndex != -1)
                    {
                        nEndIndex = LastResult.indexOf('+', 2);
                        if (nEndIndex != -1)
                        {
                            strGet = LastResult.substring(nNextIndex + 1, nEndIndex);
                            LastResult = LastResult.substring(nEndIndex);
                            nNextIndex = LastResult.indexOf('+', 0);
                        }
                        else
                        {
                            strGet = LastResult.substring(nNextIndex + 1);
                            nNextIndex = -1;
                        }
                        Server_P2P_ThreadManager.sm_PlayerIPs.add(strGet);
                        Log.d("DEBUG", strGet);
                    }
                    Server_P2P_ThreadManager.SpawnPlayerThreads();
                }
                else Log.e("SYNC Error", "Could retrieve server sync info!");
                sm_bSyncInProgress = false;
            }
        });
        sm_syncPost.SetRunnableError(new Utility_Post.RunnableArgs() {
            @Override
            public void run() {
                Log.e("SYNC Error", "Could not sync with server!");
                sm_bSyncInProgress = false;
            }
        });

        sm_bSyncInProgress = true;
        sm_syncPost.execute("https://conspiracy-squares.appspot.com/Servlet_GetServerInfo", ParemsString);
    }

    // Check for any new players before updating
    static void SyncWithServer(final Context ctx, final boolean bStart)
    {
        if (Game_Main.isStarted() && (!sm_bSyncInProgress || bStart))
        {
            ArrayList<String> params = new ArrayList<>();
            params.add("ReqPass");
            params.add("X");
            params.add("ServerName");
            params.add(Game_Main.sm_strServerName);
            params.add("ServerPassword");
            params.add(Game_Main.sm_strServerPass);
            params.add("IP");
            params.add(Game_Main.sm_strIP);
            String ParemsString = Utility_Post.GetParemsString(params);

            sm_syncPost = new Utility_Post();
            sm_syncPost.SetRunnable(new Utility_Post.RunnableArgs() {
                @Override
                public void run() {
                    String LastResult = GetArgs()[0];
                    if (LastResult != null && !LastResult.contains("PASSWORD_WRONG"))
                    {
                        if (LastResult.equals("true") || bStart) DoSync();
                        else sm_bSyncInProgress = false;
                    }
                    else
                    {
                        Log.e("SYNC Error", "Wrong Password at new player joined determination!");
                        sm_bSyncInProgress = false;
                    }
                }
            });
            sm_syncPost.SetRunnableError(new Utility_Post.RunnableArgs() {
                @Override
                public void run() {
                    Log.e("SYNC Error", "Could not determine if new player joined!");
                    sm_bSyncInProgress = false;
                }
            });

            sm_bSyncInProgress = true;
            sm_syncPost.execute("https://conspiracy-squares.appspot.com/Servlet_CheckJoined", ParemsString);
        }
    }
}
