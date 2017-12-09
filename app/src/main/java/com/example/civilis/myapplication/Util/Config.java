package com.example.civilis.myapplication.Util;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;

import com.example.civilis.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Config extends Application {
    private static final String INTERVAL = "interval";
    private static final String TARGET = "target";
    private static String HEROKU_URL;
    private static String LOCAL_URL;

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

    public static void setTarget(String target) {
        settingsMap.put(TARGET, target);
    }

    public enum PropertyKey {
        USERNAME("USERNAME"),
        PASSWORD("PASSWORD"),
        BASE_URL_HEROKU("BASE_URL_HEROKU"),
        BASE_URL_LOCAL("BASE_URL_LOCAL"),
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
        LOCAL_URL = getString(R.string.local_url_text);
        HEROKU_URL = getString(R.string.heroku_url_text);
        super.onCreate();
    }

    public static String getProperty(PropertyKey key) throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = instance.getApplicationContext().getAssets();
        InputStream inputStream = assetManager.open("application.properties");
        properties.load(inputStream);
        return properties.getProperty(key.toString());
    }

    private static boolean IS_LOCAL() throws IOException {
        String test = (String) settingsMap.get(TARGET);
        if (test != null && test.equals(HEROKU_URL)) {
            return false;
        }
        setTarget(LOCAL_URL);
        return true;
    }

    public static String getLoginUrl() throws IOException {
        return (IS_LOCAL() ? getProperty(PropertyKey.BASE_URL_LOCAL) : getProperty(PropertyKey.BASE_URL_HEROKU)) + getProperty(PropertyKey.LOGIN_PATH);
    }

    public static String getSendLocationUrl() throws IOException {
        return (IS_LOCAL() ? getProperty(PropertyKey.BASE_URL_LOCAL) : getProperty(PropertyKey.BASE_URL_HEROKU)) + getProperty(PropertyKey.SEND_LOCATION_PATH);
    }

    public static JSONObject getAuthJson() throws IOException, JSONException {
        JSONObject authJson = new JSONObject();
        authJson.put("username", getProperty(PropertyKey.USERNAME));
        authJson.put("password", getProperty(PropertyKey.PASSWORD));
        return authJson;
    }
}
