//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;

import java.util.ArrayList;

class Game_Main
{
    private static boolean sm_bStarted = false;
    private static boolean sm_bSyncInProgress = false;

    static String sm_strServerName;
    static ArrayList<Game_Player> sm_PlayersArray = new ArrayList<>();

    static private int sm_nID = 0;

    static private void PopulateFromServer(final Context ctx)
    {
        ArrayList<String> params = new ArrayList<>();
        params.add("ReqPass");
        params.add("X");
        params.add("ServerName");
        params.add(sm_strServerName);
        params.add("ServerJoined");
        params.add("TRUE");
        params.add("ID");
        params.add("0");

        params.add("Player_Color");
        params.add(Integer.toString(Game_Player.GetColor()));
        params.add("Player_Name");
        params.add(Game_Player.GetName());
        String ParemsString = Utility_Post.GetParemsString(params);

        Utility_Post newPost = new Utility_Post();
        newPost.SetRunnable(new Utility_Post.RunnableArgs() {
            @Override
            public void run() {
                String LastResult = GetArgs()[0];
                if (LastResult != null && LastResult.indexOf(';', 0) != -1)
                {
                    String strGet = LastResult.substring(LastResult.indexOf('=', 0) + 1, LastResult.indexOf('+', 0));
                    sm_nID = Integer.parseInt(strGet);
                    SyncWithServer(ctx, true);
                }
            }
        });
        newPost.SetRunnableError(new Utility_Post.RunnableArgs() {
            @Override
            public void run() {
                sm_bSyncInProgress = false;
            }
        });

        sm_bSyncInProgress = true;
        newPost.execute("https://conspiracy-squares.appspot.com/Servlet_GetServer", ParemsString);
    }

    static private void LeaveServer()
    {
        while (sm_bSyncInProgress)
        {
            try {Thread.sleep(500);}
            catch (Exception ex) {ex.printStackTrace();}
        }
        ArrayList<String> params = new ArrayList<>();
        params.add("ReqPass");
        params.add("X");
        params.add("ServerName");
        params.add(sm_strServerName);
        params.add("ServerJoined");
        params.add("LEFT");
        params.add("ID");
        params.add(Integer.toString(sm_nID));
        String ParemsString = Utility_Post.GetParemsString(params);

        Utility_Post newPost = new Utility_Post();
        newPost.execute("https://conspiracy-squares.appspot.com/Servlet_GetServer", ParemsString);
    }

    static void StartGame(Context ctx)
    {
        if (!sm_bStarted)
        {
            Game_Player.CreateSelf(ctx);
            for (int nPlayer = 0; nPlayer < Layout_Servers.MAX_PLAYERS; nPlayer++)
            {
                sm_PlayersArray.add(new Game_Player());
            }
            PopulateFromServer(ctx);
            sm_bStarted = true;
        }
    }

    static void EndGame()
    {
        LeaveServer();
        sm_PlayersArray.clear();
        sm_bSyncInProgress = false;
        sm_bStarted = false;
    }

    static void SyncWithServer(final Context ctx, boolean bStart)
    {
        if (!sm_bSyncInProgress || bStart)
        {
            ArrayList<String> params = new ArrayList<>();
            params.add("ReqPass");
            params.add("X");
            params.add("ServerName");
            params.add(sm_strServerName);
            params.add("ServerJoined");
            params.add("SYNC");
            params.add("ID");
            params.add(Integer.toString(sm_nID));

            params.add("Player_X");
            params.add(Float.toString(Game_Player.GetX()));
            params.add("Player_Y");
            params.add(Float.toString(Game_Player.GetY()));
            params.add("Player_Flags");
            params.add(Integer.toString(Game_Player.GetFlags()));
            String ParemsString = Utility_Post.GetParemsString(params);

            Utility_Post newPost = new Utility_Post();
            newPost.SetRunnable(new Utility_Post.RunnableArgs() {
                @Override
                public void run() {
                    String LastResult = GetArgs()[0];
                    if (LastResult != null && LastResult.indexOf(';', 0) != -1)
                    {
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
                            if (nPlayerIndex != sm_nID) sm_PlayersArray.get(nPlayerIndex).UpdateX(Float.parseFloat(strGet));
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
                            if (nPlayerIndex != sm_nID) sm_PlayersArray.get(nPlayerIndex).UpdateY(Float.parseFloat(strGet));
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
                            if (nPlayerIndex != sm_nID) sm_PlayersArray.get(nPlayerIndex).UpdateF(Integer.parseInt(strGet), ctx);
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
                                if (nPlayerIndex != sm_nID) sm_PlayersArray.get(nPlayerIndex).UpdateColor(Integer.parseInt(strGet), ctx);
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
                                if (nPlayerIndex != sm_nID) sm_PlayersArray.get(nPlayerIndex).UpdateName(strGet);
                                nPlayerIndex++;
                            }
                        }
                    }
                    sm_bSyncInProgress = false;
                }
            });
            newPost.SetRunnableError(new Utility_Post.RunnableArgs() {
                @Override
                public void run() {
                    sm_bSyncInProgress = false;
                }
            });

            sm_bSyncInProgress = true;
            newPost.execute("https://conspiracy-squares.appspot.com/Servlet_GetServer", ParemsString);
        }
    }
}
