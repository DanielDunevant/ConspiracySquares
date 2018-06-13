package com.novaytechnologies.conspiracysquares;

import java.util.ArrayList;

public class Utility_ServerList
{
    static final public int MAX_PLAYERS = 32;

    private ArrayList<String> ServerNames;
    private ArrayList<Integer> ServerPlayers;

    private Utility_ServerList() {}
    static private Utility_ServerList ServerListSingleton;
    static Utility_ServerList get()
    {
        if (ServerListSingleton == null) ServerListSingleton = new Utility_ServerList();
        return ServerListSingleton;
    }

    ArrayList<String> populateList(String strParseString)
    {
        ServerNames = new ArrayList<>();
        ServerPlayers = new ArrayList<>();

        if (strParseString != null && strParseString.indexOf(';', 0) != -1)
        {
            ArrayList<String> ServerPlayersString = new ArrayList<>();
            String strGet;
            String strNumPlayers;
            boolean bNext = true;
            if (!strParseString.contains("+")) bNext = false;
            while (bNext)
            {
                strGet = strParseString.substring(1, strParseString.indexOf('&', 1));

                int nLastIndex = strParseString.indexOf('+', 1);
                if (nLastIndex < 0)
                {
                    bNext = false;
                    strNumPlayers = strParseString.substring(strParseString.indexOf('&', 1) + 1, strParseString.indexOf(';', 1));
                }
                else
                {
                    strNumPlayers = strParseString.substring(strParseString.indexOf('&', 1) + 1, nLastIndex);
                    strParseString = strParseString.substring(nLastIndex);
                }

                ServerNames.add(strGet);
                ServerPlayers.add(Integer.parseInt(strNumPlayers));

                String strPlyr = strNumPlayers + " / " + Integer.toString(Utility_ServerList.MAX_PLAYERS);
                ServerPlayersString.add(strPlyr);
            }

            return ServerPlayersString;
        }
        else return null;
    }

    String getServerString(int nIndex) {
        return ServerNames.get(nIndex);
    }

    int getPlayers(int nIndex) {
        return ServerPlayers.get(nIndex);
    }

    boolean contains(String strServer)
    {
        return ServerNames.contains(strServer);
    }
}
