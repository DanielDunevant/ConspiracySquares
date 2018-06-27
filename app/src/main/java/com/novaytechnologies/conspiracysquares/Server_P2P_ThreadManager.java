package com.novaytechnologies.conspiracysquares;

import android.util.Log;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

// Manages the single Server and multiple Client P2P-logic threads
public class Server_P2P_ThreadManager
{
    static String strSecretKey = "NVT-CS-K002fn658vmx04j58gj3h9"; // Used for weak client validation
    static int nPORT = 8080;

    static ArrayList<String> sm_PlayerIPs;
    static HashMap<String, Thread> sm_Player_Threads;
    static private Thread serverThread;

    // Create the Server Listener Thread
    static void SpawnServerThread()
    {
        serverThread = new Thread(new Server_P2P_Share());
        serverThread.start();
    }

    // Create all down or uncreated Self Info Sending Threads
    static void SpawnPlayerThreads()
    {
        for (String strIP : sm_PlayerIPs)
        {
            if (!strIP.equals(Game_Main.sm_strIP))
            {
                if (!sm_Player_Threads.containsKey(strIP) || sm_Player_Threads.get(strIP) == null)
                {
                    Server_P2P_Send newSender = new Server_P2P_Send();
                    newSender.setIP(strIP);
                    Thread newThread = new Thread(newSender);
                    newThread.start();
                    sm_Player_Threads.put(strIP, newThread);
                }
                else
                {
                    Thread getThread = sm_Player_Threads.get(strIP);
                    if (getThread.isInterrupted() || !getThread.isAlive())
                    {
                        getThread.start();
                    }
                }
            }
        }
    }

    // Stops All Server and Client Threads created above
    static void StopAllThreads()
    {
        for (String strIP : sm_PlayerIPs)
        {
            if (sm_Player_Threads.containsKey(strIP))
            {
                Thread getThread = sm_Player_Threads.get(strIP);
                if (getThread != null)
                {
                    getThread.interrupt();
                }
            }
            if (serverThread != null)
            {
                try {
                    serverThread.join(100);
                    serverThread.interrupt();
                    Server_P2P_Share.closeServerSocket();
                    serverThread = null;
                }
                catch (Exception ex) {Log.e("P2P_Exception", "P2P Could Not Close Server Thread", ex);}
            }
        }
        sm_Player_Threads.clear();
    }
}
