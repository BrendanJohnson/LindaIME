package com.example.brendanjohnson.linda;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import org.json.JSONArray;
import org.json.JSONObject;


public class FetchPrediction extends AsyncTask<Object, Void, JSONArray> {

    NetListener netListener;

    public FetchPrediction(NetListener listener){
        this.netListener = listener;
    }

    @Override
    protected JSONArray doInBackground(Object[] params) {
        return connect(String.valueOf(params[0]));
    }

    @Override
    protected void onPostExecute(JSONArray resultJSON) {
        netListener.onRemoteCallComplete(resultJSON);
    }

    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    private static JSONArray connect(String text) {
        try {
            Log.d("mydebug", "try GET");
            URL url = new URL("https://inputtools.google.com/request?text=" + text + "&itc=yue-hant-t-i0-und&num=13&cp=0&cs=1&ie=utf-8&oe=utf-8");
            Log.d("mydebug", "try to create connection");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            Log.d("mydebug", "try to get input stream");
            InputStream in = urlConnection.getInputStream();
            Log.d("mydebug", "try to read input stream");
            String result= convertStreamToString(in);
            JSONArray resultsJSON = new JSONArray(result);
            JSONArray resultJSON = resultsJSON.getJSONArray(1).getJSONArray(0);
            in.close();
            return resultJSON;

        } catch (Exception e) {
            Log.d("mydebug", "generic exception");
            Log.d("mydebug", Log.getStackTraceString(e));
            return null;
        }

    }
}
