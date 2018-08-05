//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;

import java.util.ArrayList;

// Gets and Adds information about all available servers on the cloud
public class Server_ServerList
{
    // ServerList Server Information, accessed via index.
    private ArrayList<String> ServerNames;
    private ArrayList<Boolean> ServerPrivates;
    private ArrayList<Integer> ServerPlayers;

    // Singleton Code
    private Server_ServerList() {}
    static private Server_ServerList ServerListSingleton;
    static Server_ServerList get()
    {
        if (ServerListSingleton == null) ServerListSingleton = new Server_ServerList();
        return ServerListSingleton;
    }

    /*
        DESCRIPTION:
            Populates Server Information by parsing the contents of the given string.
        PRE-CONDITION:
            strParseString must be a valid string returned by a POST to Servlet_ListServers.
        POST-CONDITION:
            The ServerList will be populated with all active servers if no errors occur during execution.
            This function returns an ArrayList of formatted strings containing the number of players.
            You should Loop through the returned ArrayList to indirectly get index information.
    */
    ArrayList<String> populateList(String strParseString)
    {
        ServerNames = new ArrayList<>();
        ServerPrivates = new ArrayList<>();
        ServerPlayers = new ArrayList<>();

        ArrayList<String> ServerPlayersString = new ArrayList<>();

        if (strParseString != null && strParseString.indexOf(';', 0) != -1)
        {
            String strGet;
            String strNumPlayers;
            boolean bPrivate;
            boolean bNext = true;
            if (!strParseString.contains("+")) bNext = false;
            while (bNext)
            {
                strGet = strParseString.substring(1, strParseString.indexOf('&', 1));

                int nLastIndex = strParseString.indexOf('+', 1);
                if (nLastIndex < 0)
                {
                    bNext = false;
                    strParseString = strParseString.substring(strParseString.indexOf('&', 1) + 1, strParseString.indexOf(';', 1));
                    strNumPlayers = strParseString.substring(0, strParseString.indexOf('&', 1));
                    bPrivate = Boolean.parseBoolean(strParseString.substring(strParseString.indexOf('&', 1) + 1));
                }
                else
                {
                    int nAMPindexNext = strParseString.indexOf('&', 1) + 1;
                    strNumPlayers = strParseString.substring(nAMPindexNext, strParseString.indexOf('&', nAMPindexNext));
                    bPrivate = Boolean.parseBoolean(strParseString.substring(strParseString.indexOf('&', nAMPindexNext) + 1, nLastIndex));
                    strParseString = strParseString.substring(nLastIndex);
                }

                ServerNames.add(strGet);
                ServerPrivates.add(bPrivate);
                ServerPlayers.add(Integer.parseInt(strNumPlayers));

                String strPlyr = strNumPlayers + " / " + Integer.toString(Utility_SharedPreferences.MAX_PLAYERS);
                ServerPlayersString.add(strPlyr);
            }
        }
        return ServerPlayersString;
    }

    // Creates a non-existent server name
    private String CreateNewName(int nStartNum)
    {
        String strMakeServer = "Server_" + Integer.toString(nStartNum);
        while (contains(strMakeServer))
        {
            strMakeServer = "Server_" + Integer.toString(++nStartNum);
        }
        return strMakeServer;
    }

    // Creates a new Server
    void CreateAndJoinServer(final String strServer, final String strPass, final Context ctx)
    {
        if (!strServer.isEmpty()) {
            ArrayList<String> params = new ArrayList<>();
            params.add("ReqPass");
            params.add(Server_Sync.ResolveEncryption());
            params.add("ServerName");
            params.add(strServer);
            params.add("ServerPassword");
            params.add(strPass);
            String ParamsString = Utility_Post.GetParamsString(params);

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
                    Activity_Game.Start(strServer, strPass, ctx);
                }
            });
            newPost.execute("https://conspiracy-squares.appspot.com/Servlet_SVR_CreateServer", ParamsString);
        }
    }

    // Checks if the given password is correct, then joins the given server if it is
    void JoinPrivate(final String strServer, final String strPass, final Context ctx)
    {
        if (strServer != null && !strServer.isEmpty() && !strPass.isEmpty())
        {
            ArrayList<String> params = new ArrayList<>();
            params.add("ReqPass");
            params.add(Server_Sync.ResolveEncryption());
            params.add("ServerName");
            params.add(strServer);
            params.add("ServerPassword");
            params.add(strPass);
            String ParamsString = Utility_Post.GetParamsString(params);

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
                    String LastResult = GetArgs()[0];
                    if (LastResult != null && !LastResult.isEmpty())
                    {
                        if (LastResult.contains("PASSWORD_CORRECT"))
                            Activity_Game.Start(strServer, strPass, ctx);
                        else
                            Dialog_Popup.Wrong_Password(ctx);
                    }
                }
            });
            newPost.execute("https://conspiracy-squares.appspot.com/Servlet_SVR_CheckPassword", ParamsString);
        }
    }

    /*
        DESCRIPTION:
            Finds a non-full public server, or creates a new one if none exist, then joins said server.
        POST-CONDITION:
            The found or created server will be joined.
    */
    void FindOrCreateServer(final Context ctx)
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
                ArrayList<String> Servers = populateList(LastResult);

                boolean bPrivate;
                int nPlayers;
                int nServers = Servers.size();
                boolean bJoined = false;
                for (int nServer = 0; nServer < nServers; nServer++)
                {
                    bPrivate = getServerPrivate(nServer);
                    nPlayers = getServerPlayers(nServer);
                    if (!bPrivate && nPlayers < Utility_SharedPreferences.MAX_PLAYERS)
                    {
                        Activity_Game.Start(getServerString(nServer), "", ctx);
                        bJoined = true;
                        nServer = nServers;
                    }
                }

                if (!bJoined)
                {
                    final String strNewServer = CreateNewName(++nServers);
                    CreateAndJoinServer(strNewServer, "", ctx);
                }
            }
        });
        GetServers.execute("https://conspiracy-squares.appspot.com/Servlet_SVR_ListServers", "");
    }

    // ServerList Server Information Accessor Functions.
    String getServerString(int nIndex) {
        return ServerNames.get(nIndex);
    }
    boolean getServerPrivate(int nIndex) {
        return ServerPrivates.get(nIndex);
    }
    int getServerPlayers(int nIndex) {
        return ServerPlayers.get(nIndex);
    }

    private boolean contains(String strServer)
    {
        return ServerNames.contains(strServer);
    }
}
