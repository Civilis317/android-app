package com.example.civilis.myapplication.tasks;

import android.os.AsyncTask;

import com.example.civilis.myapplication.Connection;

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

//        try {
//            JSONObject locationList = getLocationList();
//            if (this.taskDelegate != null) {
//                taskDelegate.onTaskFinishGettingData(locationList);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return "dummy";
    }

    private JSONObject sendLocation(JSONObject location) throws IOException, JSONException {
        String localUrl = "http://56.18.1.30:5000/api/v1/location/save";
//        String herokuUrl = "https://tracktracer.herokuapp.com/api/v1/location/save";
        Connection connection = new Connection();
        JSONObject jsonResponse = connection.post(localUrl, location);
        if (HttpURLConnection.HTTP_OK != connection.getHttpStatusCode()) {
            // login and try again
            if (login()) {
                jsonResponse= connection.post(localUrl, location);
            }
        }
        return jsonResponse;
    }

//    private JSONObject getLocationList() throws IOException, JSONException {
//        Connection connection = new Connection();
//        String url = "http://56.18.1.30:5000/api/v2/location/list?phoneid=2206e9a44381684d";
//        JSONObject list = connection.get(url);
//        if (200 != connection.getHttpStatusCode()) {
//            if (login()) {
//                list = connection.get(url);
//            }
//        }
//        return list;
//    }

    private boolean login() throws IOException, JSONException {
        Connection connection = new Connection();
        JSONObject authJson = new JSONObject();
        authJson.put("username", "civilis");
        authJson.put("password", "welcome1");
        JSONObject responseJson = connection.post("http://56.18.1.30:5000/api/v2/auth/login", authJson);
        return responseJson.getBoolean("authenticated");
    }

    public void setTaskDelegate(TaskDelegate taskDelegate) {
        this.taskDelegate = taskDelegate;
    }
}
