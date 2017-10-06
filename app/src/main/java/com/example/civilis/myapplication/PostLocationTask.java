package com.example.civilis.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostLocationTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... args) {
        postLocation(args[0], args[1], args[2]);
        return "dummy";
    }


    private void postLocation(String name, String longitude, String latitude) {
        try {
            URL url = new URL("http://56.18.1.30:5000/api/v1/location/save");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);


            //conn.setRequestProperty("Accept","application/json");
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
            conn.connect();

            JSONObject location = new JSONObject();
            location.put("name", name);
            location.put("long", longitude);
            location.put("lat", latitude);

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
//            os.writeBytes(URLEncoder.encode(location.toString(), "UTF-8"));
            os.writeBytes(location.toString());

            os.flush();
            os.close();

            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            Log.i("MSG" , conn.getResponseMessage());
            conn.disconnect();

        } catch (Exception  e) {
            e.printStackTrace();
        }
    }

}
