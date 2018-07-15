package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

// Synchronizes Connection (IP/PORT) Information between players from the cloud
public class Server_Sync
{
    static Utility_Post sm_newPost;
    static Utility_Post sm_syncPost;

    static boolean sm_bSyncInProgress = false;

    static String ResolveEncryption()
    {
        return "X";
    }

    /*
        DESCRIPTION:
            Performs initial join tasks when starting a game, then syncs with the server.
            Exits the server if there is an error when joining.
    */
    static void PopulateFromServer(final Context ctx)
    {
        ArrayList<String> params = new ArrayList<>();
        params.add("ReqPass");
        params.add(ResolveEncryption());
        params.add("ServerName");
        params.add(Game_Main.sm_strServerName);
        params.add("ServerPassword");
        params.add(Game_Main.sm_strServerPass);
        params.add("IP");
        params.add(Game_Main.sm_strIP);
        String ParamsString = Utility_Post.GetParamsString(params);

        sm_newPost = new Utility_Post();
        sm_newPost.SetRunnable(new Utility_Post.RunnableArgs() {
            @Override
            public void run() {
                String LastResult = GetArgs()[0];
                if (LastResult != null)
                {
                    int nGetIDindex = LastResult.indexOf('=', 0);
                    int nGetBindex = LastResult.indexOf('+', 0);
                    int nGetBindex2 = LastResult.indexOf('=', nGetBindex);
                    int nGetIindex = LastResult.indexOf('+', nGetBindex2);
                    int nGetIindex2 = LastResult.indexOf('=', nGetIindex);
                    if (nGetIDindex != -1)
                    {
                        String strGet = LastResult.substring(nGetIDindex + 1, nGetBindex);
                        String strGet2 = LastResult.substring(nGetBindex2 + 1, nGetIindex);
                        String strGet3 = LastResult.substring(nGetIindex2 + 1);
                        Game_Main.ServerJoinComplete(Integer.parseInt(strGet), strGet2.contains("true"), strGet3, ctx);
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
        sm_newPost.execute("https://conspiracy-squares.appspot.com/Servlet_ServerJoin", ParamsString);
    }

    // Leaves the currently connected server.
    static void LeaveServer(String strIPtoDisconnect)
    {
        ArrayList<String> params = new ArrayList<>();
        params.add("ReqPass");
        params.add(ResolveEncryption());
        params.add("ServerName");
        params.add(Game_Main.sm_strServerName);
        params.add("ServerPassword");
        params.add(Game_Main.sm_strServerPass);
        params.add("IP");
        params.add(strIPtoDisconnect);
        String ParamsString = Utility_Post.GetParamsString(params);

        Utility_Post endPost = new Utility_Post();
        endPost.execute("https://conspiracy-squares.appspot.com/Servlet_ServerLeave", ParamsString);
    }

    // Updates the server-side round started state
    static void SetRoundStart(boolean bStartRound)
    {
        ArrayList<String> params = new ArrayList<>();
        params.add("ReqPass");
        params.add(ResolveEncryption());
        params.add("ServerName");
        params.add(Game_Main.sm_strServerName);
        params.add("ServerPassword");
        params.add(Game_Main.sm_strServerPass);
        params.add("RoundStart");
        if (bStartRound) params.add("true");
        else params.add("false");
        String ParamsString = Utility_Post.GetParamsString(params);

        Utility_Post endPost = new Utility_Post();
        endPost.execute("https://conspiracy-squares.appspot.com/Servlet_ServerRound", ParamsString);
    }

    // Update Local Server's Port.
    static void UpdatePort()
    {
        ArrayList<String> params = new ArrayList<>();
        params.add("ReqPass");
        params.add(ResolveEncryption());
        params.add("ServerName");
        params.add(Game_Main.sm_strServerName);
        params.add("ServerPassword");
        params.add(Game_Main.sm_strServerPass);
        params.add("IP");
        params.add(Game_Main.sm_strIP);
        params.add("PORT");
        params.add(Integer.toString(Game_Main.sm_nPort));
        String ParamsString = Utility_Post.GetParamsString(params);

        Utility_Post endPost = new Utility_Post();
        endPost.execute("https://conspiracy-squares.appspot.com/Servlet_ServerPortUpdate", ParamsString);
    }

    /*
        DESCRIPTION:
            Performs IP and PORT synchronization tasks with the server.
        POST-CONDITION:
            Updates all player IPs and PORTs on success.
            Retries synchronization later on failure.
    */
    static private void DoSync()
    {
        ArrayList<String> params = new ArrayList<>();
        params.add("ReqPass");
        params.add(ResolveEncryption());
        params.add("ServerName");
        params.add(Game_Main.sm_strServerName);
        params.add("ServerPassword");
        params.add(Game_Main.sm_strServerPass);
        params.add("IP");
        params.add(Game_Main.sm_strIP);
        String ParamsString = Utility_Post.GetParamsString(params);

        sm_syncPost = new Utility_Post();
        sm_syncPost.SetRunnable(new Utility_Post.RunnableArgs() {
            @Override
            public void run() {
                String LastResult = GetArgs()[0];
                if (LastResult != null)
                {
                    String strGetIP;
                    String strGetPort;
                    int nNextIndex;
                    Server_P2P_ThreadManager.sm_PlayerIPs = new ArrayList<>();
                    while (!LastResult.startsWith("+"))
                    {
                        nNextIndex = LastResult.indexOf('+', 0);
                        strGetIP = LastResult.substring(0, nNextIndex);
                        LastResult = LastResult.substring(nNextIndex + 1);

                        nNextIndex = LastResult.indexOf('+', 0);
                        strGetPort = LastResult.substring(0, nNextIndex);
                        LastResult = LastResult.substring(nNextIndex + 1);

                        Server_P2P_ThreadManager.sm_PlayerIPs.add(strGetIP);
                        Server_P2P_ThreadManager.sm_Player_Ports.put(strGetIP, Integer.parseInt(strGetPort));
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
        sm_syncPost.execute("https://conspiracy-squares.appspot.com/Servlet_ServerListIPs", ParamsString);
    }

    // Check for any new players before updating
    static void SyncWithServer(final boolean bStart)
    {
        if (Game_Main.isStarted() && (!sm_bSyncInProgress || bStart))
        {
            ArrayList<String> params = new ArrayList<>();
            params.add("ReqPass");
            params.add(ResolveEncryption());
            params.add("ServerName");
            params.add(Game_Main.sm_strServerName);
            params.add("ServerPassword");
            params.add(Game_Main.sm_strServerPass);
            params.add("IP");
            params.add(Game_Main.sm_strIP);
            String ParamsString = Utility_Post.GetParamsString(params);

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
            sm_syncPost.execute("https://conspiracy-squares.appspot.com/Servlet_CheckJoined", ParamsString);
        }
    }
}
