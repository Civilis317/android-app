package com.example.civilis.tracktracer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.civilis.tracktracer.Util.Config;
import com.example.civilis.tracktracer.tasks.SendLocationTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.Date;


public class MainService extends Service {
    private static final String TAG = "MainService";
    private Looper looper;
    private MainServiceHandler mainServiceHandler;

    private final class MainServiceHandler extends Handler {
        public MainServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            synchronized (this) {
                int i = 0;
                while(Config.isRunning()) {
                    try {
                        i++;
                        sendLocation();
                        if (Config.getInterval() == 0 ) {
                            Config.setInterval(1);
                        }
                        Log.i(i + ": " + Config.getInterval() +": " + new Date() +  TAG, "Sending location...");
                        Thread.sleep(Config.getInterval() * 1000);
                    } catch (Exception e) {
                        Log.i(TAG, e.getMessage());
                    }
                }
            }

            // stop service for start id:
            Log.i(TAG, "Finished...");
//            stopSelfResult(msg.arg1);
            stopSelf();

        }

    }

    public MainService() {
    }

    @Override
    public void onCreate() {
        HandlerThread handlerThread = new HandlerThread("sendLocationThread", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        looper = handlerThread.getLooper();
        mainServiceHandler = new MainServiceHandler(looper);
        Config.setRunning(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = mainServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mainServiceHandler.sendMessage(msg);
        Toast.makeText(this, "MainService started", Toast.LENGTH_SHORT).show();

        // if service is stopped, do not automatically restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Config.setRunning(false);
        Toast.makeText(this, "MainService stopped", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
