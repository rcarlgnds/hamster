package com.example.hamster.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "HamsterAppSession";
    private static final String KEY_ACCESS_TOKEN = "accessToken";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveAuthToken(String token) {
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();
    }

    public String fetchAuthToken() {
        return pref.getString(KEY_ACCESS_TOKEN, null);
    }
}