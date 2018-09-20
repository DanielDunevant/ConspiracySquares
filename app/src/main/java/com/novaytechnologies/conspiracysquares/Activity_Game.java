//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * The Game Activity. Used for starting up and containing the Game.
 * @author Jesse Primiani
 */
public class Activity_Game extends AppCompatActivity {

    // Instance State for this Activity.
    static private final String STARTED = Utility_SharedPreferences.sm_strAppDomain + ".Started";

    // Intent Extras for this Activity.
    static final String SERVER = Utility_SharedPreferences.sm_strAppDomain + ".Server_Name";
    static final String SERVER_PASS = Utility_SharedPreferences.sm_strAppDomain + ".Server_Password";

    /**
     * Starts a Game Activity.
     * @author Jesse Primiani
     * @param strName The name of the server to join
     * @param strPass The password for the joined server
     * @param ctx The application context handle
     */
    static void Start(String strName, String strPass, final Context ctx)
    {
        Intent newIntent = new Intent(ctx, Activity_Game.class);
        newIntent.putExtra(Activity_Game.SERVER, strName);
        newIntent.putExtra(Activity_Game.SERVER_PASS, strPass);
        ctx.startActivity(newIntent);
    }

    /**
     * Exits the game server whenever the phone's back button is pressed.
     * @author Jesse Primiani
     */
    @Override
    public void onBackPressed()
    {
        Game_Main.EndGame();
        this.finish();
    }

    /**
     * Exits the game server whenever the app is closed properly.
     * @author Jesse Primiani
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) Game_Main.EndGame();
    }

    /**
     * Creates the game container and joins the given server.
     * @author Jesse Primiani
     * @param savedInstanceState Used to restore the instance state on app restart or phone rotation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (savedInstanceState == null || !savedInstanceState.getBoolean(STARTED))
        {
            Intent LoadI = getIntent();
            Game_Main.JoinServer(LoadI.getStringExtra(SERVER), LoadI.getStringExtra(SERVER_PASS), this);
        }
    }

    /**
     * Saves whether the game container was created and the server joined.
     * @author Jesse Primiani
     * @param data Used to restore the instance state on app restart or phone rotation
     */
    @Override
    protected void onSaveInstanceState(Bundle data)
    {
        super.onSaveInstanceState(data);
        data.putBoolean(STARTED, true);
    }
}
