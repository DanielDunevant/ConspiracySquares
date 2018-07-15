//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

// The Game Activity.
public class Activity_Game extends AppCompatActivity {

    // Instance State for this Activity.
    static private final String STARTED = Utility_SharedPreferences.sm_strAppDomain + ".Started";

    // Intent Extras for this Activity.
    static final String SERVER = Utility_SharedPreferences.sm_strAppDomain + ".Server_Name";
    static final String SERVER_PASS = Utility_SharedPreferences.sm_strAppDomain + ".Server_Password";

    // Starts a Game Activity
    static void Start(String strName, String strPass, final Context ctx)
    {
        Intent newIntent = new Intent(ctx, Activity_Game.class);
        newIntent.putExtra(Activity_Game.SERVER, strName);
        newIntent.putExtra(Activity_Game.SERVER_PASS, strPass);
        ctx.startActivity(newIntent);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Game_Main.EndGame();
    }

    @Override
    public void onBackPressed()
    {
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
            Game_Main.JoinServer(LoadI.getStringExtra(SERVER), LoadI.getStringExtra(SERVER_PASS), this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle data)
    {
        super.onSaveInstanceState(data);
        data.putBoolean(STARTED, true);
    }
}
