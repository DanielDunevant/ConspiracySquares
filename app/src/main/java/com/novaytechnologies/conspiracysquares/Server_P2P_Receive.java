package com.novaytechnologies.conspiracysquares;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

// Acts as the Server for all incoming P2P connections
public class Server_P2P_Receive implements Runnable
{
    private static ServerSocket serverSocket;
    static void closeServerSocket()
    {
        try
        {
            if (serverSocket != null)
            {
                serverSocket.close();
                serverSocket = null;
            }
        }
        catch(Exception ex) {Log.e("P2P_Exception", "Socket Closing Exception!", ex);}
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;

        ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try
            {
                BufferedReader ReadRequest = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter WriteRequest = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true);

                String strRead;
                String strGet;
                while (Game_Main.isStarted() && (strRead = ReadRequest.readLine()) != null)
                {
                    try
                    {
                        // Check for Invalid Secret Key
                        int nIndex = strRead.indexOf('+', 0);
                        strGet = strRead.substring(0, nIndex);
                        if (!strGet.equals(Server_P2P_ThreadManager.strSecretKey)) {
                            throw new Exception("Invalid Secret Key!");
                        }
                        strRead = strRead.substring(nIndex + 1);

                        // Get Player ID
                        nIndex = strRead.indexOf('+', 0);
                        strGet = strRead.substring(0, nIndex);
                        int nPlayerID = Integer.parseInt(strGet);
                        Game_Player editPlayer = Game_Main.sm_PlayersArray.get(nPlayerID);
                        strRead = strRead.substring(nIndex + 1);

                        // Set Player Flags
                        nIndex = strRead.indexOf('+', 0);
                        strGet = strRead.substring(0, nIndex);
                        int nFlags = Integer.parseInt(strGet);
                        editPlayer.UpdateF(nFlags);
                        strRead = strRead.substring(nIndex + 1);

                        // Set Player Name
                        nIndex = strRead.indexOf('+', 0);
                        strGet = strRead.substring(0, nIndex);
                        editPlayer.UpdateName(strGet);
                        strRead = strRead.substring(nIndex + 1);

                        // Set Player Color if Different
                        nIndex = strRead.indexOf('+', 0);
                        strGet = strRead.substring(0, nIndex);
                        int nColor = Integer.parseInt(strGet);
                        if (nColor != editPlayer.GetColor())
                            editPlayer.UpdateColor(nColor);
                        strRead = strRead.substring(nIndex + 1);

                        // Set Player X
                        nIndex = strRead.indexOf('+', 0);
                        strGet = strRead.substring(0, nIndex);
                        float fX = Float.parseFloat(strGet);
                        editPlayer.UpdateX(fX);
                        strRead = strRead.substring(nIndex + 1);

                        // Set Player Y
                        nIndex = strRead.indexOf('+', 0);
                        strGet = strRead.substring(0, nIndex);
                        float fY = Float.parseFloat(strGet);
                        editPlayer.UpdateY(fY);
                        strRead = strRead.substring(nIndex + 1);

                        // Set Player X Speed
                        nIndex = strRead.indexOf('+', 0);
                        strGet = strRead.substring(0, nIndex);
                        float fXs = Float.parseFloat(strGet);
                        editPlayer.UpdateSpdX(fXs);
                        strRead = strRead.substring(nIndex + 1);

                        // Set Player Y Speed
                        nIndex = strRead.indexOf('+', 0);
                        strGet = strRead.substring(0, nIndex);
                        float fYs = Float.parseFloat(strGet);
                        editPlayer.UpdateSpdY(fYs);
                        strRead = strRead.substring(nIndex + 1);

                        // Other Relevant Updates
                        //TBD

                        WriteRequest.println("Updated");
                        WriteRequest.flush();

                        Game_Main.GotPlayerInfo(nPlayerID);
                    } catch (Exception ex) {
                        Log.e("P2P_Exception", "P2P Server Message Processing Failed", ex);
                    }
                }

                ReadRequest.close();
                clientSocket.close();
            } catch (Exception ex) {
                Log.e("P2P_Exception", "P2P Server Connection Failed", ex);
            }
        }
    }

    public void run()
    {
        try
        {
            serverSocket = new ServerSocket(0);
            Game_Main.sm_nPort = serverSocket.getLocalPort();
            Server_Sync.UpdatePort();

            while (Game_Main.isStarted() && !Thread.currentThread().isInterrupted())
                new ClientHandler(serverSocket.accept()).start();

            closeServerSocket();
        }
        catch(SocketException ex) {Log.d("Socket", "Socket Closed");}
        catch (Exception ex)
        {
            Log.e("P2P_Exception", "P2P Server Connection Failed", ex);
        }
    }
}