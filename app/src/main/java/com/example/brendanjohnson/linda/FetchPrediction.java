package com.example.brendanjohnson.linda;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;

public class FetchPrediction extends AsyncTask<Object, Void, String> {

    NetListener netListener;
    public FetchPrediction(NetListener listener){
        this.netListener = listener;
    }
    @Override
    protected String doInBackground(Object[] params) {
        return connect();
    }

    @Override
    protected void onPostExecute(String string) {
        netListener.onRemoteCallComplete(string);
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

    private static String connect() {
        try {
            Log.d("mydebug", "try GET");
            URL url = new URL("https://inputtools.google.com/request?text=%7Cng%2Cio&itc=yue-hant-t-i0-und&num=13&cp=0&cs=1&ie=utf-8&oe=utf-8");
            Log.d("mydebug", "try to create connection");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            Log.d("mydebug", "try to get input stream");
            InputStream in = urlConnection.getInputStream();
            Log.d("mydebug", "try to read input stream");
            String result= convertStreamToString(in);
            in.close();
            //InputStreamReader isw = new InputStreamReader(in);


//            int data = isw.read();
//            while (data != -1) {
//                char current = (char) data;
//                data = isw.read();
//                System.out.print(current);
//            }
           // Log.d("mydebug", result);
            return result;

        } catch (Exception e) {
            Log.d("mydebug", "generic exception");
            Log.d("mydebug", Log.getStackTraceString(e));
            return "";
        }

    }
}
