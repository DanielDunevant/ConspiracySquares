//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;

// The Game Activity.
public class Activity_Game extends AppCompatActivity {

    // Instance State for this Activity.
    static private final String STARTED = "com.novaytechnologies.conspiracysquares.Started";

    // Intent Extras for this Activity.
    static final String FIND_SERVER = "com.novaytechnologies.conspiracysquares.Find_Server";
    static final String SERVER = "com.novaytechnologies.conspiracysquares.Server_Name";

    /*
        DESCRIPTION:
            Finds a non-full server, or creates a new one if none exist, then joins said server.
        PRE-CONDITION:
            The FIND_SERVER Intent Extra must be false.
        POST-CONDITION:
            The server will be joined and Game_Main.StartGame(ctx) will thus run.
    */
    private void FindServer(final Context ctx)
    {
        Utility_Post GetServers = new Utility_Post();
        GetServers.SetRunnable(new Utility_Post.RunnableArgs() {
            @Override
            public void run() {
                String LastResult = GetArgs()[0];
                ArrayList<String> Servers = Utility_ServerList.get().populateList(LastResult);

                int nPlayers;
                int nServers = Servers.size();
                boolean bJoined = false;
                for (int nServer = 0; nServer < nServers; nServer++)
                {
                    nPlayers = Utility_ServerList.get().getPlayers(nServer);
                    if (nPlayers < Utility_ServerList.MAX_PLAYERS)
                    {
                        Game_Main.sm_strServerName = Utility_ServerList.get().getServerString(nServer);
                        bJoined = true;
                        nServer = nServers;
                    }
                }

                if (!bJoined)
                {
                    String strMakeServer = "Server_" + Integer.toString(++nServers);
                    while (Utility_ServerList.get().contains(strMakeServer))
                    {
                        strMakeServer = "Server_" + Integer.toString(++nServers);
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

        if (savedInstanceState == null || !savedInstanceState.getBoolean(STARTED))
        {
            Intent LoadI = getIntent();
            boolean bFindServer = LoadI.getBooleanExtra(FIND_SERVER, true);

            if (bFindServer)
                FindServer(this.getApplicationContext());
            else {
                Game_Main.sm_strServerName = LoadI.getStringExtra(SERVER);
                Game_Main.StartGame(this.getApplicationContext());
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle data)
    {
        super.onSaveInstanceState(data);
        data.putBoolean(STARTED, true);
    }
}
