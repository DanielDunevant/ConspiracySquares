//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// The Initial Activity.
public class Activity_Main extends AppCompatActivity {

    // Ends the game if the game is still running.
    @Override
    protected void onResume()
    {
        super.onResume();
        Game_Main.EndGame();
    }
    @Override
    protected void onDestroy()
    {
        Game_Main.EndGame();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utility_SharedPrefs.get().loadSharedPrefs(this);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        final TextView txt_error = findViewById(R.id.ID_txt_error);
        final EditText input_name = findViewById(R.id.ID_input_name);

        input_name.setText(Utility_SharedPrefs.get().loadName(this));
        input_name.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 1) Utility_SharedPrefs.get().saveName(charSequence.toString());
            }
            @Override public void afterTextChanged(Editable editable) {}
        });

        final Button btn_play = findViewById(R.id.ID_btn_play);
        btn_play.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        String strGet = input_name.getText().toString();
                        if (!strGet.isEmpty()) {
                            Utility_SharedPrefs.get().saveName(strGet);
                            txt_error.setVisibility(View.GONE);

                            Intent newIntent = new Intent(Activity_Main.this, Activity_Game.class);
                            newIntent.putExtra(Activity_Game.FIND_SERVER, true);
                            startActivity(newIntent);
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
                            Utility_SharedPrefs.get().saveName(strGet);
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
