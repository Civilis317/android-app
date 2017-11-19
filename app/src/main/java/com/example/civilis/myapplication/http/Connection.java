package com.example.civilis.myapplication.http;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Connection {

    private int httpStatusCode;
    private String httpResponseMessage;
    private JSONObject jsonResponse;

    public Connection() throws IOException {
    }

    public JSONObject get(String url) throws IOException, JSONException {
        return get(url, null);
    }

    public JSONObject get(String url, Map<String, String> parameterMap) throws IOException, JSONException {
        JSONObject result;
        URLConnection urlConnection = new URL(url).openConnection();
        if (urlConnection instanceof HttpURLConnection) {
            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();
            this.httpStatusCode = httpConnection.getResponseCode();
            this.httpResponseMessage = httpConnection.getResponseMessage();
            if (this.httpStatusCode == 200) {
                jsonResponse = getJSONResult(urlConnection.getInputStream());
            } else {
                jsonResponse = new JSONObject();
                jsonResponse.put("authenticated", false);
                jsonResponse.put("message", this.httpResponseMessage);
            }
            httpConnection.disconnect();
            return jsonResponse;
        } else if (urlConnection instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConnection = (HttpsURLConnection) urlConnection;
            httpsConnection.setRequestMethod("GET");
            httpsConnection.connect();
            result = getJSONResult(urlConnection.getInputStream());
            this.httpStatusCode = httpsConnection.getResponseCode();
            this.httpResponseMessage = httpsConnection.getResponseMessage();
            httpsConnection.disconnect();
            return result;
        }
        throw new MalformedURLException("only http and https protocols are supported");
    }

    public JSONObject post(String url) throws IOException {
        return post(null);
    }

    public JSONObject post (String url, JSONObject payload) throws IOException, JSONException {
        URLConnection urlConnection = new URL(url).openConnection();
        if (urlConnection instanceof HttpURLConnection) {
            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setDoOutput(true);
            httpConnection.connect();
            if (payload != null) {
                DataOutputStream os = new DataOutputStream(httpConnection.getOutputStream());
                os.writeBytes(payload.toString());
                os.flush();
                os.close();
            }
            this.httpStatusCode = httpConnection.getResponseCode();
            this.httpResponseMessage = httpConnection.getResponseMessage();
            if (this.httpStatusCode == 200) {
                jsonResponse = getJSONResult(urlConnection.getInputStream());
            } else {
                jsonResponse = new JSONObject();
                jsonResponse.put("authenticated", false);
                jsonResponse.put("message", this.httpResponseMessage);
            }
            httpConnection.disconnect();
            return jsonResponse;
        } else if (urlConnection instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConnection = (HttpsURLConnection) urlConnection;
            httpsConnection.setRequestMethod("POST");
            httpsConnection.setRequestProperty("Content-Type", "application/json");
            httpsConnection.setDoOutput(true);
            httpsConnection.connect();
            if (payload != null) {
                DataOutputStream os = new DataOutputStream(httpsConnection.getOutputStream());
                os.writeBytes(payload.toString());
                os.flush();
                os.close();
            }
            JSONObject result = getJSONResult(urlConnection.getInputStream());
            httpsConnection.disconnect();
            return result;
        }
        throw new MalformedURLException("only http and https protocols are supported");
    }

    private JSONObject getJSONResult(InputStream inputStream) throws IOException, JSONException {
        StringBuilder result = new StringBuilder();
        InputStream in = new BufferedInputStream(inputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        String data = result.toString();

        // if array => take 1st element
        Object json = new JSONTokener(data).nextValue();
        if (json instanceof JSONObject) {
            return new JSONObject(result.toString());
        } else if (json instanceof JSONArray){
            return new JSONArray(data).getJSONObject(0);
        }
        return null;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getHttpResponseMessage() {
        return httpResponseMessage;
    }

}
