package com.example.civilis.myapplication;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;

import com.example.civilis.myapplication.Util.Config;
import com.example.civilis.myapplication.tasks.SendLocationTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;

public class SendLocationService extends IntentService {
    private boolean stopService;

    // constructor
    public SendLocationService() {
        super("SendLocationService");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService = true;
        stopSelf();
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        if (workIntent != null) {
            String actionString = workIntent.getAction();
            System.out.println("Action: " + actionString);
            this.loopSendLocation(actionString);
        }
    }

    private void loopSendLocation(String content) {
        int n = 0;
        while(! stopService) {
            n++;
            System.out.println(n + ": sending location, Interval is: " + Config.getInterval());
            try {
                sendLocation();
                if (Config.getInterval() == 0 ) {
                    Config.setInterval(1000);
                }
                Thread.sleep(Config.getInterval());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("stopped sending locations");
    }

    public void sendLocation() throws JSONException, IOException {
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        JSONObject trackerLocation = new JSONObject();

        trackerLocation.put("phoneid", Settings.Secure.getString(getContentResolver(), "android_id"));
//        trackerLocation.put("phoneid", "2206e9a44381684d"); // NB for testing purposes
        trackerLocation.put("model", android.os.Build.MODEL);
        trackerLocation.put("name", Settings.Secure.getString(getContentResolver(), "bluetooth_name"));

        Location location = getLastBestLocation();
        if (location == null) {
            trackerLocation.put("provider", "Null");
            trackerLocation.put("long", 0);
            trackerLocation.put("lat", 0);
            trackerLocation.put("ele", 0);
            trackerLocation.put("heading", 0);
            trackerLocation.put("speed", 0);
        } else {
            trackerLocation.put("provider", location.getProvider());
            trackerLocation.put("long", String.valueOf(location.getLongitude()));
            trackerLocation.put("lat", String.valueOf(location.getLatitude()));
            trackerLocation.put("ele", String.valueOf(location.getAltitude()));
            trackerLocation.put("heading", String.valueOf(location.getBearing()));
            trackerLocation.put("speed", String.valueOf(location.getSpeed()));
        }
        SendLocationTask sendLocationTask = new SendLocationTask();
        //sendLocationTask.setTaskDelegate(this);
        sendLocationTask.execute(trackerLocation);
    }

    private Location getLastBestLocation() throws SecurityException {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            long GPSLocationTime = 0;
            if (null != locationGPS) {
                GPSLocationTime = locationGPS.getTime();
            }

            long NetLocationTime = 0;

            if (null != locationNet) {
                NetLocationTime = locationNet.getTime();
            }

            if (0 < GPSLocationTime - NetLocationTime) {
                return locationGPS;
            } else {
                return locationNet;
            }
        }
        return null;
    }

}
