//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class Utility_SharedPreferences
{
    static final public int MAX_PLAYERS = 32;
    static final String sm_strAppDomain = "com.novaytechnologies.conspiracysquares";

    static private SharedPreferences sm_SH_Pref;
    static private final String conspiracysquares_SHPREF = sm_strAppDomain + ".shpref";
    static private final String conspiracysquares_SHPREF_NAME = ".name";

    // Singleton Code
    private Utility_SharedPreferences() {}
    static private Utility_SharedPreferences SharedPrefsSingleton;
    static Utility_SharedPreferences get()
    {
        if (SharedPrefsSingleton == null) SharedPrefsSingleton = new Utility_SharedPreferences();
        return SharedPrefsSingleton;
    }

    // Loads the application's shared preferences.
    void loadSharedPrefs(Context ctx)
    {sm_SH_Pref = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());}

    // Loads the saved player's name.
    String loadName(Context ctx)
    {return sm_SH_Pref.getString(conspiracysquares_SHPREF + conspiracysquares_SHPREF_NAME, ctx.getResources().getString(R.string.input_name_def));}

    // Saves the player's name.
    void saveName(String strName)
    {sm_SH_Pref.edit().putString(conspiracysquares_SHPREF + conspiracysquares_SHPREF_NAME, strName).apply();}
}
