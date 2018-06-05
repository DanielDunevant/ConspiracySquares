//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Layout_Game extends AppCompatActivity {

    private void FindServer()
    {
        //
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

        Intent LoadI = getIntent();
        boolean bFindServer = LoadI.getBooleanExtra(Layout_Main.FIND_SERVER, true);

        if (bFindServer)
            FindServer();
        else
            Game_Main.sm_strServerName = LoadI.getStringExtra(Layout_Main.SERVER);

        Game_Main.StartGame(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Game_Main.EndGame();
    }
}
