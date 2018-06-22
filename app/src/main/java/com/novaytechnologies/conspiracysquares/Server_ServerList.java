package com.novaytechnologies.conspiracysquares;

import java.util.ArrayList;

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

    boolean contains(String strServer)
    {
        return ServerNames.contains(strServer);
    }
}
