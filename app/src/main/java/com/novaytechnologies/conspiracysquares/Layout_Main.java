//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Layout_Main extends AppCompatActivity {

    static String sm_strName;

    static SharedPreferences sm_SH_Pref;
    static final String conspiracysquares_SHPREF = "com.novaytechnologies.conspiracysquares";
    static final String conspiracysquares_SHPREF_NAME = ".name";

    static final String FIND_SERVER = "com.novaytechnologies.conspiracysquares.Find_Server";
    static final String SERVER = "com.novaytechnologies.conspiracysquares.Server_Name";

    @Override
    protected void onResume()
    {
        super.onResume();
        Game_Main.EndGame();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sm_SH_Pref = getSharedPreferences(conspiracysquares_SHPREF, Context.MODE_PRIVATE);
        sm_strName = sm_SH_Pref.getString(conspiracysquares_SHPREF + conspiracysquares_SHPREF_NAME, this.getResources().getString(R.string.input_name_def));

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        final TextView txt_error = findViewById(R.id.ID_txt_error);
        final EditText input_name = findViewById(R.id.ID_input_name);

        input_name.setText(sm_strName);
        input_name.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 1) sm_strName = charSequence.toString();
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
                            sm_strName = strGet;
                            sm_SH_Pref.edit().putString(conspiracysquares_SHPREF + conspiracysquares_SHPREF_NAME, sm_strName).apply();
                            txt_error.setVisibility(View.GONE);

                            Intent newIntent = new Intent(Layout_Main.this, Layout_Game.class);
                            newIntent.putExtra(FIND_SERVER, true);
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
                            sm_strName = strGet;
                            sm_SH_Pref.edit().putString(conspiracysquares_SHPREF + conspiracysquares_SHPREF_NAME, sm_strName).apply();
                            txt_error.setVisibility(View.GONE);

                            Intent newIntent = new Intent(Layout_Main.this, Layout_Servers.class);
                            startActivity(newIntent);
                        }
                        else txt_error.setVisibility(View.VISIBLE);
                    }
                }
        );
    }
}
