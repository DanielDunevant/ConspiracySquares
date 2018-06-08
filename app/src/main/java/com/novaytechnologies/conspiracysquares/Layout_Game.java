//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;

public class Layout_Game extends AppCompatActivity {

    static private final String strSTARTED = "com.novaytechnologies.conspiracysquares.STARTED";

    private void FindServer(final Context ctx)
    {
        Utility_Post GetServers = new Utility_Post();
        GetServers.SetRunnable(new Utility_Post.RunnableArgs() {
            @Override
            public void run() {
                String LastResult = GetArgs()[0];
                if (LastResult != null && LastResult.indexOf(';', 0) != -1)
                {
                    HashSet<String> ServerNames = new HashSet<>();
                    String strGet;
                    int nNumPlayers;
                    int nServerCount = 0;
                    boolean bJoined = false;
                    boolean bNext = true;
                    if (!LastResult.contains("+")) bNext = false;
                    while (bNext)
                    {
                        strGet = LastResult.substring(1, LastResult.indexOf('&', 1));
                        ServerNames.add(strGet);
                        int nLastIndex = LastResult.indexOf('+', 1);
                        if (nLastIndex < 0)
                        {
                            bNext = false;
                            nNumPlayers = Integer.parseInt(LastResult.substring(LastResult.indexOf('&', 1) + 1, LastResult.indexOf(';', 1)));
                        }
                        else
                        {
                            nNumPlayers = Integer.parseInt(LastResult.substring(LastResult.indexOf('&', 1) + 1, nLastIndex));
                            LastResult = LastResult.substring(nLastIndex);
                        }

                        if (nNumPlayers < Layout_Servers.MAX_PLAYERS)
                        {
                            Game_Main.sm_strServerName = strGet;
                            bNext = false;
                            bJoined = true;
                        }
                        nServerCount++;
                    }
                    if (!bJoined)
                    {
                        String strMakeServer = "Server_" + Integer.toString(++nServerCount);
                        while (ServerNames.contains(strMakeServer))
                        {
                            strMakeServer = "Server_" + Integer.toString(++nServerCount);
                        }
                        final String strNewServer = strMakeServer;

                        ArrayList<String> params = new ArrayList<>();
                        params.add("ReqPass");
                        params.add("X");
                        params.add("ServerName");
                        params.add(strNewServer);
                        String ParemsString = Utility_Post.GetParemsString(params);

                        Utility_Post newPost = new Utility_Post();
                        newPost.SetRunnable(new Utility_Post.RunnableArgs() {
                            @Override
                            public void run() {
                                Game_Main.sm_strServerName = strNewServer;
                                Game_Main.StartGame(ctx);
                            }
                        });
                        newPost.execute("https://conspiracy-squares.appspot.com/Servlet_CreateServer", ParemsString);
                    }
                    else Game_Main.StartGame(ctx);
                }
            }
        });
        GetServers.execute("https://conspiracy-squares.appspot.com/Servlet_ListServers", "");
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (savedInstanceState == null || !savedInstanceState.getBoolean(strSTARTED))
        {
            Intent LoadI = getIntent();
            boolean bFindServer = LoadI.getBooleanExtra(Layout_Main.FIND_SERVER, true);

            if (bFindServer)
                FindServer(this);
            else {
                Game_Main.sm_strServerName = LoadI.getStringExtra(Layout_Main.SERVER);
                Game_Main.StartGame(this);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle data)
    {
        super.onSaveInstanceState(data);
        data.putBoolean(strSTARTED, true);
    }
}
