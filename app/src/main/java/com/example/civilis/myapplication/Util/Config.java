package com.example.civilis.myapplication.Util;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config extends Application {
    public enum PropertyKey {
        USERNAME("USERNAME"),
        PASSWORD("PASSWORD"),
        BASE_URL_HEROKU("BASE_URL_HEROKU"),
        BASE_URL_LOCAL("BASE_URL_LOCAL"),
        LOGIN_PATH("LOGIN_PATH"),
        SEND_LOCATION_PATH("SEND_LOCATION_PATH"),
        GET_LOCATION_LIST_PATH("GET_LOCATION_LIST_PATH"),
        LOCAL("LOCAL");

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
        String local = getProperty(PropertyKey.LOCAL);
        return Boolean.parseBoolean(local);
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
