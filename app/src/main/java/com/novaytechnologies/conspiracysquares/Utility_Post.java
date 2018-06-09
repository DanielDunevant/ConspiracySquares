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


// Manages tasks related to sending information to a webserver via POST.
class Utility_Post extends AsyncTask<String, String, String> {

    // A subclass that allows arguments to be sent to the POST result function.
    abstract static class RunnableArgs implements Runnable {
        private String[] Args;
        void SetArgs(String[] ArgArray) {Args = ArgArray;}
        String[] GetArgs() {return Args;}
    }

    private String ErrorString = "ERROR";
    private String OkayString = "OKAY";
    private RunnableArgs PostFunc = null;
    private RunnableArgs PostFuncErr = null;

    /*
        DESCRIPTION:
            Turns an ArrayList of key-value string pairs into a parameter string that can be sent
            to a webserver via the POST method.
        PRE-CONDITION:
            strParems should be an even length array of strings, with every even-indexed string
            set to the parameter name, and every string inserted next set to the previous parameter's value.
        POST-CONDITION:
            A string formatted such that a webserver could easily parse it via POST.
    */
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

    /*
        DESCRIPTION:
            Contacts the given webserver, sends it a parameter string, then has a function process the result.
        PRE-CONDITION:
            data[0] should contain the web address of the server to contact via POST.
            data[1] should contain a properly formatted string of parameters.
            Both are set when the POST is sent via Utility_Post_OBJECT.execute(data[0], data[1]);
        POST-CONDITION:
            The webserver is contacted with the given parameters via POST.
            If the transaction succeeds, then the function given in SetRunnable() will be run
            to process the result. Otherwise, function given in SetRunnableError() will be run.
    */
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

    // Chooses which result function to run based on the success or failure of the POST.
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

    /*
        SetRunnable sets the function to run after a normal result is returned from the webserver via POST.
        SetRunnableError sets the function to run after an error is returned from the webserver via POST.
        Both require a new Utility_Post.RunnableArgs(); as the parameter with the run() function Overrided.
    */
    void SetRunnable(RunnableArgs func) {PostFunc = func;}
    void SetRunnableError(RunnableArgs func) {PostFuncErr = func;}
}
