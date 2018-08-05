//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

// Synchronizes Information between players from the cloud server
public class Server_Sync
{
    static Utility_Post sm_newPost;
    static Utility_Post sm_syncPost;

    static boolean sm_bInitialJoinDone = false;
    static boolean sm_bSyncInProgress = false;

    static String ResolveEncryption()
    {
        return "X";
    }

    /*
        DESCRIPTION:
            Performs initial join tasks when starting a game.
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
        params.add("Self_Name");
        params.add(Utility_SharedPreferences.get().loadName(ctx));
        String ParamsString = Utility_Post.GetParamsString(params);

        sm_newPost = new Utility_Post();
        sm_newPost.SetRunnable(new Utility_Post.RunnableArgs() {
            @Override
            public void run() {
                String LastResult = GetArgs()[0];
                if (LastResult != null && !LastResult.isEmpty())
                {
                    int nGetIDindex = LastResult.indexOf('=', 0);
                    int nGetBindex = LastResult.indexOf('+', 0);
                    int nGetBindex2 = LastResult.indexOf('=', nGetBindex);
                    int nGetCindex = LastResult.indexOf('+', nGetBindex2);
                    int nGetCindex2 = LastResult.indexOf('=', nGetCindex);
                    if (nGetIDindex != -1)
                    {
                        String strGet = LastResult.substring(nGetIDindex + 1, nGetBindex);
                        String strGet2 = LastResult.substring(nGetBindex2 + 1, nGetCindex);
                        String strGet3 = LastResult.substring(nGetCindex2 + 1);
                        Game_Main.ServerJoinComplete(Integer.parseInt(strGet), strGet2.contains("true"), Integer.parseInt(strGet3), ctx);
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
                sm_bSyncInProgress = false;
            }
        });
        sm_newPost.SetRunnableError(new Utility_Post.RunnableArgs() {
            @Override
            public void run() {
                Dialog_Popup.Connect_Error(ctx);
                Game_Main.EndGame();
                sm_bSyncInProgress = false;
            }
        });

        sm_bSyncInProgress = true;
        sm_newPost.execute("https://conspiracy-squares.appspot.com/Servlet_SVR_ServerJoin", ParamsString);
    }

    // Leaves the currently connected server.
    static void LeaveServer()
    {
        ArrayList<String> params = new ArrayList<>();
        params.add("ReqPass");
        params.add(ResolveEncryption());
        params.add("ServerName");
        params.add(Game_Main.sm_strServerName);
        params.add("ServerPassword");
        params.add(Game_Main.sm_strServerPass);
        params.add("Self_ID");
        params.add(Integer.toString(Game_Player.GetSelfID()));
        String ParamsString = Utility_Post.GetParamsString(params);

        Utility_Post endPost = new Utility_Post();
        endPost.execute("https://conspiracy-squares.appspot.com/Servlet_SVR_ServerLeave", ParamsString);
    }

    //Sends player movement information to the server
    static void SendMove(float fX, float fY, float fDX, float fDY, long lMoveNum)
    {
        ArrayList<String> params = new ArrayList<>();
        params.add("ReqPass");
        params.add(ResolveEncryption());
        params.add("ServerName");
        params.add(Game_Main.sm_strServerName);
        params.add("ServerPassword");
        params.add(Game_Main.sm_strServerPass);

        params.add("Self_ID");
        params.add(Integer.toString(Game_Player.GetSelfID()));
        params.add("Self_X");
        params.add(Float.toString(fX));
        params.add("Self_Y");
        params.add(Float.toString(fY));
        params.add("Self_dX");
        params.add(Float.toString(fDX));
        params.add("Self_dY");
        params.add(Float.toString(fDY));
        params.add("Self_MoveNum");
        params.add(Long.toString(lMoveNum));
        String ParamsString = Utility_Post.GetParamsString(params);

        Utility_Post movePost = new Utility_Post();
        movePost.execute("https://conspiracy-squares.appspot.com/Servlet_Game_Move", ParamsString);
    }

    //Updates Game State and Players' States from the Cloud Server
    static void CheckAndUpdate()
    {
        if ((!sm_bSyncInProgress || sm_syncPost.isCancelled()) && sm_bInitialJoinDone)
        {
            ArrayList<String> params = new ArrayList<>();
            params.add("ReqPass");
            params.add(ResolveEncryption());
            params.add("ServerName");
            params.add(Game_Main.sm_strServerName);
            params.add("ServerPassword");
            params.add(Game_Main.sm_strServerPass);
            String ParamsString = Utility_Post.GetParamsString(params);

            sm_syncPost = new Utility_Post();
            sm_syncPost.SetRunnable(new Utility_Post.RunnableArgs() {
                @Override
                public void run() {
                    String LastResult = GetArgs()[0];
                    if (LastResult != null && !LastResult.isEmpty() && LastResult.indexOf(';', 0) != -1)
                    {
                        String strGet;
                        int nGetID;
                        int nLastIndex;
                        boolean bNext = LastResult.contains("+");
                        while (bNext)
                        {
                            nLastIndex = LastResult.indexOf('-', 1);
                            strGet = LastResult.substring(1, nLastIndex);
                            nGetID = Integer.parseInt(strGet);

                            Game_Player Player = Game_Main.sm_PlayersArray.get(nGetID);

                            LastResult = LastResult.substring(nLastIndex + 1);
                            nLastIndex = LastResult.indexOf('-', 0);
                            strGet = LastResult.substring(0, nLastIndex);

                            Player.UpdateX(Float.parseFloat(strGet));

                            LastResult = LastResult.substring(nLastIndex + 1);
                            nLastIndex = LastResult.indexOf('-', 0);
                            strGet = LastResult.substring(0, nLastIndex);

                            Player.UpdateY(Float.parseFloat(strGet));

                            LastResult = LastResult.substring(nLastIndex + 1);
                            nLastIndex = LastResult.indexOf('-', 0);
                            strGet = LastResult.substring(0, nLastIndex);

                            Player.UpdateSpdX(Float.parseFloat(strGet));

                            LastResult = LastResult.substring(nLastIndex + 1);
                            nLastIndex = LastResult.indexOf('-', 0);
                            strGet = LastResult.substring(0, nLastIndex);

                            Player.UpdateSpdY(Float.parseFloat(strGet));

                            LastResult = LastResult.substring(nLastIndex + 1);
                            nLastIndex = LastResult.indexOf('-', 0);
                            strGet = LastResult.substring(0, nLastIndex);

                            Player.UpdateF(Integer.parseInt(strGet));

                            LastResult = LastResult.substring(nLastIndex + 1);
                            nLastIndex = LastResult.indexOf('-', 0);
                            strGet = LastResult.substring(0, nLastIndex);

                            Player.UpdateColor(Integer.parseInt(strGet));

                            LastResult = LastResult.substring(nLastIndex + 1);
                            nLastIndex = LastResult.indexOf('+', 0);
                            if (nLastIndex == -1) nLastIndex = LastResult.indexOf(';', 0);
                            strGet = LastResult.substring(0, nLastIndex);

                            Player.UpdateName(strGet);

                            LastResult = LastResult.substring(nLastIndex);
                            bNext = LastResult.contains("+");
                        }
                    }
                    sm_bSyncInProgress = false;
                }
            });
            sm_syncPost.SetRunnableError(new Utility_Post.RunnableArgs() {
                @Override
                public void run() {
                    sm_bSyncInProgress = false;
                }
            });

            sm_bSyncInProgress = true;
            sm_syncPost.execute("https://conspiracy-squares.appspot.com/Servlet_Game_Sync", ParamsString);
        }
    }
}
