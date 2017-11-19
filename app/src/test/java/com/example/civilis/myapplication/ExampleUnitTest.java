package com.example.civilis.myapplication;

import org.junit.Test;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void connect() throws Exception {
        String localUrl = "http://56.18.1.30:5000/api/v2/auth/login";
        URLConnection urlConnection = new URL(localUrl).openConnection();

        if (urlConnection instanceof HttpURLConnection) {
            HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setDoOutput(true);
            httpConnection.connect();
            DataOutputStream os = new DataOutputStream(httpConnection.getOutputStream());
            os.writeBytes("{\"username\": \"civilis\",\"password\": \"welcome1\"}");
            os.flush();
            os.close();
            int code = httpConnection.getResponseCode();
            String responseMsg = httpConnection.getResponseMessage();

//            Log.i("STATUS", String.valueOf(httpConnection.getResponseCode()));
//            Log.i("MSG", httpConnection.getResponseMessage());

            httpConnection.disconnect();
        }


    }
}