//Copyright (c) 2018 Novay Technologies

package com.novaytechnologies.conspiracysquares;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Server Search Activity. Used to manually find and choose a server to join.
 * @author Jesse Primiani
 */
public class Activity_Servers extends AppCompatActivity {

    // The RadioGroup containing the selectable list of Servers to join.
    RadioGroup m_ServerLayoutBtns;

    // The first Integer contains Button IDs for the RadioGroup
    // The second Integer contains Indices for the related Server Info contained in Server_ServerList.
    private HashMap<Integer, Integer> m_ServerButtonMap;

    // Variable to disable refreshing if a refresh is already in progress
    private boolean bRefreshing = false;

    @Override
    public void onBackPressed() {
        this.finish();
    }

    /**
     * Refresh the Server List and populate it with available servers.
     * @author Jesse Primiani
     */
    protected void Refresh()
    {
        bRefreshing = true;
        final Activity_Servers ctx_servers = this;

        m_ServerButtonMap = new HashMap<>();

        m_ServerLayoutBtns = findViewById(R.id.ID_serverlist);
        m_ServerLayoutBtns.clearCheck();
        m_ServerLayoutBtns.clearFocus();
        m_ServerLayoutBtns.removeAllViews();

        LinearLayout playerCounts = findViewById(R.id.ID_serverlist_players);
        playerCounts.removeAllViews();

        ArrayList<String> params = new ArrayList<>();
        params.add("ReqPass");
        params.add(Server_Sync.ResolveEncryption());
        String ParamsString = Utility_Post.GetParamsString(params);

        Utility_Post GetServers = new Utility_Post();
        GetServers.SetConnectionCheck();
        GetServers.SetRunnableError(new Utility_Post.RunnableArgs() {
            @Override
            public void run() {
                Dialog_Popup.Connect_Error(ctx_servers);
                bRefreshing = false;
            }
        });
        GetServers.SetRunnable(new Utility_Post.RunnableArgs() {
            @Override
            public void run() {
                String LastResult = GetArgs()[0];
                ArrayList<String> ServerPlayersStringArray;

                // Parse information about all returned servers and store Player Count Information
                ServerPlayersStringArray = Server_ServerList.get().populateList(LastResult);

                // Loop through all returned servers and get each server's player count.
                int nIndex = 0;
                LinearLayout playerNums = findViewById(R.id.ID_serverlist_players);
                for (String strPlayerCount : ServerPlayersStringArray)
                {
                    // Add a button for joining the current server to the server list
                    RadioButton addBtn = new RadioButton(ctx_servers);
                    addBtn.setText(Server_ServerList.get().getServerString(nIndex));
                    addBtn.setTextColor(getResources().getColor(R.color.colorInput));
                    RadioGroup.LayoutParams LparamsRadio = new RadioGroup.LayoutParams(
                            RadioGroup.LayoutParams.WRAP_CONTENT,
                            (int)getResources().getDimension(R.dimen.list_height));
                    addBtn.setLayoutParams(LparamsRadio);
                    m_ServerLayoutBtns.addView(addBtn);

                    // Add the button id and server information index to the global button map
                    m_ServerButtonMap.put(addBtn.getId(), nIndex);

                    // Create a new LinearLayout for the text and optional lock icon
                    LinearLayout TextLayout = new LinearLayout(ctx_servers);
                    TextLayout.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams LparamsLinear = new RadioGroup.LayoutParams(
                            RadioGroup.LayoutParams.WRAP_CONTENT,
                            (int)getResources().getDimension(R.dimen.list_height));
                    TextLayout.setLayoutParams(LparamsLinear);
                    playerNums.addView(TextLayout);

                    // Create the text containing the player count
                    TextView txtPlr = new TextView(ctx_servers);
                    txtPlr.setText(strPlayerCount);
                    txtPlr.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.button_text));
                    txtPlr.setTextColor(getResources().getColor(R.color.colorInput));
                    txtPlr.setGravity(Gravity.CENTER_VERTICAL);
                    txtPlr.setLayoutParams(LparamsLinear);

                    // Add the lock icon if the server is private
                    if (Server_ServerList.get().getServerPrivate(nIndex))
                    {
                        ImageView imgLock = new ImageView(ctx_servers);
                        imgLock.setImageResource(R.drawable.vec_lock);
                        imgLock.setScaleX(txtPlr.getScaleX());
                        imgLock.setScaleY(txtPlr.getScaleY());
                        imgLock.setLayoutParams(LparamsLinear);
                        TextLayout.addView(imgLock);
                    }

                    // Add the player count text to the player num layout in the server list
                    TextLayout.addView(txtPlr);

                    nIndex++;
                }
                bRefreshing = false;
                playerNums.invalidate(); // Refresh the server list layout
            }
        });
        GetServers.execute("https://conspiracy-squares.appspot.com/Servlet_SVR_ListServers", ParamsString);
    }

    /**
     * Ends the game just in case it is still running for some unexpected reason.
     * @author Jesse Primiani
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        Game_Main.EndGame();
    }

    /**
     * Creates and manages the server list.
     * @author Jesse Primiani
     * @param savedInstanceState Used to restore the instance state on app restart or phone rotation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers);

        final Activity_Servers ctx_servers = this;

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        LinearLayout playerNums = findViewById(R.id.ID_serverlist_players);
        playerNums.setScrollContainer(false);

        Refresh();

        Button btn_join = findViewById(R.id.ID_btn_join);
        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = m_ServerLayoutBtns.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    int nServerIndex = m_ServerButtonMap.get(selectedId);

                    String strServer = Server_ServerList.get().getServerString(nServerIndex);
                    int nPlayers = Server_ServerList.get().getServerPlayers(nServerIndex);
                    boolean bPrivate = Server_ServerList.get().getServerPrivate(nServerIndex);

                    if (nPlayers < Utility_SharedPreferences.MAX_PLAYERS)
                    {
                        if (bPrivate)
                            Dialog_Server.Show_Dialog_Join(ctx_servers, strServer);
                        else
                            Activity_Game.Start(strServer, "", ctx_servers);
                    }
                    else Dialog_Popup.Server_Full(ctx_servers);
                }
            }
        });

        Button btn_host = findViewById(R.id.ID_btn_create);
        btn_host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_Server.Show_Dialog_Host(ctx_servers);
            }
        });

        Button btn_refresh = findViewById(R.id.ID_btn_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bRefreshing) Refresh();
            }
        });
    }
}
