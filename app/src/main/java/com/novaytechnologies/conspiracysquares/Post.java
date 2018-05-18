package com.novaytechnologies.conspiracysquares;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

class Post extends AsyncTask<String, String, String> {

    abstract static class RunnableArgs implements Runnable {
        private String[] Args;
        void SetArgs(String[] ArgArray) {Args = ArgArray;}
        String[] GetArgs() {return Args;}
    }

    private String ErrorString = "ERROR";
    private String OkayString = "OKAY";
    private RunnableArgs PostFunc = null;
    private RunnableArgs PostFuncErr = null;

    static String GetParemsString(ArrayList<String> strParems)
    {
        try {
            StringBuilder postSend = new StringBuilder();
            boolean bFirst = true;
            for (int nParem = 0; nParem < strParems.size(); ) {
                if (bFirst) bFirst = false;
                else postSend.append("&");

                postSend.append(URLEncoder.encode(strParems.get(nParem++), "UTF-8"));
                postSend.append("=");
                postSend.append(URLEncoder.encode(strParems.get(nParem++), "UTF-8"));
            }
            return postSend.toString();
        } catch (Exception e) {
            Log.e("Exception", e.getMessage(), e);
            return null;
        }
    }

    protected String doInBackground(String... data)
    {
        String strResult;

        try {
            OutputStream out;
            InputStream in;

            URL urlTest = new URL("http://connectivitycheck.gstatic.com/generate_204");
            HttpURLConnection urlConnectionTest = (HttpURLConnection) urlTest.openConnection();
            int nStatusReach = urlConnectionTest.getResponseCode();
            if (nStatusReach != 204)
            {
                Log.e("HTTP Reach Exception", "http response code is: " + nStatusReach);
                strResult = ErrorString;
            }
            else
            {
                URL url = new URL(data[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

                urlConnection.setReadTimeout(15000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                urlConnection.connect();

                out = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                writer.write(data[1]);
                writer.flush();
                writer.close();
                out.close();

                int nStatus = urlConnection.getResponseCode();
                if (nStatus != HttpsURLConnection.HTTP_OK && nStatus != HttpsURLConnection.HTTP_NO_CONTENT) {
                    Log.e("HTTP Connect Exception", "http response code is: " + nStatus);
                    strResult = ErrorString;
                } else {
                    in = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

                    String line;
                    StringBuilder postGet = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        postGet.append(line);
                    }

                    strResult = postGet.toString();
                    if (strResult.isEmpty()) strResult = OkayString;

                    in.close();
                }

                urlConnection.disconnect();
            }
        } catch (Exception e) {
            strResult = ErrorString;
            Log.e("HTTP POST Exception", e.getMessage(), e);
        }

        return strResult;
    }

    protected void onPostExecute(String result) {
        if (result != null && !result.isEmpty())
        {
            if (!result.equals(ErrorString))
            {
                if (PostFunc != null) {
                    PostFunc.SetArgs(new String[]{result});
                    PostFunc.run();
                }
            }
            else if (PostFuncErr != null)
            {
                Log.e("Post_Exception", "Post Failed: " + result);
                PostFuncErr.run();
            }
            else Log.e("Post_Exception", "Post Failed: " + result);
        }
        else if (PostFuncErr != null)
        {
            Log.e("Post_Exception", "Post Failed!");
            PostFuncErr.run();
        }
        else Log.e("Post_Exception", "Post Failed!");
    }

    void SetRunnable(RunnableArgs func) {PostFunc = func;}
    void SetRunnableError(RunnableArgs func) {PostFuncErr = func;}
}
