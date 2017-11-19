package com.example.civilis.myapplication;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class LoginTask extends AsyncTask<JSONObject, Void, String> {

    @Override
    protected String doInBackground(JSONObject... jsonObjects) {
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        try {
            login(jsonObjects[0]);
            getlist();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void getlist() throws IOException {
        String localUrl = "http://56.18.1.30:5000/api/v2/location/list?phoneid=2206e9a44381684d";
        URLConnection urlConnection = new URL(localUrl).openConnection();

        if (urlConnection instanceof HttpURLConnection) {
            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();
            StringBuilder result = new StringBuilder();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            int code = httpConnection.getResponseCode();
            String responseMsg = httpConnection.getResponseMessage();
            System.out.println("code: " + code);
            System.out.println("message: " + responseMsg);
            System.out.println(result.toString());

            httpConnection.disconnect();
        }

    }

    private void login(JSONObject authJson) throws IOException {
        String localUrl = "http://56.18.1.30:5000/api/v2/auth/login";
        URLConnection urlConnection = new URL(localUrl).openConnection();

        if (urlConnection instanceof HttpURLConnection) {
            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setDoOutput(true);
            httpConnection.connect();
            DataOutputStream os = new DataOutputStream(httpConnection.getOutputStream());
            os.writeBytes(authJson.toString());
            os.flush();
            os.close();
            StringBuilder result = new StringBuilder();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            int code = httpConnection.getResponseCode();
            String responseMsg = httpConnection.getResponseMessage();
            System.out.println("code: " + code);
            System.out.println("message: " + responseMsg);
            System.out.println(result.toString());
            httpConnection.disconnect();
        }

    }
}
