package com.example.civilis.tracktracer.Util;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Config extends Application {
    private static final String INTERVAL = "interval";
    private static final String RUNNING = "running";
    private static final String TARGET = "target";

    public static Map<String, Object> settingsMap = new HashMap<>(1024);

    public static void setInterval(Integer interval) {
        settingsMap.put(INTERVAL, interval);
    }

    public static Integer getInterval() {
        Integer result = (Integer) settingsMap.get(INTERVAL);
        if (result == null) {
            result = 0;
        }
        return result;
    }

    public static void setRunning(boolean isRunning) {
        settingsMap.put(RUNNING, new Boolean(isRunning));
    }

    public static Boolean isRunning() {
        return (Boolean) settingsMap.get(RUNNING);
    }

    public static void setTarget(String target) {
        settingsMap.put(TARGET, target);
    }

    public static String getTarget() {
        return (String) settingsMap.get(TARGET);
    }

    public enum PropertyKey {
        USERNAME("USERNAME"),
        PASSWORD("PASSWORD"),
        BASE_URL("BASE_URL"),
        LOGIN_PATH("LOGIN_PATH"),
        SEND_LOCATION_PATH("SEND_LOCATION_PATH");

        private final String name;

        private PropertyKey(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    private static Config instance;

    public static Config getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        try {
            settingsMap.put(TARGET, getProperty(PropertyKey.BASE_URL));
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onCreate();
    }

    public static String getProperty(PropertyKey key) throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = instance.getApplicationContext().getAssets();
        InputStream inputStream = assetManager.open("application.properties");
        properties.load(inputStream);
        return properties.getProperty(key.toString());
    }

    public static String getLoginUrl() throws IOException {
        return getProperty(PropertyKey.BASE_URL) + getProperty(PropertyKey.LOGIN_PATH);
    }

    public static String getSendLocationUrl() throws IOException {
        return getProperty(PropertyKey.BASE_URL) + getProperty(PropertyKey.SEND_LOCATION_PATH);
    }

    public static JSONObject getAuthJson() throws IOException, JSONException {
        JSONObject authJson = new JSONObject();
        authJson.put("username", getProperty(PropertyKey.USERNAME));
        authJson.put("password", getProperty(PropertyKey.PASSWORD));
        return authJson;
    }
}
