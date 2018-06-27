//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// The Game Activity.
public class Activity_Game extends AppCompatActivity {

    // Instance State for this Activity.
    static private final String STARTED = Utility_SharedPreferences.sm_strAppDomain + ".Started";

    // Intent Extras for this Activity.
    static final String FIND_SERVER = Utility_SharedPreferences.sm_strAppDomain + ".Find_Server";
    static final String SERVER = Utility_SharedPreferences.sm_strAppDomain + ".Server_Name";
    static final String SERVER_PASS = Utility_SharedPreferences.sm_strAppDomain + ".Server_Password";

    /*
        DESCRIPTION:
            Finds a non-full public server, or creates a new one if none exist, then joins said server.
        POST-CONDITION:
            The server will be joined and Game_Main.StartGame(ctx) will run.
    */
    private void FindServer(final Context ctx)
    {
        Utility_Post GetServers = new Utility_Post();
        GetServers.SetRunnableError(new Utility_Post.RunnableArgs() {
            @Override
            public void run() {
                Dialog_Popup.Connect_Error(ctx);
            }
        });
        GetServers.SetRunnable(new Utility_Post.RunnableArgs() {
            @Override
            public void run() {
                String LastResult = GetArgs()[0];
                ArrayList<String> Servers = Server_ServerList.get().populateList(LastResult);

                boolean bPrivate;
                int nPlayers;
                int nServers = Servers.size();
                boolean bJoined = false;
                for (int nServer = 0; nServer < nServers; nServer++)
                {
                    bPrivate = Server_ServerList.get().getServerPrivate(nServer);
                    nPlayers = Server_ServerList.get().getServerPlayers(nServer);
                    if (!bPrivate && nPlayers < Utility_SharedPreferences.MAX_PLAYERS)
                    {
                        Game_Main.sm_strServerName = Server_ServerList.get().getServerString(nServer);
                        Game_Main.sm_strServerPass = "";
                        bJoined = true;
                        nServer = nServers;
                    }
                }

                if (!bJoined)
                {
                    String strMakeServer = "Server_" + Integer.toString(++nServers);
                    while (Server_ServerList.get().contains(strMakeServer))
                    {
                        strMakeServer = "Server_" + Integer.toString(++nServers);
                    }
                    final String strNewServer = strMakeServer;

                    ArrayList<String> params = new ArrayList<>();
                    params.add("ReqPass");
                    params.add("X");
                    params.add("ServerName");
                    params.add(strNewServer);
                    params.add("ServerPassword");
                    params.add("");
                    String ParemsString = Utility_Post.GetParemsString(params);

                    Utility_Post newPost = new Utility_Post();
                    newPost.SetRunnableError(new Utility_Post.RunnableArgs() {
                        @Override
                        public void run() {
                            Dialog_Popup.Connect_Error(ctx);
                        }
                    });
                    newPost.SetRunnable(new Utility_Post.RunnableArgs() {
                        @Override
                        public void run() {
                            Game_Main.sm_strServerName = strNewServer;
                            Game_Main.sm_strServerPass = "";
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

            if (bFindServer) FindServer(this);
            else {
                Game_Main.sm_strServerName = LoadI.getStringExtra(SERVER);
                Game_Main.sm_strServerPass = LoadI.getStringExtra(SERVER_PASS);
                Game_Main.StartGame(this);
            }

            final Context ctx = this;
            final ScheduledThreadPoolExecutor TestJoined = new ScheduledThreadPoolExecutor(1);
            TestJoined.schedule(new Runnable() {
                @Override
                public void run() {
                    if (!Game_Main.isStarted())
                    {
                        Log.e("Find_Exception", "Could not find or start server!");
                        Dialog_Popup.Find_Error(ctx.getApplicationContext());
                    }
                }
            }, 4, TimeUnit.SECONDS);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle data)
    {
        super.onSaveInstanceState(data);
        data.putBoolean(STARTED, true);
    }
}
