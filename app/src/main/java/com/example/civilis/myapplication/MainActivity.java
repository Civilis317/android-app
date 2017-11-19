package com.example.civilis.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.civilis.myapplication.Util.Constants;
import com.example.civilis.myapplication.tasks.SendLocationTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;

public class MainActivity extends AppCompatActivity implements SendLocationTask.TaskDelegate {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("HardwareIds")
    public void sendLocation(View view) throws JSONException, IOException {
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
        sendLocationTask.setTaskDelegate(this);
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

    public void sendMessage(View view) throws JSONException {
        // call sendLocationTask using delegate...
        SendLocationTask sendLocationTask = new SendLocationTask();
        sendLocationTask.setTaskDelegate(this);
        sendLocationTask.execute();
    }

    @Override
    public void onTaskFinishGettingData(JSONObject result) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        intent.putExtra(Constants.NAME, result.toString());
        startActivity(intent);
    }
}

