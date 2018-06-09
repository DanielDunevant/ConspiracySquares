package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.content.SharedPreferences;

public class Utility_SharedPrefs
{
    static private SharedPreferences sm_SH_Pref;
    static private final String conspiracysquares_SHPREF = "com.novaytechnologies.conspiracysquares";
    static private final String conspiracysquares_SHPREF_NAME = ".name";

    private Utility_SharedPrefs() {}
    static private Utility_SharedPrefs SharedPrefsSingleton;
    static Utility_SharedPrefs get()
    {
        if (SharedPrefsSingleton == null) SharedPrefsSingleton = new Utility_SharedPrefs();
        return SharedPrefsSingleton;
    }

    // Loads the application's shared preferences.
    void loadSharedPrefs(Context ctx)
    {sm_SH_Pref = ctx.getSharedPreferences(conspiracysquares_SHPREF, Context.MODE_PRIVATE);}

    // Loads the saved player's name.
    String loadName(Context ctx)
    {return sm_SH_Pref.getString(conspiracysquares_SHPREF + conspiracysquares_SHPREF_NAME, ctx.getResources().getString(R.string.input_name_def));}

    // Saves the player's name.
    void saveName(String strName)
    {sm_SH_Pref.edit().putString(conspiracysquares_SHPREF + conspiracysquares_SHPREF_NAME, strName).apply();}
}
