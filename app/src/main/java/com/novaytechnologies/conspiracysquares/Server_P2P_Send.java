package com.novaytechnologies.conspiracysquares;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

// Acts as the Client for all outgoing P2P connections
public class Server_P2P_Send implements Runnable
{
    private String m_strRequestIP;

    public void setIP(String strIP) {m_strRequestIP = strIP;}
    public void run()
    {
        try
        {
            InetAddress serverAddress = InetAddress.getByName(m_strRequestIP);
            Socket socket = new Socket(serverAddress, Server_P2P_ThreadManager.nPORT);
            while (Game_Main.isStarted())
            {
                try
                {
                    PrintWriter WriteRequest = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    WriteRequest.print(Server_P2P_ThreadManager.strSecretKey);

                    Game_Player Self = Game_Player.GetSelf();

                    // Send Self Player Data to the other Player specified with SetIP()
                    WriteRequest.print("+");
                    WriteRequest.print(Integer.toString(Self.GetID()));
                    WriteRequest.print("+");
                    WriteRequest.print(Integer.toString(Self.GetFlags()));
                    WriteRequest.print("+");
                    WriteRequest.print(Self.GetName());
                    WriteRequest.print("+");
                    WriteRequest.print(Self.GetColor());
                    WriteRequest.print("+");
                    WriteRequest.print(Float.toString(Self.GetX()));
                    WriteRequest.print("+");
                    WriteRequest.print(Float.toString(Self.GetY()));
                    WriteRequest.print("+");
                    WriteRequest.print(Float.toString(Self.GetSpeedX()));
                    WriteRequest.print("+");
                    WriteRequest.print(Float.toString(Self.GetSpeedY()));

                    // Other Relevant Updates
                    //TBD
                }
                catch (Exception ex)
                {
                    Log.e("P2P_Exception", "P2P Client Message Failed", ex);
                }
            }
            socket.close();
        }
        catch (Exception ex)
        {
            Log.e("P2P_Exception", "P2P Client Connection Failed", ex);
            Server_P2P_ThreadManager.sm_Player_Threads.remove(m_strRequestIP);
        }
    }
}
