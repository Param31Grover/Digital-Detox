package com.subconscious.atomdigitaldetox.store;

import android.content.Context;
import android.content.SharedPreferences;

import com.subconscious.atomdigitaldetox.BuildConfig;

public class SharedPrefManager {

    private static final String SP_NAME = BuildConfig.APPLICATION_ID;
    private static final int ACCESS_MODE = Context.MODE_PRIVATE;

    public static final String FCM_TOKEN = "FCM_TOKEN";

    public static void setString(Context context, String key, String value)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, ACCESS_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, ACCESS_MODE);
        return sharedPreferences.getString(key, "");
    }

    public static boolean containsKey(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, ACCESS_MODE);
        return sharedPreferences.contains(key);
    }

    public static void setLong(Context context, String key, long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, ACCESS_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, ACCESS_MODE);
        return sharedPreferences.getLong(key, 0);
    }

    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, ACCESS_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, ACCESS_MODE);
        return sharedPreferences.getBoolean(key, false);
    }

    public static void removeKey(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, ACCESS_MODE);
        sharedPreferences.edit().remove(key).apply();
    }

}
