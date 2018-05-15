package com.novaytechnologies.conspiracysquares;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Game extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }
}
