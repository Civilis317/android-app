package com.example.civilis.myapplication.tasks;

import android.os.AsyncTask;

import com.example.civilis.myapplication.Util.Config;
import com.example.civilis.myapplication.http.Connection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

public class SendLocationTask extends AsyncTask<JSONObject, Void, String> {
    private TaskDelegate taskDelegate;

    public interface TaskDelegate {
        void onTaskFinishGettingData(JSONObject result);
    }

    @Override
    protected String doInBackground(JSONObject... jsonObjects) {
        try {
            JSONObject jsonResponse = sendLocation(jsonObjects[0]);
            if (taskDelegate != null) {
                taskDelegate.onTaskFinishGettingData(jsonResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "dummy";
    }

    private JSONObject sendLocation(JSONObject location) throws IOException, JSONException {
        String url = Config.getSendLocationUrl();
        Connection connection = new Connection();
        JSONObject jsonResponse = connection.post(url, location);
        if (HttpURLConnection.HTTP_OK != connection.getHttpStatusCode()) {
            // login and try again
            if (login()) {
                jsonResponse= connection.post(url, location);

            }
        }

        if (jsonResponse.getBoolean("posted")) {
            Config.setInterval(jsonResponse.getInt("interval"));
        }
        return jsonResponse;
    }

    private boolean login() throws IOException, JSONException {
        String url = Config.getLoginUrl();
        Connection connection = new Connection();
        JSONObject authJson = Config.getAuthJson();
        JSONObject responseJson = connection.post(url, authJson);
        return responseJson.getBoolean("authenticated");
    }

    public void setTaskDelegate(TaskDelegate taskDelegate) {
        this.taskDelegate = taskDelegate;
    }
}
