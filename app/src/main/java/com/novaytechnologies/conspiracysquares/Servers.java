package com.novaytechnologies.conspiracysquares;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.HashMap;

public class Servers extends AppCompatActivity {

    static HashMap<Integer, Integer> Servers;
    static ArrayList<String> ServerNames;
    static ArrayList<Integer> ServerPlayers;
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
                ArrayList<String> params = new ArrayList<>();
                params.add("ReqPass");
                params.add("X");
                params.add("ServerName");
                params.add(input_name.getText().toString());
                String ParemsString = Post.GetParemsString(params);

                Post newPost = new Post();
                newPost.execute("https://conspiracy-squares.appspot.com/Servlet_CreateServer", ParemsString);

                ((Servers)ctx).Refresh(ctx);
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
        ServerLayoutBtns = findViewById(R.id.ID_serverlist);
        ServerLayoutBtns.removeAllViews();

        Post GetServers = new Post();
        GetServers.SetRunnable(new Post.RunnableArgs() {
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

                        ServerPlayers.add(nNumPlayers);

                        RadioButton addBtn = new RadioButton(ctx_servers);
                        String strText = strGet + "  w/ " + nNumPlayers + " Player(s) Online";
                        addBtn.setText(strText);
                        addBtn.setTextColor(getResources().getColor(R.color.colorInput));
                        ServerLayoutBtns.addView(addBtn);
                        Servers.put(addBtn.getId(), ServerNames.size()-1);
                    }
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
                    int nPlayers = ServerPlayers.get(nServerIndex);

                    //TEMP CODE BELOW
                    ArrayList<String> params = new ArrayList<>();
                    params.add("ReqPass");
                    params.add("X");
                    params.add("ServerName");
                    params.add(strServer);
                    String ParemsString = Post.GetParemsString(params);

                    Post newPost = new Post();
                    newPost.execute("https://conspiracy-squares.appspot.com/Servlet_EndServer", ParemsString);

                    Refresh(ctx_servers);
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
