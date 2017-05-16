package com.sip.softphone.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.sip.softphone.sip.SipAccountData;

/**
 * Created by hari on 10/4/17.
 */

public class Utils {

    public static SharedPreferences mSharedPreferences;

    public static SharedPreferences getSharedPrefs(Context c) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        return mSharedPreferences;
    }

    public static void putStringInPrefs(Context c, String key, String value) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        mSharedPreferences.edit().putString(key, value).apply();
    }
    public static void putBooleanInPrefs(Context c, String key, boolean value) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        mSharedPreferences.edit().putBoolean(key,value).apply();
    }
    public static boolean getBooleanInPrefs(Context c, String key) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        return mSharedPreferences.getBoolean(key,false);
    }

    public static String getStringFromPrefs(Context c, String key) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        return mSharedPreferences.getString(key, "");
    }

    public static void putObjectInPrefs(Context c, SipAccountData accountData) {
        Gson gson = new Gson();
        String json = gson.toJson(accountData);
        getEditor(c).putString(Const.USER_ACCOUNT, json).apply();
    }

    public static SipAccountData getObjectInPrefs(Context c) {
        Gson gson = new Gson();
        String json = getPreferences(c).getString(Const.USER_ACCOUNT, "");
        SipAccountData obj = gson.fromJson(json, SipAccountData.class);
        return obj;
    }

    public static void claerAccountDetails(Context c) {
        getEditor(c).clear().apply();
        mSharedPreferences.edit().clear().apply();
    }

    public static String timeFormat(int sec) {

        // int hours = sec / 3600;
        long minutes = sec / 60;
        long seconds = sec % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);

        return timeString;
    }

    public static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }
}
