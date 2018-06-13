//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

// The Server Search Activity.
public class Activity_Servers extends AppCompatActivity {

    private HashMap<Integer, Integer> ServerButtonMap;
    RadioGroup ServerLayoutBtns;

    @Override
    public void onBackPressed() {
        this.finish();
    }

    // Create a dialog used to name, create, then join a new server hosted in Google's Appengine
    protected static void Show_Dialog_Host(final Context ctx) {
        final Dialog dialog_basic;
        dialog_basic = new Dialog(ctx);
        dialog_basic.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_basic.setContentView(R.layout.dialog_host);

        Button dialog_btn_back = dialog_basic.findViewById(R.id.ID_dialog_button_back);
        dialog_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_basic.dismiss();
            }
        });

        final EditText input_name = dialog_basic.findViewById(R.id.server_name_input);
        Button dialog_btn_host = dialog_basic.findViewById(R.id.ID_dialog_button_host);
        dialog_btn_host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strSVRname = input_name.getText().toString();
                if (!strSVRname.isEmpty())
                {
                    ArrayList<String> params = new ArrayList<>();
                    params.add("ReqPass");
                    params.add("X");
                    params.add("ServerName");
                    params.add(strSVRname);
                    String ParemsString = Utility_Post.GetParemsString(params);

                    Utility_Post newPost = new Utility_Post();
                    newPost.SetRunnable(new Utility_Post.RunnableArgs() {
                        @Override
                        public void run() {
                            Intent newIntent = new Intent(ctx, Activity_Game.class);
                            newIntent.putExtra(Activity_Game.FIND_SERVER, false);
                            newIntent.putExtra(Activity_Game.SERVER, strSVRname);
                            ctx.startActivity(newIntent);
                        }
                    });
                    newPost.execute("https://conspiracy-squares.appspot.com/Servlet_CreateServer", ParemsString);

                    ((Activity_Servers) ctx).Refresh(ctx);
                }
                dialog_basic.dismiss();
            }
        });

        dialog_basic.setOnDismissListener(new Dialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface v) { }
        });
        dialog_basic.show();
        if (dialog_basic.getWindow() != null)
            dialog_basic.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    // Refresh the serverlist view and populate it with available servers
    protected void Refresh(final Context ctx_servers)
    {
        ServerButtonMap = new HashMap<>();

        ServerLayoutBtns = findViewById(R.id.ID_serverlist);
        ServerLayoutBtns.removeAllViews();

        LinearLayout playerN = findViewById(R.id.ID_serverlist_players);
        playerN.removeAllViews();

        Utility_Post GetServers = new Utility_Post();
        GetServers.SetRunnable(new Utility_Post.RunnableArgs() {
            @Override
            public void run() {
                String LastResult = GetArgs()[0];
                ArrayList<String> ServerPlayersString;
                ServerPlayersString = Utility_ServerList.get().populateList(LastResult);

                int nIndex = 0;
                LinearLayout playerNums = findViewById(R.id.ID_serverlist_players);
                for (String strPlyrCnt : ServerPlayersString)
                {
                    RadioButton addBtn = new RadioButton(ctx_servers);
                    addBtn.setText(Utility_ServerList.get().getServerString(nIndex));
                    addBtn.setTextColor(getResources().getColor(R.color.colorInput));
                    RadioGroup.LayoutParams Lparams = new RadioGroup.LayoutParams(
                            RadioGroup.LayoutParams.WRAP_CONTENT,
                            (int)getResources().getDimension(R.dimen.list_height));
                    addBtn.setLayoutParams(Lparams);
                    ServerLayoutBtns.addView(addBtn);

                    ServerButtonMap.put(addBtn.getId(), nIndex);

                    TextView txtPlr = new TextView(ctx_servers);
                    txtPlr.setText(strPlyrCnt);
                    txtPlr.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.button_text));
                    txtPlr.setTextColor(getResources().getColor(R.color.colorInput));
                    LinearLayout.LayoutParams Lparams2 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            (int)getResources().getDimension(R.dimen.list_height));
                    txtPlr.setLayoutParams(Lparams2);
                    txtPlr.setGravity(Gravity.CENTER_VERTICAL);
                    playerNums.addView(txtPlr);

                    nIndex++;
                }
                playerNums.invalidate();
            }
        });
        GetServers.execute("https://conspiracy-squares.appspot.com/Servlet_ListServers", "");
    }

    // Ends the game if the game is still running.
    @Override
    protected void onResume()
    {
        super.onResume();
        Game_Main.EndGame();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        LinearLayout playerNums = findViewById(R.id.ID_serverlist_players);
        playerNums.setScrollContainer(false);

        final Context ctx_servers = this;
        Refresh(ctx_servers);

        Button btn_join = findViewById(R.id.ID_btn_join);
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = ServerLayoutBtns.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    int nServerIndex = ServerButtonMap.get(selectedId);
                    String strServer = Utility_ServerList.get().getServerString(nServerIndex);
                    int nPlayers = Utility_ServerList.get().getPlayers(nServerIndex);

                    if (nPlayers < Utility_ServerList.MAX_PLAYERS)
                    {
                        Intent newIntent = new Intent(Activity_Servers.this, Activity_Game.class);
                        newIntent.putExtra(Activity_Game.FIND_SERVER, false);
                        newIntent.putExtra(Activity_Game.SERVER, strServer);
                        startActivity(newIntent);
                    }
                }
            }
        });

        Button btn_host = findViewById(R.id.ID_btn_create);
        btn_host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Show_Dialog_Host(ctx_servers);
            }
        });

        Button btn_refresh = findViewById(R.id.ID_btn_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Refresh(ctx_servers);
            }
        });
    }
}
