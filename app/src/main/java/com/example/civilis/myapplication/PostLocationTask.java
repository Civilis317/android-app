package com.example.civilis.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class PostLocationTask extends AsyncTask<JSONObject, Void, String> {

    @Override
    protected String doInBackground(JSONObject... args) {
        postLocation(args[0]);
        return "dummy";
    }

    private void postLocation(JSONObject location) {
        try {
            String localUrl = "http://56.18.1.30:5000/api/v1/location/save";
            String herokuUrl = "https://tracktracer.herokuapp.com/api/v1/location/save";
            URLConnection urlConnection = new URL(localUrl).openConnection() ;


            if (urlConnection instanceof HttpURLConnection) {
                HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
                httpConnection.setRequestMethod("POST");
                httpConnection.setRequestProperty("Content-Type", "application/json");
                httpConnection.setDoOutput(true);
                httpConnection.connect();
                DataOutputStream os = new DataOutputStream(httpConnection.getOutputStream());
                os.writeBytes(location.toString());
                os.flush();
                os.close();
                Log.i("STATUS", String.valueOf(httpConnection.getResponseCode()));
                Log.i("MSG" , httpConnection.getResponseMessage());
                httpConnection.disconnect();
            } else if (urlConnection instanceof HttpsURLConnection) {
                HttpsURLConnection httpsConnection = (HttpsURLConnection) urlConnection;
                httpsConnection.setRequestMethod("POST");
                httpsConnection.setRequestProperty("Content-Type", "application/json");
                httpsConnection.setDoOutput(true);
                httpsConnection.connect();
                DataOutputStream os = new DataOutputStream(httpsConnection.getOutputStream());
                os.writeBytes(location.toString());
                os.flush();
                os.close();
                Log.i("STATUS", String.valueOf(httpsConnection.getResponseCode()));
                Log.i("MSG" , httpsConnection.getResponseMessage());
                httpsConnection.disconnect();
            }

        } catch (Exception  e) {
            e.printStackTrace();
        }
    }

}
