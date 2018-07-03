//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

// The Initial Main Menu Activity.
public class Activity_Main extends AppCompatActivity {

    ProgressBar progress;

    @Override
    protected void onResume() {
        super.onResume();
        progress.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utility_SharedPreferences.get().loadSharedPrefs(this);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        final TextView txt_error = findViewById(R.id.ID_txt_error);
        final EditText input_name = findViewById(R.id.ID_input_name);
        progress = findViewById(R.id.ID_progressBar);

        final Context ctx = this;

        final Button btn_play = findViewById(R.id.ID_btn_play);
        btn_play.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        String strGet = input_name.getText().toString();
                        if (!strGet.isEmpty()) {
                            Utility_SharedPreferences.get().saveName(strGet);
                            txt_error.setVisibility(View.GONE);

                            progress.setVisibility(View.VISIBLE);
                            Server_ServerList.get().FindOrCreateServer(ctx);
                        }
                        else txt_error.setVisibility(View.VISIBLE);
                    }
                }
        );

        final Button btn_server = findViewById(R.id.ID_btn_server);
        btn_server.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        String strGet = input_name.getText().toString();
                        if (!strGet.isEmpty()) {
                            Utility_SharedPreferences.get().saveName(strGet);
                            txt_error.setVisibility(View.GONE);

                            Intent newIntent = new Intent(Activity_Main.this, Activity_Servers.class);
                            startActivity(newIntent);
                        }
                        else txt_error.setVisibility(View.VISIBLE);
                    }
                }
        );
    }
}
