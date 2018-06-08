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

public class Layout_Servers extends AppCompatActivity {

    static final public int MAX_PLAYERS = 32;

    static HashMap<Integer, Integer> Servers;
    static ArrayList<String> ServerNames;
    static ArrayList<String> ServerPlayers;
    static ArrayList<Integer> ServerPlayersInt;
    RadioGroup ServerLayoutBtns;

    String strChosenServer;

    @Override
    public void onBackPressed() {
        this.finish();
    }

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
                //TEMP CODE BELOW
                String strSVRname = input_name.getText().toString();
                if (!strSVRname.isEmpty())
                {
                    ArrayList<String> params = new ArrayList<>();
                    params.add("ReqPass");
                    params.add("X");
                    params.add("ServerName");
                    params.add(strSVRname);
                    String ParemsString = Utility_Post.GetParemsString(params);

                    Utility_Post newPost = new Utility_Post();
                    newPost.execute("https://conspiracy-squares.appspot.com/Servlet_CreateServer", ParemsString);

                    ((Layout_Servers) ctx).Refresh(ctx);
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

    protected void Refresh(final Context ctx_servers)
    {
        strChosenServer = "";
        Servers = new HashMap<>();
        ServerNames = new ArrayList<>();
        ServerPlayers = new ArrayList<>();
        ServerPlayersInt = new ArrayList<>();
        ServerLayoutBtns = findViewById(R.id.ID_serverlist);
        ServerLayoutBtns.removeAllViews();
        LinearLayout playerN = findViewById(R.id.ID_serverlist_players);
        playerN.removeAllViews();

        Utility_Post GetServers = new Utility_Post();
        GetServers.SetRunnable(new Utility_Post.RunnableArgs() {
            @Override
            public void run() {
                String LastResult = GetArgs()[0];
                if (LastResult != null && LastResult.indexOf(';', 0) != -1)
                {
                    String strGet;
                    int nNumPlayers;
                    boolean bNext = true;
                    if (!LastResult.contains("+")) bNext = false;
                    while (bNext)
                    {
                        strGet = LastResult.substring(1, LastResult.indexOf('&', 1));
                        ServerNames.add(strGet);

                        int nLastIndex = LastResult.indexOf('+', 1);
                        if (nLastIndex < 0)
                        {
                            bNext = false;
                            nNumPlayers = Integer.parseInt(LastResult.substring(LastResult.indexOf('&', 1) + 1, LastResult.indexOf(';', 1)));
                        }
                        else
                        {
                            nNumPlayers = Integer.parseInt(LastResult.substring(LastResult.indexOf('&', 1) + 1, nLastIndex));
                            LastResult = LastResult.substring(nLastIndex);
                        }

                        RadioButton addBtn = new RadioButton(ctx_servers);
                        addBtn.setText(strGet);
                        addBtn.setTextColor(getResources().getColor(R.color.colorInput));
                        RadioGroup.LayoutParams Lparams = new RadioGroup.LayoutParams(
                                RadioGroup.LayoutParams.WRAP_CONTENT,
                                (int)getResources().getDimension(R.dimen.list_height));
                        addBtn.setLayoutParams(Lparams);
                        ServerLayoutBtns.addView(addBtn);
                        Servers.put(addBtn.getId(), ServerNames.size()-1);

                        String strPlyr = Integer.toString(nNumPlayers) + " / " + Integer.toString(MAX_PLAYERS);
                        ServerPlayers.add(strPlyr);
                        ServerPlayersInt.add(nNumPlayers);
                    }

                    LinearLayout playerNums = findViewById(R.id.ID_serverlist_players);
                    for (String strPlyrCnt : ServerPlayers)
                    {
                        TextView txtPlr = new TextView(ctx_servers);
                        txtPlr.setText(strPlyrCnt);
                        txtPlr.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.button_text));
                        txtPlr.setTextColor(getResources().getColor(R.color.colorInput));
                        LinearLayout.LayoutParams Lparams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                (int)getResources().getDimension(R.dimen.list_height));
                        txtPlr.setLayoutParams(Lparams);
                        txtPlr.setGravity(Gravity.CENTER_VERTICAL);
                        playerNums.addView(txtPlr);
                    }
                    playerNums.invalidate();
                }
            }
        });
        GetServers.execute("https://conspiracy-squares.appspot.com/Servlet_ListServers", "");
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
                    int nServerIndex = Servers.get(selectedId);
                    String strServer = ServerNames.get(nServerIndex);
                    int nPlayers = ServerPlayersInt.get(nServerIndex);

                    if (nPlayers < MAX_PLAYERS)
                    {
                        Intent newIntent = new Intent(Layout_Servers.this, Layout_Game.class);
                        newIntent.putExtra(Layout_Main.FIND_SERVER, false);
                        newIntent.putExtra(Layout_Main.SERVER, strServer);
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
