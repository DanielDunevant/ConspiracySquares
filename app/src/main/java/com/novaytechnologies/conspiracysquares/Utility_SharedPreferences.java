//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

        import android.content.Context;
        import android.content.SharedPreferences;
        import android.preference.PreferenceManager;

/**
 * The shared preferences class for saved recurring game data.
 * @author Jesse Primiani
 */
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

    /**
     * Loads the application's shared preferences.
     * @author Jesse Primiani
     * @param ctx The application context handler
     */
    void loadSharedPrefs(Context ctx)
    {sm_SH_Pref = PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());}

    /**
     * Loads the saved player's name.
     * @author Jesse Primiani
     * @param ctx The application context handler
     * @return Your player's name
     */
    String loadName(Context ctx)
    {return sm_SH_Pref.getString(conspiracysquares_SHPREF + conspiracysquares_SHPREF_NAME, ctx.getResources().getString(R.string.input_name_def));}

    /** Saves the player's name.
     * @author Jesse Primiani
     * @param strName The name to save on the device
     */
    void saveName(String strName)
    {sm_SH_Pref.edit().putString(conspiracysquares_SHPREF + conspiracysquares_SHPREF_NAME, strName).apply();}
}
