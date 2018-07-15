package com.novaytechnologies.conspiracysquares;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

// Acts as the Client for all outgoing P2P connections
public class Server_P2P_Send implements Runnable
{
    private String m_strRequestIP;
    private int m_nRequestPort;

    public void setPort(int nPort) {m_nRequestPort = nPort;}
    public void setIP(String strIP) {m_strRequestIP = strIP;}
    public void run()
    {
        try
        {
            InetAddress serverAddress = InetAddress.getByName(m_strRequestIP);
            Game_Player Self = Game_Player.GetSelf();
            while (Game_Main.isStarted() && !Thread.currentThread().isInterrupted())
            {
                try
                {
                    Socket socket = new Socket(serverAddress, m_nRequestPort);
                    PrintWriter WriteRequest = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                    // Send Self Player Data to the other Player specified with SetIP()
                    WriteRequest.println(Server_P2P_ThreadManager.strSecretKey +
                    "+" +
                    Integer.toString(Self.GetID()) +
                    "+" +
                    Integer.toString(Self.GetFlags()) +
                    "+" +
                    Self.GetName() +
                    "+" +
                    Self.GetColor() +
                    "+" +
                    Float.toString(Self.GetX()) +
                    "+" +
                    Float.toString(Self.GetY()) +
                    "+" +
                    Self.GetSpeedX() +
                    "+" +
                    Float.toString(Self.GetSpeedY()) +
                    "+");

                    // Other Relevant Updates
                    //TBD

                    WriteRequest.close();
                    WriteRequest = null;
                    socket.close();
                    socket = null;

                    Game_Main.SentSelfInfo();
                }
                catch (Exception ex)
                {
                    Log.e("P2P_Exception", "P2P Client Message Failed", ex);
                }
            }
        }
        catch (Exception ex)
        {
            Log.e("P2P_Exception", "P2P Client Connection Failed", ex);
            Server_P2P_ThreadManager.sm_Player_Threads.remove(m_strRequestIP);
        }
    }
}
