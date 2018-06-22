package com.novaytechnologies.conspiracysquares;

import android.content.Context;

import java.util.ArrayList;

public class Server_Sync
{
    static Utility_Post newPost;
    static Utility_Post syncPost;

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
        ArrayList<String> params = new ArrayList<>();
        params.add("ReqPass");
        params.add("X");
        params.add("ServerName");
        params.add(Game_Main.sm_strServerName);
        params.add("ServerPassword");
        params.add(Game_Main.sm_strServerPass);
        params.add("ServerJoined");
        params.add("TRUE");
        params.add("ID");
        params.add("0");

        params.add("Player_Color");
        params.add(Integer.toString(Game_Player.GetNewSelfColor()));
        params.add("Player_Name");
        params.add(Utility_SharedPreferences.get().loadName(ctx));
        String ParemsString = Utility_Post.GetParemsString(params);

        newPost = new Utility_Post();
        newPost.SetRunnable(new Utility_Post.RunnableArgs() {
            @Override
            public void run() {
                String LastResult = GetArgs()[0];
                if (LastResult != null && LastResult.indexOf(';', 0) != -1)
                {
                    String strGet = LastResult.substring(LastResult.indexOf('=', 0) + 1, LastResult.indexOf('+', 0));
                    Game_Player.SetSelfID(Integer.parseInt(strGet));
                    Game_Player.CreateSelf(ctx);
                    SyncWithServer(ctx, true);
                }
            }
        });
        newPost.SetRunnableError(new Utility_Post.RunnableArgs() {
            @Override
            public void run() {
                Server_Error.Connect_Error(ctx);
                Game_Main.EndGame();
            }
        });

        sm_bSyncInProgress = true;
        newPost.execute("https://conspiracy-squares.appspot.com/Servlet_GetServer", ParemsString);
    }

    // Leaves the currently connected server.
    static void LeaveServer()
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
        params.add("ID");
        params.add(Integer.toString(Game_Player.GetSelfID()));
        String ParemsString = Utility_Post.GetParemsString(params);

        Utility_Post endPost = new Utility_Post();
        endPost.execute("https://conspiracy-squares.appspot.com/Servlet_GetServer", ParemsString);
    }

    /*
        DESCRIPTION:
            Performs synchronization tasks with the server.
        POST-CONDITION:
            Updates all player information on success.
            Retries synchronization on failure.
    */
    static void SyncWithServer(final Context ctx, boolean bStart)
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
            params.add("ServerJoined");
            params.add("SYNC");
            params.add("ID");
            params.add(Integer.toString(Game_Player.GetSelfID()));

            params.add("Player_X");
            params.add(Float.toString(Game_Player.GetX()));
            params.add("Player_Y");
            params.add(Float.toString(Game_Player.GetY()));
            params.add("Player_Flags");
            params.add(Integer.toString(Game_Player.GetFlags()));
            String ParemsString = Utility_Post.GetParemsString(params);

            syncPost = new Utility_Post();
            syncPost.SetRunnable(new Utility_Post.RunnableArgs() {
                @Override
                public void run() {
                    String LastResult = GetArgs()[0];
                    if (LastResult != null && LastResult.indexOf(';', 0) != -1)
                    {
                        int nSelfID = Game_Player.GetSelfID();

                        String strGet;
                        LastResult = LastResult.substring(LastResult.indexOf('+', 0) + 2);

                        int nPlayerIndex = 0;
                        boolean bGetNext = true;
                        while (bGetNext)
                        {
                            strGet = LastResult.substring(0, LastResult.indexOf('&', 0));
                            if (strGet.contains("+"))
                            {
                                strGet = LastResult.substring(0, LastResult.indexOf('+', 0));
                                LastResult = LastResult.substring(LastResult.indexOf('+', 0) + 2);
                                bGetNext = false;
                            }
                            else LastResult = LastResult.substring(LastResult.indexOf('&', 0) + 1);
                            if (nPlayerIndex != nSelfID) Game_Main.sm_PlayersArray.get(nPlayerIndex).UpdateX(Float.parseFloat(strGet));
                            nPlayerIndex++;
                        }

                        nPlayerIndex = 0;
                        bGetNext = true;
                        while (bGetNext)
                        {
                            strGet = LastResult.substring(0, LastResult.indexOf('&', 0));
                            if (strGet.contains("+"))
                            {
                                strGet = LastResult.substring(0, LastResult.indexOf('+', 0));
                                LastResult = LastResult.substring(LastResult.indexOf('+', 0) + 2);
                                bGetNext = false;
                            }
                            else LastResult = LastResult.substring(LastResult.indexOf('&', 0) + 1);
                            if (nPlayerIndex != nSelfID) Game_Main.sm_PlayersArray.get(nPlayerIndex).UpdateY(Float.parseFloat(strGet));
                            nPlayerIndex++;
                        }

                        nPlayerIndex = 0;
                        bGetNext = true;
                        while (bGetNext)
                        {
                            strGet = LastResult.substring(0, LastResult.indexOf('&', 0));
                            if (strGet.contains("+"))
                            {
                                strGet = LastResult.substring(0, LastResult.indexOf('+', 0));
                                LastResult = LastResult.substring(LastResult.indexOf('+', 0) + 2);
                                bGetNext = false;
                            }
                            else LastResult = LastResult.substring(LastResult.indexOf('&', 0) + 1);
                            if (nPlayerIndex != nSelfID) Game_Main.sm_PlayersArray.get(nPlayerIndex).UpdateF(Integer.parseInt(strGet), ctx);
                            nPlayerIndex++;
                        }

                        if (LastResult.length() > 4)
                        {
                            nPlayerIndex = 0;
                            bGetNext = true;
                            while (bGetNext)
                            {
                                strGet = LastResult.substring(0, LastResult.indexOf('&', 0));
                                if (strGet.contains("+"))
                                {
                                    strGet = LastResult.substring(0, LastResult.indexOf('+', 0));
                                    LastResult = LastResult.substring(LastResult.indexOf('+', 0) + 2);
                                    bGetNext = false;
                                }
                                else LastResult = LastResult.substring(LastResult.indexOf('&', 0) + 1);
                                if (nPlayerIndex != nSelfID) Game_Main.sm_PlayersArray.get(nPlayerIndex).UpdateColor(Integer.parseInt(strGet), ctx);
                                nPlayerIndex++;
                            }

                            nPlayerIndex = 0;
                            bGetNext = true;
                            while (bGetNext)
                            {
                                strGet = LastResult.substring(0, LastResult.indexOf('&', 0));
                                if (strGet.contains("+"))
                                {
                                    strGet = LastResult.substring(0, LastResult.indexOf('+', 0));
                                    LastResult = LastResult.substring(LastResult.indexOf('+', 0) + 2);
                                    bGetNext = false;
                                }
                                else LastResult = LastResult.substring(LastResult.indexOf('&', 0) + 1);
                                if (nPlayerIndex != nSelfID) Game_Main.sm_PlayersArray.get(nPlayerIndex).UpdateName(strGet);
                                nPlayerIndex++;
                            }
                        }
                    }
                    sm_bSyncInProgress = false;
                }
            });
            syncPost.SetRunnableError(new Utility_Post.RunnableArgs() {
                @Override
                public void run() {
                    sm_bSyncInProgress = false;
                }
            });

            sm_bSyncInProgress = true;
            syncPost.execute("https://conspiracy-squares.appspot.com/Servlet_GetServer", ParemsString);
        }
    }
}
